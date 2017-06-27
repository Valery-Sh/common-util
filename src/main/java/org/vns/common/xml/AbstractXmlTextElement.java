package org.vns.common.xml;

import java.util.Objects;
import org.w3c.dom.Element;

/**
 *
 * @author Valery
 */
public abstract class AbstractXmlTextElement extends AbstractXmlElement implements XmlTextElement {

    private String text;

    public AbstractXmlTextElement(String tagName, Element element, XmlCompoundElement parent) {
        super(tagName, element, parent);
    }

    /**
     * Returns the value of the {@link #text} property;
     *
     * @return the value of the text property;
     */
    @Override
    public String getTextContent() {
        return text;
    }

    /**
     * Sets the value of the {@link #text} property;
     *
     * @param text a string value toe set
     */
    @Override
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Invokes the override method of a superclass. Additionally sets the
     * {@code DOM Element textContent} property to the value of the property {@link #text
     * }
     *
     * @see AbstractXmlElement#commitUpdates()
     */
    @Override
    public void commitUpdates() {
        super.commitUpdates();
        getElement().setTextContent(text);

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
     *
     * If the {@link #text} value of this element is not equals to the
     * {@code text} of the {@code other} element the returns {@code false }.
     * <p>
     *
     * Otherwise returns {@code true}.
     *
     * @param other the element to be compared
     * @return true if elements are weekly equal
     */
    @Override
    public boolean weakEquals(Object other) {

        if (!super.weakEquals(other)) {
            return false;
        }
        return XmlElement.equals(getTextContent(), ((XmlTextElement) other).getTextContent());
    }

    /**
     * Returns a new instance of the same type as this element. The
     * {@code tagName}, {@code attributes} and {@code text} are cloned.
     *
     * @return a new instance of the same type as this element
     */
    @Override
    public XmlElement getClone() {
        XmlElement clone = (XmlElement) super.getClone();
        ((XmlTextElement) clone).setText(getTextContent());
        return clone;
    }

}
