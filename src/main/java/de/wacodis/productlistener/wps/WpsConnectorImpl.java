/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.productlistener.wps;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.n52.geoprocessing.wps.client.WPSClientException;
import org.n52.geoprocessing.wps.client.WPSClientSession;
import org.n52.geoprocessing.wps.client.model.Result;
import org.n52.geoprocessing.wps.client.model.execution.ComplexData;
import org.n52.geoprocessing.wps.client.model.execution.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author matthes
 */
@Component
public class WpsConnectorImpl implements WpsConnector {
    
    private static final Logger LOG = LoggerFactory.getLogger(WpsConnectorImpl.class);
    
    @Value("${productlistener.wps-base-url}")
    private String wpsBaseUrl;

    @Override
    public List<Path> resolveProcessResult(String jobId, String... outputIdentifier) {
        synchronized (this) {
            WPSClientSession.reset();
        }
        
        WPSClientSession session = WPSClientSession.getInstance();
        try {
            Result result = session.retrieveProcessResult(wpsBaseUrl, jobId);
            List<Path> resultingFiles = new ArrayList<>();
            
            for (String oi : outputIdentifier) {
                Optional<Data> o = result.getOutputs().stream().filter(d -> oi.equals(d.getId())).findAny();
                if (o.isPresent()) {
                    resultingFiles.add(storeFile(o.get()));
                }
            }
            
            return resultingFiles;
        } catch (IOException | URISyntaxException | WPSClientException ex) {
            LOG.warn("Could not process GetResult: " + ex.getMessage());
            LOG.debug(ex.getMessage(), ex);
        }
        
        return Collections.emptyList();
    }

    private Path storeFile(Data data) throws IOException, URISyntaxException {
        URI asUri;
        if (data instanceof ComplexData) {
            asUri = ((ComplexData) data).getReference().getHref().toURI();
        } else {
            asUri = data.asComplexData().getReference().getHref().toURI();            
        }
        
        Resource response = new RestTemplate().getForObject(asUri, Resource.class);
        
        if (response == null) {
            throw new IOException("Could not download result from WPS.");
        }
        
        Path target = Files.createTempFile("temp-", ".geotiff");
        Files.copy(response.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        
        return target;
    }
    
}
