/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.productlistener.wps;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author matthes
 */
@Component
public class WpsConnectorImpl implements WpsConnector {
    
    @Value("${productlistener.wps-base-url}")
    private String wpsBaseUrl;

    @Override
    public List<Path> resolveProcessResult(String jobId, String... outputIdentifier) {
        return Collections.singletonList(Paths.get("/home/matthes/git/wacodis/productlistener/src/test/resources/i30dem.tif"));
    }
    
}
