package org.vns.common.xml.pom;

import org.w3c.dom.Element;
import org.vns.common.xml.AbstractCompoundXmlElement;
import org.vns.common.xml.AbstractXmlTextElement;
import org.vns.common.xml.XmlChilds;
import org.vns.common.xml.XmlCompoundElement;
import org.vns.common.xml.XmlElement;
import org.vns.common.xml.XmlTagMap;
import org.w3c.dom.Element;

/**
 *
 * @author Valery Shyshkin
 */
public class DependencyArtifact extends AbstractXmlTextElement {
    
    public DependencyArtifact(String tagName) {
        super(tagName, null, null);
    }

    protected DependencyArtifact(Element element, XmlCompoundElement parent) {
        super(element.getTagName(), element, parent);
    }

    protected DependencyArtifact(String tagName, XmlCompoundElement parent) {
        super(tagName, null, parent);
    }
    @Override
    public boolean weakEquals(Object other) {
        if ( ! super.weakEquals(other)) {
            return false;
        }
        String s1 = getTextContent();
        String s2 = "jar";
        if ( "type".equals(getTagName() )) {
            s2 = ((DependencyArtifact)other).getTextContent();
        } 
        return XmlElement.equals(s1, s2);
    }
    
    public String resolveByProperties() {
        String r = getTextContent();
        PomRoot root = (PomRoot) PomRoot.findXmlRoot(this);
        if ( root == null) {
            return r;
        }
        PomProperties props = root.getProperties();
        if ( props == null ) {
            return r;
        }
        
        return r;
    }
     private String resolveVersionByProperties() {
         String r = getTextContent();
         return r;
     }
    
}
