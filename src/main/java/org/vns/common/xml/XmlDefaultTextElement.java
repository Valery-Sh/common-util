package org.vns.common.xml;

import java.util.Objects;
import org.w3c.dom.Element;

/**
 *
 * @author Valery Shyshkin
 */
public class XmlDefaultTextElement extends AbstractXmlTextElement {
    
    public XmlDefaultTextElement(String tagName) {
        super(tagName, null, null);
    }

    protected XmlDefaultTextElement(Element element, XmlCompoundElement parent) {
        super(element.getTagName(), element, parent);
    }

    protected XmlDefaultTextElement(String tagName, XmlCompoundElement parent) {
        super(tagName, null, parent);
    }
}
