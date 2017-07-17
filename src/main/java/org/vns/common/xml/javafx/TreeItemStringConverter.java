package org.vns.common.xml.javafx;

import java.io.ByteArrayInputStream;
import java.util.Properties;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TreeItem;
import javafx.util.StringConverter;
import org.vns.common.xml.AbstractCompoundXmlElement;
import org.vns.common.xml.XmlAttributes;
import org.vns.common.xml.XmlChilds;
import org.vns.common.xml.XmlDocument;
import org.vns.common.xml.XmlElement;
import org.vns.common.xml.XmlRoot;

/**
 *
 * @author Valery
 */
public class TreeItemStringConverter extends StringConverter<TreeItem<Properties>> {

    public static final String FIELD_NAME_ATTR = "ld:fieldName";
    public static final String CLASS_NAME_ATTR = "ld:className";
    public static final String TAG_NAME_ATTR = "ld:tagName";
    public static final String TREEITEM_ATTR = "ignore:treeItem";
    //public static final String REGSTERED_ATTR = "ld:registered";
    public static final String ISDOCKABLE_ATTR = "ld:isdockable";
    public static final String ISDOCKTARGET_ATTR = "ld:isdocktarget";

    @Override
    public String toString(TreeItem<Properties> treeItem) {
        StringBuilder sb = new StringBuilder();
        append(treeItem, sb);
        //sb.append(System.lineSeparator());

        System.err.println("size = " + treeItem.getChildren().size());
        /*        treeItem.getChildren().forEach(it -> {
            append(it, sb);
            sb.append(System.lineSeparator());
        });
         */
        sb.append(System.lineSeparator());
        sb.append("</")
                .append(treeItem.getValue().getProperty(TAG_NAME_ATTR))
                .append(">");
        //sb.append(System.lineSeparator());

        return sb.toString();
    }

    public void append(TreeItem<Properties> treeItem, StringBuilder sb) {
        Properties props = treeItem.getValue();
        sb.append(System.lineSeparator());
        sb.append("<");
        sb.append(props.getProperty(TAG_NAME_ATTR))
                .append(" ");
        props.forEach((k, v) -> {
            if (!((String) k).startsWith("ignore:")) {
                sb.append((String) k)
                        .append("='")
                        .append((String) v)
                        .append("' ");

            }
        });
        sb.append("> ");
        treeItem.getChildren().forEach(it -> {
            append(it, sb);
            sb.append(System.lineSeparator());
            sb.append("</")
                    .append(it.getValue().getProperty(TAG_NAME_ATTR))
                    .append(">");

        });
    }

    @Override
    public TreeItem<Properties> fromString(String strValue) {
        String str = "<root>" + strValue + "</root>";
        //String str = strValue;
        TreeItem<Properties> item = new TreeItem<>();
        Properties props = new Properties();
        item.setValue(props);
        item.setExpanded(true);
        props.put(TREEITEM_ATTR, item);
        
        XmlDocument doc = new XmlDocument(new ByteArrayInputStream(str.getBytes()));
        XmlRoot root = new XmlRoot(doc);
        XmlChilds rootChilds = root.getChilds();
        XmlElement elem = rootChilds.get(0);
        XmlAttributes attrs = elem.getAttributes();
        attrs.toMap().forEach((k, v) -> {
            item.getValue().setProperty(k, v);
        });

        if (elem instanceof AbstractCompoundXmlElement) {
            ((AbstractCompoundXmlElement) elem).getChilds().list().forEach(el -> {
                TreeItem<Properties> it = new TreeItem<>();
                Properties p = new Properties();
                it.setValue(p);
                p.put(TREEITEM_ATTR, it);
                build(it, el);
                item.getChildren().add(it);
                it.setExpanded(true);
            });
        }

        return item;
    }

    protected void build(TreeItem<Properties> item, XmlElement el) {

        XmlAttributes attrs = el.getAttributes();
        attrs.toMap().forEach((k, v) -> {
            item.getValue().setProperty(k, v);
        });
        if (el instanceof AbstractCompoundXmlElement) {
            ((AbstractCompoundXmlElement) el).getChilds().list().forEach(e -> {
                TreeItem<Properties> it = new TreeItem<>();
                Properties p = new Properties();
                it.setValue(p);
                p.put(TREEITEM_ATTR, it);
                it.setExpanded(true);
                build(it, e);
                item.getChildren().add(it);
            });
        }
    }

    protected String buildTag(TreeItem<Properties> item, StringBuilder sb) {
        //sb.append(buildTag(item, sb));
        item.getChildren().forEach(it -> {
            sb.append(buildTag(it, sb));
        });
        return sb.toString();
    }

}
