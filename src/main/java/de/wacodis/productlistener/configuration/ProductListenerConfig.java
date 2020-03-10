/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
