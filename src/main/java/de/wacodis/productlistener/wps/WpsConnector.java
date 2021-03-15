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
