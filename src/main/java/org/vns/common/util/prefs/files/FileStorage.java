package org.vns.common.util.prefs.files;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.vns.common.PathObject;
import org.vns.common.files.FilePathObject;
import org.vns.common.files.WatchRegistry;

/**
 *
 * @author Valery
 */
public class FileStorage extends AbstractStorage {

    protected FileStorage(FilePreferences owner, final String nodePath, boolean userRoot) {
        super(owner, nodePath, userRoot);
    }

    @Override
    public final boolean existsNode() {
        return (getPropertiesFile() != null) || (SFS().getPathObject(folderPath()) != null);
    }

    @Override
    public void removeNode() {
        PathObject propertiesFile = getPropertiesFile();
        if (propertiesFile != null && propertiesFile.isValid()) {
            try {
                propertiesFile.delete();
                PathObject folder = propertiesFile.getParent();
                while (folder != null && folder != preferencesRoot() && folder.getChildren().isEmpty()) {
                    folder.delete();
                    folder = folder.getParent();
                }
            } catch (IOException ex) {
                Logger.getLogger(FileStorage.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public PropertiesExt load() {

        final PropertiesExt props = new PropertiesExt();

        PathObject po = SFS().getPathObject(buildPropertiesPath());

        if (po == null) {
            return props;
        }
        try (FileInputStream fis = new FileInputStream(po.getPath());) {
            props.load(fis);
        } catch (IOException ex) {
            Logger.getLogger(FileStorage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return props;
    }

    @Override
    public void save(final PropertiesExt properties) throws IOException {
        if (isModified()) {
            setModified(false);
//            try {
                if (!properties.isEmpty()) {
                    try (OutputStream out = ((FilePathObject) createPropertiesFile()).getOutputStream()) {
                        properties.store(out);
                    } catch (IOException e) {
                        Logger.getLogger(FileStorage.class.getName()).log(Level.SEVERE, null, e);
                    }
                } else {
                    PathObject file = getPropertiesFile();
                    if (file != null) {
                        file.delete();
                    }
                    PathObject folder = SFS().getPathObject(folderPath());
                    while (folder != null && folder != preferencesRoot() && folder.getChildren().size() == 0) {
                        folder.delete();
                        folder = folder.getParent();
                    }
                }
        }
    }
    @Override
    public void runAtomic(final Runnable run) {
        try {
            WatchRegistry.getInstance().suspendListeners(Paths.get(folderPath()));
            WatchRegistry.getInstance().suspendListeners(Paths.get(folderPath()).getParent());            
            run.run();
        } finally {
            WatchRegistry.getInstance().resumeListeners(Paths.get(folderPath()));
            WatchRegistry.getInstance().resumeListeners(Paths.get(folderPath()).getParent());            
        }
    }


    protected PathObject getPropertiesFile() {
        return SFS().getPathObject(buildPropertiesPath());
    }

    protected PathObject createPropertiesFile() throws IOException {
        PathObject retval = getPropertiesFile();
        if (retval == null) {
            Path absPath = Paths.get(SFS().getPath(), buildPropertiesPath());
            retval = ((FilePathObject) SFS()).createDataFile(absPath);
        }
        return retval;
    }

    @Override
    protected boolean filterChildren(PathObject child) {
        boolean retval = false;
        //
        // All folders and files recursively
        //
        List<PathObject> list = child.getChildren(true);

        for (PathObject po : list) {
            if (!po.isFolder() && po.hasExt("properties")) { // NOI18N
                retval = true;
                break;
            }
        }
        return retval;
    }

    protected String buildPropertiesPath() {
        
        String fileExtension = owner().typeExtention();
        
        StringBuilder sb = new StringBuilder();

        Path path = Paths.get(folderPath());
        int length = path.getNameCount();
        if (path.getNameCount() > 0) {
            String lastName = path.getName(length - 1).toString();
            sb.append(path.getParent().toString())
                    .append("/")
                    .append(lastName)
                    //.append("properties");
                    .append(".")
                    .append(fileExtension);
                    
        } else {
            //sb.append("root.properties");//NOI18N
            sb.append(rootFileName)
                    .append(".")
                    .append(fileExtension);
        }
        return sb.toString();
    }

    @Override
    protected PropertiesExt createProperties() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }



}
