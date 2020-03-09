/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.productlistener;

import de.wacodis.productlistener.model.AbstractBackend;

import java.nio.file.Path;

/**
 *
 * @author matthes
 */
public interface IngestionBackend {
    
    public void ingestFileIntoCollection(Path resultFile, Path metadataFile, String collectionId) throws IngestionException;

    public AbstractBackend getServiceBackend(String collectionId);
    
}
