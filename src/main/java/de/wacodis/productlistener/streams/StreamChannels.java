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
