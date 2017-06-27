package org.vns.common.xml;

import java.util.List;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Represents a root of the {@link XmlDocument }.
 * The class is aware of {@code DDM Document } and contains few methods to 
 * manipulate the last. 
 * 
 * @author Valery Shyshkin
 */
public class XmlRoot extends XmlBase {//AbstractCompoundXmlElement {

    
    private final Document document;
    /**
     * Creates an instance of the class by the given object of type
     * {@link XmlDocument }.
     * 
     * @param xmlDocument the object of type {@link XmlDocument} to create an instance for.
     */
    public XmlRoot(XmlDocument xmlDocument) {
        super(xmlDocument.getDocument().getDocumentElement().getTagName());
        this.document = xmlDocument.getDocument();
    }
    /**
     * Returns the object of type {@code org.w3c.dom.Document }.
     * This class represents the root element of the corresponding 
     * {@code DOM Document}.
     * 
     * @return the object of type {@code org.w3c.dom.Document }.
     */
    public Document getDocument() {
        return document;
    }


    /**
     * Return a {@code DOM element} which this object is a wrapper of.
     * 
     * @return the value of document.getDocumentElement() where the document 
     *   is a DOM Document
     */
    @Override
    public Element getElement() {
        return document.getDocumentElement();
    }
    /**
     * Adds the comment lines after the last child node of the target.
     * 
     * @param target the element where the comment is appended.
     * @param comment a string to be added
     */
    public static void addComment(XmlElement target, String comment) {
        if ( target.getElement() == null ) {
            return;
        }
        XmlRoot root = (XmlRoot) XmlBase.findXmlRoot(target);
        if ( root == null || root.getDocument() == null) {
            return;
        }
        Document doc = root.getDocument();
        Comment c = doc.createComment(comment);
        Element el = target.getElement();
        el.appendChild(c);
        
    }
    /**
     * Inserts the specified comment before the given element.
     * 
     * @param target an element to insert a node before it.
     * 
     * @param comment a string to be inserted
     */
    public static void insertComment(XmlElement target, String comment) {
        if ( target.getElement() == null ) {
            return;
        }
        XmlRoot root = (XmlRoot) XmlBase.findXmlRoot(target);
        if ( root == null ) {
            return;
        }
        Document doc = root.getDocument();
        Comment c = doc.createComment(comment);
        Element el = target.getElement();
        Node parentNode = el.getParentNode();
        if ( parentNode == null ) {
            return;
        }
        parentNode.insertBefore(c, el);
    }

    /**
     * Just call the method {@link #commitUpdates(boolean) } with an argument
     * value {@code true }.
     * @see #commitUpdates(boolean) 
     * 
     */
    @Override
    public void commitUpdates() {
        commitUpdates(true);
    }
    /**
     * When working with a {@code xml document } we can create, add, modify
     * or remove elements that are not actually {@code  DOM Tree }
     * members. For each child element of type {@code XmlElement }
     * sets it's parent as this object and invokes child's
     * {@code  commitUpdates} method. 
     * 
     * @param throwException if true then the method {@link #check() } is called
     *   and an exception may be thrown as a result of checking.
     */
    @Override
    public void commitUpdates(boolean throwException) {
        
        List<XmlElement> list = getChilds().list();
        list.forEach(el -> {
            el.setParent(this);
            el.commitUpdates();
        });
        if ( ! throwException ) {
            return;
        }

        XmlErrors errors = check();
        
        if ( errors.isEmpty() ) {
            return;
        }
        errors.getErrorList().forEach( er -> {
            if ( ! er.isWarning()) {
                RuntimeException ex = er.getException();
                if ( ex != null ) {
                    throw ex;
                }
            }
        });
        
        
    }    
}//class XmlRoot
