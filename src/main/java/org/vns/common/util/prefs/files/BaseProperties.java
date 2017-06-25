package org.vns.common.util.prefs.files;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Valery
 */
public class BaseProperties {

    private static final Logger LOG = Logger.getLogger(IniProperties.class.getName());

    public final static String DEFAULT_SECTION = "[]";

    protected static String START = "{" + System.lineSeparator();
    protected static String END = System.lineSeparator() + "}";

    protected IniFile iniFile;

    public BaseProperties() {
        iniFile = new IniFile();
    }

    protected Section section() {
        return section(DEFAULT_SECTION);
    }

    protected Section section(String sectionName) {
        Section retval = iniFile().section(sectionName);
        if (retval == null) {
            retval = new Section(sectionName);
            iniFile().sections.add(retval);
        }
        return retval;

    }

    protected IniFile iniFile() {
        if (iniFile == null) {
            iniFile = new IniFile();
        }
        return iniFile;
    }

    protected void setIniFile(IniFile iniFile) {
        this.iniFile = iniFile;
    }

    protected Section[] sections() {
        if (iniFile == null) {
            return null;
        }
        return iniFile().sections().toArray(new Section[0]);
    }

    public void load(InputStream stream) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(stream))) {
            iniFile().load(br);
        }
    }

    public void load(String str) throws IOException {
        InputStream stream = new ByteArrayInputStream(str.getBytes());
        try (BufferedReader br = new BufferedReader(new InputStreamReader(stream))) {
            iniFile().load(br);
        }
    }

    public void store(OutputStream stream) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(stream))) {
            iniFile().save(bw);
        }
    }

    public String store() throws IOException {
        OutputStream stream = new ByteArrayOutputStream();
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(stream))) {
            iniFile().save(bw);
        }
        return stream.toString();
    }

    public List<String> headerComments() {
        List<String> retval = new ArrayList<>();
        retval = iniFile().headerComments;
        return retval;
    }

    public void setHeaderComments(String... comments) {
        iniFile().headerComments.clear();
        if ( comments == null ) {
            return;
        }
        for (int i = 0; i < comments.length; i++) {
            if (!comments[i].startsWith("#")) {
                if (!comments[i].startsWith("# ")) {
                    comments[i] = "# " + comments[i];
                } else {
                    comments[i] = "#" + comments[i];
                }
            }
        }
        
        Collections.addAll(iniFile().headerComments, comments);
    }

    public List<String> bottomComments() {
        List<String> retval = new ArrayList<>();
        retval.addAll(iniFile().bottomComments);
        return retval;
    }

    public void setBottomComments(String... comments) {
        iniFile().bottomComments.clear();
        if ( comments == null ) {
            return;
        }            
        for (int i = 0; i < comments.length; i++) {
            if (!comments[i].startsWith("#")) {
                if (!comments[i].startsWith("# ")) {
                    comments[i] = "# " + comments[i];
                } else {
                    comments[i] = "#" + comments[i];
                }
            }
        }

        Collections.addAll(iniFile().bottomComments, comments);
    }

    private static int decodeNext(char[] chars, int start, StringBuilder sb) {
        int pos = start;
        if (chars[pos] == '[') {
            pos++;
        } else {
            return -1;
        }
        while (pos <= chars.length - 1) {
            if (chars[pos] != '[' && chars[pos] != ']') {
                sb.append(chars[pos]);
                pos += 1;
                continue;
            }
            if (chars[pos] == '[' && pos < chars.length - 1 && chars[pos + 1] == '[') {
                sb.append('[');
                pos += 2;
                continue;
            }
            //
            // Одиночеый символ '['
            //
            if (chars[pos] == '[' && pos == chars.length - 1) {
                pos = -1;
                break;
            }
            if (chars[pos] == '[' && chars[pos + 1] != '[') {
                pos = -1;
                break;
            }

            if (chars[pos] == ']' && pos == chars.length - 1) {
                pos += 1;
                break;
            }
            if (chars[pos] == ']' && pos < chars.length - 1 && chars[pos + 1] == ']') {
                sb.append(']');
                pos += 2;
                continue;
            }
            if (chars[pos] == ']') {
                pos++;
                break;
            }

        }//while
        return pos;
    }

    public static String[] decodeArray(String value) {

        if (value == null) {
            return null;
        }

        if (!value.trim().startsWith(START)) {
            //return new String[]{value};
            return null;
        }
        if (!value.trim().endsWith(END)) {
            //return new String[]{value};
            return null;
        }

        //
        // Empty array
        //
        if (value.trim().equals(START + END)) {
            return new String[0];
        }

        List<String> resultList = new ArrayList<>();
        String str = value.trim().substring(START.length());
        str = str.trim().substring(0, str.indexOf(END));

        char[] chars = str.toCharArray();

        int pos = 0;

        StringBuilder sb = new StringBuilder();

        while (true) {
            if (chars[pos] != '[') {
                return null;
            }
            if (pos > chars.length - 1) {
                return null;
            }

            pos = decodeNext(chars, pos, sb);

            if (pos == -1) {
                return null;
            }
            resultList.add(sb.toString());
            sb.setLength(0);
            if (pos == chars.length) {
                break;
            }
            if (chars[pos] != '[') {
                return null;
            }
        }
        return resultList.toArray(new String[0]);
    }

    public static String joinValues(String... values) {
        if (values == null) {
            return null;
        }
        String dlm = "\\" + System.lineSeparator();
        if (values.length == 0) {
            return dlm + System.lineSeparator();
        }
        return dlm + String.join(dlm, values);
    }

    public static String encodeArray(String... values) {
        if (values == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();

        sb.append(START);
        for (String value : values) {
            String v = value.replace("[", "[[").replace("]", "]]");
            sb.append("[")
                    .append(v)
                    .append("]");
        }

        return sb.append(END).toString();
    }

    /**
     * If the given string is {@code null} then the result is {@code null}.
     * <p>
     * If the given string is {@code null} then the result is {@code null}.
     *
     * @param value
     * @return null if the property for the given key doesn't exist.
     */
    public static String[] splitValue(String value) {

        if (value == null) {
            return null;
        }
        String lineSep = System.lineSeparator();
        String sep = "\\" + lineSep;
        //
        // !!! First symbol cannot be a space character for the string to be a String
        // array View.
        //
        if (!value.startsWith(sep)) {
            return new String[]{value};
        }
        //
        // The string starts and ends with a separator. Return an empty array
        // 

        if (value.endsWith(sep + lineSep) && value.length() == sep.length() + lineSep.length()) {
            return new String[0];
        }

        String sepPair = sep + sep;

        List<String> list = new ArrayList<>();

        char[] chars = value.toCharArray();
        String workVal = value;

        int idx = 0;
        int pos = 0;

        StringBuilder sb = new StringBuilder();

        while (!workVal.isEmpty()) {
            if (workVal.startsWith(sepPair) || workVal.startsWith(sep)) {
                idx += sep.length();
                if (sb.length() != 0) {
                    list.add(sb.toString());
                    sb.setLength(0);
                }
                if (workVal.startsWith(sepPair) || idx == workVal.length()) {
                    list.add("");
                }
            } else {
                sb.append(chars[pos]);
                idx++;
            }
            workVal = workVal.substring(idx);
            pos += idx;
            idx = 0;

            if (workVal.isEmpty() && sb.length() > 0) {
                list.add(sb.toString());
            }
        }
        return list.toArray(new String[0]);
    }

    public static boolean isEncodedArray(String value) {
        if (value == null) {
            return false;
        }
        if (!value.trim().startsWith(START) || !value.trim().endsWith(END)) {
            return false;
        }
        return decodeArray(value) != null;

    }

    public static class IniFile {

        public Long iniFileTime = System.currentTimeMillis();

        private final Pattern _section = Pattern.compile("\\s*\\[([^]]*)\\]\\s*");
        private final Pattern _keyValue = Pattern.compile("\\s*([^=]*)=(.*)");
        private final List<String> _bottomComments = new ArrayList<>();
        private List<String> _headerComments = new ArrayList<>();
        protected final List<String> bottomComments = new ArrayList<>();
        protected final List<String> headerComments = new ArrayList<>();

        private final List<Section> sections = new ArrayList<>();

        private String path;

        protected IniFile(String path) {
            this.path = path;
            //sections.add(new Section(DEFAULT_SECTION));
        }

        protected IniFile() {
        }

        protected void clear() {
            _bottomComments.clear();
            _headerComments.clear();
            sections.forEach(s -> {
                s.comments().clear();
                s.keyValues().clear();
            });
        }

        public void addSection(Section section) {

        }

        public void clearKeys() {
            sections.forEach(s -> {
                s.keyValues().clear();
            });
        }

        public List<Section> sections() {
            return sections;
        }

        public Section section(String name) {
            Section retval = null;
            for (Section s : sections) {
                if (s.name == null) {
                    int i = sections.indexOf(s);
                    continue;
                }

                if (s.name().equals(name)) {
                    retval = s;
                    break;
                }
            }
            return retval;
        }

        public void load() throws IOException {
            try (BufferedReader br = new BufferedReader(new FileReader(path))) {
                load(br);
            }
        }

        public void load(BufferedReader br) throws IOException {
            sections.clear();
            _bottomComments.clear();
            _headerComments.clear();

            String line;
            String section = null;
            Section sec = null;

            List<String> commentLines = new ArrayList<>();

            boolean firstCommentsDone = false;

            while ((line = br.readLine()) != null) {

                if (!firstCommentsDone && (line.trim().isEmpty() || line.trim().startsWith("#")
                        && !CommentHelper.isCommentedKeyValue(line))) {
                    _headerComments.add(line);
                    continue;
                }

                if (sections.isEmpty()) {
                    section = DEFAULT_SECTION;
                    sec = new Section(section);
                    sections.add(sec);
                    commentLines.addAll(CommentHelper.lastFragment(_headerComments));
                }

                firstCommentsDone = true;

                if (firstCommentsDone && (line.trim().isEmpty()
                        || line.trim().startsWith("#")
                        && !CommentHelper.isCommentedKeyValue(line))) {
                    commentLines.add(line);
                    _bottomComments.add(line);
                    continue;
                }

                Matcher m = _section.matcher(line);
                if (m.matches()) {
                    section = m.group(1).trim();
                    if (!section.equals(DEFAULT_SECTION)) {
                        sec = new Section(section);
                        sec.comments().addAll(commentLines);
                        sections.add(sec);
                        commentLines.clear();
                        _bottomComments.clear();
                    }
                } else if (CommentHelper.isCommentedKeyValue(line)) {
                    String[] pair = CommentHelper.getKeyValue(line);
                    String key = pair[0];
                    String value = pair[1];
                    ValueObject vo = new ValueObject(key, value);
                    vo.markCommented();
                    sec.valueObjects.add(vo);
                    vo.comments.addAll(commentLines);
                    commentLines.clear();
                    _bottomComments.clear();

                } else if (section != null) {
                    m = _keyValue.matcher(line);
                    if (m.matches()) {
                        String key = m.group(1).trim();
                        String value = m.group(2).trim();
                        if (value.startsWith("\\") && value.trim().length() == 1) {
                            value = loadArray(key, br);
                        }
                        int idx = sec.addProperty(key, value);
                        sec.keyValue(key).get(idx).comments().addAll(commentLines);

                        commentLines.clear();
                        _bottomComments.clear();
                    }
                }
            }

            bottomComments.clear();

            bottomComments.addAll(_bottomComments);
            headerComments.clear();
            if (_headerComments.isEmpty()) {
                return;
            }
            List<String[]> h = CommentHelper.split(_headerComments);
            if (h.size() > 1) {
                Collections.addAll(headerComments, h.get(0));
                h.remove(0);
            }

            if (h.size() > 0) {
                h.remove(h.size() - 1);
            }
            if (h.size() != 0) {
                section(DEFAULT_SECTION).comments.addAll(CommentHelper.join(h));
            }

        }

        protected String loadArray(String key, BufferedReader br) throws IOException {
            String line = null;
            List<String> values = new ArrayList<>();

            while ((line = br.readLine()) != null) {
                Matcher m1 = _section.matcher(line);
                Matcher m2 = _keyValue.matcher(line);
                line = line.trim();
                if (line.startsWith("#") || line.isEmpty()) {
                    continue;
                }
                if (line.trim().endsWith("\\")) {
                    line = line.substring(0, line.length() - 1);
                    values.add(line);
                } else {
                    values.add(line);
                    break;
                }
            }//while

            return PropertiesExt.joinValues(values.toArray(new String[0]));
        }

        public void saveSection(BufferedWriter bw, Section section) throws IOException {
            for (ValueObject vo : section.valueObjects()) {
                if (vo.isDeleted()) {
                    List<String> comments = CommentHelper.fragmentsExceptLast(vo.comments());
                    for (String line : comments) {
                        bw.write(line + System.lineSeparator());
                    }
                    continue;
                }
                for (String c : vo.comments()) {
                    bw.write(c + System.lineSeparator());
                }
                String pref = vo.isCommented() ? "# " : "";
                bw.write(pref + vo.key().trim() + "=" + vo.value() + System.lineSeparator());
            }
            /*            for (String c : section.bottomComments()) {
                bw.write(c + System.lineSeparator());
            }
            for (String c : section.removedComments()) {
                bw.write(c + System.lineSeparator());
            }
             */
        }

        public void save(BufferedWriter bw) throws IOException {
            for (String c : headerComments) {
                bw.write(c + System.lineSeparator());
            }
            for (Section s : sections) {
                for (String c : s.comments()) {
                    bw.write(c + System.lineSeparator());
                }
                if (!DEFAULT_SECTION.equals(s.name())) {
                    bw.write("[" + s.name() + "]" + System.lineSeparator());
                }
                saveSection(bw, s);

            }
            for (String c : bottomComments) {
                bw.write(c + System.lineSeparator());
            }
        }

        public void save() throws IOException {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
                save(bw);
            }
        }

        public PropertiesExt properties(String sectionName) {
            PropertiesExt retval = new PropertiesExt(sectionName);

            Section section = section(sectionName);
            if (section == null) {
                sections().add(section = new Section(sectionName));
            }
            retval.section(sectionName).merge(section);
            return retval;
        }
    }//class IniFile

    public static class Section {

        private final String name;
        private final List<String> comments = new ArrayList<>();

        private final List<KeyValue> keyValues = new ArrayList<>();

        private final List<ValueObject> valueObjects = new ArrayList<>();

        public Section(String name) {
            this.name = name;
            init();
        }

        private void init() {
        }

        public String name() {
            return name;
        }

        public Set<String> keySet() {
            Set<String> retval = new HashSet<>();
            keyValues.forEach(kv -> {
                retval.add(kv.key());
            });
            return retval;
        }

        public void clear() {
            comments.clear();
            keyValues.clear();
        }

        public List<String> comments() {
            return comments;
        }

        public void setComments(String... comments) {
            this.comments.clear();
            if ( comments == null ) {
                return;
            }               
            for (int i = 0; i < comments.length; i++) {
                if (!comments[i].startsWith("#")) {
                    if (!comments[i].startsWith("# ")) {
                        comments[i] = "# " + comments[i];
                    } else {
                        comments[i] = "#" + comments[i];
                    }
                }
            }

            Collections.addAll(this.comments, comments);

            if (comments.length == 0 || comments.length > 0 && !comments[0].trim().isEmpty()) {
                this.comments.add(0, "");
            } 
        }

        public void putAll(List<KeyValue> from) {
            keyValues.addAll(from);
        }

        public void copyAll(Section from) {
            keyValues.addAll(from.keyValues());
            comments.addAll(from.comments());
        }

        public void merge(Section from) {
            comments.clear();
            comments.addAll(from.comments());
            valueObjects().clear();
            keyValues.clear();
            from.valueObjects().forEach(vo -> {

                KeyValue kvnew = keyValue(vo.key());
                ValueObject newvo;

                if (kvnew == null) {
                    //newvo = new ValueObject(kvnew, vo.value());                   
                    newvo = new ValueObject(vo.key(), vo.value(), vo.isDeleted(), vo.isCommented());
                    kvnew = new KeyValue(this, vo.key());
                    kvnew.add(newvo);
                    keyValues.add(kvnew);

                } else {
                    newvo = new ValueObject(vo.key(), vo.value(), vo.isDeleted(), vo.isCommented());
                    kvnew.add(newvo);
                }
                newvo.comments.addAll(vo.comments);
            });
        }

        public String getProperty(String key) {
            if (keyValue(key) == null) {
                return null;
            }
            return keyValue(key).value();
/*            String[] vs = keyValue(key).values();
            if (vs.length == 0) {
                return null;
            }
            if (vs.length == 1) {
                return vs[0];
            }
            return encodeArray(vs);
*/            
        }

        public void remove(String key) {
            KeyValue toRemove = null;
            for (KeyValue kv : keyValues) {
                if (key.equals(kv.key())) {
                    toRemove = kv;
                    break;
                }
            }
            keyValues.remove(toRemove);
        }

        public void remove(String key, int idx) {
            KeyValue toRemoveFrom = keyValue(key);
            if (toRemoveFrom == null) {
                return;
            }
            if (idx < 0) {
                return;
            }
            toRemoveFrom.remove(idx);
        }

        /**
         * Removes the first occurrence of the specified {@code key/value).
         * @param key
         * @param value
         */
        public void remove(String key, String value) {
            KeyValue toRemove = keyValue(key);
            if (toRemove == null) {
                return;
            }
            int idx = toRemove.indexOf(value);
            remove(key, idx);
        }

        public void uncomment(String key, String value) {
            for (int i = 0; i < valueObjects().size(); i++) {

                ValueObject vo = valueObjects.get(i);
                if (!vo.isCommented() || !key.equals(vo.key())) {
                    continue;
                }
                vo.uncomment();
                KeyValue kv;
                if (!contains(key)) {
                    kv = new KeyValue(this, key);
                    kv.valueObjects.add(vo);
                    keyValues.add(kv);
                } else {
                    keyValue(key).valueObjects.add(vo);
                }
                break;
            }
        }

        public void uncomment(String key) {
            for (int i = 0; i < valueObjects().size(); i++) {
                ValueObject vo = valueObjects.get(i);
                if (!vo.isCommented() || !key.equals(vo.key())) {
                    continue;
                }
                vo.uncomment();
                KeyValue kv;
                if (!contains(key)) {
                    kv = new KeyValue(this, key);
                    kv.valueObjects.add(vo);
                    keyValues.add(kv);
                } else {
                    keyValue(key).valueObjects.add(vo);
                }
                //break;
            }
        }

        public void comment(String key) {
            String[] values = get(key);
            for (String v : values) {
                comment(key, v);
            }
        }

        public void comment(String key, String value) {
            if (keyValue(key) == null) {
                return;
            }
            int idx = keyValue(key).indexOf(value);
            if (idx < 0) {
                return;
            }
            ValueObject vo = keyValue(key).get(idx);
            keyValue(key).valueObjects.remove(vo);

            vo.markCommented();
        }

        public String[] get(String key) {
            if (keyValue(key) == null) {
                return null;
            }
            return keyValue(key).values();
        }

        public void setProperty(String key, String value) {
            String[] vs = new String[]{value};
            if (isEncodedArray(value)) {
                vs = decodeArray(value);
            }
            put(key, vs);
        }

        public void put(String key, String... values) {
            KeyValue kv;
            if (contains(key)) {
                KeyValue oldkv = keyValue(key);
                kv = new KeyValue(this, key);
                kv.valueObjects.addAll(oldkv.valueObjects);

                List<ValueObject> toAdd = new ArrayList<>();
                for (String v : values) {
                    ValueObject vo = kv.getFirst(v);
                    if (vo != null) {
                        kv.valueObjects.remove(vo);
                        continue;
                    }
                    toAdd.add(new ValueObject(key, v));
                }
                //
                // Now kv contains objects that must be removed
                //
                ValueObject first = null;
                if (!kv.valueObjects.isEmpty()) {
                    first = kv.valueObjects.get(0);
                }
                if (first != null && !toAdd.isEmpty()) {
                    first.replaceValue(toAdd.get(0).value);
                    toAdd.remove(0);
                    kv.valueObjects.remove(0);
                }
                for (int i = 0; i < kv.valueObjects.size(); i++) {
                    remove(key, i);
                }
                for (int i = 0; i < toAdd.size(); i++) {
                    addProperty(key, toAdd.get(i).value);
                }

            } else {
                kv = new KeyValue(this, key);
                for (String v : values) {
                    //
                    // Now kv contains objects that must be removed
                    //
                    kv.add(v);
                }
                keyValues.add(kv);
            }
        }

        public KeyValue addProperties(String key, String... values) {

            for (String value : values) {
                addProperty(key, value);
            }
            return keyValue(key);
        }

        public int addProperty(String key, String value) {
            if (!contains(key)) {
                KeyValue kv = new KeyValue(this, key, value);
                keyValues.add(kv);
                return 0;
            }
            return keyValue(key).add(value);
        }

        public List<KeyValue> keyValues() {
            return keyValues;
        }

        protected List<ValueObject> valueObjects() {
            return valueObjects;
        }

        public boolean contains(String key) {
            //
            // Two KeyValue are aqual if their keys are equal => we ca use an empty value
            //
            return keyValue(key) != null;
        }

        public KeyValue keyValue(String key) {
            if (keyValues == null) {
                return null;
            }
            KeyValue retval = null;
            for (KeyValue kv : keyValues) {
                if (kv.key().equals(key)) {
                    retval = kv;
                    break;
                }
            }
            return retval;
        }

        public int indexOf(KeyValue kv) {
            return keyValues.indexOf(kv);
        }

        public int size() {
            return keyValues.size();
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 37 * hash + Objects.hashCode(this.name);
            hash = 37 * hash + Objects.hashCode(this.keyValues);
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
            final Section other = (Section) obj;
            if (!Objects.equals(this.name, other.name)) {
                return false;
            }
            return true;
        }

    }//class Section

    public static class KeyValue {

        private final String key;
        private final Section section;
        private final List<ValueObject> valueObjects = new ArrayList<>();

        public KeyValue(Section section, String key, String value) {
            this.key = key;
            this.section = section;
            init(value);
        }

        public KeyValue(Section section, String key) {
            this.key = key;
            this.section = section;
        }

        private void init(String value) {
            add(value);
        }
        /**
         * 
         */
        public void clear() {
            valueObjects.forEach(vo -> {
                vo.markDeleted();
            });
            valueObjects.clear();
        }

        /**
         * Creates a new object of type {@link ValueObject} and adds it to an
         * internal collection.
         *
         * If the given value is {@code null } then the method does nothing. No
         * check for duplicate values and no conversions of the given value are
         * made.
         *
         * @param value a value to be added
         */
        public int add(String value) {
            if (value == null) {
                return -1;
            }
            ValueObject vo = new ValueObject(key(), value);
            valueObjects.add(vo);
            section.valueObjects.add(vo);
            return valueObjects.size() - 1;
        }
        /**
         * 
         * @param value 
         * @throws NullPointerException if the value of the given parameter is null
         */
        public void add(ValueObject value) {
            if ( value == null ) {
                throw new NullPointerException(BaseProperties.class.getName()
                    + ". Method add(int,ValueObject). Parameter 'value' cannot be null."
                    );
            }
            
            if (!value.isCommented() && !value.isDeleted()) {
                valueObjects.add(value);
            }
            section.valueObjects.add(value);
            value.setKey(this.key());
        }
        
        /**
         * Inserts the specified value at the specified position in the
         * internal collection.
         * 
         * @param idx - index at which the specified value is to be inserted
         * @param value - value to be inserted
         * 
         * @throws NullPointerException if the value of the given parameter is null
         * @throws ArrayIndexOutOfBoundsException if the value of the 
         * {@code idx} parameter is less then zero or exceeds the upper bound of
         * the internal collection
         * 
         */
        public void add(int idx, ValueObject value) {
            if ( value == null ) {
                throw new NullPointerException(BaseProperties.class.getName()
                    + ". Method add(int,ValueObject). Parameter 'value' cannot be null."
                    );
            }
            
            ValueObject vo = valueObjects.get(idx);
            
            if (vo == null) {
                section.valueObjects.add(value);
            } else {
                int sIdx = section.valueObjects.indexOf(vo);
                section.valueObjects.add(sIdx, value);
            }
            if (!value.isCommented() && !value.isDeleted()) {
                valueObjects.add(idx, value);
            }

        }

        /**
         * Removes an object of type {@link ValueObject} from the internal collection
         * at the specified index.
         * If an elements doesn't exist then the method does nothing.
         * The removed object is marked as deleted and still stays in the 
         * internal collection of the object of type {@link Section}
         *
         * @param idx an index of the object to be removed
         *
         * @throws ArrayIndexOutOfBoundsException in the same cases as
         * {@code java.util.List} does.
         */
        public void remove(int idx) {
            ValueObject vo = valueObjects.get(idx);
            if ( vo == null ) {
                return;
            }
            valueObjects.remove(idx);
            vo.markDeleted();
        }
        /**
         * Removes the specified object from the internal collection
         * 
         * The removed object is marked as deleted and still stays in the 
         * internal collection of the object of type {@link Section}
         * 
         * @param vo the object to be removed
         */
        public void remove(ValueObject vo) {
            valueObjects.remove(vo);
            vo.markDeleted();
        }
        /**
         * Returns an object of type {@code ValueObject} of the internal
         * collection.
         * If the object doesn't exist at the specified index than the method
         * returns {@code null}. 
         * 
         * @param idx the index of an object to return
         * @return an object at the specified position
         * @throws ArrayIndexOutOfBoundsException if the given index is out of
         * range ( must be >= 0 and < size() ) 
         */
        public ValueObject get(int idx) {
            return valueObjects.get(idx);
        }
        /**
         * Searches for the first object of type {@code ValueObject} in the internal 
         * collection by the specified value/
         * 
         * @param value the value to search for
         * @return the first found object with the given value or null if the 
         * object doesn't exist 
         * 
         * @throws NullPointerException if the value of the given parameter is null
         */
        public ValueObject getFirst(String value) {
            if ( value == null ) {
                throw new NullPointerException(BaseProperties.class.getName()
                    + ". Method add(int,ValueObject). Parameter 'value' cannot be null."
                    );
            }
            ValueObject retval = null;
            int idx = indexOf(value);
            if (idx >= 0) {
                retval = valueObjects.get(idx);
            }
            return retval;
        }
        /**
         * Returns the number of objects of type {@code ValueObject} of
         * the internal collection
         * 
         * @return the number of elements of the internal collection of objects
         * of type {@link ValueObject}
         */
        public int size() {
            return valueObjects.size();
        }
        /**
         * Checks whether the internal collection of objects of type 
         * {@code ValueObject} contains an element with the specified value.
         * 
         * @param value used to search in the internal collection  for the object
         *   of type ValueObject
         * @return true if the object exists. false - otherwise
         */
        public boolean valueExists(String value) {
            if ( value == null ) {
                return false;
            }
            return indexOf(value) >= 0;
        }
        /**
         * Returns the key value for which this instance is created
         * @return key value for which this instance is created.
         */
        public String key() {
            return key;
        }

        /**
         * Returns a List of all objects of type {@link ValueObject}.
         * Creates a new object of type {@code List<String> } ad add all elements
         * of the internal collection of objects of type {@code ValueObject}.
         * @return a list of all objects of type {@link ValueObject}.
         */
        public List<ValueObject> valueObjects() {
            List<ValueObject> list = new ArrayList<>();
            list.addAll(valueObjects);
            return list;
        }
        /**
         * Returns the position of the object of type {@code ValueObject} that
         *   has the specified value
         * 
         * @param value used to search for the object with such a value
         * @return the position of the object with the specified value 
         *    in the internal collection of objects of type ValueObject.
         *    -1 if the object doesn't exist
         */
        public int indexOf(String value) {
            return valueObjects.indexOf(new ValueObject(key, value));
        }

        /**
         * Extracts value from each object of type {@link ValueObject} and adds
         * it to the result array.
         *
         * @return an array of string values. Return an empty array if the
         *  internal collection is empty
         */
        public String[] values() {
            List<String> list = new ArrayList<>();
            valueObjects.forEach(v -> {
                list.add(v.value());
            });
            
            return list.toArray(new String[0]);
        }

        /**
         * Returns a string representations of the array of all objects of type
         * {@link ValueObject}.
         * 
         * 
         *
         * @return a string representations of the array of all objects of type
         * {@link ValueObject}. If the internal collection is empty than 
         *  {@code null} is returned. If the internal collection has a single 
         * element than the result is a simple string and is not encoded 
         * as array.
         * @see #encodeArray(java.lang.String...) 
         * @see #decodeArray(java.lang.String) 
         * @see #isEncodedArray(java.lang.String) 
         */
        public String value() {
            if (valueObjects.isEmpty()) {
                return null;
            } else if (valueObjects.size() == 1) {
                return valueObjects.get(0).value();
            }
            String[] ar = new String[valueObjects.size()];
            for (int i = 0; i < valueObjects.size(); i++) {
                ar[i] = valueObjects.get(i).value();
            }

            return encodeArray(ar);
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 97 * hash + Objects.hashCode(this.key);
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
            final KeyValue other = (KeyValue) obj;
            if (!Objects.equals(this.key, other.key)) {
                return false;
            }
            if (!Objects.equals(this.section.name(), other.section.name())) {
                return false;
            }

            return true;
        }

        @Override
        public String toString() {
            return valueObjects.toString();
        }
    }//class KeyValue

    public static class ValueObject {

        private final List<String> comments = new ArrayList<>();
        private String value;
        private boolean deleted;
        private boolean commented;

        private String key;

        public ValueObject(String key, String value) {
            this.value = value;
            this.key = key;
            init();
        }

        protected ValueObject(String key, String value, boolean deleted, boolean commented) {
            this.value = value;
            this.key = key;
            this.deleted = deleted;
            this.commented = commented;
            init();
        }

        private void init() {
        }

        public boolean isDeleted() {
            return deleted;
        }

        public void markDeleted() {
            this.deleted = true;
        }

        public boolean isCommented() {
            return commented;
        }

        public void uncomment() {
            this.commented = false;
        }

        public void markCommented() {
            this.commented = true;
        }

        public String key() {
            return key;
        }

        protected void setKey(String key) {
            this.key = key;
        }

        public void replaceValue(String value) {
            this.value = value;
        }

        public List<String> comments() {
            return comments;
        }

        public void setComments(String... comments) {
            this.comments.clear();
            if ( comments == null ) {
                return;
            }
            for (int i = 0; i < comments.length; i++) {
                if (!comments[i].startsWith("#")) {
                    if (!comments[i].startsWith("# ")) {
                        comments[i] = "# " + comments[i];
                    } else {
                        comments[i] = "#" + comments[i];
                    }
                }
            }
            Collections.addAll(this.comments, comments);
            if (comments.length == 0 || comments.length > 0 && !comments[0].trim().isEmpty()) {
                this.comments.add(0, "");
            }
        }

        public boolean isPropertyArray() {
            return value.startsWith("\\" + System.lineSeparator());
        }

        public String value() {
            return value;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 97 * hash + Objects.hashCode(this.value);
            hash = 97 * hash + (this.deleted ? 1 : 0);
            hash = 97 * hash + (this.commented ? 1 : 0);
            hash = 97 * hash + Objects.hashCode(this.key);
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
            final ValueObject other = (ValueObject) obj;
            if (this.deleted != other.deleted) {
                return false;
            }
            if (this.commented != other.commented) {
                return false;
            }
            if (!Objects.equals(this.value, other.value)) {
                return false;
            }
            if (!Objects.equals(this.key, other.key)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return value;
        }

    }//class ValueObject

    public static class CommentHelper {

        private final static Pattern keyValuePattern = Pattern.compile("\\s*([^=]*)=(.*)");

        /**
         * Returns comment lines of all fragments except the last one. If the
         * source list has zero ore one fragment then the method returns an
         * empty list.
         *
         * @param source the list to create fragmentsExceptLast
         * @return a list of string
         */
        public static List<String> fragmentsExceptLast(List<String> source) {
            List<String> retval = new ArrayList<>();
            List<String[]> f = split(source);
            if (f.size() > 1) {
                retval = join(f.subList(0, f.size() - 1));
            }
            return retval;
        }

        public static List<String> lastFragment(List<String> source) {
            List<String> retval = new ArrayList<>();
            List<String[]> f = split(source);
            if (f.size() >= 1) {
                retval = join(f.subList(f.size() - 1, f.size()));
            }
            return retval;
        }

        public static List<String[]> split(List<String> comments, int lineIdx) {
            List<String[]> fs = split(comments);
            int leftIdx = 0;
            List<String[]> retval = new ArrayList<>();
            int idx = -1;
            for (int i = 0; i < fs.size(); i++) {
                int rightIdx = leftIdx + fs.get(i).length - 1;
                if (lineIdx >= leftIdx && lineIdx <= rightIdx) {
                    idx = i;
                    break;
                }
                leftIdx += fs.get(i).length;
            }
            if (idx < 0) {
                return retval;
            }
            String[] f = fs.get(idx);
            int fIdx = lineIdx - leftIdx; // an index of line in the fragment
            List<String> upperPart = new ArrayList<>();
            for (int i = 0; i < idx; i++) {
                for (String s : fs.get(i)) {
                    upperPart.add(s);
                }
            }
            for (int i = 0; i < fIdx; i++) {
                upperPart.add(f[i]);
            }
            List<String> lowerPart = new ArrayList<>();
            for (int i = fIdx + 1; i < f.length; i++) {
                lowerPart.add(f[i]);
            }
            for (int i = idx + 1; i < fs.size(); i++) {
                for (String s : fs.get(i)) {
                    lowerPart.add(s);
                }
            }
            retval.add(upperPart.toArray(new String[0]));
            retval.add(lowerPart.toArray(new String[0]));

            return retval;
        }

        public static List<String> join(List<String[]> source) {
            List<String> retval = new ArrayList<>();
            source.forEach(ar -> {
                for (String line : ar) {
                    retval.add(line);
                }
            });
            return retval;
        }

        public static List<String[]> split(List<String> source) {
            List<String[]> retval = new ArrayList<>();
            List<String> list = new ArrayList<>();

            List<String> src = source.subList(0, source.size());

            while (true) {
                int n = -1;

                for (int i = 0; i < src.size(); i++) {
                    if (src.get(i).trim().isEmpty()) {
                        list.add(src.get(i));
                    } else {
                        n = i;
                        break;
                    }
                }

                if (n < 0) {
                    //
                    // No not empty string found
                    //
                    if (!list.isEmpty()) {
                        retval.add(list.toArray(new String[0]));
                    }
                    break;
                }
                //
                // Found not empty string with an index equals to n
                //
                if (n == src.size() - 1) {
                    list.add(src.get(n));
                    retval.add(list.toArray(new String[0]));
                    break;
                }
                //
                // We'll try find the first empty string if exists
                //
                int start = n;
                n = -1;
                for (int i = start; i < src.size(); i++) {
                    if (!src.get(i).trim().isEmpty()) {
                        list.add(src.get(i));
                    } else {
                        n = i;
                        break;
                    }
                }
                retval.add(list.toArray(new String[0]));
                if (n < 0) {
                    break;
                }
                list.clear();
                src = src.subList(n, src.size());
            }//while
            return retval;
        }

        public static int indexOf(List<String> comments, String key, String value) {
            int retval = -1;
            Pattern p = Pattern.compile("\\s*([^=]*)=(.*)");
            for (int i = 0; i < comments.size(); i++) {
                String comment = comments.get(i);
                String[] kv = getKeyValue(comment);
                if (kv != null) {
                    String k = kv[0];
                    String v = kv[1];
                    if (key.equals(k) && value.equals(v)) {
                        retval = i;
                        break;
                    }
                }
            }
            return retval;
        }

        public static boolean isCommentedKeyValue(String line) {
            // 
            // Minimum characters:
            // # k=
            //
            if (!line.startsWith("# ") || line.length() < 4) {
                return false;
            }
            Pattern p = Pattern.compile("\\s*([^=]*)=(.*)");
            String s = line.substring(2);
            Matcher m = p.matcher(s);
            if (!m.matches()) {
                return false;
            }
            if (m.group(1).trim().isEmpty()) {
                return false;
            }
            String key = m.group(1);
            Pattern keyPattern = Pattern.compile("[a-zA-Z\\-_][a-zA-Z\\-_0-9.]*");
            m = keyPattern.matcher(key);
            return m.matches();
        }

        public static String[] getKeyValue(String line) {
            // 
            // Minimum characters:
            // # k=
            //
            if (!line.startsWith("# ") || line.length() < 4) {
                return null;
            }
            Pattern p = Pattern.compile("\\s*([^=]*)=(.*)");
            String s = line.substring(2);
            Matcher m = p.matcher(s);
            if (!m.matches()) {
                return null;
            }
            if (m.group(1).trim().isEmpty()) {
                return null;
            }
            String key = m.group(1);
            Pattern keyPattern = Pattern.compile("[a-zA-Z\\-_][a-zA-Z\\-_0-9.]*");
            Matcher mkey = keyPattern.matcher(key);
            if (!mkey.matches()) {
                return null;
            }
            String[] kv = new String[2];
            kv[0] = m.group(1);
            kv[1] = m.group(2);
            return kv;
        }

        public static List<String> list(String... comments) {
            List<String> list = new ArrayList<>();
            Collections.addAll(list, comments);
            return list;
        }
    }//class CommentHelper
}//class BaseProperties
