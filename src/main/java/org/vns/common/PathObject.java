package org.vns.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 *
 * @author Valery
 */
public interface PathObject {

//    PropertiesExt getProperties();
    
//    PropertiesExt createProperties();
    
    PathObject createData(String name) throws IOException;
    
/*    default PropertiesExt createProperties(PropertiesExt properties) {
        PropertiesExt props = getProperties();
        if ( props == null ) {
            props  = createProperties();
        }
        props.putAll(properties);
        return props;
    }
*/    
    public static Path toPath(PathObject po) {
        return Paths.get(po.getPath());
    }
    
    default PathObject getPathObject(String... relativePath) {
        if ( ! isValid() ) {
            return null;
        }
        if (relativePath == null) {
            throw new NullPointerException("PathObject.getPathObject(String): the parameter value cannot be null. ");
        }
        if ( ! isFolder() ) {
            throw new IllegalArgumentException("The file " + getPath() + "is not a folder" );
        }
        if (relativePath.length == 0) {
            return this;
        }
        Path absPath = Paths.get(String.join("/", relativePath));
        
        PathObject po = this;
        int lastIdx = -1;

        for (int i = 0; i < absPath.getNameCount(); i++) {
            PathObject relObj = create(po, absPath.getName(i).toString());
            int idx = po.getChildren().indexOf(relObj);
            if (idx >= 0) {
                lastIdx = i;
                po = po.getChildren().get(idx);
            } else {
                break;
            }
        }
        if (lastIdx < absPath.getNameCount() - 1) {
            return null;
        }
        return po;

    }
    
    default void create() {};
    
    PathObject create(PathObject parent, String name);

    /**
     * Creates a directory by creating all nonexistent parent directories first.
     * @param folderName
     * @return the new folder
     * @throws java.io.IOException
     */
    default PathObject createFolder(String folderName) throws IOException {
        
        if (folderName != null && folderName.trim().isEmpty()) {
            return this;
        }
        
        if (!isFolder() ) {
            throw new IOException("The file " + getPath() + " is not a folder");
        }
        if (folderName == null ) {
            throw new IOException("The name argument value  cannot be null");
        }
        if (folderName.contains("\\") || folderName.contains("/") ) {
            throw new IOException("The argument value cannot contain slashes");
        }
        return createFolders(folderName);
    }    
    default PathObject createFolders(String relativePath) throws IOException {
        if (relativePath == null || relativePath.isEmpty()) {
            return null;
        }
        //Path absPath = Paths.get(getPath().joinValues(), relativePath);
        Path absPath = Paths.get(relativePath);

        PathObject po = this;
        int lastIdx = -1;

        for (int i = 0; i < absPath.getNameCount(); i++) {
            PathObject relObj = create(po, absPath.getName(i).toString());
            int idx = po.getChildren().indexOf(relObj);
            if (idx >= 0) {
                lastIdx = i;
                po = po.getChildren().get(idx);
            } else {
                break;
            }
        }
        Path relPath = Paths.get(relativePath);
        if (lastIdx < 0) {
            lastIdx = 0;
        } else {
            ++lastIdx;
        }
        for (int i = lastIdx; i < relPath.getNameCount(); i++) {
            String name = relPath.getName(i).toString();
            PathObject toAdd = create(po, name);
            po.getChildren().add(toAdd);
            po = toAdd;
        }
        return po;
    }

/*    default String getPath() {
        return normalize(absolutePath().joinValues());
    }
*/
    String getPath();
    
    //Path absolutePath();

    default String normalize(String str) {
        return str.replace("\\", "/");
    }
    /**
     * Test whether this object is a folder.
     * @return true if the path object is a folder (i.e., can have children)
     */
    boolean isFolder();
    
    /**
     * Test whether this object is valid.
     * The default implementation always returns {@code true}. But for others,
     * foe example {@code FilePathObject} makes additional checks.
     * 
     * @return true if the path object is valid and false otherwise. 
     * The default implementation always returns {@code true}.
     */
    default boolean isValid() {
        return true; 
    }

    default List<PathObject> getChildren() {
        return getChildren(false);
    }
    
    List<PathObject> getChildren(boolean rec);


    PathObject getParent();

    default void delete() throws IOException{
        getParent().getChildren().remove(this);
        
    }
    
    default boolean hasExt(String ext) {
        if (ext == null) {
            return false;
        }
        String name = Paths.get(getPath()).getFileName().toString();
        //
        // period at first position is not considered as extension-separator 
        //
        if ((name.length() - ext.length()) <= 1) {
            return false;
        }

        return name.endsWith("." + ext);
    }

    default String getNameExt() {
        return new File(getPath()).getName();
    }

    default String getName() {
        String s = new File(getPath()).getName();
        int l = s.lastIndexOf(".");
        if (l > 0) {
            s = s.substring(0, l);
        }
        return s;
    }
    default String getExt() {
        String s = new File(getPath()).getName();
        String ext = "";
        int l = s.lastIndexOf(".");
        if (l > 0) {
            if ( s.length() > l + 1 ) {
                ext = s.substring(l+1);
            }
        }
        return ext;
    }



    default String stringOf(PathObject po) {
        StringBuilder sb = new StringBuilder();
        sb.append("PathObject name: ")
                .append(po.getNameExt())
                .append(": ")
                .append(po.getPath())
                .append(System.lineSeparator())
                .append("children: [");
        getChildren().forEach(p -> {
            sb.append(p.getName());
            sb.append(",");
        });
        if (sb.charAt(sb.length() - 1) == ',') {
            sb.deleteCharAt(sb.length() - 1);
        }

        sb.append("]");
        return sb.toString();
    }

    default String string() {
        StringBuilder sb = new StringBuilder();
        if (getChildren().isEmpty()) {
            sb.append(getName());
        } else {
            sb.append(getName());
            sb.append("[");
            getChildren().forEach(p -> {
                String s = sb.toString();
                //sb.append(p.getName());
                sb.append(p.string());
                sb.append(",");
            });
            if (sb.charAt(sb.length() - 1) == ',') {
                sb.deleteCharAt(sb.length() - 1);
            }
            sb.append("]");

        }
        return sb.toString();
    }
}//class
