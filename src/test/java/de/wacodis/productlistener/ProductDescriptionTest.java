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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.wacodis.productlistener.model.ProductDescription;
import java.util.Collections;

/**
 *
 * @author Matthes Rieke <m.rieke@52north.org>
 */
public class ProductDescriptionTest {
    
    public static void main(String[] args) throws JsonProcessingException {
        ProductDescription dummyResult = new ProductDescription();
        dummyResult.setProductCollection("EO:WACODIS:DAT:S2A_RAW");
        dummyResult.setWpsJobIdentifier("30d5a717-880a-4bd3-b911-d911a5b986fb");
        dummyResult.setOutputIdentifiers(Collections.singletonList("PRODUCT"));
        
        System.out.println(new ObjectMapper().writeValueAsString(dummyResult));
    }

}
