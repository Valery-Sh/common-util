package org.vns.common.util.prefs.files;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import org.vns.common.files.FilePathObject;
import org.vns.common.util.prefs.files.BaseProperties.Section;

/**
 *
 * @author Valery Shyshkin
 */
public class IniPreferences {
    private static final Logger LOG = Logger.getLogger(IniPreferences.class.getName());

    private FilePreferences preferencesNode;
    private final String sfsRoot;
    protected IniSection section;
    private IniPreferences parent;
    private final List<IniPreferences> children = new CopyOnWriteArrayList<>();

    private final String nodePath;

    public IniPreferences(String sfsRoot) throws IOException {
        this.sfsRoot = sfsRoot;
        nodePath = "";
        FilePathObject.createDirectories(Paths.get(sfsRoot));

        init(sfsRoot);
    }

    public IniPreferences(IniPreferences parent, FilePreferences owner, String name) throws IOException {
        this.sfsRoot = owner.sfsRoot().getPath();
        this.preferencesNode = owner;
        nodePath = name;
        this.parent = parent;
        init();

    }

    private void init() throws IOException {
        parent.children.add(this);
    }

    private void init(String sfsRoot) throws IOException {
        preferencesNode = new FilePreferences(FilePathObject.toPathObject(Paths.get(sfsRoot)));
        parent = null;
    }

    protected FilePreferences preferencesNode() {
        return preferencesNode;
    }

    protected IniPreferences node(IniPreferences parent, Path path) {
        IniPreferences retval = null;
        try {
            String spath = path.toString();
            if (parent.child(spath) != null) {
                retval = child(spath);
            } else {
                FilePreferences afp = parent.preferencesNode().node(spath);
                retval = new IniPreferences(parent, afp, Paths.get(sfsRoot, spath).toString());
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage());
        }
        return retval;
    }

    public IniPreferences node(String path) {
        if (path.isEmpty()) {
            return this;
        }
        Iterator<Path> it = Paths.get(path).iterator();
        IniPreferences retval = this;
        while (it.hasNext()) {
            Path p = it.next();
            retval = node(retval, p);
        }
        return retval;
    }

    protected IniPreferences createSection(String sectionName) {
        FilePreferences afp = preferencesNode.node(sectionName);
        afp.sectionName = sectionName;
        IniPreferences retval = null;
        try {
            retval = new IniPreferences(this, afp, Paths.get(sfsRoot, sectionName).toString());
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage());
        }
        return retval;
    }

    protected IniSection iniSection() {
        return section;
    }

    protected IniPreferences parent() {
        return parent;
    }

    public IniPreferences[] children() {
        return children.toArray(new IniPreferences[0]);
    }

    public IniPreferences child(String name) {
        IniPreferences retval = null;
        for (IniPreferences ip : children) {
            String nodePathName = Paths.get(ip.nodePath()).getFileName().toString();
            if (name.equals(nodePathName)) {
                retval = ip;
            }
        }
        return retval;
    }

    public String name() {
        return Paths.get(nodePath()).getFileName().toString();
    }

    public IniSection section(String name) {
        IniSection retval;
        if (child(name) != null) {
            IniPreferences child = child(name);
            //child.onSaveValue(onSaveFunction);            
            retval = child.iniSection();
        } else {
            IniPreferences ip = createSection(name);
            //ip.onSaveValue(onSaveFunction);            
            retval = new IniSection(name, ip);
            ip.section = retval;
        }
        return retval;
    }

    public IniSection section() {
        return section(IniStorage.DEFAULT_SECTION);
    }

    /**
     * Converts a value returned by the method
     * {@code Preferences.absolutePath()} removing the first character if it is
     * forward or back slash.
     *
     * @return a converted path.
     */
    public String nodePath() {
        String s = Paths.get(preferencesNode.absolutePath()).normalize().toString().replace("\\", "/");
        if (s.startsWith("/")) {
            s = s.length() > 1 ? s.substring(1) : "";
        }
        return s;
    }

    protected String userRoot() {
        return sfsRoot;
    }

/*    public Section[] read() {
        return storage().sections();
    }
*/
    protected List<Section> listSections() {
        List<Section> retval = new ArrayList<>();

        try {
            String[] nodes = preferencesNode.childrenNames();

            FilePreferences afp = null;
            for (String sn : nodes) {
                if (preferencesNode.node(sn).sectionName != null) {
                    afp = preferencesNode.node(sn);
                }
            }
            if (afp != null) {
                Section[] a = ((IniStorage) afp.storage()).sections();
                if (a != null) {
                    retval = new ArrayList<>(Arrays.asList(a));
                }
            }
        } catch (BackingStoreException ex) {
            LOG.log(Level.SEVERE, ex.getMessage());
        }
        return retval;
    }

/*    public String[] headerComments() {
        return storage().headerComments().toArray(new String[0]);
    }

    public String[] bottomComments() {
        return storage().bottomComments().toArray(new String[0]);
    }

    public void setHeaderComments(String... comments) {
        storage().setHeaderComments(comments);
        try {
            preferencesNode().flush();
        } catch (BackingStoreException ex) {
            LOG.log(Level.SEVERE, ex.getMessage());

        }
    }

    public void setBottomComments(String... comments) {
        storage().setBottomComments(comments);
        try {
            preferencesNode().flush();
        } catch (BackingStoreException ex) {
            LOG.log(Level.SEVERE, ex.getMessage());
        }
    }

    protected IniStorage storage() {
        return (IniStorage) preferencesNode().storage();
    }
*/
}//class IniPreferences
