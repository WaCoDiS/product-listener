/*
 * Copyright 2019-2021 52°North Spatial Information Research GmbH
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
package de.wacodis.productlistener.decode;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import java.io.IOException;
import org.springframework.stereotype.Component;

/**
 *
 * @author Matthes Rieke <m.rieke@52north.org>
 */
@Component
public class JsonDecoder {

    private final ObjectMapper om;

    public JsonDecoder() {
        this.om = new ObjectMapper();
        this.om.registerModule(new JodaModule());
        this.om.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS , false);
        this.om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    
    public <T> T decodeFromJson(String data, Class<T> clazz) throws IOException {
        return this.om.readValue(data, clazz);
    }
    
    public <T> String encodeToJson(T data) throws JsonProcessingException {
        return this.om.writeValueAsString(data);
    }
    
}
