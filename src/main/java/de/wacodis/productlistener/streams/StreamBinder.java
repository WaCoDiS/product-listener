
package de.wacodis.productlistener.streams;

import de.wacodis.productlistener.IngestionBackend;
import de.wacodis.productlistener.IngestionException;
import de.wacodis.productlistener.model.NewProcessResultAvailable;
import de.wacodis.productlistener.model.NewProductAvailable;
import de.wacodis.productlistener.wps.WpsConnector;
import java.nio.file.Path;
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
            NewProcessResultAvailable dummyResult = new NewProcessResultAvailable();
            dummyResult.setCollectionId("EO:WACODIS:DAT:LAND-CLASSIFICATION");
            dummyResult.setJobId("asddasdsad-test-id");
            dummyResult.setOutputIdentifier("raster");
            onNewProcessResult(dummyResult);
        }).start();
    }
    
    @Async
    public void publishNewProductAvailable(NewProductAvailable p) {
        channels.publishNewProduct().send(MessageBuilder.withPayload(p).build());
        LOG.info("Published a new product availability: {}", p);
    }
    
    @StreamListener(StreamChannels.NEW_PROCESS_RESULT_AVAILABLE)
    public void onNewProcessResult(NewProcessResultAvailable r) {
        LOG.info("New process result available: {}", r);
        Path resultFile = this.wpsConnector.resolveProcessResult(r.getJobId(), r.getOutputIdentifier());
        
        try {
            this.backend.ingestFileIntoCollection(resultFile, r.getCollectionId());
            
            NewProductAvailable p = new NewProductAvailable();
            p.setCollectionId(r.getCollectionId());
            p.setIngestionTime(new DateTime());
            
            this.publishNewProductAvailable(p);
        } catch (IngestionException ex) {
            LOG.warn(ex.getMessage());
            LOG.debug(ex.getMessage(), ex);
        }
    }
    
}
