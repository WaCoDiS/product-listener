
package de.wacodis.productlistener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.wacodis.productlistener.model.ProductDescription;
import java.util.Collections;

/**
 *
 * @author Matthes Rieke <m.rieke@52north.org>
 */
public class ProductDescriptionTest {
    
    public static void main(String[] args) throws JsonProcessingException {
        ProductDescription dummyResult = new ProductDescription();
        dummyResult.setProductCollection("EO:WACODIS:DAT:S2A_RAW");
        dummyResult.setWpsJobIdentifier("30d5a717-880a-4bd3-b911-d911a5b986fb");
        dummyResult.setOutputIdentifiers(Collections.singletonList("PRODUCT"));
        
        System.out.println(new ObjectMapper().writeValueAsString(dummyResult));
    }

}
