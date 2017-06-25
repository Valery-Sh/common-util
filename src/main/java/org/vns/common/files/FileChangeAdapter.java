/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.common.files;

import java.util.Objects;

/**
 *
 * @author Valery
 */
public class FileChangeAdapter implements FileChangeListener{
    
    private String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }


    public FileChangeAdapter() {
    }
    /**
     * Fired when a data file is modified. 
     * @param ev the event describing context where action has taken place
     */
    @Override
    public void fileModified(FileEvent ev) {
    }
    /**
     * Fired when a content of a folder is modified. 
     * @param ev the event describing context where action has taken place
     */
    @Override
    public void folderModified(FileEvent ev) {
        
    }
    /**
     * Fired when a new data file is created. 
     * @param ev the event describing context where action has taken place
     */
    @Override
    public void fileCreated(FileEvent ev) {
        
    }
    /**
     * Fired when a new folder is created. 
     * @param ev the event describing context where action has taken place
     */
    @Override
    public void folderCreated(FileEvent ev) {
    }

    /**
     * Fired when a data file is deleted. 
     * @param ev the event describing context where action has taken place
     */
    @Override
    public void fileDeleted(FileEvent ev) {
    }
    /**
     * Fired when a folder is deleted. 
     * @param ev the event describing context where action has taken place
     */
    @Override
    public void folderDeleted(FileEvent ev) {
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        return true;
    }
    
    
}
