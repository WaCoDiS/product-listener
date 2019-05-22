package de.wacodis.productlistener;

import de.wacodis.productlistener.configuration.ProductListenerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableAsync
public class ProductlistenerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductlistenerApplication.class, args);
    }
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    @Bean
    @ConfigurationProperties
    public AppConfiguration productCollectionMapping() {
        return new AppConfiguration();
    }
    
    
    public static class AppConfiguration {

        private ProductListenerConfig productListener;

        public ProductListenerConfig getProductListener() {
            return productListener;
        }

        public void setProductListener(ProductListenerConfig productListener) {
            this.productListener = productListener;
        }
        
    }

}
