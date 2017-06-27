package org.vns.common.xml.pom;

import org.vns.common.xml.AbstractCompoundXmlElement;
import org.vns.common.xml.XmlChilds;
import org.vns.common.xml.XmlCompoundElement;
import org.vns.common.xml.XmlElement;
import org.vns.common.xml.XmlRoot;
import org.vns.common.xml.XmlTagMap;
import org.w3c.dom.Element;

/**
 * @author Valery Shyshkin
 */

public class PomRoot extends XmlRoot {

    public PomRoot(PomDocument pomDocument) {
        super(pomDocument);
        init();
    }
    private void init() {
        XmlTagMap map = new XmlTagMap();
        map.put("dependencies", Dependencies.class.getName());
        map.put("properties", PomProperties.class.getName());
        setTagMap(map);
        getTagMap().setDefaultClass(null);
    }

/*    @Override
    public Element getElement() {
        return document.getDocumentElement();
    }

    @Override
    public void createDOMElement() {
    }
*/    
    /**
     * A convenient method to access a {@code Dependencies} pom element.
     * 
     * @return an instance of type {@link Dependencies } or null if the pom
     * doesn't contain the {@code dependencies} tag.
     */
    public Dependencies getDependencies() {
        for (XmlElement e : getChilds().list()) {
            if (e instanceof Dependencies) {
                return (Dependencies) e;
            }
        }
        return null;
    }
    /**
     * A convenient method to access a {@code PomProperties} pom element.
     * 
     * @return an instance of type {@link PomProperties } or null if the pom
     * doesn't contain the {@code properties} tag.
     */
    public PomProperties getProperties() {
        for (XmlElement e : getChilds().list()) {
            if (e instanceof PomProperties) {
                return (PomProperties) e;
            }
        }
        return null;
    }
    
    
    public boolean isPomDocument() {
        if ( ! "project".equals(getElement().getTagName()) ) {
            return false;
        }
        String xmlns = "http://maven.apache.org/POM";
        String attr = getElement().getAttribute("xmlns");
        return !(attr == null || ! attr.substring(0,xmlns.length()).equals(xmlns));
    }
}//class XmlRoot
