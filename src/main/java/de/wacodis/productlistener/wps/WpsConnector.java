/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.productlistener.wps;

import java.nio.file.Path;
import org.springframework.stereotype.Component;

/**
 *
 * @author matthes
 */
@Component
public interface WpsConnector {

    public Path resolveProcessResult(String jobId, String outputIdentifier);
    
}
