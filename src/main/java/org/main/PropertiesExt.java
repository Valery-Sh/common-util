package org.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Valery Shyshkin
 */
public class PropertiesExt {

    private static final Logger LOG = Logger.getLogger(PropertiesExt.class.getName());

    protected static String START = "{" + System.lineSeparator();
    protected static String END = System.lineSeparator() + "}";

    private static final String KEY_COMMENT = "[e139bab1-1662-41c1-9a3b-74c203e8a8e6]";

    public static final String HEADER_COMMENT = "[header-e139bab1-1662-41c1-9a3b-74c203e8a8e6]";
    public static final String BOTTOM_COMMENT = "[bottom-e139bab1-1662-41c1-9a3b-74c203e8a8e6]";
    public static final String SECTION_COMMENT = "[section-e139bab1-1662-41c1-9a3b-74c203e8a8e6]";

//    private final Map<String, Map<Integer,String[]>> keyValueComments = new HashMap<>();
    
    private final Map<String, String[]> keyComments = new HashMap<>();
    private final Map<String, String> keyValues = new HashMap<>();

    private String[] sectionComments = new String[0];
    private String[] headerComments = new String[0];
    private String[] bottomComments = new String[0];

    private final Pattern _keyValue = Pattern.compile("\\s*([^=]*)=(.*)");
    private final List<String> _bottomComments = new ArrayList<>();
    private final List<String> _headerComments = new ArrayList<>();
    private final List<String> _sectionComments = new ArrayList<>();

    private String sectionName;

    public PropertiesExt() {
    }

    public PropertiesExt(String sectionName) {
        this.sectionName = sectionName;
    }

    public void put(String key, String value) {
        if (key.startsWith(KEY_COMMENT) || SECTION_COMMENT.equals(key)
                || HEADER_COMMENT.equals(key) || BOTTOM_COMMENT.equals(key)) {
            String[] a = splitValue(value);
            defineAndPutComments(key, a);
        } else {
            keyValues.put(key, value);
        }
    }

    public void putAll(PropertiesExt properties) {
        keyValues.putAll(properties.keyValues);
        //sectionName = properties.getSectionName();
    }

    public String getSectionName() {
        return sectionName;
    }

/*    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }
*/
    public String get(String key) {
        String retval;
        if (key.startsWith(KEY_COMMENT) || SECTION_COMMENT.equals(key)
                || HEADER_COMMENT.equals(key) || BOTTOM_COMMENT.equals(key)) {
            retval = joinValues(defineAndGetComments(key));
        } else {
            retval = keyValues.get(key);
        }
        return retval;
    }

    public String getProperty(String key) {
        return get(key);
    }

    public void setProperty(String key, String value) {
        put(key, value);
    }

    public void putHeaderComments(String... comments) {
        this.headerComments = Arrays.copyOf(comments, comments.length);
    }

    public void putBottomComments(String... comments) {
        this.bottomComments = Arrays.copyOf(comments, comments.length);
    }

    public String[] getHeaderComments() {
        return Arrays.copyOf(headerComments, headerComments.length);
    }

    public String[] getBottomComments() {
        return Arrays.copyOf(bottomComments, bottomComments.length);
    }

    public String[] getComments(String key) {
        String[] retval = new String[0];
        if (keyComments.get(key) != null) {
            retval = Arrays.copyOf(keyComments.get(key), keyComments.get(key).length);
        }
        return retval;
    }

    public void putComments(String key, String... comments) {
        String[] a = Arrays.copyOf(comments, comments.length);
        keyComments.put(key, a);
    }

    protected void defineAndPutComments(String key, String... comments) {
        if (comments == null) {
            return;
        }
        if (key.startsWith(KEY_COMMENT)) {
            String k = key.substring(KEY_COMMENT.length());
            String[] a = Arrays.copyOf(comments, comments.length);
            keyComments.put(k, a);
        } else if (HEADER_COMMENT.equals(key)) {
            this.headerComments = Arrays.copyOf(comments, comments.length);
        } else if (BOTTOM_COMMENT.equals(key)) {
            this.bottomComments = Arrays.copyOf(comments, comments.length);
        } else if (SECTION_COMMENT.equals(key)) {
            this.sectionComments = Arrays.copyOf(comments, comments.length);
        }
    }

    protected String[] defineAndGetComments(String key) {
        String[] retval = new String[0];

        if (key.startsWith(KEY_COMMENT)) {
            String k = key.substring(KEY_COMMENT.length());
            if (keyComments.get(k) != null) {
                retval = Arrays.copyOf(keyComments.get(k), keyComments.get(k).length);
            }
        } else if (HEADER_COMMENT.equals(key)) {
            retval = Arrays.copyOf(headerComments, headerComments.length);
        } else if (BOTTOM_COMMENT.equals(key)) {
            retval = Arrays.copyOf(bottomComments, bottomComments.length);
        } else if (SECTION_COMMENT.equals(key)) {
            retval = Arrays.copyOf(sectionComments, sectionComments.length);
        }

        return retval;
    }

    public static String commentKey(String key) {
        if (key == null) {
            return null;
        }
        if (key.startsWith(KEY_COMMENT)) {
            return key;
        }
        return KEY_COMMENT + key;

    }

    public void load(InputStream stream) throws IOException {

        try (BufferedReader br = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            List<String> commentLines = new ArrayList<>();

            boolean writeEndComments = false;
            //
            // All first headerComments until first blank line
            //
            boolean isHeaderLine = true;

            while ((line = br.readLine()) != null) {
                if (!isHeaderLine && (line.trim().isEmpty() || line.trim().startsWith("#"))) {
                    commentLines.add(line);
                    if (writeEndComments) {
                        _bottomComments.add(line);
                    }
                    continue;
                }

                if (isHeaderLine) {
                    if (line.trim().isEmpty() || line.trim().startsWith("#")) {
                        _headerComments.add(line);
                        continue;
                    }
                    int last = _headerComments.size() - 1;
                    if (!_headerComments.isEmpty() && !_headerComments.get(last).trim().isEmpty()) {
                        //
                        // Last line is not an empty string => we 
                        // accept that it is a first key/value comment
                        // Search back for a first blank line
                        //
                        int blank = -1;
                        for (int i = last; i >= 0; i--) {
                            if (_headerComments.get(i).trim().isEmpty()) {
                                blank = i;
                                break;
                            }
                        }
                        if (blank > 0) {
                            commentLines.addAll(_headerComments.subList(blank, last + 1));
                            headerComments = _headerComments.subList(0, blank).toArray(headerComments);
                        } else {
                            commentLines.addAll(_headerComments);
                            headerComments = _headerComments.toArray(headerComments);
                        }
                        _headerComments.clear();
                    } else {
                        headerComments = _headerComments.toArray(headerComments);
                        _headerComments.clear();

                    }
                }

                writeEndComments = true;
                isHeaderLine = false;

                Matcher m = _keyValue.matcher(line);

                m = _keyValue.matcher(line);
                if (m.matches()) {
                    String key = m.group(1).trim();
                    String value = m.group(2).trim();
                    if ("\\".equals(value)) {
                        value = loadArray(key, br);
                    }
                    keyValues.put(key, value);
                    defineAndPutComments(commentKey(key), commentLines.toArray(new String[0]));

                    commentLines.clear();
                    _bottomComments.clear();
                }
            }//while

            bottomComments = _bottomComments.toArray(bottomComments);

        }//try//try

    }

    protected String loadArray(String key, BufferedReader br) throws IOException {
        String line = null;
        List<String> values = new ArrayList<>();

        while ((line = br.readLine()) != null) {
            Matcher m = _keyValue.matcher(line);
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

        return joinValues(values.toArray(new String[0]));
    }

    private int keyValueCommentsSize() {
        int retval = 0;
        for (String[] v : keyComments.values()) {
            retval += v.length;
        }
        return retval;
    }

    private int remainLineCount(String type) {
        int retval = 0;
        switch (type) {
            case KEY_COMMENT:
                retval = bottomComments.length + keyValueCommentsSize() + keyValues.size();
                break;
            case HEADER_COMMENT:
                retval = sectionComments.length + keyValues.size() + bottomComments.length + keyValueCommentsSize() + headerComments.length;
                break;
            case BOTTOM_COMMENT:
                retval = bottomComments.length;
                break;
            case SECTION_COMMENT:
                retval = sectionComments.length + keyValues.size() + bottomComments.length + keyValueCommentsSize();
                break;
        }
        return retval;
    }

    public void store(OutputStream stream) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(stream))) {
            //
            // store header comments
            //
            int count = remainLineCount(HEADER_COMMENT);
            for (String c : headerComments) {
                String sep = System.lineSeparator();
                if (--count == 0) {
                    sep = "";
                }
                bw.write(c + sep);
            }
            //
            // store key/values
            //
            count = remainLineCount(KEY_COMMENT);
            for (Map.Entry<String, String> en : keyValues.entrySet()) {
                String k = en.getKey();
                String v = en.getValue();
                //
                // store key/value comments
                //
                for (String c : getComments(k)) {
                    String sep = System.lineSeparator();
                    if (--count == 0) {
                        sep = "";
                    }
                    bw.write(c + sep);
                }
                String sep = System.lineSeparator();
                if (--count == 0) {
                    sep = "";
                }
                bw.write(k + "=" + v + sep);
            }
            //
            // store bottom comments
            //
            count = remainLineCount(BOTTOM_COMMENT);
            for (String c : bottomComments) {
                String sep = System.lineSeparator();
                if (--count == 0) {
                    sep = "";
                }
                bw.write(c + sep);
            }
        }
    }

/*    public void saveSection(BufferedWriter bw) throws IOException {
        for (Map.Entry<String, String> en : keyValues.entrySet()) {
            String k = en.getKey();
            String v = en.getValue();

            for (String c : getComments(k)) {
                bw.write(c + System.lineSeparator());
            }
            bw.write(k + "=" + v + System.lineSeparator());
        }
    }
*/
    public Set<String> stringPropertyNames() {
        return keyValues.keySet();
    }

    public void remove(String key) {
        if (KEY_COMMENT.equals(key)) {
            headerComments = new String[0];
        } else if (key.startsWith(KEY_COMMENT)) {
            String k = key.substring(KEY_COMMENT.length());
            keyComments.remove(k);
        } else {
            keyValues.remove(key);
            keyComments.remove(key);
        }
    }

    /**
     * Clears removes all key/value pairs and all comments including header
     * comments, bottom headers and section headers.
     */
    public void clear() {
        keyValues.clear();
        keyComments.clear();
        headerComments = new String[0];
        bottomComments = new String[0];
        sectionComments = new String[0];
    }

    /**
     * Clears removes all key/value pairs. Header comments, bottom headers and
     * section headers keeps uncleared.
     */
    public void clearKeys() {
        keyValues.clear();
        keyComments.clear();
    }

    public void forEach(BiConsumer<String, String> consumer) {
        keyValues.forEach(consumer);
    }

    public boolean isEmpty() {
        return keyValues.isEmpty();
    }

    public int size() {
        return keyValues.size();
    }

    public static boolean isJoinValues(String value) {
        if (value == null) {
            return false;
        }

        String sep = "\\" + System.lineSeparator();
        //
        // !!! First symbol cannot be a space character for the string to be a String
        // array View.
        //
        if (!value.startsWith(sep)) {
            return false;
        }

        return true;
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

    public static String[] test(String value) {
        String[] retval = null;
        return retval;
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

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.keyComments);
        hash = 53 * hash + Objects.hashCode(this.keyValues);
        hash = 53 * hash + Arrays.deepHashCode(this.sectionComments);
        hash = 53 * hash + Arrays.deepHashCode(this.headerComments);
        hash = 53 * hash + Arrays.deepHashCode(this.bottomComments);
        hash = 53 * hash + Objects.hashCode(this._bottomComments);
        hash = 53 * hash + Objects.hashCode(this.sectionName);
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
        final PropertiesExt other = (PropertiesExt) obj;
        if (!Objects.equals(this.keyValues, other.keyValues)) {
            return false;
        }
        return true;
    }

/*    public boolean deepEquals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PropertiesExt other = (PropertiesExt) obj;
        if (!Objects.equals(this.sectionName, other.sectionName)) {
            return false;
        }
        if (!Objects.equals(this.keyComments, other.keyComments)) {
            return false;
        }
        if (!Objects.equals(this.keyValues, other.keyValues)) {
            return false;
        }
        if (!Arrays.deepEquals(this.sectionComments, other.sectionComments)) {
            return false;
        }
        if (!Arrays.deepEquals(this.headerComments, other.headerComments)) {
            return false;
        }
        if (!Arrays.deepEquals(this.bottomComments, other.bottomComments)) {
            return false;
        }
        return true;
    }
*/
    public static class PropertyObject {

        private final String stringValue;
        private final List<String> listValue = new ArrayList<>();
        private boolean removeDublicates;

        public PropertyObject(String stringValue) {
            this.stringValue = stringValue;
            init();
        }

        private void init() {
            if (stringValue == null) {
                return;
            }

            if (!isArray()) {
                listValue.add(stringValue);
            } else {
                Collections.addAll(listValue, PropertiesExt.decodeArray(stringValue));
            }
        }

        public boolean isArray() {
            return PropertiesExt.isEncodedArray(stringValue);
        }

        public List<String> list() {
            return listValue;
        }

        public String value() {
            if (removeDublicates) {
                removeDublicateValues();
            }
            String[] a = listValue.toArray(new String[0]);
            if (a.length == 0) {
                return null;
            }
            if (a.length == 1) {
                return a[0];
            }
            return PropertiesExt.encodeArray(a);
        }

        public void removeDublicateValues() {
            removeDublicates = true;
            List<String> list = new ArrayList<>(listValue);
            listValue.clear();
            list.forEach(v -> {
                if (!listValue.contains(v)) {
                    listValue.add(v);
                }
            });
        }

    }//class PropertyObject

    public class PropertyArray {

        private final String stringValue;
        private final List<String> listValue = new ArrayList<>();
        private boolean removeDublicates;

        public PropertyArray(String stringValue) {
            this.stringValue = stringValue;
            init();
        }

        private void init() {
            if (stringValue == null) {
                return;
            }
            if (!isArray()) {
                listValue.add(stringValue);
            } else {
                Collections.addAll(listValue, PropertiesExt.splitValue(stringValue));
            }
        }

        public boolean isArray() {
            return PropertiesExt.isJoinValues(stringValue);
        }

        public List<String> list() {
            return listValue;
        }

        public String value() {
            if (removeDublicates) {
                removeDublicateValues();
            }
            String[] a = listValue.toArray(new String[0]);
            if (a.length == 0) {
                return null;
            }
            if (a.length == 1 && !isArray()) {
                return a[0];
            }
            return PropertiesExt.joinValues(a);
        }

        public void removeDublicateValues() {
            removeDublicates = true;
            List<String> list = new ArrayList<>(listValue);
            listValue.clear();
            list.forEach(v -> {
                if (!listValue.contains(v)) {
                    listValue.add(v);
                }
            });
        }

    }// class PropertyArray

}//class PropertiesExt
