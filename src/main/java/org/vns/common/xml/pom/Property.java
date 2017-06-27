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
 * The class that corresponds to the  child element of the element
 * {@code "PomProperties" }.
 * 
 * @see PomProperties
 * @author Valery Shyshkin
 */
public class Property extends AbstractXmlTextElement {
    /**
     * Creates a new instance of the class with a specified {@code tagName}.
     *
     * @param tagName the name of the tag property
     */
    public Property(String tagName) {
        super(tagName,null,null);
    }
    
    protected Property(Element element, XmlCompoundElement parent) {
        super(element.getTagName(), element, parent);
    }
    
    protected Property(String tagName, Element element, XmlCompoundElement parent) {
        super(tagName,element, parent);
    }

    protected Property(String tagName,XmlCompoundElement parent) {
        super(tagName, null, parent);
    }
    
}
