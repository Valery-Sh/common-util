package org.vns.common.util.prefs.files;

import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.vns.common.PathObject;
//import org.vns.common.PathObject;
import org.vns.common.RequestExecutor;
import org.vns.common.util.prefs.Storage;

/**
 *
 * @author Valery
 */
public class FilePreferences extends AbstractPreferences {

    private static final Logger LOG = Logger.getLogger(FilePreferences.class.getName());

    public static String SYS_KEY = "org.vns.common.util.prefs";
    private Storage storage;
    protected PropertiesExt properties;
    protected PathObject SFS;
    protected String sectionName;

    public enum Scope {
        HEADER, BOTTOM, SECTION, KEYVALUE, VALUE
    }
    
    public FilePreferences(PathObject sfsRoot) {
        super(null, "");
        SFS = sfsRoot;
        init();
    }

    protected FilePreferences(FilePreferences parent, String name) {
        super(parent, name);
        SFS = parent.sfsRoot();
        init();
        newNode = !storage().existsNode();
    }

    private void init() {
        storage = getStorage(this, absolutePath());
        //storage.attachChangeListener(this);
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
        /*        Path up = ((PathPreferences) getRoot()).userRootPath;
        int start = up.getNameCount() - 2;
        int end = up.getNameCount() - 1;

        Path p = up.subpath(start, end);
        return p.joinValues();
         */
        return null;
    }

    public String typeExtention() {
        /*        Path up = ((FilePreferences) getRoot()).userRootPath;
        int start = up.getNameCount() - 1;
        Path p = up.subpath(start, up.getNameCount());
        return p.joinValues();
         */
        return null;
    }

    protected Storage getStorage(String absolutePath) {
        return IniStorage.instance(null, absolutePath());
    }

    protected Storage getStorage(FilePreferences parent, String absolutePath) {
        return IniStorage.instance(parent, absolutePath());
    }

    @Override
    protected AbstractPreferences childSpi(String name) {
        return new FilePreferences(this, name);
    }

    @Override
    public FilePreferences node(String path) {
        return (FilePreferences) super.node(path);
    }

    public Storage storage() {
        return storage;
    }

    public PathObject sfsRoot() {
        PathObject sfs = SFS;
        if (sfs == null && parent() != null) {
            sfs = ((FilePreferences) parent()).sfsRoot();
        }
        return sfs;
    }

    /**
     * Returns an object of type {@code Properties}. If the properties file
     * doesn't exist then the method returns a new instance of
     * {@code Properties}. If the file exists then it is loaded and it's content
     * returns as a {@code Properties } object.
     *
     * @return an object of type {@code Properties}.
     */
    protected PropertiesExt properties() {
        if (properties == null) {
            synchronized (lock) {
                try {
                    PropertiesExt props = storage().load();
                    properties = new PropertiesExt(props.getSectionName());
                    properties.merge(props);
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, ex.getMessage());
                }
            }
        }
        return properties;
    }

    public String sectionName() {
        return sectionName;
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
     * The method {@code getFirst(String,String)} will simply concatenate all the
     * items into one string, so generally separators (such as : for path-like
     * properties) must be included in the items (for example, at the end of all
     * but the last item).
     *
     * @param key a property name; cannot be null nor empty
     * @param values an array of strings to be set
     * @return a converted string value
     */
    public String setProperties(String key, String... values) {
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
     * The method first invokes this method of the super class then tests
     * whether the result is an array of strings as specified by the method {@link #setProperty(java.lang.String, java.lang.String...).
     * If so then converts the result to a regular string.
     *
     * @param key key whose associated value is to be returned.
     * @param def the value to be returned in the event that this preference
     * node has no value associated with key.
     *
     * @return the value associated with the given key, or def if no value is
     * associated with the key.
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

    /*    @Override
    public final String[] childrenNames() throws BackingStoreException {
        return super.childrenNames();
    }
     */
    @Override
    protected final String[] childrenNamesSpi() throws BackingStoreException {
        return storage.childrenNames();
    }

    @Override
    protected final void removeNodeSpi() throws BackingStoreException {
        storage.removeNode();
    }

    @Override
    protected final void removeSpi(String key) {
        removeProperty(key);
        storage.markModified();
        asyncInvocationOfFlushSpi();
    }

    private void removeProperty(String key) {
        synchronized (lock) {
            properties().remove(key);
        }
    }

    @Override
    protected String[] keysSpi() throws BackingStoreException {
        return getKeysSpi();
    }

    private String[] getKeysSpi() throws BackingStoreException {
        synchronized (lock) {
            PropertiesExt props = properties();
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

    public String[] get(String key) {
        synchronized (lock) {
            String v = get(key, null);
            if (v == null) {
                return null;
            }
            if ( PropertiesExt.isEncodedArray(v)) {
                return PropertiesExt.decodeArray(v);
            } 
            return new String[] {v};
        }
    }

    public void put(String key, String[] values) {
        put(key, PropertiesExt.encodeArray(values));
    }

    protected String getProperty(String key) {
        synchronized (lock) {
            PropertiesExt props = properties();
            if (props == null) {
                return null;
            }
            return props.getProperty(key);
        }
    }

    protected void setProperty(String key, String value) {
        synchronized (lock) {
            PropertiesExt props = properties();
            if (props == null) {
                return;
            }
            props.setProperty(key, value);
        }
    }

    public int addProperty(String key, String value) {
        int idx = -1;
        synchronized (lock) {
            PropertiesExt props = properties();
            if (props == null) {
                return idx;
            }
            idx = props.addProperty(key, value);
        }
        storage.markModified();
        asyncInvocationOfFlushSpi();
        return idx;
    }

    public void addProperties(String key, String... values) {
        synchronized (lock) {
            PropertiesExt props = properties();
            if (props == null) {
                return;
            }
            props.addProperties(key, values);
        }
        storage.markModified();
        asyncInvocationOfFlushSpi();

    }

    @Override
    protected final void putSpi(String key, String value) {
        setProperty(key, value);
        storage.markModified();
        asyncInvocationOfFlushSpi();
    }

    protected void asyncInvocationOfFlushSpi() {
        if (System.getProperty(SYS_KEY) != null) {
            try {
                flushSpi();
            } catch (BackingStoreException ex) {
                LOG.log(Level.SEVERE, ex.getMessage());
            }

        }
        if (!storage().isReadOnly()) {
            synchronized (lock) {
                try {
                    flushSpi();
                } catch (BackingStoreException ex) {
                    LOG.log(Level.SEVERE, ex.getMessage());
                }
            }
        }
    }

    protected void asyncInvocationOfFlushSpi_1() {
        if (System.getProperty(SYS_KEY) != null) {
            try {
                flushSpi();
            } catch (BackingStoreException ex) {
                LOG.log(Level.SEVERE, ex.getMessage());
            }

        }
        if (!storage().isReadOnly()) {
            Runnable runnable = () -> {
                synchronized (lock) {
                    try {
                        flushSpi();
                    } catch (BackingStoreException ex) {
                        LOG.log(Level.SEVERE, ex.getMessage());
                    }
                }
            };
            RequestExecutor.Task task = RequestExecutor.createTask(runnable);
            task.schedule(100);
            task.shutdown();
        }
    }

    @Override
    public final void flush() throws BackingStoreException {
        if (storage().isReadOnly()) {
            throw new BackingStoreException("Unsupported operation: read-only storage");//NOI18N
        } else {
            super.flush();
        }
    }

    @Override
    protected void flushSpi() throws BackingStoreException {
        synchronized (lock) {
            try {
                storage().save(properties());
            } catch (IOException ex) {
                throw new BackingStoreException(ex);
            }
        }
    }

    public @Override
    final void sync() throws BackingStoreException {
        if (storage().isReadOnly()) {
            throw new BackingStoreException("Unsupported operation: read-only storage");//NOI18N
        } else {
            if (super.isRemoved()) {
                return;
            }
            super.sync();
        }
    }

    @Override
    protected void syncSpi() throws BackingStoreException {
        if (properties != null) {
            synchronized (lock) {
                try {
                    properties().clear();
                    properties().putAll(storage().load());
                } catch (IOException ex) {
                    throw new BackingStoreException(ex);
                }
            }
        }
    }
    
    public String[] headerComments() {
        synchronized (lock) {
            PropertiesExt props = properties();
            if (props == null) {
                return null;
            }
            return props.headerComments().toArray(new String[0]);
        }
    }
    
    public void setHeaderComments(String[] comments) {
        synchronized (lock) {
            PropertiesExt props = properties();
            if (props == null) {
                return;
            }
            props.setHeaderComments(comments);
        }
        storage.markModified();
        asyncInvocationOfFlushSpi();
    }
    
    public String[] bottomComments() {
        synchronized (lock) {
            PropertiesExt props = properties();
            if (props == null) {
                return null;
            }
            return props.bottomComments().toArray(new String[0]);
        }
    }
    public void setBottomComments(String[] comments) {
        synchronized (lock) {
            PropertiesExt props = properties();
            if (props == null) {
                return;
            }
            props.setBottomComments(comments);
        }
        storage.markModified();
        asyncInvocationOfFlushSpi();
        
    }
    
    
    public String[] sectionComments() {
        synchronized (lock) {
            PropertiesExt props = properties();
            if (props == null) {
                return null;
            }
            return props.section(sectionName).comments().toArray(new String[0]);
        }
    }
    public void setSectionComments(String[] comments) {
        synchronized (lock) {
            PropertiesExt props = properties();
            if (props == null) {
                return;
            }
            
            props.section(sectionName).setComments(comments);
        }
        storage.markModified();
        asyncInvocationOfFlushSpi();
    }
    
    public String[] propertyComments(String key, int idx) {
        synchronized (lock) {
            PropertiesExt props = properties();
            if (props == null) {
                return null;
            }
            
            return props.section(sectionName).keyValue(key).get(idx).comments().toArray(new String[0]);
        }
    }
    public void setPropertyComments(String key, int idx, String[] comments) {
        synchronized (lock) {
            PropertiesExt props = properties();
            if (props == null) {
                return;
            }
            props.section(sectionName).keyValue(key).get(idx).setComments(comments);
        }
        storage.markModified();
        asyncInvocationOfFlushSpi();
    }
    public void remove(String key, String value) {
        synchronized (lock) {
            PropertiesExt props = properties();
            if (props == null) {
                return;
            }
            props.section(sectionName).remove(key, value);
        }
        storage.markModified();
        asyncInvocationOfFlushSpi();
    }
    public void comment(String key, String value) {
        synchronized (lock) {
            PropertiesExt props = properties();
            if (props == null) {
                return;
            }
            props.section(sectionName).comment(key, value);
        }
        storage.markModified();
        asyncInvocationOfFlushSpi();
    }
    public void uncomment(String key, String value) {
        synchronized (lock) {
            PropertiesExt props = properties();
            if (props == null) {
                return;
            }
            props.section(sectionName).uncomment(key, value);
        }
        storage.markModified();
        asyncInvocationOfFlushSpi();
    }
    
    public void comment(String key) {
        synchronized (lock) {
            PropertiesExt props = properties();
            if (props == null) {
                return;
            }
            props.section(sectionName).comment(key);
        }
        storage.markModified();
        asyncInvocationOfFlushSpi();
    }
    public void uncomment(String key) {
        synchronized (lock) {
            PropertiesExt props = properties();
            if (props == null) {
                return;
            }
            props.section(sectionName).uncomment(key);
        }
        storage.markModified();
        asyncInvocationOfFlushSpi();
    }
    
}
