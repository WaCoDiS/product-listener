/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.productlistener;

import de.wacodis.productlistener.configuration.ProductListenerConfig;
import de.wacodis.productlistener.decode.JsonDecoder;
import de.wacodis.productlistener.model.AbstractDataEnvelope;
import de.wacodis.productlistener.model.CopernicusDataEnvelope;
import de.wacodis.productlistener.model.ProductDescription;
import de.wacodis.productlistener.model.WacodisProductDataEnvelope;
import de.wacodis.productlistener.streams.StreamChannels;
import de.wacodis.productlistener.wps.WpsConnector;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 *
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
    
    @Value("${product-listener.file-storage-directory}")
    public void setStorageDirectory(String storageDirectory) {
        this.storageDirectory = storageDirectory;
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
            Map<String, Path> resultFiles = this.wpsConnector.resolveProcessResult(r.getJobIdentifier(), asArray);

            if (resultFiles != null && !resultFiles.isEmpty()) {
                try {
                    ProductListenerConfig.ProductCollectionMappingConfig collProperties = this.collectionMapping.get(r.getProductCollection());
                    
                    // retrieve the metadata (i.e. the copernicus data envelope of the input product)
                    String metadata = this.wpsConnector.getProcessResult(r.getJobIdentifier(), "METADATA");
                    
                    if (metadata == null || metadata.isEmpty()) {
                        LOG.warn("The process result did not provide a metadata output: {}", r.getJobIdentifier());
                        return;
                    }
                    
                    CopernicusDataEnvelope metaEnvelope = this.jsonDecoder.decodeFromJson(metadata, CopernicusDataEnvelope.class);
                    
                    if (metaEnvelope == null || metaEnvelope.getTimeFrame() == null) {
                        LOG.warn("The process result did not provide a (valid?) metadata output: {}", r.getJobIdentifier());
                        return;
                    }
                    
                    resultFiles.keySet().stream()
                            .filter(k -> !k.equalsIgnoreCase("metadata"))
                            .forEach(k -> {
                                Path resultFile = resultFiles.get(k);
                                
                                try {
                                    WacodisProductDataEnvelope p = new WacodisProductDataEnvelope();
                                    p.setSourceType(AbstractDataEnvelope.SourceTypeEnum.WACODISPRODUCTDATAENVELOPE);
                                    p.setProductCollection(r.getProductCollection());
                                    p.setCreated(new DateTime().toDateTime(DateTimeZone.UTC));
                                    p.setModified(p.getCreated());
                                    
                                    p.setProductType(collProperties.getProductType());
                                    p.setServiceName(collProperties.getServiceName());
                                    p.setIdentifier(String.format("%s_%s", r.getProductCollection(), metaEnvelope.getTimeFrame().getStartTime()));
                                    
                                    // use the time frame and AoI of the original sentinel scene
                                    p.setTimeFrame(metaEnvelope.getTimeFrame());
                                    
                                    if (metaEnvelope.getAreaOfInterest() != null) {
                                        p.setAreaOfInterest(metaEnvelope.getAreaOfInterest());
                                    }
                                    
                                    Path metaFile = Paths.get(this.storageDirectory).resolve(r.getJobIdentifier() + ".meta");
                                    Files.write(metaFile, this.jsonDecoder.encodeToJson(p).getBytes());
                                    
                                    this.backend.ingestFileIntoCollection(resultFile, metaFile, r.getProductCollection(), collProperties.getServiceName());
                                    
                                    this.ingester.submit(() -> {
                                        publishNewProductAvailable(p);
                                    });
                                } catch (IngestionException | IOException ex) {
                                    LOG.warn("Error on ingestion execution: " + ex.getMessage());
                                    LOG.debug("Error on ingestion execution: " + ex.getMessage(), ex);
                                }
                            });
                } catch (IOException ex) {
                    LOG.warn("Error on wps result processing: " + ex.getMessage());
                    LOG.debug("Error on wps result processing: " + ex.getMessage(), ex);
                }

            } else {
                LOG.warn("No valid/referenced process outputs found for JobID '{}'", r.getJobIdentifier());
            }

        });

    }

    public void publishNewProductAvailable(WacodisProductDataEnvelope p) {
        channels.publishNewProduct().send(MessageBuilder.withPayload(p).build());
        LOG.info("Published a new product availability: {}", p);
    }

}
