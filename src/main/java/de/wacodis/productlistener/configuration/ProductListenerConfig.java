/*
 * Copyright 2019-2021 52°North Spatial Information Research GmbH
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
package de.wacodis.productlistener.configuration;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author matthes
 */
public class ProductListenerConfig {
    
    private String pythonScriptLocation;
    private String wpsBaseUrl;
    private List<ProductCollectionMappingConfig> productCollectionMapping;

    public String getPythonScriptLocation() {
        return pythonScriptLocation;
    }

    public void setPythonScriptLocation(String pythonScriptLocation) {
        this.pythonScriptLocation = pythonScriptLocation;
    }

    public String getWpsBaseUrl() {
        return wpsBaseUrl;
    }

    public void setWpsBaseUrl(String wpsBaseUrl) {
        this.wpsBaseUrl = wpsBaseUrl;
    }

    public List<ProductCollectionMappingConfig> getProductCollectionMapping() {
        return productCollectionMapping;
    }

    public void setProductCollectionMapping(List<ProductCollectionMappingConfig> productCollectionMapping) {
        this.productCollectionMapping = productCollectionMapping;
    }
    
    public Map<String, ProductCollectionMappingConfig> getProductCollectionMappingAsMap() {
        List<ProductCollectionMappingConfig> mapping = this.getProductCollectionMapping();
        
        if (mapping == null) {
            return Collections.emptyMap();
        }
        
        Map<String, ProductCollectionMappingConfig> result = new HashMap<>(mapping.size());
        mapping.forEach(pcmc -> result.put(pcmc.productCollection, pcmc));
     
        return result;
    }
    
    public static class ProductCollectionMappingConfig {

        /**
         * Matches the resulting collection within the service backend
         */
        private String productCollection;

        /**
         * Matches the productCollection parameter from WacodisJobDefinition
         */
        private String productType;

        public String getProductCollection() {
            return productCollection;
        }

        public void setProductCollection(String productCollection) {
            this.productCollection = productCollection;
        }

        public String getProductType() {
            return productType;
        }

        public void setProductType(String productType) {
            this.productType = productType;
        }
        
    }
}
