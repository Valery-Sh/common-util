package org.vns.common.util.prefs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.event.ChangeListener;
import org.vns.common.util.prefs.files.PropertiesExt;

/**
 *
 * @author Valery
 */
public interface Storage {

    boolean isReadOnly();

    String[] childrenNames();

    boolean existsNode();

    void removeNode();

    void markModified();

    PropertiesExt load() throws IOException;

    void save(final PropertiesExt properties) throws IOException;

    default void runAtomic(Runnable run) {
        run.run();
    }

    default void attachChangeListener(ChangeListener changeListener) {

    }
    /**
     * If the given string is {@code null} then the result is {@code null}.
     * <p>
     * If the given string is {@code null} then the result is {@code null}.
     *
     * @param value
     * @return null if the property for the given key doesn't exist.
     */
    public static String[] toArray(String value) {

        if (value == null) {
            return null;
        }

        String sep = "\\" + System.lineSeparator();

        if (value.trim().length() < sep.length()) {
            return new String[]{value};
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
    public static String toString(String... values) {
        String dlm = "\\" + System.lineSeparator();
        if (values.length == 0) {
            return "";
        }
        return dlm + String.join(dlm, values);
    }

    public static String toString_old(String... values) {
        StringBuilder sb = new StringBuilder();
        String dlm = "\\" + System.lineSeparator();
        if (values.length > 0) {
          sb.append(dlm);
        }
        //sb.append(dlm);
        for (int i = 0; i < values.length; i++) {
            if (i == values.length - 1) {
                dlm = "";
            }
            sb.append(values[i])
                    .append(dlm);
        }
        return sb.toString();

    }

    public static String[] addAll(String[] array, String... values) {
        if (values.length == 0) {
            return array;
        }
        List<String> alist = Arrays.asList(array);
        List<String> list = new ArrayList<>(alist);
        list.addAll(Arrays.asList(values));
        return list.toArray(new String[0]);
    }
    public static String[] removeAll(String[] array, String... values) {
        if (values.length == 0) {
            return array;
        }
        List<String> alist = Arrays.asList(array);
        List<String> list = new ArrayList<>(alist);
        list.addAll(Arrays.asList(values));
        list.removeAll(Arrays.asList(values));
        return list.toArray(new String[0]);
    }
    public static String[] removeAll(String[] array, int... idx) {
        if (idx.length == 0) {
            return array;
        }
        List<String> alist = Arrays.asList(array);
        List<String> list = new ArrayList<>(alist);
        for ( int i : idx) {
            list.remove(i);
        }
        return list.toArray(new String[0]);
    }
    
    public static String[] insertTo(String[] array, int idx, String... values) {
        if (values.length == 0) {
            return array;
        }
        List<String> alist = Arrays.asList(array);
        List<String> list = new ArrayList<>(alist);
        list.addAll(idx,Arrays.asList(values));
        return list.toArray(new String[0]);
    }

    
}
