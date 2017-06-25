package org.vns.common.util.prefs;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.vns.common.PathObject;
import org.vns.common.util.prefs.files.PropertiesExt;

/**
 *
 * @author Valery
 */
public abstract class PathPreferences extends AbstractPreferences implements ChangeListener {

    private static final Logger LOG = Logger.getLogger(PathPreferences.class.getName());

    //protected static final Map<Path, Preferences> user_roots = new ConcurrentHashMap<>();
    protected static final Map<Path, Preferences> user_roots = new ConcurrentHashMap<>();
    public static final Path USER_ROOT_PATH = Paths.get("PATH_USER_ROOT");

    //protected static Preferences USER_ROOT;
    private static Preferences SYSTEM_ROOT;
    private Storage storage;
    protected PropertiesExt properties;

//    protected String fileType;
//    protected String fileTypeExtention;
    protected Path userRootPath;

    protected PathPreferences(Path type, boolean user) {
        super(null, "");
        userRootPath = type;
        init();
    }

    
    protected PathPreferences(boolean user) {
        super(null, "");
        init();
    }
    private void init() {
        storage = getStorage(this, absolutePath());
        storage.attachChangeListener(this);
    }

    @Override
    public PathPreferences node(String path) {
        return (PathPreferences) super.node(path);
    }

    public PathObject sfsRoot() {
        return null;
    }

    protected PathPreferences(PathPreferences parent, String name) {
        super(parent, name);
        init();
        newNode = true;
    }

    /*    public static PathPreferences pathUserRoot_OLD() {
        if (USER_ROOT == null) {
            USER_ROOT = new UserRootPreferences();
        }
        assert USER_ROOT != null;
        return (PathPreferences)USER_ROOT;
    }
     */
    public static PathPreferences pathUserRoot() {
        if (!user_roots.containsKey(USER_ROOT_PATH)) {
            UserRootPreferences ur = new UserRootPreferences();
            user_roots.put(USER_ROOT_PATH, ur);
        }
        return (PathPreferences) user_roots.get(USER_ROOT_PATH);
    }

    static Preferences pathSystemRoot() {
        if (SYSTEM_ROOT == null) {
            SYSTEM_ROOT = new SystemRootPreferences();
        }
        assert SYSTEM_ROOT != null;
        return SYSTEM_ROOT;
    }

    protected abstract Storage getStorage(String absolutePath);

    protected abstract Storage getStorage(PathPreferences parent, String absolutePath);

    public Storage storage() {
        return storage;
    }

    private String getProperty(String key) {
        synchronized (lock) {
            PropertiesExt props = properties();
            if (props == null) {
                return null;
            }
            return props.getProperty(key);
        }
    }

    public String get(String key) {
        synchronized (lock) {
            PropertiesExt props = properties();

            if (props == null) {
                return null;
            }
            return props.getProperty(key);
        }
    }

    protected PropertiesExt properties() {
        if (properties == null) {
            
            properties = new PropertiesExt();
            
            try {
                properties.putAll(storage().load());
            } catch (IOException ex) {
                // Never happens in this class
                Logger.getLogger(PathPreferences.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return properties;
    }

    @Override
    public void sync() throws BackingStoreException {
        if (storage.isReadOnly()) {
            throw new BackingStoreException("Unsupported operation: read-only storage");//NOI18N
        } else {
            if (super.isRemoved()) {
                return;
            }
            //flushTask.waitFinished();
            super.sync();
            //cachedKeyValues.clear();
        }
    }

    @Override
    public final String[] childrenNames() throws BackingStoreException {
        return super.childrenNames();
    }

    @Override
    protected final String[] childrenNamesSpi() throws BackingStoreException {
        return storage.childrenNames();
    }

    @Override
    protected String[] keysSpi() throws BackingStoreException {
        return getKeysSpi();
    }

    private String[] getKeysSpi() throws BackingStoreException {
        synchronized (lock) {
            PropertiesExt
                    props = properties();
            if (props == null) {
                return new String[0];
            }
            Set<String> keySet = props.stringPropertyNames();
            return keySet.toArray(new String[keySet.size()]);
        }
    }

    @Override
    protected final String getSpi(String key) {
        return getProperty(key);
    }

    @Override
    protected final void putSpi(String key, String value) {
        putProperty(key, value);
        /*        if (Boolean.TRUE.equals(localThread.get())) {
            return;
        }
         */
        storage.markModified();
        asyncInvocationOfFlushSpi();
    }

    protected void putProperty(String key, String value) {
        synchronized (lock) {
            PropertiesExt props = properties();
            if (props == null) {
                return;
            }
            props.setProperty(key, value);
        }
    }
    
    protected String getArrayAsString(String key) {
        return get(key, null);
    }
    public void addToArray(String key, String... values) {
        synchronized (lock) {
            if (values.length == 0) {
                return;
            }
            String value = null;

            String oldValue = get(key, null);
            if (oldValue == null) {
                if (values.length == 1) {
                    value = values[0];
                } else {
                    value = Storage.toString(values);
                }
            }
            if (value == null) {
                String[] ar = getAsArray(key);
                ar = Storage.addAll(ar, values);
                value = Storage.toString(ar);
            }
            put(key, value);
        }
    }
    public void addToArray(String key, int idx, String... values) {
        synchronized (lock) {
            if (values.length == 0) {
                return;
            }
            String value = null;

            String oldValue = get(key, null);
            if (oldValue == null) {
                if (values.length == 1) {
                    value = values[0];
                } else {
                    value = Storage.toString(values);
                }
            }
            
            if (value == null) {
                String[] ar = getAsArray(key);
                ar = Storage.addAll(ar, values);
                value = Storage.toString(ar);
            }
            put(key, value);
        }
    }

    public void removeFromArray(String key, String... values) {
        synchronized (lock) {
            if (values.length == 0) {
                return;
            }
            String value = null;

            String oldValue = get(key, null);
            if (oldValue == null) {
                if (values.length == 1) {
                    value = values[0];
                } else {
                    value = Storage.toString(values);
                }
            }
            if (value == null) {
                String[] ar = getAsArray(key);
                ar = Storage.removeAll(ar, values);
                value = Storage.toString(ar);
            }

            put(key, value);
        }
    }

    public void removeFromArray(String key, int idx, String... values) {
        synchronized (lock) {
            if (values.length == 0) {
                return;
            }
            String value = null;

            String oldValue = get(key, null);
            if (oldValue == null) {
                if (values.length == 1) {
                    value = values[0];
                } else {
                    value = Storage.toString(values);
                }
            }
            if (value == null) {
                String[] ar = getAsArray(key);
                ar = Storage.removeAll(ar, idx);
                value = Storage.toString(ar);
            }
            put(key, value);
        }
    }
    
    @Override
    protected final void removeSpi(String key) {
        removeProperty(key);
        storage.markModified();
        asyncInvocationOfFlushSpi();
    }

    private void removeProperty(String key) {
        synchronized (lock) {

            //Properties props = storage.load();
            String name = properties().getSectionName();
            properties().remove(key);

            /*            if (props == null) {
                return;
            }
            props.removeFromArray(key);
             */
        }
    }

    @Override
    protected final void removeNodeSpi() throws BackingStoreException {
        storage.removeNode();
    }

    protected void asyncInvocationOfFlushSpi() {
        if (!storage.isReadOnly()) {
            try {
                flushSpi();
            } catch (BackingStoreException ex) {
                Logger.getLogger(PathPreferences.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    protected void flushSpi() throws BackingStoreException {
        try {
            storage().save(properties());
        } catch (IOException ex) {
            throw new BackingStoreException(ex);
        }
    }

    @Override
    protected void syncSpi() throws BackingStoreException {
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        synchronized (lock) {
        }
    }

    public Preferences getRoot() {
        Preferences retval = this;
        while (true) {

            if (retval.parent() == null) {
                break;
            }
            retval = (Preferences) retval.parent();

        }
        return retval;
    }

    public String storageType() {
        Path up = ((PathPreferences) getRoot()).userRootPath;
        int start = up.getNameCount() - 2;
        int end = up.getNameCount() - 1;

        Path p = up.subpath(start, end);
        return p.toString();
    }

    public String typeExtention() {
        Path up = ((PathPreferences) getRoot()).userRootPath;
        int start = up.getNameCount() - 1;
        Path p = up.subpath(start, up.getNameCount());
        return p.toString();
    }

    /**
     * Converts the given array of strings into the string of special format.
     * Sets a property to a value broken into segments for readability. Each
     * item will be stored on its own line of text. Example:
     * <pre>
     *   key01=\
     *       value01\
     *       value02
     * </pre> The value of the first line contains a concatenation the backslash
     * and a symbols specified by the {@code System.lineSeparator}. Other lines
     * has the same concatenation at the end of the line except the last line.
     * <p>
     * The method {@code get(String,String)} will simply concatenate all the
     * items into one string, so generally separators (such as : for path-like
     * properties) must be included in the items (for example, at the end of all
     * but the last item).
     *
     * @param key a property name; cannot be null nor empty
     * @param values an array of strings to be set
     * @return a converted string value
     */
    public String setProperty(String key, String... values) {
        String retval = get(key, null);
        StringBuilder sb = new StringBuilder();
        String dlm = "\\" + System.lineSeparator();
        if (values.length > 0) {
            sb.append(dlm);
        }

        for (int i = 0; i < values.length; i++) {
            if (i == values.length - 1) {
                dlm = "";
            }
            sb.append(values[i])
                    .append(dlm);
        }
        put(key, sb.toString());
        String s = super.get(key, null);
        return retval;
    }

    /**
     *
     * @param key
     * @return null if the property for he given key doesn't exist. Empty array
     * if the value is an empty string
     */
    public String[] getAsArray(String key) {
        String[] retval;
        String val = super.get(key, null);
        if (val == null) {
            return null;
        }
        String sep = "\\" + System.lineSeparator();

        if (val.trim().length() < sep.length()) {
            return new String[]{val};
        }
        if (val.startsWith(sep)) {
            val = val.substring(sep.length());
        }
        String newsep = ".,;-" + System.lineSeparator();
        val = val.replace(sep, newsep);
        retval = val.split(newsep);

        return retval;
    }

    /**
     * The method first invokes this method of the super class.
     *
     * Then tests whether the result is an array of strings as specified by the
     * method {@link #setProperty(java.lang.String, java.lang.String...).
     * If so then converts the result to a regular string.
     *
     * @param key key whose associated value is to be returned.
     * @param def the value to be returned in the event that this preference
     * node has no value associated with key.
     *
     * @return the value associated with key, or def if no value is associated
     * with key.
     */
    public String join(String key, String def) {
        String retval = super.get(key, def);
        String sep = "\\" + System.lineSeparator();

        if (retval == null || retval.trim().length() < sep.length()) {
            return retval;
        }

        String[] ar = getAsArray(key);
        retval = String.join("", ar);
        return retval;
    }

}//class PathPreferences
