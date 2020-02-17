/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.productlistener.wps;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 *
 * @author matthes
 */
@Component
public interface WpsConnector {

    /**
     * resolves (e.g. store the body or download the reference) of one or more process
     * results.
     * 
     * @param jobId the WPS job id
     * @param outputIdentifier the identifiers of the to be extracted outputs
     * @return a map of files, containing the outputs
     */
    public Map<String, Path> resolveProcessResult(String jobId, String... outputIdentifier);

    /**
     * provides the contents of an inline process result output as a String.
     * 
     * @param jobId the WPS job id
     * @param outputIdentifier the identifiers of the to be provided output
     * @return the output body as a String
     * @throws java.io.IOException
     */
    public String getProcessResult(String jobId, String outputIdentifier) throws IOException;
    
}
