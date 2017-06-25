package org.vns.common.util.prefs;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.event.ChangeListener;
import org.vns.common.PathObject;
import org.vns.common.util.prefs.files.PropertiesExt;

/**
 *
 * @author Valery
 */
public class PathStorage implements Storage {

    private static final Logger LOG = Logger.getLogger(PathStorage.class.getName());
    //public static final String SUPPORTED
    private static final String USERROOT_PREFIX = "/Preferences";//NOI18N
    private static final String SYSTEMROOT_PREFIX = "/SystemPreferences";//NOI18N

    public final static PathObject SFS_ROOT = new CachePathObject("");
    protected PathObject USER_ROOT_FOLDER;
    protected PathObject SYSTEM_ROOT_FOLDER;
    public PathObject PREFERENCES_ROOT;
    protected PathObject ROOT;

    //private final String rootFolderPath;
    //private String filePath;
    private boolean isModified;


    //private FileChangeAdapter fileChangeAdapter;

    /*test*/ static Runnable TEST_FILE_EVENT = null;

    /**
     * Creates a new instance
     */
    private PathStorage(final String absolutePath, boolean userRoot) {
        try {
            //StringBuilder sb = new StringBuilder();
            //String prefix = (userRoot) ? USERROOT_PREFIX : SYSTEMROOT_PREFIX;
            //sb.append(prefix).append(absolutePath);
            //rootFolderPath = sb.joinValues();
            PREFERENCES_ROOT = (userRoot) ? SFS_ROOT.createFolders(USERROOT_PREFIX) : SFS_ROOT.createFolders(SYSTEMROOT_PREFIX);
            ROOT = PREFERENCES_ROOT.createFolders(absolutePath);
        } catch (IOException ex) {
            // Never happens in this class
        }
    }

    static PathStorage instance(final String absolutePath) {

        PathStorage storage = new PathStorage(absolutePath, true);

        return storage;
    }

    protected PathObject preferencesRoot() {
        return SFS_ROOT.getPathObject(USERROOT_PREFIX);
    }

    static Storage instanceReadOnly(final String absolutePath) {

        return new PathStorage(absolutePath, false) {
            public @Override
            boolean isReadOnly() {
                return true;
            }

            public @Override
            final String[] childrenNames() {
                return new String[0];
            }

            public @Override
            final PropertiesExt load() {
                return new PropertiesExt();
            }
        };
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public void markModified() {
        isModified = true;
    }

    @Override
    public final boolean existsNode() {
        //return (getPropertiesPath() != null) || (rootFolder() != null);
        return ROOT != null;
    }

    @Override
    public String[] childrenNames() {
        List<PathObject> list = ROOT.getChildren();
        String[] retval = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            retval[i] = list.get(i).getNameExt();
        }
        return retval;
    }

    @Override
    public final void removeNode() {
        try {
            ROOT.delete();
            ROOT = null;
        } catch (IOException ex) {
            // Never happens in this class
        }

    }

    @Override
    public PropertiesExt load() {
        PropertiesExt props = ((CachePathObject) ROOT).getProperties();
        if (props == null) {
            props = new PropertiesExt();
        }
        return props;
    }
    private PropertiesExt properties;
    public PropertiesExt getProperties() {
        return properties;
    }
    
    public PropertiesExt createProperties() {
        if (properties == null) {
            properties = new PropertiesExt();
        }
        return properties;
    }

    public PropertiesExt createProperties(PropertiesExt properties) {
        PropertiesExt props = getProperties();
        if (props == null) {
            props = createProperties();
        }
        props.putAll(properties);
        return props;
    }

    @Override
    public void save(final PropertiesExt properties) {
        PropertiesExt props = getProperties();
        if (props != null) {
            props.clear();
            properties.forEach((k, v) -> {
                props.setProperty(k, v);
            });
        } else {
            createProperties(properties);
        }
    }


    /*    protected Properties createProperties() {
        Properties props =  ROOT.createProperties();
        return props;
    }
     */
    @Override
    public void runAtomic(final Runnable run) {
        run.run();

        /*        try {
            
            SFS_ROOT.getFileSystem().runAtomicAction(new AtomicAction() {
                public void run() throws IOException {
                    run.run();
                }
            });
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
         */
    }

    @Override
    public void attachChangeListener(final ChangeListener changeListener) {
        /*        try {
            fileChangeAdapter = new FileChangeAdapter() {

                @Override
                public void fileDataCreated(FileEvent fe) {
                    if (fe.getFile().equals(getPropertiesPath())) {
                        if (TEST_FILE_EVENT != null) {
                            TEST_FILE_EVENT.run();
                        }
                        changeListener.stateChanged(new ChangeEvent(PropertiesStorage.this));
                    }
                }

                @Override
                public void fileFolderCreated(FileEvent fe) {
                    if (fe.getFile().equals(getPropertiesPath())) {
                        changeListener.stateChanged(new ChangeEvent(PropertiesStorage.this));
                    }
                }

                @Override
                public void fileChanged(FileEvent fe) {
                    if (fe.getFile().equals(getPropertiesPath())) {
                        if (TEST_FILE_EVENT != null) {
                            TEST_FILE_EVENT.run();
                        }
                        changeListener.stateChanged(new ChangeEvent(PropertiesStorage.this));
                    }
                }

                @Override
                public void fileDeleted(FileEvent fe) {
                    if (fe.getFile().equals(getPropertiesPath())) {
                        changeListener.stateChanged(new ChangeEvent(PropertiesStorage.this));
                    }
                }

                @Override
                public void fileRenamed(FileRenameEvent fe) {
                    if (fe.getFile().equals(getPropertiesPath())) {
                        changeListener.stateChanged(new ChangeEvent(PropertiesStorage.this));
                    }
                }

            };
            SFS_ROOT.getFileSystem().addFileChangeListener(FileUtil.weakFileChangeListener(fileChangeAdapter, SFS_ROOT.getFileSystem()));
        } catch (FileStateInvalidException ex) {
            Exceptions.printStackTrace(ex);
        }
         */
    }

}
