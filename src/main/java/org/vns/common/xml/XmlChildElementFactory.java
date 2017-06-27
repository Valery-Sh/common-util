package org.vns.common.xml;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import org.w3c.dom.Element;

/**
 *
 * @author Valery Shyshkin
 */
public class XmlChildElementFactory {

    private final XmlCompoundElement xmlParent;

    public XmlChildElementFactory(XmlCompoundElement xmlParent) {
        this.xmlParent = xmlParent;
    }

    public XmlElement createXmlElement(Element domElement) {
        XmlElement element;
        element = createInstance(domElement);

        return element;
    }

    protected XmlElement createInstance(Element domElement) {
        XmlElement element;

        String className = getClassName(domElement);
        if (className == null) {
            return null;
        }
        try {
            Class<?> clazz = Class.forName(className);
            Constructor<?> ctor = clazz.getDeclaredConstructor(Element.class, XmlCompoundElement.class);
            ctor.setAccessible(true);
            element = (XmlElement) ctor.newInstance(new Object[]{domElement, xmlParent});
            //newInstance(ctor, new Object[]{domElement, xmlParent});
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalArgumentException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            element = null;
        }
        return element;
    }

    /*    protected String relativePath(List<XmlElement> list) {
        //String path = "";
        StringBuilder pathBuilder = new StringBuilder();
        String slash = "/";
        for (int i = 0; i < list.size(); i++) {
            if (i == list.size() - 1) {
                slash = "";
            }
            pathBuilder
                    .append(list.get(i).getTagName());
            pathBuilder.append(slash);
        }
        return pathBuilder.toString();
    }
     */
    protected String getClassName(Element domElement) {

        String result = "";
        String tagName = domElement.getTagName();
        XmlCompoundElement el = xmlParent;
        String path = tagName;

        while (true) {

            XmlTagMap map = el.getTagMap();
            if (map.get(path) != null) {
                // found class
                result = map.get(path);
                break;
            } else if (map.getDefaultClass() != null) {
                // found default class
                result = map.getDefaultClass();
                break;
            }

            if (el.getParent() == null) {
                break;
            }

            path = el.getTagName() + "/" + path;
            el = el.getParent();

        }// while

        return result;
    }

    /*    protected String getClassName(Element domElement) {
        String className = null;
//        if ( true ) {
//            className = findElementClass(xmlParent, domElement.getTagName());
//            return className;
//        }
        className = xmlParent.getTagMap().get(domElement.getTagName());
        
        if (className == null) {
            // try get className from the root
            List<XmlCompoundElement> parentChain = new ArrayList<>();
            parentChain.add(xmlParent);
            className = getClassNameFromParent(parentChain, domElement);
        }
        
        
        if ( className == null ) {
            if ( xmlParent.getTagMap().getDefaultClass() != null ) {
                className = xmlParent.getTagMap().getDefaultClass();
            } else {
                className = getDefaultClassNameFromParent(xmlParent);
            }
        }
        
        
        return className;
    }
     */
/*    protected String getDefaultClassNameFromParent(XmlCompoundElement el) {
        String className = null;

        if (el.getParent() == null) {
            return null;
        }

        className = el.getParent().getTagMap().getDefaultClass();

        if (className == null) {
            className = getDefaultClassNameFromParent(el.getParent());
        }

        return className;
    }

    protected String getClassNameFromParent(List<XmlCompoundElement> parentChain, Element domElement) {
        String className = null;

        XmlCompoundElement xmlEl = parentChain.get(0);

        if (xmlEl.getParent() == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parentChain.size(); i++) {
            sb.append(parentChain.get(i).getTagName())
                    .append("/");
        }
        sb.append(domElement.getTagName());

        className = xmlEl.getParent().getTagMap().get(sb.toString());
        if (className == null) {
            parentChain.add(0, xmlEl.getParent());
            className = getClassNameFromParent(parentChain, domElement);
        }

        return className;
    }
*/
    protected String getClassNameFromRoot(Element domElement) {
        String className = null;
        //
        // find XmlBase
        //
        XmlRoot root = (XmlRoot) XmlBase.findXmlRoot(xmlParent);
        if (root != null && root.getTagMap() != null && !root.getTagMap().isEmpty()) {
            //
            // parentList includes all elements starting from root and 
            // ending with domElement
            //
            List<Element> parentList = XmlDocument.getParentChainList(domElement);

            if (parentList.isEmpty()) {
                return null; // something wrong. Throw exception ???
            }
            if (parentList.size() == 1) {
                return null; // domElement is a root element
            }

            //
            // Create path relative to the root
            //
            StringBuilder pathBuilder = new StringBuilder();
            String slash = "/";
            for (int i = 1; i < parentList.size(); i++) {
                if (i == parentList.size() - 1) {
                    slash = "";
                }

                pathBuilder
                        .append(parentList.get(i).getTagName())
                        .append(slash);
            }

            String path = pathBuilder.toString();
            className = root.getTagMap().get(path);
            if (className == null) {
                //
                // We try to use "*" pattern. If "a/b/*"
                // then for all elements whose path starts with
                // "a/b" we'll try to find "a/b/*" in XmlPath. 
                //
                int idx = path.lastIndexOf("/");
                if (idx > 0) {
                    path = path.substring(0, idx) + "/*";
                    className = root.getTagMap().get(path);
                    if (className == null) {
                        //
                        // We try to use "*text" pattern. If "a/b/*text"
                        // then for all elements whose path starts with
                        // "a/b" we'll try to find "a/b/*text" in XmlPath. 
                        //
                        //path += "text";
                        className = root.getTagMap().get(path);
                    }
                }
            }
        }
        return className;
    }

}
