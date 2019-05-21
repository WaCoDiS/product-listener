/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.productlistener.model;

/**
 *
 * @author matthes
 */
public class NewProcessResultAvailable {
    
    private String jobId;
    private String outputIdentifier;
    private String collectionId;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getOutputIdentifier() {
        return outputIdentifier;
    }

    public void setOutputIdentifier(String outputIdentifier) {
        this.outputIdentifier = outputIdentifier;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }
    
}
