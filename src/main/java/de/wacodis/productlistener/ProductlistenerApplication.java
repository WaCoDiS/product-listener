package de.wacodis.productlistener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ProductlistenerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductlistenerApplication.class, args);
    }
    

}
