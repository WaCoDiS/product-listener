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
package de.wacodis.productlistener.wps;

import de.wacodis.productlistener.WpsMetadataExecption;
import org.apache.commons.io.FileUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
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
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author matthes
 */
@Component
public class WpsConnectorImpl implements WpsConnector {

    private static final Logger LOG = LoggerFactory.getLogger(WpsConnectorImpl.class);

    private static final String DEFAULT_FILEENDING = ".tif";

    private String wpsBaseUrl;

    @Autowired
    private CloseableHttpClient httpClient;

    private WPSClientSession wpsSession;

    private String storageDirectory;

    private boolean useOutputIDInFileName;

    @Value("${product-listener.wps-base-url}")
    public void setWpsBaseUrl(String wpsBaseUrl) {
        this.wpsBaseUrl = wpsBaseUrl;
    }

    @Value("${product-listener.file-storage-directory}")
    public void setStorageDirectory(String storageDirectory) {
        this.storageDirectory = storageDirectory;
    }

    /**
     * # should be true to ensure unique filenames when process has multiple outputs (except METADATA)
     * @param useOutputIDInFileName 
     */
    @Value("${product-listener.useOutputIDInFileName}")
    public void setUseOutputIDinFileName(boolean useOutputIDInFileName) {
        this.useOutputIDInFileName = useOutputIDInFileName;
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
    public Map<String, Path> resolveProcessResult(String jobId, String... outputIdentifier) {
        try {
            Result result = this.wpsSession.retrieveProcessResult(this.wpsBaseUrl, jobId);
            Map<String, Path> resultingFiles = new HashMap<>();
            String fileNamePrefix = DateTime.now().toString(DateTimeFormat.forPattern("yyyyMMdd_HHmm")) + "_" + jobId;

            for (String oi : outputIdentifier) {
                int retriesOnOutput = 0;
                boolean outputRetrieved = false;
                Optional<Data> o = result.getOutputs().stream().filter(d -> oi.equalsIgnoreCase(d.getId())).findAny();
                while (o.isPresent() && retriesOnOutput++ < 3 && !outputRetrieved) {
                    try {
                        resultingFiles.put(oi, storeFile(o.get(), buildFilePath(fileNamePrefix, oi, DEFAULT_FILEENDING)));
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

        return Collections.emptyMap();
    }

    private Path storeFile(Data data, Path target) throws WpsMetadataExecption, URISyntaxException, IOException {

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

        LOG.info("Start downloading WPS result: {}", asUri);

        HttpGet httpGet = new HttpGet(asUri);

        // do the download
        try (CloseableHttpResponse response = this.httpClient.execute(httpGet);) {

            InputStream is = response.getEntity().getContent();
            FileUtils.copyInputStreamToFile(is, target.toFile());

        } catch (Exception e) {
            throw new IOException("Could not download result from WPS.", e);
        }

        LOG.info("Result stored to: {}", target);

        return target;
    }

    private Path buildFilePath(String prefix, String outputID, String ending) {
        String fileName = prefix;
        if(this.useOutputIDInFileName){
            fileName = prefix + "_" + outputID;
        }

        return Paths.get(this.storageDirectory).resolve(fileName + ending);
    }

    @Override
    public String getProcessResult(String jobId, String outputIdentifier) throws IOException {
        try {
            LOG.info("retrieving output '{}' for job '{}' from WPS at {}", outputIdentifier, jobId, this.wpsBaseUrl);
            Result result = this.wpsSession.retrieveProcessResult(this.wpsBaseUrl, jobId);

            Optional<Data> o = result.getOutputs().stream().filter(d -> outputIdentifier.equalsIgnoreCase(d.getId())).findAny();
            LOG.info("Found output data: {}", o);
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
