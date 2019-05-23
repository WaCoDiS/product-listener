
package de.wacodis.productlistener.decode;

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
    }
    
    public <T> T decodeFromJson(String data, Class<T> clazz) throws IOException {
        return this.om.readValue(data, clazz);
    }
    
}
