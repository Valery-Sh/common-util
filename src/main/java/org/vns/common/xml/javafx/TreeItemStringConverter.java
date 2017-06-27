/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.common.xml.javafx;

import java.util.Properties;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.TreeItem;
import javafx.util.Pair;
import javafx.util.StringConverter;

/**
 *
 * @author Valery
 */
public class TreeItemStringConverter extends StringConverter<TreeItem<Pair<ObjectProperty, Properties>>> {

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
                .append(treeItem.getValue().getValue().getProperty("-ld:tagName"))
                .append(">");
        //sb.append(System.lineSeparator());

        return sb.toString();
    }

    public void append(TreeItem<Pair<ObjectProperty, Properties>> treeItem, StringBuilder sb) {
        Pair<ObjectProperty, Properties> pair = treeItem.getValue();
        sb.append(System.lineSeparator());
        sb.append("<");
        sb.append(pair.getValue().getProperty("-ld:tagName"))
                .append(" ");
        pair.getValue().forEach((k, v) -> {
            if (!((String) k).startsWith("-ignore:")) {
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
                    .append(it.getValue().getValue().getProperty("-ld:tagName"))
                    .append(">");

        });
    }

    @Override
    public TreeItem<Pair<ObjectProperty, Properties>> fromString(String strValue) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    protected String buildTag(TreeItem<Pair<ObjectProperty, Properties>> item, StringBuilder sb) {
        //sb.append(buildTag(item, sb));
        item.getChildren().forEach(it -> {
            sb.append(buildTag(it, sb));
        });
        return sb.toString();
    }

}
