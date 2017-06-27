package org.vns.common.xml;

import java.util.Objects;
import org.w3c.dom.Element;

/**
 *
 * @author Valery Shyshkin
 */
public class XmlDefaultElement extends AbstractCompoundXmlElement implements XmlTextElement {

    private String text;

    public XmlDefaultElement(String tagName) {
        this(tagName, null);
    }

    protected XmlDefaultElement(Element element, XmlCompoundElement parent) {
        super(element.getTagName(), element, parent);
    }

    protected XmlDefaultElement(String tagName, XmlCompoundElement parent) {
        super(tagName, null, parent);
    }
    /**
     * Checks whether two elements are {@code weekly equals }. You should be
     * aware that {@link #childs}, {@link #tagMap} and {@link #attributes} not
     * taken into account.
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
     * {@code other} then returns {@code false}.
     * <p>
     * If the class of this element implements {@link XmlTextElement} and the
     * value of the [@code text} property of this element is not equals to the
     * value of the {code text} property then returns {@code false}.
     * <p>
     * Otherwise returns {@code true}.
     *
     * @param other the element to be compared
     * @return true if elements are weekly equal
     */
    @Override
    public boolean weakEquals(Object other) {
        if ( ! super.weakEquals(other) ) {
            return false;
        }
        
        return  XmlElement.equals(
                        ((XmlTextElement)this).getTextContent(), 
                        ((XmlTextElement) other).getTextContent());
    }


    @Override
    public String getTextContent() {
        return text;
    }

    @Override
    public void setText(String text) {
        if (!getChilds().isEmpty()) {
            throw new IllegalStateException(
                    "XmlDefaultElement.setText: can't set text since the element has child elements");
        }
        this.text = text;
        if (getElement() != null) {
            if (text == null) {
                getElement().setTextContent("");
            } else {
                getElement().setTextContent(text);
            }
        }
    }
    
}
