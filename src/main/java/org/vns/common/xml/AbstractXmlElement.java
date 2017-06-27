package org.vns.common.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Valery Shyshkin
 */
public class AbstractXmlElement implements XmlElement {

    /**
     * The tag name of the element.
     */
    private final String tagName;
    /**
     * The {@code DOM Element} this element is bound to..
     */
    private Element element;
    /**
     * The parent element of {@code this} element.
     */
    private XmlCompoundElement parent;
    /**
     * A collection of {@code attributes} of this element. Never {@code null}.
     */
    private final XmlAttributes attributes;

    protected AbstractXmlElement(String tagName, Element element, XmlCompoundElement parent) {
        this.tagName = tagName;
        this.parent = parent;
        this.element = element;
        //2208this.attributes = new XmlAttributes(this);
        this.attributes = new XmlAttributes();
    }

    /**
     * The method must be private. For use in addChild method only. Don't use in
     * in code.
     *
     * @param parent sets the parent
     * {@link org.netbeans.modules.jeeserver.base.deployment.utils.pom#PomElement}
     */
    @Override
    public void setParent(XmlCompoundElement parent) {
        this.parent = parent;
    }

    @Override
    public XmlCompoundElement getParent() {
        return parent;
    }

    @Override
    public Element getElement() {
        return element;
    }

    /**
     * Sets the property {@code element } to null for this element. Removes the
     * DOM element from it's parent node.
     *
     * @return The previous value of the element
     */
    @Override
    public Element nullElement() {
        Element old = element;

        if (element == null) {
            return null;
        }
        if (element.getParentNode() != null) {
            element.getParentNode().removeChild(getElement());
        }

        element = null;

        return old;
    }

    @Override
    public XmlAttributes getAttributes() {
        return attributes;
    }

    protected XmlElement findFirstParentWithDOMElement() {
        XmlElement foundElement = null;
        XmlElement xmlElement = this;
        while (true) {
            if (xmlElement.getElement() != null) {
                foundElement = xmlElement;
                break;
            }
            if (xmlElement.getParent() == null) {
                break;
            }
            xmlElement = xmlElement.getParent();
        }
        return foundElement;
    }

    protected void createDOMElement() {
        Document doc = null;
        if (getElement() != null) {
            return;
        }
        XmlElement hasDomElement = findFirstParentWithDOMElement();

//        if (getParent().getElement() != null) {
        if (hasDomElement != null) {
            doc = hasDomElement.getElement().getOwnerDocument();
        }

        if (doc == null) {
            throw new NullPointerException(
                    " AbstractXmlElement.createElement: Can't find an object of type org.w3c.dom.Document ");
        }

        this.element = doc.createElement(getTagName());
    }

    /**
     * Does work to create a {@code DOM Element} and append it to a
     * {@code Dom Tree}. Fist checks whether the element has a {@link #parent}.
     * If the parent property is {@code null } then a
     * {@code NullPointerException} is thrown.
     * <p>
     * If the property {@link #element } is null then creates a new element of
     * type {@code org.w3c.dom.Element} and sets the property.
     * <p>
     * If this element implements {@link XmlTextElement} then sets if necessary
     * the {@code textContent} property of the DOM element.
     * <p>
     * Copies values of {@link #attributes} to the the {@code DOM element}.
     * <p>
     * Appends the DOM {@link element} to the {@code DOM Tree}.
     */
    @Override
    public void commitUpdates() {
        if (getParent() == null) {
            throw new NullPointerException(
                    " AbstractXmlElement.commitUpdates: The element '" + getTagName() + "' doesn't have a parent element");
        }
        Element parentEl = getParent().getElement();
        if (getElement() == null) {
            createDOMElement();
        }
        if ((this instanceof XmlTextElement) && !((this instanceof XmlCompoundElement))) {
            if (null != ((XmlTextElement) this).getTextContent()) {
                getElement().setTextContent(((XmlTextElement) this).getTextContent());
//                return;
            }
        }

        this.getAttributes().copyTo(element);
        if (parentEl != null && !XmlDocument.hasParentElement(getElement())) {
            parentEl.appendChild(getElement());
        }

    }

    /**
     * Returns the tag name of the element.
     *
     * @return the tag name of the element.
     */
    @Override
    public String getTagName() {
        return tagName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb
                .append(System.lineSeparator())
                .append("class = ")
                .append(getClass().getSimpleName())
                .append(System.lineSeparator())
                .append("tagName = ")
                .append(getTagName())
                .append(System.lineSeparator())
                .append("parent = ")
                .append(getParent() == null ? "<NOT DEFINED>" : getParent().getTagName())
                .append(System.lineSeparator())
                .append("DOM Element Name = ")
                .append(getElement() == null ? "<NOT DEFINED>" : getElement().getNodeName());
        return sb.toString();
    }

    /**
     * Checks whether two elements are {@code weekly equals }. You should be
     * aware that {@link #attributes} not taken into account.
     * <p>
     * If {@code this} element and {@code other} are the same then the method
     * return {@code true}.
     * <p>
     * If {@code other } is {@code null} then returns {@code false}
     * <p>
     * If the class of this element is not equals to the {@code other} class
     * then returns {@code false}
     * .<p>
     * If the tag name of this element is not equals to the tag name of the
     * {@code other} then returns {@code false}
     * .<p>
     * Otherwise returns {@code true}.
     *
     * @param other the element to be compared
     * @return true if elements are weekly equal
     */
    public boolean weakEquals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || !getClass().equals(other.getClass())) {
            return false;
        }
        if (!getTagName().equals(((XmlElement) other).getTagName())) {
            return false;
        }
        return true;
    }

}
