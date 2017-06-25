package org.vns.common.util.prefs.files;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.vns.common.PathObject;
import org.vns.common.files.FilePathObject;
import org.vns.common.util.prefs.Storage;
import org.vns.common.util.prefs.files.BaseProperties.IniFile;
import org.vns.common.util.prefs.files.BaseProperties.KeyValue;
import org.vns.common.util.prefs.files.BaseProperties.Section;

/**
 *
 * @author Valery
 */
public class IniStorage extends FileStorage {

    private static final Logger LOG = Logger.getLogger(IniStorage.class.getName());

    //private IniFile iniFile;
    private final IniProperties iniProperties;

    public final static String DEFAULT_SECTION = BaseProperties.DEFAULT_SECTION;

    protected IniStorage(FilePreferences owner, final String nodePath, boolean userRoot) {
        super(owner, nodePath, userRoot);
        iniProperties = new IniProperties();
    }

    static Storage instance(FilePreferences owner, final String absolutePath) {
        return new IniStorage(owner, absolutePath, true);
    }

    static Storage instanceReadOnly(final String absolutePath) {

        return new IniStorage(null, absolutePath, false) {
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

    public Section[] sections() {
        return iniProperties.sections();
    }
    private IniFile iniFile;
    
    protected IniFile iniFile() throws IOException {

        if (iniFile == null) {
            iniFile = new IniFile(createPropertiesFile().getPath());
            iniProperties.setIniFile(iniFile);
        }
        return iniProperties.iniFile();
    }

    public boolean isReady() throws IOException {
        return iniFile != null;
    }

    @Override
    protected String folderPath() {
        String retval = super.folderPath();
        if (retval == null) {
            retval = "";
        }
        if (owner.sectionName != null) {
            Path np = Paths.get(nodePath());
            retval = np.subpath(0, np.getNameCount() - 1).toString();
        }
        return retval;
    }

    @Override
    public String[] childrenNames() {

        List<String> folderNames = new ArrayList<String>();

        Path folderPath = Paths.get(folderPath()).getParent();
        if (folderPath == null) {
            return new String[0];
        }
        FilePathObject folder = (FilePathObject) SFS().getPathObject(folderPath.toString());

        if (folder != null) {
            for (PathObject po : folder.getFolders(false)) {
                List<PathObject> clist = po.getChildren(true);
                for (PathObject ppo : clist) {
                    if (supportsExtension(ppo.getExt())) {
                        folderNames.add(po.getNameExt());
                        break;
                    }
                }
            }
            for (PathObject po : folder.getData(false)) {
                if (supportsExtension(po.getExt())) {
                    folderNames.add(po.getName());
                }
            }
        }

        return folderNames.toArray(new String[folderNames.size()]);
    }

    public Section section(String sectionName) {
        Section retval = null;
        if (iniProperties.iniFile() == null) {
            return null;
        }
        for (Section s : iniProperties.iniFile().sections()) {
            if (s.name() == null) {
                continue;
            }
            if (s.name().equals(sectionName)) {
                retval = s;
                break;
            }
        }
        return retval;
    }

    @Override
    public PropertiesExt load() {

        String sectionName = owner().sectionName();
        System.out.println("0000 section name = " + sectionName);

        PropertiesExt props = new PropertiesExt(sectionName);
        //PropertiesExt props = new PropertiesExt();

        PathObject po = SFS().getPathObject(buildPropertiesPath());

        if (po == null) {
            return props;
        }

        try {
            iniFile().load();
            props = iniFile().properties(sectionName);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage());
        }
        return props;
    }

//    private TripleConsumer<String, String, String> onSaveValue;
//    public void onSaveValueFunction(TripleConsumer<String,String,String>  consumer) {
//        onSaveValue = consumer;
//    }
    
    @Override
    public void save(final PropertiesExt properties) throws IOException {
        String sectionName = owner.sectionName();
        iniFile = null;

        load();

        if (isModified()) {
            setModified(false);

            if (!properties.isEmpty()) {
                if (iniFile().section(sectionName) == null) {
                    //
                    // Create new ini file
                    //
                    iniFile().sections().add(new Section(sectionName));
                }

                Section section = iniFile().section(sectionName);

                section.clear();
                Section propSection = properties.section(sectionName);
                section.merge(propSection);
                iniFile().save();
            } else {
                Section section = iniFile().section(sectionName);
                iniFile().sections().remove(section);
                if (!iniFile().sections().isEmpty()) {
                    iniFile().save();
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
    }

    public boolean supportsExtension(String fileExtension) {
        return fileExtension.equals("ini");
    }

    @Override
    protected String buildPropertiesPath() {

        // !!!!!!!!!! String fileExtension = owner().typeExtention();
        String fileExtension = "ini";
        StringBuilder sb = new StringBuilder();

        Path path = Paths.get(folderPath());

        String fileName;
        if (path.getNameCount() == 0) {
            fileName = rootFileName;
        } else if (path.getNameCount() == 1) {
            fileName = path.getFileName().toString();
        } else {
            fileName = path.getFileName().toString();
            sb.append(path.getParent().getFileName().toString())
                    .append("/");
        }
        sb.append(fileName)
                .append(".")
                .append(fileExtension);
        return sb.toString();
    }


    /*    public void removeFromArray(String sectionName, String key, String... values) {
        IniFile ini = getIniFile();
        if (ini == null) {
            return;
        }
        KeyValue kv = ini.section(sectionName).keyValue(key);
        for (String value : values) {
            kv.removeByValue(value);
        }
    }

    public void removeFromArray(String sectionName, String key, int idx) {
        IniFile ini = getIniFile();
        if (ini == null) {
            return;
        }
        KeyValue kv = ini.section(sectionName).keyValue(key);
        if (kv == null) {
            return;
        }
        kv.remove(idx);
    }
     */
}// class IniStorage
