package org.vns.common.util.prefs.files;

import java.nio.file.Paths;
import java.util.Objects;
import java.util.logging.Logger;

/**
 *
 * @author Valery Shyshkin
 */
public class IniSection {

    private static final Logger LOG = Logger.getLogger(FilePreferences.class.getName());

    private final String name;
    private final IniPreferences owner;

    public IniSection(String sectionName, IniPreferences owner) {
        this.name = sectionName;
        this.owner = owner;
        init();
    }

    private void init() {
    }

    public String name() {
        return name;
    }

    protected FilePreferences node() {
        return owner().preferencesNode();
    }

    protected IniPreferences owner() {
        return owner;
    }

    protected IniStorage storage() {
        return (IniStorage) node().storage();
    }

    /**
     * Converts a value returned by the method
     * {@code Preferences.absolutePath()} removing the first character if it is
     * forward or back slash.
     *
     * @return a converted path.
     */
    public String nodePath() {
        String s = Paths.get(node().absolutePath()).normalize().toString().replace("\\", "/");
        if (s.startsWith("/")) {
            s = s.length() > 1 ? s.substring(1) : "";
        }
        return s;
    }

    public String[] comments() {
        return node().sectionComments();
    }

    public void setComments(String... comments) {
        node().setSectionComments(comments);
    }

    public String[] headerComments() {
        return node().headerComments();
    }

    public void setHeaderComments(String... comments) {
        node().setHeaderComments(comments);
    }
    public String[] bottomComments() {
        return node().bottomComments();
    }

    public void setBottomComments(String... comments) {
        node().setBottomComments(comments);
    }
    
    public String[] propertyComments(String key, int idx) {
        return node().propertyComments(key, idx);
    }

    public void setPropertyComments(String key, int idx, String... comments) {
        node().setPropertyComments(key, idx, comments);
    }
    public void setProperty(String key, String value) {
        node().put(key, value);
    }

    public void put(String key, String[] values) {
        node().put(key, values);
    }

    public void addProperties(String key, String... values) {
        node().addProperties(key, values);
    }

    public int addProperty(String key, String value) {
        return node().addProperty(key, value);
    }

    /*    public void modifyValue(String key, Consumer<PropertyObject> consumer) {
        String orig = node().get(key, null);
        PropertyObject po = new PropertyObject(orig);
        consumer.accept(po);
        String newValue = po.value();
        if (newValue != null) {
            node().put(key, newValue);
        }

    }
     */
    public void removeValues(String key, String... values) {
        for ( String value : values) {
            node().remove(key, value);
        }
    }
    
    public void comment(String key, String value) {
        node().comment(key, value);
    }
    public void uncomment(String key, String value) {
        node().uncomment(key, value);
    }
    public void comment(String key) {
        node().comment(key);
    }
    public void uncomment(String key) {
        node().uncomment(key);
    }

    public String getProperty(String key) {
        return node().get(key, null);
    }

    public String[] get(String key) {
        return node().get(key);
    }

    public String get(String key, String def) {
        return node().get(key, def);
    }

    public String setProperty(String key, String... values) {
        return node().setProperties(key, values);
    }

    public String join(String key, String def) {
        return node().join(key, def);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.name);
        hash = 37 * hash + Objects.hashCode(this.owner);
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
        final IniSection other = (IniSection) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.owner, other.owner)) {
            return false;
        }
        return true;
    }

    /*    protected TripleConsumer<String, String, String> defaultOnSaveFunction = (String secName, String key, String value) -> {

        IniStorage.Section storageSection = storage().section(secName);
        if (storageSection == null) {
            return;
        }

        if (PropertiesExt.isJoinValues(value)) {
            String[] values = PropertiesExt.splitValue(value);
            for (String v : values) {
                //section.add(key, v);
                storageSection.replaceDublicateValues(key, v);
            }
        } else {
            //section.add(key, value);
            storageSection.replaceDublicateValues(key, value);
        }
    };
    protected TripleConsumer<String, String, String> uniqueKeyValueOnSaveFunction = (String secName, String key, String value) -> {

        IniStorage.Section storageSection = storage().section(secName);
        if (storageSection == null) {
            return;
        }

        if (PropertiesExt.isJoinValues(value)) {
            String[] values = PropertiesExt.splitValue(value);
            storageSection.replaceDublicates(key, value);
            for (String v : values) {
                //section.add(key, v);
                //storageSection.replaceDublicates(key, v);
            }
        } else {
            //section.add(key, value);
            storageSection.replaceDublicates(key, value);
        }
    };
    protected TripleConsumer<String, String, String> uniqueKeyOnSaveFunction = (String secName, String key, String value) -> {

        IniStorage.Section storageSection = storage().section(secName);
        if (storageSection == null) {
            return;
        }
        KeyValue oldKeyValue = storageSection.keyValue(key);

        if (oldKeyValue != null) {
            String old = oldKeyValue.value();
            if (old == null || !old.equals(value)) {
                //section.set(key, value);
            }
            oldKeyValue.addValueObject(value);
            String newvalue = oldKeyValue.value();
            storageSection.set(key, newvalue);
        } else {
            storageSection.set(key, value);
        }

    };

    protected TripleConsumer<String, String, String> allowDublicateKeyOnSaveFunction = (String secName, String key, String value) -> {

        IniStorage.Section storageSection = storage().section(secName);
        if (storageSection == null) {
            return;
        }

        if (PropertiesExt.isJoinValues(value)) {
            String[] values = PropertiesExt.splitValue(value);
            for (String v : values) {
                storageSection.add(key, v);
            }
        } else {
            //section.add(key, value);
            storageSection.add(key, value);
        }
    };

    private TripleConsumer<String, String, String> onSaveFunction = defaultOnSaveFunction;

    protected void setOnSaveValueFunction() {
        storage().onSaveValueFunction(onSaveFunction);
    }

    //private TripleConsumer<String, String, String> onSaveFunction = uniqueKeyValueOnSaveFunction;
    public void onSaveValue(TripleConsumer<String, String, String> consumer) {
        onSaveFunction = consumer;
        setOnSaveValueFunction();
    }

    public void onSaveValue(SaveValueOption option) {
        switch (option) {
            case REPLACE_KEY_VALUE:
                onSaveFunction = defaultOnSaveFunction;
                break;
            case DOUBLICATE_KEY_VALUE:
                onSaveFunction = allowDublicateKeyOnSaveFunction;
                break;
            case UNIQUE_KEY_VALUE:
                onSaveFunction = uniqueKeyValueOnSaveFunction;
                break;
            case UNIQUE_KEY:
                onSaveFunction = uniqueKeyOnSaveFunction;
                break;
        }
        setOnSaveValueFunction();
    }

    public static enum SaveValueOption {
        REPLACE_KEY_VALUE,
        DOUBLICATE_KEY_VALUE,
        UNIQUE_KEY_VALUE,
        UNIQUE_KEY
    }
     */
}
