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

import de.wacodis.productlistener.NewProductHandler;
import de.wacodis.productlistener.model.ProductDescription;
import de.wacodis.productlistener.model.WacodisJobFinished;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

/**
 *
 * @author <a href="mailto:m.rieke@52north.org">Matthes Rieke</a>
 */
@EnableBinding(StreamChannels.class)
public class StreamBinder implements InitializingBean {
    
    private static final Logger LOG = LoggerFactory.getLogger(StreamBinder.class.getName());
    
    @Autowired
    private NewProductHandler handler;
    

    @Override
    public void afterPropertiesSet() throws Exception {
        LOG.info("StreamBinder started.");
    }
    
    @StreamListener(StreamChannels.NEW_PROCESS_RESULT_AVAILABLE)
    public void onNewProcessResult(WacodisJobFinished jobFinishedMsg) {
        LOG.info("New process result available: {}", jobFinishedMsg);
        
        ProductDescription pd = jobFinishedMsg.getProductDescription();
        this.handler.handleNewProduct(pd);
    }
    
}
