/*
 * Copyright 2019-${currentYearDynamic} 52°North Spatial Information Research GmbH
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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import de.wacodis.productlistener.decode.JsonDecoder;
import de.wacodis.productlistener.model.AbstractDataEnvelopeAreaOfInterest;
import de.wacodis.productlistener.model.AbstractDataEnvelopeTimeFrame;
import de.wacodis.productlistener.model.CopernicusDataEnvelope;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import org.hamcrest.CoreMatchers;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.n52.geoprocessing.wps.client.WPSClientException;
import org.n52.geoprocessing.wps.client.WPSClientSession;
import org.n52.geoprocessing.wps.client.xml.WPSResponseReader;

/**
 *
 * @author Matthes Rieke <m.rieke@52north.org>
 */
public class WpsConnectorImplTest {

    @Test
    public void testResultParsing() throws JsonProcessingException, IOException, XMLStreamException, WPSClientException {
        // spy on the WPS session to fake an http response
        XMLEventReader xmlReader = XMLInputFactory.newInstance()
                .createXMLEventReader(new InputStreamReader(resolveResource("/process-result.xml"), StandardCharsets.UTF_8));
        Object mockResponse = new WPSResponseReader().readElement(xmlReader);
        
        WPSClientSession spied = Mockito.mock(WPSClientSession.class);
        Mockito.doReturn(mockResponse).when(spied).
                    retrieveProcessResult(Mockito.anyString(), Mockito.anyString());
        
        // now init the connector
        WpsConnectorImpl conn = new WpsConnectorImpl();
        conn.setWpsSession(spied);
        conn.setWpsBaseUrl("test");
        String result = conn.getProcessResult("asdf", "metadata");
        CopernicusDataEnvelope cde = new JsonDecoder().decodeFromJson(result, CopernicusDataEnvelope.class);
        
        Assert.assertThat(cde.getDatasetId(), CoreMatchers.equalTo("20190515T103657_32UMB_0"));
        
        AbstractDataEnvelopeAreaOfInterest aoi = cde.getAreaOfInterest();
        Assert.assertThat(aoi.getExtent().get(0), CoreMatchers.equalTo(51.0f));
        Assert.assertThat(aoi.getExtent().get(1), CoreMatchers.equalTo(6.0f));
        Assert.assertThat(aoi.getExtent().get(2), CoreMatchers.equalTo(52.0f));
        Assert.assertThat(aoi.getExtent().get(3), CoreMatchers.equalTo(7.0f));
        
        AbstractDataEnvelopeTimeFrame tf = cde.getTimeFrame();
        Assert.assertThat(tf.getStartTime(), CoreMatchers.equalTo(new DateTime("2019-05-15T10:36:43Z").toDateTime(DateTimeZone.UTC)));
        Assert.assertThat(tf.getEndTime(), CoreMatchers.equalTo(new DateTime("2019-05-15T10:36:57Z").toDateTime(DateTimeZone.UTC)));
    }
    
    @Test
    public void testResultParsingAlternativeFormat() throws JsonProcessingException, IOException, XMLStreamException, WPSClientException {
        // spy on the WPS session to fake an http response
        XMLEventReader xmlReader = XMLInputFactory.newInstance()
                .createXMLEventReader(new InputStreamReader(resolveResource("/process-result_alt.xml"), StandardCharsets.UTF_8));
        Object mockResponse = new WPSResponseReader().readElement(xmlReader);
        
        WPSClientSession spied = Mockito.mock(WPSClientSession.class);
        Mockito.doReturn(mockResponse).when(spied).
                    retrieveProcessResult(Mockito.anyString(), Mockito.anyString());
        
        // now init the connector
        WpsConnectorImpl conn = new WpsConnectorImpl();
        conn.setWpsSession(spied);
        conn.setWpsBaseUrl("test");
        String result = conn.getProcessResult("asdf", "metadata");
        CopernicusDataEnvelope cde = new JsonDecoder().decodeFromJson(result, CopernicusDataEnvelope.class);
        
        AbstractDataEnvelopeTimeFrame tf = cde.getTimeFrame();
        Assert.assertThat(tf.getStartTime(), CoreMatchers.equalTo(new DateTime("2019-05-15T10:36:43Z").toDateTime(DateTimeZone.UTC)));
        Assert.assertThat(tf.getEndTime(), CoreMatchers.equalTo(new DateTime("2019-05-15T10:36:57Z").toDateTime(DateTimeZone.UTC)));
    }

    private InputStream resolveResource(String processresultxml) {
        return this.getClass().getResourceAsStream(processresultxml);
    }
    
}
