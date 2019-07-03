/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.productlistener.wps;

import de.wacodis.productlistener.WpsMetadataExecption;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.n52.geoprocessing.wps.client.WPSClientException;
import org.n52.geoprocessing.wps.client.WPSClientSession;
import org.n52.geoprocessing.wps.client.model.Result;
import org.n52.geoprocessing.wps.client.model.execution.ComplexData;
import org.n52.geoprocessing.wps.client.model.execution.Data;
import org.n52.geoprocessing.wps.client.model.execution.LiteralData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
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
    
    private String wpsBaseUrl;
    
    @Autowired
    private RestTemplate restTemplate;
    
    private WPSClientSession wpsSession;
    
    private String storageDirectory;

    @Value("${product-listener.wps-base-url}")
    public void setWpsBaseUrl(String wpsBaseUrl) {
        this.wpsBaseUrl = wpsBaseUrl;
    }

    @Value("${product-listener.file-storage-directory}")
    public void setStorageDirectory(String storageDirectory) {
        this.storageDirectory = storageDirectory;
    }
    
    @Autowired
    public void setWpsSession(WPSClientSession wpsSession) {
        this.wpsSession = wpsSession;
    }
    
    @Bean
    public WPSClientSession wpsSession() {
        return new WPSClientSession();
    }
    
    @Override
    public List<Path> resolveProcessResult(String jobId, String... outputIdentifier) {
        try {
            Result result = this.wpsSession.retrieveProcessResult(this.wpsBaseUrl, jobId);
            List<Path> resultingFiles = new ArrayList<>();
            
            for (String oi : outputIdentifier) {
                int retriesOnOutput = 0;
                boolean outputRetrieved = false;
                Optional<Data> o = result.getOutputs().stream().filter(d -> oi.equalsIgnoreCase(d.getId())).findAny();
                while (o.isPresent() && retriesOnOutput++ < 3 && !outputRetrieved) {
                    try {
                        resultingFiles.add(storeFile(o.get()));
                        outputRetrieved = true;
                    } catch (IOException ex) {
                        LOG.warn("Could not retrieve process output: " + ex.getMessage());
                        LOG.info("Number of tries for output {}: {}", oi, retriesOnOutput);
                    }
                }
            }
            
            return resultingFiles;
        } catch (WpsMetadataExecption | IOException | URISyntaxException | WPSClientException ex) {
            LOG.warn("Could not process GetResult: " + ex.getMessage());
            LOG.debug(ex.getMessage(), ex);
        }
        
        return Collections.emptyList();
    }

    private Path storeFile(Data data) throws WpsMetadataExecption, URISyntaxException, IOException {
        
        ComplexData complexData;
        if (data instanceof ComplexData) {
            complexData = ((ComplexData) data);
        } else {
            complexData = data.asComplexData();
        }
        
        // we currently only support result by reference
        URI asUri;
        if (complexData.getReference() != null && complexData.getReference().getHref() != null) {
            asUri = complexData.getReference().getHref().toURI();
        } else {
            throw new WpsMetadataExecption("Could not resolve reference of process output: " + complexData);
        }
        
        // two cases can happen here:
        // 1) no RequestBody: it is a plain HTTP GET link
        // 2) a RequestBody with Body or BodyReference: a HTTP POST must be done with the body
        // currently we only support HTTP GET
        
        if (complexData.getReference().getBody() != null) {
            LOG.warn("A complex data with a POST body was found. This is currently not supported. POST body: " + complexData.getReference().getBody());
            throw new WpsMetadataExecption("Unsupported reference via HTTP POST");
        }
        
        if (complexData.getReference().getBodyReference() != null) {
            LOG.warn("A complex data with a reference to POST body was found. This is currently not supported. POST body: " + complexData.getReference().getBodyReference());
            throw new WpsMetadataExecption("Unsupported reference via HTTP POST");
        }
        
        LOG.info("Downloading WPS result: {}", asUri);
        
        // do the download
        Resource response = this.restTemplate.getForObject(asUri, Resource.class);
        
        if (response == null) {
            throw new IOException("Could not download result from WPS.");
        }
        
        String fileNamePrefix = DateTime.now().toString(DateTimeFormat.forPattern("yyyyMMdd_HHmm"));
        
        Path target = Paths.get(this.storageDirectory).resolve(fileNamePrefix + ".geotiff");
        Files.copy(response.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        
        LOG.info("Result stored to: {}", target);
        
        return target;
    }

    @Override
    public String getProcessResult(String jobId, String outputIdentifier) throws IOException {
        try {
            Result result = this.wpsSession.retrieveProcessResult(this.wpsBaseUrl, jobId);
            
            Optional<Data> o = result.getOutputs().stream().filter(d -> outputIdentifier.equalsIgnoreCase(d.getId())).findAny();
            if (o.isPresent()) {
                Data data = o.get();
                
                if (data instanceof ComplexData) {
                    data.getValue();
                    return data.getValue().toString();
                } else if (data instanceof LiteralData) {
                    LiteralData literal = (LiteralData) data;
                    return literal.getValue().toString();
                }
                
            }
            
        } catch (IOException | WPSClientException ex) {
            LOG.warn("Could not process GetResult: " + ex.getMessage());
            LOG.debug(ex.getMessage(), ex);
        }
        
        throw new IOException("Could not resolve process job/output: " + jobId + "/" + outputIdentifier);
    }
    
}
