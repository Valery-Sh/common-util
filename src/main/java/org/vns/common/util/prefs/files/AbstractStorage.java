package org.vns.common.util.prefs.files;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeListener;
import org.vns.common.PathObject;
import org.vns.common.util.prefs.Storage;

/**
 *
 * @author Valery
 */
public abstract class AbstractStorage implements Storage {

    private static final Logger LOG = Logger.getLogger(AbstractStorage.class.getName());

    private static final String USERROOT_PREFIX = "/Preferences";//NOI18N
    private static final String SYSTEMROOT_PREFIX = "/SystemPreferences";//NOI18N

    protected PathObject USER_ROOT_FOLDER;
    protected PathObject SYSTEM_ROOT_FOLDER;

    protected PathObject SFS;
    
    public PathObject PREFERENCES_ROOT;

    private boolean isModified;

    protected String nodePath;

    protected String folderPath;

    private final boolean userRoot;
    
    FilePreferences owner;

    protected String rootFileName = "root-config";
    
    protected boolean separateDoubleKeyValues = true;

    protected AbstractStorage(FilePreferences owner, final String nodePath, boolean userRoot) {
        this.owner = owner;
        this.userRoot = userRoot;
        this.nodePath = nodePath;
        if ( owner != null ) {
            SFS = owner.sfsRoot();
        }

    }
    protected FilePreferences owner() {
        return owner;
    }
    
    protected PathObject SFS() {
        
        String prefix = "";
        
        SFS = owner.sfsRoot();
        if (SFS != null) {
            try {
                PREFERENCES_ROOT = SFS.createFolders("");
                StringBuilder sb = new StringBuilder();
                sb.append(prefix).append(nodePath());
                folderPath = sb.toString();
            } catch (IOException ex) {
                // Never happens in this class
                Logger.getLogger(AbstractStorage.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return SFS;
    }

    protected String folderPath() {
        return folderPath;
    }

    protected String nodePath() {
        return nodePath;
    }

    protected PathObject FOLDER() throws IOException {
        return SFS().getPathObject(folderPath());
    }

    protected PathObject preferencesRoot() throws IOException {
        return SFS().getPathObject(USERROOT_PREFIX);
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public void markModified() {
        isModified = true;
    }

    protected boolean isModified() {
        return isModified;
    }

    protected void setModified(boolean value) {
        isModified = value;
    }

    @Override
    public abstract boolean existsNode();
    //
    // Methods to manipalate with Arrays
    //
    protected String getArrayAsString(String key) {
        return owner.get(key, null);
    }
    @Override
    public String[] childrenNames() {
        List<PathObject> list = new ArrayList<>();
        try {
            list = FOLDER().getChildren();
        } catch (IOException ex) {
            Logger.getLogger(AbstractStorage.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<String> filteredlist = new ArrayList<>();

        for (PathObject po : list) {
            if (filterChildren(po)) {
                filteredlist.add(po.getNameExt());
            }
        }

        return filteredlist.toArray(new String[]{});
    }

    protected boolean filterChildren(PathObject child) {
        return true;
    }

    @Override
    public abstract void removeNode();

    @Override
    public abstract PropertiesExt load()  throws IOException;


    @Override
    public abstract void save(final PropertiesExt properties) throws IOException;

    //protected abstract Properties getProperties();

    protected abstract PropertiesExt createProperties();

    @Override
    public void runAtomic(final Runnable run) {
        
        run.run();
    }

    @Override
    public void attachChangeListener(final ChangeListener changeListener) {
    }

}
