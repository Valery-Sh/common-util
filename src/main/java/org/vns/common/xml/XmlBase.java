package org.vns.common.xml;

import java.util.ArrayList;
import java.util.List;
import org.vns.common.xml.XmlChilds.ParentVisitor;
import org.w3c.dom.Element;

/**
 * Serves as a root for {@code XmlElement Tree}. The class does not perform any
 * operations with a {@code DOM Tree} and works only with object of type {@link XmlElement
 * } and knows nothing about {@code DOM Document}.
 *
 * @author Valery Shyshkin
 */
public class XmlBase extends AbstractCompoundXmlElement {

    /**
     * Create a new instance of the class by the given {@code tagName}
     *
     * @param tagName a tag name to create an instance
     */
    public XmlBase(String tagName) {
        super(tagName);
        init();

    }

    private void init() {
        getTagMap().setDefaultClass(XmlDefaultElement.class.getName());
    }

    /**
     *
     * @param xmlElement
     * @return
     */
    public static XmlBase findXmlRoot(XmlElement xmlElement) {
        XmlBase root = null;
        XmlElement el = xmlElement;
        while (true) {
            if (el instanceof XmlBase) {
                root = (XmlBase) el;
                break;
            }
            if (el.getParent() == null) {
                break;
            }
            el = el.getParent();
        }
        return root;
    }

    /**
     * Just invokes the {@link #check() ) method.
     */
    @Override
    public void commitUpdates() {
        commitUpdates(true);
    }

    public void commitUpdates(boolean throwException) {

        if (!throwException) {
            return;
        }
        
        XmlErrors errors = check();

        if (errors.isEmpty()) {
            return;
        }
        errors.getErrorList().forEach(er -> {
            if (!er.isWarning()) {
                RuntimeException ex = er.getException();
                if (ex != null) {
                    throw ex;
                }
            }
        });

    }

    public XmlErrors check(XmlElement element) {
        XmlErrors errors = new XmlErrors();
        if (element.getParent() != null) {
            errors = element.getParent().getChilds().checkElement(element);
        }
        return errors;
    }

    public XmlErrors check() {
        return getChilds().check();
    }

    /**
     * Returns a list of elements. Each subsequent element is a child of the
     * previous element. The first element is the one that has no parent.
     *
     * @param leaf the element that ends the result of the method.
     * @return a list of elements.
     */
    public static List<XmlElement> getParentChainList(XmlElement leaf) {   
        List<XmlElement> list = new ArrayList<>();
        ParentVisitor v = new ParentVisitor();
        v.visit(leaf, el -> {
            list.add(0,el);
        });
        return list;
    } 
    /**
     * Returns {code null}.
     *
     * The class does not perform any operations with {@code DOM Tree }.
     *
     * @return null
     */
    @Override
    public Element getElement() {
        return null;
    }

    /**
     * The method overrides the method of the base class and does nothing.
     */
    @Override
    public void createDOMElement() {
    }
}//class XmlBase
