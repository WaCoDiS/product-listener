/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.productlistener;

/**
 *
 * @author matthes
 */
public class IngestionException extends Exception {

    public IngestionException(String message) {
        super(message);
    }
    
    public IngestionException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
