package org.vns.common.xml;

import java.util.List;
import org.w3c.dom.Element;

/**
 *
 * @author Valery Shyshkin
 */
public class AbstractCompoundXmlElement extends AbstractXmlElement implements XmlCompoundElement {

    private XmlTagMap tagMap;
    protected XmlChilds childs;

    protected AbstractCompoundXmlElement(String tagName) {
        this(tagName, null, null);
    }

    protected AbstractCompoundXmlElement(String tagName, Element element, XmlCompoundElement parent) {
        super(tagName, element, parent);
        tagMap = new XmlTagMap();
    }

    /**
     * Sets the property {@code element } to null for thex element and all it's
     * child elements recursively. Removes the DOM element from it's parent
     * node.
     *
     * @return The previous value of the element
     */

    @Override
    public Element nullElement() {
        Element old = super.nullElement();
        childs.list().forEach(e -> {
            e.nullElement();
        });
        return old;
    }

    /**
     * Returns an instance of an object of type {@link XmlChilds}. If the {@code childs
     * } property value has not been set yet the this method creates a new
     * instance and sets the property value.
     *
     * @return an Object of type {@link XmlChilds }
     */
    @Override
    public XmlChilds getChilds() {
        if (childs == null) {
            childs = new XmlChilds(this);
        }
        return childs;
    }

    /*    @Override
    public XmlCompoundElement getClone() {
        XmlCompoundElement p = (XmlCompoundElement) super.getClone();
        if ( this instanceof XmlTextElement ) {
            ((XmlTextElement) p).setText(((XmlTextElement)this).getTextContent());
        }
        List<XmlElement> list = getChilds().list();
        list.forEach(e -> {
            p.getChilds().add(e.getClone());
        });
        return p;
    }
     */
    /**
     * Getter method of the property {@link #tagMap}. Never {@code null}
     *
     * @return the property tagMap
     *
     * @see XmlTagMap
     */
    @Override
    public XmlTagMap getTagMap() {
        return tagMap;
    }

    /**
     * Setter method of the property {@link #tagMap}.
     *
     * @param tagMap can't be null otherwise NullPointerException is thrown
     * @see XmlTagMap
     */
    @Override
    public void setTagMap(XmlTagMap tagMap) {
        if (tagMap == null) {
            throw new NullPointerException("AbstractCompoundElement,setTagMap(XmlTagMap). The parameter can't be null");
        }
        this.tagMap = tagMap;
    }

    /**
     *
     */
    @Override
    public void commitUpdates() {
        super.commitUpdates();

        List<XmlElement> list = getChilds().list();
        if (this instanceof XmlTextElement) {
            if (list.isEmpty() && ((XmlTextElement) this).getTextContent() != null) {
                getElement().setTextContent(((XmlTextElement) this).getTextContent());
                return;
            }
        }

        list.forEach(el -> {
            el.setParent(this);
            el.commitUpdates();
        });
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb
                .append(System.lineSeparator())
                .append("---------------------------")
                .append(System.lineSeparator())
                .append("tagMap.size() = ")
                .append(tagMap.size())
                .append(System.lineSeparator())
                .append("childs.size() = ")
                .append(childs.size());
        return sb.toString();
    }


}
