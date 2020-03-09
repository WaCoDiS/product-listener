package de.wacodis.productlistener.geoserver;

import de.wacodis.productlistener.IngestionBackend;
import de.wacodis.productlistener.IngestionException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Base64;

import de.wacodis.productlistener.model.AbstractBackend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Matthes Rieke <m.rieke@52north.org>
 */
@Component
@ConditionalOnProperty(value = "product-listener.geoserver.enabled", havingValue = "true")
public class GeoserverIngestionBackend implements IngestionBackend, InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger(GeoserverIngestionBackend.class);

    @Autowired
    private RestTemplate restTemplate;

    private String geoserverUrl;
    private String username;
    private String password;

    @Value("${product-listener.geoserver.location}")
    public void setGeoserverUrl(String geoserverUrl) {
        this.geoserverUrl = geoserverUrl;
    }

    @Value("${product-listener.geoserver.username}")
    public void setUsername(String username) {
        this.username = username;
    }

    @Value("${product-listener.geoserver.password}")
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LOG.info("Using GeoserverIngestionBackend at: {}", this.geoserverUrl);
    }

    @Override
    public void ingestFileIntoCollection(Path resultFile, Path metadataFile, String collectionId) throws IngestionException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "text/plain");

        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.getEncoder().encode(
                auth.getBytes(Charset.forName("UTF-8")));
        String authHeader = "Basic " + new String(encodedAuth);
        headers.add("Authorization", authHeader);

        HttpEntity<String> entity = new HttpEntity("file://" + resultFile.toAbsolutePath().toString(), headers);
        
        String targetUrl = String.format("%s/rest/workspaces/%s/coveragestores/%s/external.geotiff",
                this.geoserverUrl, collectionId, resultFile.toFile().getName());
        
        LOG.info("Ingesting new geotiff: {}, {}", targetUrl, entity);
        this.restTemplate.put(targetUrl, entity);
    }

    @Override
    public AbstractBackend getServiceBackend(String collectionId) {
        return null;
    }

}
