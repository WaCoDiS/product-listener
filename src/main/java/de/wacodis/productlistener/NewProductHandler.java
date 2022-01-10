/*
 * Copyright 2019-${currentYearDynamic} 52°North Spatial Information Research GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.wacodis.productlistener;

import de.wacodis.productlistener.configuration.ProductListenerConfig;
import de.wacodis.productlistener.decode.JsonDecoder;
import de.wacodis.productlistener.model.*;
import de.wacodis.productlistener.streams.StreamChannels;
import de.wacodis.productlistener.wps.WpsConnector;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * @author matthes
 */
@Component
public class NewProductHandler implements InitializingBean, DisposableBean {

    private static final Logger LOG = LoggerFactory.getLogger(NewProductHandler.class);

    @Autowired
    private IngestionBackend backend;

    @Autowired
    private WpsConnector wpsConnector;

    @Autowired
    private StreamChannels channels;

    @Autowired
    private JsonDecoder jsonDecoder;

    @Autowired
    private ProductlistenerApplication.AppConfiguration productCollectionMapping;
    private String storageDirectory;
    private String dataAccessGetDataEnvelopeEndpoint;

    @Value("${product-listener.file-storage-directory}")
    public void setStorageDirectory(String storageDirectory) {
        this.storageDirectory = storageDirectory;
    }

    public String getDataAccessGetDataEnvelopeEndpoint() {
        return dataAccessGetDataEnvelopeEndpoint;
    }

    @Value("${product-listener.dataAccessGetDataEnvelopeEndpoint}")
    public void setDataAccessGetDataEnvelopeEndpoint(String dataAccessGetDataEnvelopeEndpoint) {
        this.dataAccessGetDataEnvelopeEndpoint = dataAccessGetDataEnvelopeEndpoint;
    }

    private Map<String, ProductListenerConfig.ProductCollectionMappingConfig> collectionMapping;
    private final ExecutorService ingester = Executors.newSingleThreadExecutor();

    @Override
    public void afterPropertiesSet() throws Exception {
        this.collectionMapping = this.productCollectionMapping.getProductListener().getProductCollectionMappingAsMap();
    }

    @Override
    public void destroy() throws Exception {
        this.ingester.shutdown();
    }

    public void handleNewProduct(ProductDescription r) {
        if (!this.collectionMapping.containsKey(r.getProductCollection())) {
            LOG.warn("The product collection is not configured and therefore not supported for ingestion: {}",
                    r.getProductCollection());
            return;
        }

        /*
         * submit to executor
         */
        this.ingester.submit(() -> {
            String[] asArray = new String[r.getOutputIdentifiers().size()];
            asArray = r.getOutputIdentifiers().toArray(asArray);
            Map<String, Path> resultFiles = this.wpsConnector.resolveProcessResult(r.getWpsJobIdentifier(), asArray);

            if (resultFiles != null && !resultFiles.isEmpty()) {
                try {
                    ProductListenerConfig.ProductCollectionMappingConfig collProperties = this.collectionMapping.get(r.getProductCollection());

                    // retrieve the metadata (i.e. the copernicus data envelope of the input product)
                    LOG.info("Resolving metadata output for WPS Job {}", r.getWpsJobIdentifier());
                    String metadata = this.wpsConnector.getProcessResult(r.getWpsJobIdentifier(), "METADATA");

                    if (metadata == null || metadata.isEmpty()) {
                        LOG.warn("The process result did not provide a metadata output: {}", r.getWpsJobIdentifier());
                        return;
                    }

                    ProcessingMetadata metaEnvelope = this.jsonDecoder.decodeFromJson(metadata, ProcessingMetadata.class);
                    LOG.info("metadata JSON for WPS Job {}: {}", metaEnvelope);

                    if (metaEnvelope == null || metaEnvelope.getTimeFrame() == null) {
                        LOG.warn("The process result did not provide a (valid?) metadata output: {}", r.getWpsJobIdentifier());
                        return;
                    }

                    LOG.info("Processing resolved process outputs. Keys: {}", resultFiles.keySet().stream().collect(Collectors.joining(", ")));
                    resultFiles.keySet().stream()
                            .filter(k -> !k.equalsIgnoreCase("metadata"))
                            .forEach(k -> {
                                Path resultFile = resultFiles.get(k);
                                LOG.info("Processing resolved process outputs. {}}: {}", k, resultFile);

                                try {
                                    WacodisProductDataEnvelope p = new WacodisProductDataEnvelope();
                                    p.setSourceType(AbstractDataEnvelope.SourceTypeEnum.WACODISPRODUCTDATAENVELOPE);
                                    p.setProductType(r.getProductCollection());
                                    p.setDataEnvelopeReferences(r.getDataEnvelopeReferences());
                                    p.setDataEnvelopeServiceEndpoint(this.dataAccessGetDataEnvelopeEndpoint);

                                    AbstractBackend backendDef = backend.getServiceBackend(collProperties.getProductType());
                                    p.setServiceDefinition(backendDef);
                                    if (metaEnvelope.getProcess() == null || metaEnvelope.getProcess().isEmpty()) {
                                        p.setProcess(String.format("%s_%s", r.getProductCollection(), metaEnvelope.getTimeFrame().getStartTime()));
                                    } else {
                                        p.setProcess(metaEnvelope.getProcess());
                                    }

                                    // use the time frame, AoI and creation timestamp of process result
                                    p.setTimeFrame(metaEnvelope.getTimeFrame());
                                    p.setCreated(metaEnvelope.getCreated());
                                    p.setModified(p.getCreated());

                                    if (metaEnvelope.getAreaOfInterest() != null) {
                                        p.setAreaOfInterest(metaEnvelope.getAreaOfInterest());
                                    }

                                    Path metaFile = Paths.get(this.storageDirectory).resolve(r.getWpsJobIdentifier() + ".meta");
                                    Files.write(metaFile, this.jsonDecoder.encodeToJson(p).getBytes());

                                    LOG.info("Starting data ingestion with input files: {}, {}, '{}'", resultFile, metaFile, r.getProductCollection());
                                    this.backend.ingestFileIntoCollection(resultFile, metaFile, r.getProductCollection());

                                    this.ingester.submit(() -> {
                                        publishNewProductAvailable(p);
                                    });
                                } catch (IOException ex) {
                                    LOG.warn("Error on encoding WacodisProductDataEnvelope: " + ex.getMessage());
                                    LOG.debug("Error on encoding WacodisProductDataEnvelope: " + ex.getMessage(), ex);
                                } catch (IngestionException ex) {
                                    LOG.warn("Error on ingestion execution: " + ex.getMessage());
                                    LOG.debug("Error on ingestion execution: " + ex.getMessage(), ex);
                                }
                            });
                } catch (IOException | RuntimeException ex) {
                    LOG.warn("Error on wps result processing: " + ex.getMessage());
                    LOG.debug("Error on wps result processing: " + ex.getMessage(), ex);
                }

            } else {
                LOG.warn("No valid/referenced process outputs found for JobID '{}'", r.getWpsJobIdentifier());
            }

        });

    }

    public void publishNewProductAvailable(WacodisProductDataEnvelope p) {
        channels.publishNewProduct().send(MessageBuilder.withPayload(p).build());
        LOG.info("Published a new product availability: {}", p);
    }

}
