/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.productlistener.python;

import de.wacodis.productlistener.IngestionBackend;
import de.wacodis.productlistener.IngestionException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 *
 * @author matthes
 */
@Component
@ConditionalOnProperty(value = "product-listener.arcgis-image-server.enabled", havingValue = "true")
public class ArcPyBackend implements IngestionBackend, InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger(ArcPyBackend.class);

    private String arcgisImageServerUrl;
    private String[] serviceTypes;

    @Value("${product-listener.arcgis-image-server.python-script-location}")
    private String pythonScriptLocation;

    @Value("${product-listener.arcgis-image-server.url}")
    public void setGeoserverUrl(String url) {
        this.arcgisImageServerUrl = url;
    }

    @Value("${product-listener.arcgis-image-server.service-types}")
    public void setServiceTypes(String[] serviceTypes) {
        this.serviceTypes = serviceTypes;
    }

    private Path pythonScript;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private boolean isWindows;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.pythonScript = Paths.get(this.pythonScriptLocation);
        if (!Files.exists(this.pythonScript)) {
            throw new IllegalStateException("The python executable is not availabe: " + this.pythonScriptLocation);
        }

        LOG.info("Using python ingestion script at: {}", this.pythonScriptLocation);
        
        isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
        
        LOG.info("Running on windows? {}", isWindows);
    }

    @Override
    public void ingestFileIntoCollection(Path resultFile, Path metadataFile, String collectionId,
            String serviceName) throws IngestionException {
        ProcessBuilder builder = new ProcessBuilder();
        String pythonScriptCommand = String.format("python %s %s %s",
                this.pythonScript.toAbsolutePath().toString(),
                resultFile.toFile().getAbsolutePath(),
                metadataFile.toFile().getAbsolutePath());
        if (isWindows) {
            builder.command("cmd.exe", "/c", pythonScriptCommand);
        } else {
            builder.command("sh", "-c", pythonScriptCommand);
        }
        builder.directory(this.pythonScript.getParent().toFile());
        
        LOG.info("Executing command '{}'", builder.command());
        
        try {
            Process process = builder.start();
            ProcessStreamHandler handler = new ProcessStreamHandler(process.getInputStream(), this::logOutput);
            ProcessStreamHandler errorHandler = new ProcessStreamHandler(process.getErrorStream(), this::logOutput);
            executor.submit(handler);
            executor.submit(errorHandler);
            int exitCode = process.waitFor();
            
            if (exitCode != 0) {
                throw new IngestionException("Python script ended with non-zero exit code: " + exitCode);
            }
        } catch (IOException | InterruptedException ex) {
            throw new IngestionException(ex.getMessage(), ex);
        }
    }

    private void logOutput(String o) {
        LOG.info("[Process output] {}", o);
    }
    
}
