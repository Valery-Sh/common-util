package org.vns.common.xml;

import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 *
 * @author Valery Shyshkin
 */
public class XmlAttributes {
    
    private final Map<String,String> attributes;
    //private final XmlElement xmlElement;

/*    public XmlAttributes(XmlElement xmlElement) {
        attributes = new HashMap<>();
        this.xmlElement = xmlElement;
    }
*/    
    public XmlAttributes() {
        attributes = new HashMap<>();
    }
    
    public XmlAttributes put(String attrName, String attrValue) {
        attributes.put(attrName, attrValue);
        return this;
    }
    public XmlAttributes putAll(XmlAttributes attrs) {
        attributes.putAll(attrs.toMap());
        return this;
    }
    public String get(String attrName) {
        return attributes.get(attrName);
    }
    public String remove(String attrName) {
        return attributes.remove(attrName);
    }
    public int size() {
        return attributes.size();
    }
    
    public boolean isEmpty() {
        return attributes.isEmpty();
    }
    
    public  Map<String,String> toMap() {
        Map<String,String> map =  new HashMap<>();        
        map.putAll(attributes);
        return map;
    }
    
    
    public  XmlAttributes putAll(Map<String,String> attributes) {
        attributes.putAll(attributes);
        return this;
    }


    public void copyFrom(Element el) {
        NamedNodeMap nodeMap = el.getAttributes();

        if ( nodeMap == null || nodeMap.getLength() == 0 ) {
            return;
        }
        for ( int i=0; i < nodeMap.getLength(); i++ ) {
             Node n = nodeMap.item(i); 
             //String s = null;
             if ( nodeMap.item(i) instanceof Attr ) {
                Attr attr = (Attr) nodeMap.item(i);
                String name = attr.getName();
                String attrValue = attr.getValue();
                this.attributes.put(name, attrValue);
             }
        }
        
    }
    
    public void copyTo(Element el) {
       attributes.forEach((name,value) -> {
            el.setAttribute(name, value);
       });
 
    }
    
/*    
    protected String getDOMAttribute(String name) {
        return xmlElement.getElement() == null ? "" :   xmlElement.getElement().getAttribute(name);
    }
    protected String getDOMAttribute(String namespaceURI,String localName) {
        return xmlElement.getElement() == null ? "" :  xmlElement.getElement().getAttributeNS(namespaceURI,localName);
    }
    protected void removeDOMAttribute(String name) {
        if ( xmlElement.getElement() == null ) {
            return;
        }
        xmlElement.getElement().removeAttribute(name);
    }    
    protected void setDOMAttribute(String name, String value) {
        if ( xmlElement.getElement() == null ) {
            return;
        }
        xmlElement.getElement().setAttribute(name, value);
    }    
    protected void setDOMAttributes(Map<String,String> attrMap) {
        attrMap.forEach((name,value) -> {
            //xmlElement.setAttribute(name, value);
        });
    }    
    
    protected Map<String,String> getDOMAttributes() {
        Map<String, String> map = new HashMap<>();
        
        if ( xmlElement.getElement() == null ) {
            return map;
        }
        NamedNodeMap nodeMap = xmlElement.getElement().toMap();

        if ( nodeMap == null || nodeMap.getLength() == 0 ) {
            return map;
        }
        for ( int i=0; i < nodeMap.getLength(); i++ ) {
             Node n = nodeMap.item(i); 
             //String s = null;
             if ( nodeMap.item(i) instanceof Attr ) {
                Attr attr = (Attr) nodeMap.item(i);
                String name = attr.getName();
                String attrValue = attr.getValue();
                map.put(name, attrValue);
             }
        }
        return map;
    }
*/    
    
}
