
package de.wacodis.productlistener;

/**
 *
 * @author Matthes Rieke <m.rieke@52north.org>
 */
public class WpsMetadataExecption extends Exception {

    public WpsMetadataExecption(String msg) {
        super(msg);
    }

    public WpsMetadataExecption(String msg, Throwable cause) {
        super(msg, cause);
    }

}
