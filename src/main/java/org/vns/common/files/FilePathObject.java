package org.vns.common.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.vns.common.PathObject;

/**
 *
 * @author Valery Shyshkin
 */
public class FilePathObject implements PathObject {

    private static final int BOTH = 0;
    private static final int DIR_ONLY = 1;
    private static final int DATA_ONLY = 2;

    private Path absolutePath;

    //private PathObject parent;
    //protected PropertiesExt properties;

    /**
     * Create a new instance of the class for the given absolute path.
     *
     * @param absPath the absolute path. This should be the correct value, which
     * is suitable for creating an object of type {
     * @ java.nio.Path }
     */
    protected FilePathObject(String absPath) {
        absolutePath = Paths.get(absPath).normalize();
    }

    /**
     * Create a new instance of the class for the given parent object and the
     * path.
     *
     * @param parent the direct parent object for the one to be created
     * @param relativePath the relativePath to the path specified bt the first
     * parameter. This should be the correct value, which is suitable for
     * creating an object of type {
     * @ java.nio.Path }
     */
    protected FilePathObject(PathObject parent, String relativePath) {
        absolutePath = Paths.get(parent.getPath(), relativePath).normalize();
    }

    /**
     * Creates an instance of the class for the given parent object and relative
     * path to the parent. The method actually doesn't create folders or/and
     * files in the file system and the file the new object specifies must
     * already exist that is {@code parent.getPathObject(relativePath) } must
     * return not {@code null} value otherwise an exception of type {
     *
     * @IllegalArgumentException ) will be thrown.
     *
     * Useful for test purpose.
     *
     * @param parent the parent object
     * @param relativePath relative path to the parent object path.
     * @return new instance of type FilePathObject
     */
    @Override
    public PathObject create(PathObject parent, String relativePath) {
        if (parent.getPathObject(relativePath) == null) {
            throw new IllegalArgumentException("The file " + relativePath + " is not found");
        }
        return new FilePathObject(parent, relativePath);
    }

    /**
     * Test whether this object is a folder.
     *
     * @return true if the file object is a folder (i.e., can have children)
     */
    @Override
    public boolean isFolder() {
        return new File(getPath()).isDirectory();
    }

    /**
     * Returns a list of names of the descendent folders of the directory
     * specified by the parameter.
     *
     * Only the names of the direct descendent directories are included into the
     * result.
     *
     * @param root the folder path to search children. is not a directory then
     * an exception of type IllegalArgumentException will be thrown.
     *
     * @return a list names of the descendent folders of the directory specified
     * by the parameter.
     * @throws IllegalArgumentException if the root parameter is not a folder or
     * is null
     */
    public static List<String> childrenFolderNames(Path root) {

        List<String> names = new ArrayList<>();
        if (root == null || !Files.isDirectory(root)) {
            throw new IllegalArgumentException("The file " + root.toString() + " is not a folder");
        }

        File[] files = root.toFile().listFiles();
        for (File f : files) {
            if (f.isDirectory()) {
                names.add(f.getName());
            }
        }
        return names;
    }

    /**
     * Returns a list of names of the descendent data files of the directory
     * specified by the parameter.
     *
     * Only the names of the direct descendent files are included into the
     * result.
     *
     * @param root the folder path to search children. If null or is not a
     * directory then an exception of type IllegalArgumentException will be
     * thrown.
     *
     * @return a list names of the descendent data files of the directory
     * specified by the parameter.
     * @throws IllegalArgumentException if the root parameter is not a folder or
     * is null
     */
    public static List<String> childrenFileNames(Path root) {

        List<String> names = new ArrayList<>();
        if (root == null || !Files.isDirectory(root)) {
            throw new IllegalArgumentException("The file " + root.toString() + " is not a folder");
        }

        File[] files = root.toFile().listFiles();
        for (File f : files) {
            if (!f.isDirectory()) {
                names.add(f.getName());
            }
        }
        return names;
    }

    /**
     * Returns a list of names of the descendent data files or directories of
     * the directory specified by the parameter.
     *
     * Only the names of the direct descendent directories and files are
     * included into the result.
     *
     * @param root the folder path to search children. If null or is not a
     * directory then an exception of type IllegalArgumentException will be
     * thrown.
     *
     * @return a list of names of the descendent data files or directories of
     * the directory specified by the parameter.
     * @throws IllegalArgumentException if the root parameter is not a folder or
     * is null
     */
    public static List<String> childrenNames(Path root) {

        List<String> names = new ArrayList<>();
        if (root == null || !Files.isDirectory(root)) {
            throw new IllegalArgumentException("The file " + root.toString() + " is not a folder");
        }

        File[] files = root.toFile().listFiles();
        for (File f : files) {
            names.add(f.getName());
        }
        return names;
    }

    /**
     * Lists the subdirectories of this folder.
     *
     * @param rec whether to recursively list subdirectories
     * @return a list of the subdirectories of this folder. If this object is
     * not a directory or doesn't exists then an empty list will be returned
     */
    public List<PathObject> getFolders(boolean rec) {
        List<PathObject> folders = new ArrayList<>();
        if (!isValid() || !isFolder()) {
            return folders;
        }
        files(absolutePath, folders, DIR_ONLY, rec);
        return folders;
    }

    /**
     * Returns a list of all data files in this folder. If this object is not a
     * folder then an empty list will be returned.
     *
     * @param rec - whether to recursively search subdirectories
     * @return a list of type PathObject (satisfying ! isFolder()). If this
     * object is not a folder then the result will be an empty list.
     */
    public List<PathObject> getData(boolean rec) {
        List<PathObject> dataFiles = new ArrayList<>();
        if (!isValid() || !isFolder()) {
            return dataFiles;
        }
        files(absolutePath, dataFiles, DATA_ONLY, rec);
        return dataFiles;
    }

    /**
     * Test whether this object is valid.
     *
     * @return true if the object corresponds to an existing file or directory.
     */
    @Override
    public boolean isValid() {
        return Files.exists(absolutePath);
    }

    /**
     * Retrieve file or folder relative to a current folder, with a given
     * relative path. Note that neither file nor folder is created on disk. If
     * the array specified by the parameter is null or it's length equals to
     * zero the {@code this} object will be returned.
     *
     *
     * @param names the array of names used to build a relative path to this
     * object. If the parameter is null or represents an array with a zero
     * length then then this object returns.
     *
     * @return the object representing this file or null if the file or folder
     * does not exist
     *
     * @throws IllegalArgumentException if this is not a folder
     */
    @Override
    public PathObject getPathObject(String... names) {
        if (names == null || names.length == 0) {
            return this;
        }

        if (!isValid()) {
            throw new IllegalArgumentException("The file " + getPath() + "is not a folder");
        }

        Path p = Paths.get(absolutePath.toString(), names);
        if (!Files.exists(p)) {
            return null;
        }
        return new FilePathObject(p.toString());
    }

    /**
     * Get all children of this folder (data files and subdirectories). If the
     * file does not have children (does not exist or is not a folder) then an
     * empty list should be returned. No particular order is assumed.
     *
     * @return array of direct children
     */
    @Override
    public List<PathObject> getChildren() {
        List<PathObject> files = getChildren(false);
        return files;
    }

    /**
     * Lists all children of this folder. If the children should be listed
     * recursively, first all direct children are listed; then children of
     * direct subdirectories; and so on.
     *
     * @param rec whether to list recursively
     * @return list of objects of type FilePathObject or an empty list if no
     * children exists.
     */
    @Override
    public List<PathObject> getChildren(boolean rec) {
        if (!isValid() || !isFolder()) {
            return new ArrayList<>();
        }
        List<PathObject> files = new ArrayList<>();
        files(absolutePath, files, BOTH, rec);
        return files;
    }

    protected void files(Path root, List<PathObject> folders, int cond, boolean rec) {
        List<String> names;

        switch (cond) {
            case BOTH:
            case DATA_ONLY:
                names = childrenNames(root);
                break;
            case DIR_ONLY:
                names = childrenFolderNames(root);
                break;
            default:
                names = childrenNames(root);
                break;
        }

        List<String> newnames = new ArrayList<>();
        for (String name : names) {
            Path d = Paths.get(root.toString(), name);

            switch (cond) {
                case BOTH:
                    folders.add(new FilePathObject(d.toString()));
                    break;
                case DIR_ONLY:
                    if (Files.isDirectory(d)) {
                        folders.add(new FilePathObject(d.toString()));
                    }
                    break;
                case DATA_ONLY:
                    if (!Files.isDirectory(d)) {
                        folders.add(new FilePathObject(d.toString()));
                    }
                    break;
            }
            if (Files.isDirectory(d)) {
                newnames.add(name);
            }
        }
        if (rec) {
            newnames.stream().map((name) -> Paths.get(root.toString(), name)).forEach((d) -> {
                files(d, folders, cond, rec);
            });
        }

    }

    /**
     * Get parent folder. The returned object will satisfy isFolder().
     *
     * @return the parent folder or null if this object is a root object as
     * specifies by the method {@link #isRoot() ).
     */
    @Override
    public PathObject getParent() {
        if (isRoot()) {
            return null;
        }
        return new FilePathObject(absolutePath.getParent().toString());
    }

    /**
     * Tests whether the absolute path of this object if a root path.
     *
     * @return true if the object represents a file system root directory
     */
    public boolean isRoot() {
        return absolutePath.equals(absolutePath.getRoot());
    }

    /**
     * Returns an internal object of type {@code Properties}.
     *
     * @return object of type {@code Properties}.
     */
/*    @Override
    public PropertiesExt getProperties() {
        return properties;
    }
*/
    /**
     * Creates and returns an internal object of type {@code Properties}.
     *
     * @return object of type {@code Properties}.
     */
/*    @Override
    public PropertiesExt createProperties() {
        properties = new PropertiesExt();
        return properties;
    }
*/
    /**
     * Returns an absolute path of this object as a String. The result string is
     * normalized and all slashes are forward slashes.
     *
     * @return absolute path of this object as a String.
     */
    @Override
    public String getPath() {
        return normalize(absolutePath.toString());
    }

    /**
     * Delete this file. If the file is a folder and it is not empty then all of
     * its contents are also recursively deleted.
     * If the parameter value is {@code true} then the method checks whether 
     * a directory to be deleted is registered in the {@code WatchRegistry}. and if
     * so then the method doesn't delete that directory and adds it to the 
     * returned list.
     * 
     *  WatchRegistry are not deleted.
     * @throws IOException when some file cannot be deleted
     */
    @Override
    public void delete() throws IOException {
        FileLock lock = null;
        try {
            lock = lock();
            WatchRegistry.deleteFolder(absolutePath);
        } finally {
            if (lock != null) {
                try {
                    lock.release();
                    lock.channel().close();
                } catch (IOException ex) {
                    Logger.getLogger(FilePathObject.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     * Delete this file. If the file is a folder and it is not empty then all of
     * its contents are also recursively deleted.
     * The method checks whether  a directory to be deleted is registered in 
     * the {@code WatchRegistry}. and if so then the method doesn't delete that
     * directory and adds it to the  returned list.
     * 
     * @return a list of objects of type path where each item is registered in 
     * the WatchRegistry.
     * 
     * @throws IOException when some file cannot be deleted
     */
    public List<Path> deleteNotWatchables() throws IOException {
        FileLock lock = null;
        
        final List<Path> notDeleted = new ArrayList<>();
        
        try {
            lock = lock();
            Files.walkFileTree(absolutePath, new FileVisitor<Path>() {
                public boolean containsRegistered(Path path) {
                    boolean retval = false;
                    for ( Path p : notDeleted ) {
                        if ( p.startsWith(path)) {
                            retval = true;
                            break;
                        }
                    }
                    return retval;
                }
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
//                    System.out.println("FileVisitResult preVisitDirectory = " + dir);                    
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    //Files.deleteIfExists(file);
//                    System.out.println("FileVisitResult visitFile = " + file);
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
//                    System.out.println("FileVisitResult postVisitDir = " + dir + "; isRegistered = " + WatchRegistry.getInstance().isRegistered(dir));                    
                    if (exc == null) {
                        if ( WatchRegistry.getInstance().isRegistered(dir)) {
                            notDeleted.add(dir);
                            return FileVisitResult.CONTINUE;
                        } else if (containsRegistered(dir)) {
                            return FileVisitResult.CONTINUE;
                        }
                        
                        try {
                            Files.deleteIfExists(dir);
                        } catch(IOException ex) {
                            System.out.println("FileVisitResult EXCEPTION postVisitDir = " + dir);
                        }
                        return FileVisitResult.CONTINUE;
                    } else {
                        // directory iteration failed
                        throw new IOException("") ;
                    }
                }
            });
        } finally {
            if (lock != null) {
                try {
                    lock.release();
                    lock.channel().close();
                } catch (IOException ex) {
                    Logger.getLogger(FilePathObject.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return notDeleted;
    }
    
    
    /**
     *
     * @return for now always returns null
     * @throws IOException throws exception
     */
    protected FileLock lock() throws IOException {
        if (true) {
            return null;
        }

        File file = absolutePath.toFile();
        FileLock lock = null;

        try {
            FileChannel channel = new RandomAccessFile(file, "rw").getChannel();
            //FileChannel channel = Files.file.getChannel();
            lock = channel.tryLock();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FilePathObject.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lock;
    }

    /**
     * Create new data file in this folder with the specified name. If the file
     * cannot be created (e.g. already exists), or if <i>this</i> object is not
     * a folder then an exception of type {@code IllegalArgunentException)
     * will be thrown.
     *
     * @param name name - the name of data object to create (can contain a
     *    period but not slashes)
     *
     * @return the new data file object
     * @throws java.io.IOException  if the file cannot be created (e.g. already
     *      exists), or if this is not a folder or the parameter is null or contains slashes
     *
     */
    @Override
    public PathObject createData(String name) throws IOException {
        if (!isFolder()) {
            throw new IOException("The file " + getPath() + " is not a folder");
        }
        if (!isFolder() || name == null || name.contains("\\") || name.contains("/")) {
            throw new IOException("The the name argument value  cannot be null and cannot contain slashes");
        }

        Path file = Files.createFile(Paths.get(absolutePath.toString(), name));
        return new FilePathObject(file.toString());
    }

    /**
     * Returns PathObject for a given path. If a data file for the given path
     * does not exist then it is created, including any necessary but
     * nonexistent parent folders. Note that if this operation fails it may have
     * succeeded in creating some of the necessary parent folders. If a data
     * file already exists then a PathObjects for it returns.
     *
     * @param absPath the path of data object to create
     * @return the new data file object
     * @throws java.io.IOException if the parameter value is null or the
     * operation fails
     */
    public static PathObject createDataFile(Path absPath) throws IOException {
        if (absPath == null) {
            throw new IOException("The the path argument value  cannot be null");
        }

        PathObject result = new FilePathObject(absPath.toString());

        if (Files.exists(absPath)) {
            return result;
        }
        Path parent = absPath.getParent();
        FilePathObject po = (FilePathObject) createDirectories(parent);
        result = po.createData(absPath.getFileName().toString());
        return result;
    }

    /**
     * Returns PathObject for a given path. If a folder for the given path does
     * not exist then it is created, including any necessary but nonexistent
     * parent folders. Note that if this operation fails it may have succeeded
     * in creating some of the necessary parent folders. If a folder already
     * exists then {@code this} object returns.
     *
     * @param absPath the path of data object to create
     * @return the new data file object
     * @throws java.io.IOException if the parameter value is null or the
     * operation fails
     */
    public static PathObject createDirectories(Path absPath) throws IOException {
        if (absPath == null) {
            throw new IOException("The the path argument value  cannot be null");
        }

        PathObject result = new FilePathObject(absPath.toString());

        if (Files.exists(absPath)) {
            return result;
        }

        Files.createDirectories(absPath);
        return result;
    }
    /**
     * Returns PathObject for a given path. If a folder or a file for the given path does
     * not exist then it is created, including any necessary but nonexistent
     * parent folders. Note that if this operation fails it may have succeeded
     * in creating some of the necessary parent folders. If a folder already
     * exists then {@code this} object returns.
     *
     * @param absPath the path of data object to create
     * @return the new folder or file object
     * @throws java.io.IOException if the parameter value is null or the
     * operation fails
     */
    public static FilePathObject toPathObject(Path absPath) throws IOException {
        if (absPath == null) {
            throw new IOException("The the path argument value  cannot be null");
        }

        FilePathObject result = new FilePathObject(absPath.toString());

        if (Files.exists(absPath)) {
            return result;
        }
        if ( Files.isDirectory(absPath)) {
            result = (FilePathObject) createDirectories(absPath);
        } else {
            result = (FilePathObject) createDataFile(absPath);
        }
        return result;
    }

    public static PathObject createDirectories(String absPath) throws IOException {
        return createDirectories(Paths.get(absPath));
    }    
    /**
     * Creates a directory by creating all nonexistent parent directories first.
     *
     * @param relativePath the relative path of the folder to be created to this
     * file object
     *
     * @return the new folder
     */
    @Override
    public PathObject createFolders(String relativePath) throws IOException {

        if (relativePath != null && relativePath.trim().isEmpty()) {
            return this;
        }
        if (!isFolder()) {
            throw new IllegalArgumentException("The file " + getPath() + " is not a folder");
        }
        if (relativePath == null) {
            throw new IllegalArgumentException("The the name argument value  cannot be null");
        }
        PathObject result = null;
        Path p;
        p = Files.createDirectories(Paths.get(absolutePath.toString(), relativePath));
        return new FilePathObject(p.toString());
    }

    /**
     * Create a new folder below this one with the specified name.
     *
     * @param folderName the name of folder to create. Periods in name are
     * allowed (but not slashes).
     *
     * @return the new folder
     * @throws IOException if this object is not a folder or the parameter
     * folderName is null or contains slashes or the folder to be created
     * already exists
     */
    @Override
    public PathObject createFolder(String folderName) throws IOException {

        if (folderName != null && folderName.trim().isEmpty()) {
            return this;
        }

        if (!isFolder()) {
            throw new IOException("The file " + getPath() + " is not a folder");
        }
        if (folderName == null) {
            throw new IOException("The name argument value  cannot be null");
        }
        if (folderName.contains("\\") || folderName.contains("/")) {
            throw new IOException("The argument value cannot contain slashes");
        }
        if (Files.exists(Paths.get(getPath(), folderName))) {
            throw new IOException("The folder with a name " + folderName + " alredy exists");
        }

        String s = Files.createDirectory(Paths.get(absolutePath.toString(), folderName))
                .toString();
        return new FilePathObject(s);
    }

    /**
     * Create new data file in this folder with the specified name. If the file
     * cannot be created (e.g. already exists), or if this is not a folder then
     * an exception of type {@code IllegalArgunentException)
     * will be thrown.
     *
     * @param name  the name of data object to create (can contain a period, but not slashes)
     * @param ext the extension of the file (or null or "")
     * @return the new data file object.
     */
    public PathObject createData(String name, String ext) throws IOException {
        return createData(name + "." + ext);
    }

    public InputStream getInputStream() throws IOException {
        return Files.newInputStream(absolutePath);
    }

    public OutputStream getOutputStream() throws IOException {
        return Files.newOutputStream(absolutePath);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.absolutePath);
        return hash;
    }

    /**
     * Two objects of this class ar considered to be equal if their absolute
     * paths are equal
     *
     * @param obj an object to be compared to
     *
     * @return true if the absolute path of this object equals to the the
     * absolute path of the object specified by the parameter
     */
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
        final FilePathObject other = (FilePathObject) obj;
        return Objects.equals(this.absolutePath, other.absolutePath);
    }

    @Override
    public String toString() {
        return stringOf(this);
    }

    /**
     * If a file or a directory with an absolute path specified by the method 
     * {@link #getPath() } already exists then the method does nothing.
     * Otherwise it creates all folders for the path specified by the method {@code getPath()
     * }. In the last case the method cannot define whether the
     * {@code getPath()} specifies a file or folder. If the file to be created
     * already exists the the method does nothing
     *
     * @throws IllegalArgumentException if something goes wrong
     */
    @Override
    public void create() {
        if (Files.exists(absolutePath)) {
            return;
        }
        try {
            Files.createDirectories(absolutePath);
        } catch (IOException ex) {
            Logger.getLogger(FilePathObject.class.getName()).log(Level.SEVERE, null, ex);
            throw new IllegalArgumentException(ex.getMessage());
        }
    }
}//class
