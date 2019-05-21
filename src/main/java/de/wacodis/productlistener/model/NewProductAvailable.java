/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.productlistener.model;

import org.joda.time.DateTime;

/**
 *
 * @author matthes
 */
public class NewProductAvailable {
    
    private String collectionId;
    private DateTime ingestionTime;

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    public DateTime getIngestionTime() {
        return ingestionTime;
    }

    public void setIngestionTime(DateTime ingestionTime) {
        this.ingestionTime = ingestionTime;
    }
    
}
