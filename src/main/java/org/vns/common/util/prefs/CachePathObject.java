/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.common.util.prefs;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.vns.common.PathObject;
import org.vns.common.util.prefs.files.PropertiesExt;

/**
 *
 * @author Valery
 */
public class CachePathObject implements PathObject{


    private Path absolutePath;
    
    private PathObject parent;

    protected PropertiesExt properties;

    private final List<PathObject> childrens = new ArrayList<>();

    protected CachePathObject(String simpleName) {
        this((CachePathObject) null, simpleName);
    }

    protected CachePathObject(PathObject parent, String simpleName) {
        this.parent = parent;
        if (parent == null) {
            absolutePath = Paths.get(simpleName).normalize();
        } else {
            absolutePath = Paths.get(parent.getPath().toString(), simpleName).normalize();
        }
    }
    @Override
    public boolean isFolder() {
        return properties == null;
    }


    public PropertiesExt getProperties() {
        return properties;
    }
    
    public PropertiesExt createProperties() {
        if (isFolder()) {
            properties = new PropertiesExt();
        }
        return properties;
    }

    @Override
    public List<PathObject> getChildren(boolean rec) {
        return this.childrens;
    }

    @Override
    public PathObject getParent() {
        return parent;
    }

    @Override
    public int hashCode() {
        int hash = 7;
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
        final CachePathObject other = (CachePathObject) obj;
        if (!Objects.equals(this.getPath(), other.getPath())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return stringOf(this);
    }

    @Override
    public PathObject create(PathObject parent, String name) {
        return new CachePathObject((CachePathObject) parent,name);
    }

    @Override
    public String getPath() {
        return normalize(absolutePath.toString());
    }

    @Override
    public PathObject createData(String name) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}//class
