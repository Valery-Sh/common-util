package org.vns.common.xml.javafx;

import java.io.ByteArrayInputStream;
import java.util.Properties;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TreeItem;
import javafx.util.Pair;
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
public class TreeItemStringConverter extends StringConverter<TreeItem<Pair<ObjectProperty, Properties>>> {

    public static final String FIELD_NAME_ATTR = "ld:fieldName";
    public static final String CLASS_NAME_ATTR = "ld:className";
    public static final String TAG_NAME_ATTR = "ld:tagName";
    public static final String IGNORE_ATTR = "ignore:treeItem";
    public static final String REGSTERED_ATTR = "ld:registered";
    public static final String ISDOCKABLE_ATTR = "ld:isdockable";
    public static final String ISDOCKTARGET_ATTR = "ld:isdocktarget";

    @Override
    public String toString(TreeItem<Pair<ObjectProperty, Properties>> treeItem) {
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
                .append(treeItem.getValue().getValue().getProperty(TAG_NAME_ATTR))
                .append(">");
        //sb.append(System.lineSeparator());

        return sb.toString();
    }

    public void append(TreeItem<Pair<ObjectProperty, Properties>> treeItem, StringBuilder sb) {
        Pair<ObjectProperty, Properties> pair = treeItem.getValue();
        sb.append(System.lineSeparator());
        sb.append("<");
        sb.append(pair.getValue().getProperty(TAG_NAME_ATTR))
                .append(" ");
        pair.getValue().forEach((k, v) -> {
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
                    .append(it.getValue().getValue().getProperty(TAG_NAME_ATTR))
                    .append(">");

        });
    }

    @Override
    public TreeItem<Pair<ObjectProperty, Properties>> fromString(String strValue) {
        String str = "<root>" + strValue + "</root>";
        //String str = strValue;
        TreeItem<Pair<ObjectProperty, Properties>> item = new TreeItem<>();
        Pair<ObjectProperty, Properties> pair = new Pair<>(new SimpleObjectProperty(), new Properties());
        item.setValue(pair);
        item.setExpanded(true);
        pair.getValue().put(IGNORE_ATTR, item);
        
        XmlDocument doc = new XmlDocument(new ByteArrayInputStream(str.getBytes()));
        XmlRoot root = new XmlRoot(doc);
        XmlChilds rootChilds = root.getChilds();
        XmlElement elem = rootChilds.get(0);
        XmlAttributes attrs = elem.getAttributes();
        attrs.toMap().forEach((k, v) -> {
            item.getValue().getValue().setProperty(k, v);
        });

        if (elem instanceof AbstractCompoundXmlElement) {
            ((AbstractCompoundXmlElement) elem).getChilds().list().forEach(el -> {
                TreeItem<Pair<ObjectProperty, Properties>> it = new TreeItem<>();
                Pair<ObjectProperty, Properties> p = new Pair<>(new SimpleObjectProperty(), new Properties());
                it.setValue(p);
                p.getValue().put(IGNORE_ATTR, it);
                build(it, el);
                item.getChildren().add(it);
                it.setExpanded(true);
            });
        }

        return item;
    }

    protected void build(TreeItem<Pair<ObjectProperty, Properties>> item, XmlElement el) {

        XmlAttributes attrs = el.getAttributes();
        attrs.toMap().forEach((k, v) -> {
            item.getValue().getValue().setProperty(k, v);
        });
        if (el instanceof AbstractCompoundXmlElement) {
            ((AbstractCompoundXmlElement) el).getChilds().list().forEach(e -> {
                TreeItem<Pair<ObjectProperty, Properties>> it = new TreeItem<>();
                Pair<ObjectProperty, Properties> p = new Pair<>(new SimpleObjectProperty(), new Properties());
                it.setValue(p);
                p.getValue().put(IGNORE_ATTR, it);
                it.setExpanded(true);
                build(it, e);
                item.getChildren().add(it);
            });
        }
    }

    protected String buildTag(TreeItem<Pair<ObjectProperty, Properties>> item, StringBuilder sb) {
        //sb.append(buildTag(item, sb));
        item.getChildren().forEach(it -> {
            sb.append(buildTag(it, sb));
        });
        return sb.toString();
    }

}
