/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.productlistener;

import de.wacodis.productlistener.configuration.ProductListenerConfig;
import de.wacodis.productlistener.decode.JsonDecoder;
import de.wacodis.productlistener.model.AbstractDataEnvelope;
import de.wacodis.productlistener.model.AbstractDataEnvelopeTimeFrame;
import de.wacodis.productlistener.model.CopernicusDataEnvelope;
import de.wacodis.productlistener.model.ProductDescription;
import de.wacodis.productlistener.model.WacodisProductDataEnvelope;
import de.wacodis.productlistener.streams.StreamBinder;
import de.wacodis.productlistener.wps.WpsConnector;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author matthes
 */
@Component
public class NewProductHandler implements InitializingBean {
    
    private static final Logger LOG = LoggerFactory.getLogger(NewProductHandler.class);
    
    @Autowired
    private IngestionBackend backend;
    
    @Autowired
    private WpsConnector wpsConnector;
    
    @Autowired
    private StreamBinder streams;
    
    @Autowired
    private JsonDecoder jsonDecoder;
    
    @Autowired
    private ProductlistenerApplication.AppConfiguration productCollectionMapping;
    
    private Map<String, ProductListenerConfig.ProductCollectionMappingConfig> collectionMapping;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.collectionMapping = this.productCollectionMapping.getProductListener().getProductCollectionMappingAsMap();
    }
    
    public void handleNewProduct(ProductDescription r) {
        if (!this.collectionMapping.containsKey(r.getProductCollection())) {
            LOG.warn("The product collection is not configured and therefore not supported for ingestion: {}",
                    r.getProductCollection());
            return;
        }
        
        String[] asArray = new String[r.getOutputIdentifiers().size()];
        asArray = r.getOutputIdentifiers().toArray(asArray);
        List<Path> resultFiles = this.wpsConnector.resolveProcessResult(r.getJobIdentifier(), asArray);
        
        try {
            if (resultFiles != null && !resultFiles.isEmpty()) {
                ProductListenerConfig.ProductCollectionMappingConfig collProperties = this.collectionMapping.get(r.getProductCollection());
                for (Path resultFile : resultFiles) {
                    this.backend.ingestFileIntoCollection(resultFile, r.getProductCollection(), collProperties.getServiceName());
                }

                // retrieve the metadata (i.e. the copernicus data envelope of the input product)
                String metadata = this.wpsConnector.getProcessResult(r.getJobIdentifier(), "metadata");
                CopernicusDataEnvelope metaEnvelope = this.jsonDecoder.decodeFromJson(metadata, CopernicusDataEnvelope.class);
                
                if (metaEnvelope == null || metaEnvelope.getTimeFrame() == null) {
                    LOG.warn("The process result did not provide a (valid?) metadata output: {}", r.getJobIdentifier());
                    return;
                }

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

                streams.publishNewProductAvailable(p);                
            } else {
                LOG.warn("No valid/referenced process outputs found for JobID '{}'", r.getJobIdentifier());
            }
        } catch (IngestionException | IOException ex) {
            LOG.warn("Error on ingestion execution: " + ex.getMessage());
            LOG.debug("Error on ingestion execution: " + ex.getMessage(), ex);
        }
    }
    
}
