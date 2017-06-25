package org.vns.common.util.prefs.files;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.logging.Logger;

/**
 *
 * @author Valery Shyshkin
 */
public class PropertiesExt extends BaseProperties {

    private static final Logger LOG = Logger.getLogger(PropertiesExt.class.getName());

    private String sectionName;

    public PropertiesExt() {
        sectionName = DEFAULT_SECTION;
    }

    protected PropertiesExt(String sectionName) {
        this.sectionName = sectionName;
    }

    public void forEach(BiConsumer<String, String> consumer) {
        section(sectionName).keySet().forEach(k -> {
            consumer.accept(k, section(sectionName).keyValue(k).value());
        });
    }

    public KeyValue addProperties(String key, String... values) {
        return section(sectionName).addProperties(key, values);
    }

    public int addProperty(String key, String value) {
        return section(sectionName).addProperty(key, value);
    }

    public String getProperty(String key) {
        return section(sectionName).getProperty(key);
    }

    public String[] get(String key) {
        return section(sectionName).get(key);
    }
    
    public void put(String key, String... values) {
        section(sectionName).put(key, values);
    }

    public void setProperty(String key, String value) {
        section(sectionName).setProperty(key, value);
    }

    public void remove(String key) {
        section(sectionName).remove(key);
    }

    /**
     * Copies key/values and comments
     *
     * @param properties the copy source
     */
    public void copyAll(PropertiesExt properties) {
        section(sectionName).copyAll(properties.section(properties.getSectionName()));
    }
    public void merge(PropertiesExt properties) {
        section(sectionName).merge(properties.section(properties.getSectionName()));
    }

    /**
     * Copies key/values but not section comments
     *
     * @param properties the copy source
     */
    public void putAll(PropertiesExt properties) {
        section(sectionName).putAll(properties.section(properties.getSectionName()).keyValues());
    }

    public String getSectionName() {
        return sectionName;
    }

    /*    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }
     */
    public Set<String> stringPropertyNames() {
        return section(sectionName).keySet();
    }

    /**
     * Clears removes all key/value pairs and all comments including header
     * comments, bottom headers and section headers.
     */
    public void clear() {
        iniFile().clear();
    }

    /**
     * Clears removes all key/value pairs. Header comments, bottom headers and
     * section headers keeps uncleared.
     */
    public void clearKeys() {
        iniFile().clearKeys();
    }

    public boolean isEmpty() {
        return section(sectionName).keyValues().isEmpty();
    }
/*    public boolean hasComments() {
        return section(sectionName).keyValues().isEmpty();
    }
*/
    public int size() {
        return section(sectionName).keyValues().size();
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
