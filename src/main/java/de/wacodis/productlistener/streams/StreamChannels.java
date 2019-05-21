
package de.wacodis.productlistener.streams;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 *
 * @author <a href="mailto:m.rieke@52north.org">Matthes Rieke</a>
 */
public interface StreamChannels {
    
    String NEW_PROCESS_RESULT_AVAILABLE = "new-process-result";
    String NEW_PRODUCT_AVAILABLE = "new-product";
 
    @Input(NEW_PROCESS_RESULT_AVAILABLE)
    SubscribableChannel receiveNewProcessResult();
 
    @Output(NEW_PRODUCT_AVAILABLE)
    MessageChannel publishNewProduct();
 
}
