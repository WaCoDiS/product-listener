/*
 * Copyright 2019-2021 52°North Initiative for Geospatial Open Source
 * Software GmbH
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
package de.wacodis.productlistener.python;

import de.wacodis.productlistener.IngestionBackend;
import de.wacodis.productlistener.IngestionException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.wacodis.productlistener.model.AbstractBackend;
import de.wacodis.productlistener.model.ArcGISImageServerBackend;
import de.wacodis.productlistener.model.ProductBackend;
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

    private static final String WACODIS_SERVICE_FOLDER = "WaCoDiS";

    private static final Logger LOG = LoggerFactory.getLogger(ArcPyBackend.class);

    private String arcgisImageServerUrl;
    private String serviceType;

    @Value("${product-listener.arcgis-image-server.python-script-location}")
    private String pythonScriptLocation;

    @Value("${product-listener.arcgis-image-server.url}")
    public void setGeoserverUrl(String url) {
        this.arcgisImageServerUrl = url;
    }

    @Value("${product-listener.arcgis-image-server.service-type}")
    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
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
    public void ingestFileIntoCollection(Path resultFile, Path metadataFile, String collectionId) throws IngestionException {
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

    @Override
    public AbstractBackend getServiceBackend(String collectionId) {
        ArcGISImageServerBackend backend = new ArcGISImageServerBackend();
        backend.setBackendType(ProductBackend.ARCGISIMAGESERVERBACKEND);
        backend.setBaseUrl(this.arcgisImageServerUrl);
        backend.setServiceTypes(Collections.singletonList(this.serviceType));
        backend.setProductCollection(String.join("/", WACODIS_SERVICE_FOLDER, collectionId));
        return backend;
    }

    private void logOutput(String o) {
        LOG.info("[Process output] {}", o);
    }

}
