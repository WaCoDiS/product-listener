
package de.wacodis.productlistener.streams;

import de.wacodis.productlistener.IngestionBackend;
import de.wacodis.productlistener.IngestionException;
import de.wacodis.productlistener.model.AbstractDataEnvelopeTimeFrame;
import de.wacodis.productlistener.model.ProductDescription;
import de.wacodis.productlistener.model.WacodisProductDataEnvelope;
import de.wacodis.productlistener.wps.WpsConnector;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Async;

/**
 *
 * @author <a href="mailto:m.rieke@52north.org">Matthes Rieke</a>
 */
@EnableBinding(StreamChannels.class)
public class StreamBinder implements InitializingBean {
    
    private static final Logger LOG = LoggerFactory.getLogger(StreamBinder.class.getName());
    
    @Autowired
    private StreamChannels channels;
    
    @Autowired
    private IngestionBackend backend;
    
    @Autowired
    private WpsConnector wpsConnector;

    @Override
    public void afterPropertiesSet() throws Exception {
        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                LOG.warn(ex.getMessage());
            }
            ProductDescription dummyResult = new ProductDescription();
            dummyResult.setProductCollection("EO:WACODIS:DAT:LAND-CLASSIFICATION");
            dummyResult.setJobIdentifier("a2dec313-108a-4741-af23-c1d62fa96288");
            dummyResult.setOutputIdentifiers(Collections.singletonList("result"));
            onNewProcessResult(dummyResult);
        }).start();
    }
    
    @Async
    public void publishNewProductAvailable(WacodisProductDataEnvelope p) {
        channels.publishNewProduct().send(MessageBuilder.withPayload(p).build());
        LOG.info("Published a new product availability: {}", p);
    }
    
    @StreamListener(StreamChannels.NEW_PROCESS_RESULT_AVAILABLE)
    public void onNewProcessResult(ProductDescription r) {
        LOG.info("New process result available: {}", r);
        String[] asArray = new String[r.getOutputIdentifiers().size()];
        asArray = r.getOutputIdentifiers().toArray(asArray);
        List<Path> resultFiles = this.wpsConnector.resolveProcessResult(r.getJobIdentifier(), asArray);
        
        try {
            for (Path resultFile : resultFiles) {
                this.backend.ingestFileIntoCollection(resultFile, r.getProductCollection());
            }
            
            WacodisProductDataEnvelope p = new WacodisProductDataEnvelope();
            p.setProductCollection(r.getProductCollection());
            p.setCreated(new DateTime());
            p.setModified(p.getCreated());
            AbstractDataEnvelopeTimeFrame tf = new AbstractDataEnvelopeTimeFrame();
            tf.setEndTime(p.getCreated());
            tf.setStartTime(p.getCreated());
            p.setTimeFrame(tf);
            
            this.publishNewProductAvailable(p);
        } catch (IngestionException ex) {
            LOG.warn(ex.getMessage());
            LOG.debug(ex.getMessage(), ex);
        }
    }
    
}
