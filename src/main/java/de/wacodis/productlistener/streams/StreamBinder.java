
package de.wacodis.productlistener.streams;

import de.wacodis.productlistener.NewProductHandler;
import de.wacodis.productlistener.model.ProductDescription;
import de.wacodis.productlistener.model.WacodisProductDataEnvelope;
import java.util.Collections;
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
    private NewProductHandler handler;
    

    @Override
    public void afterPropertiesSet() throws Exception {
        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                LOG.warn(ex.getMessage());
            }
            ProductDescription dummyResult = new ProductDescription();
            dummyResult.setProductCollection("EO:WACODIS:DAT:LAND-COVER-CLASSIFICATION");
            dummyResult.setJobIdentifier("f1ba2902-8164-4c45-820a-cacd5bc5bf62");
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
        this.handler.handleNewProduct(r);
    }
    
}
