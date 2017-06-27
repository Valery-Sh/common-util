package org.vns.common.xml;

import java.util.List;
import java.util.function.Predicate;

/**
 *
 * @author Valery Shyshkin
 */
public interface XmlCompoundElement extends XmlElement {

    XmlTagMap getTagMap();

    void setTagMap(XmlTagMap tagMapping);

    XmlChilds getChilds();

    default boolean beforeAddChild(XmlElement child) {
        return true;
    }
    /**
     * Returns a new instance of the same type as this element.
     * The {@code tagName} and {@code attributes} are cloned.
     * If the class of this element implements {@link XmlTextElement} then
     * the {@code text} property is cloned too.
     * 
     * You should know that the {@code tagMap} property is not cloned.
     * 
     * @return a new instance of the same type as this element
     */
    @Override
    default XmlCompoundElement getClone() {
        XmlCompoundElement p = (XmlCompoundElement) XmlElement.super.getClone();
        p.getTagMap().putAll(getTagMap());
        if ( this instanceof XmlTextElement ) {
            ((XmlTextElement) p).setText(((XmlTextElement)this).getTextContent());
        }
        List<XmlElement> list = getChilds().list();
        list.forEach(e -> {
            p.getChilds().add(e.getClone());
        });
        return p;
    }

    /**
     * A convenient method which just invokes {@code getChilds().add(child)
     * }
     *
     * @param child an element to be added.
     * @return a calling instance of type {@link XmlCompoundElement }.
     */
    default XmlCompoundElement addChild(XmlElement child) {
        return getChilds().add(child);
    }

    /**
     * A convenient method which just invokes {@code getChilds().remove(child)}
     *
     * @param toRemove the element to be removed
     * @return a calling instance of type {@link XmlCompoundElement }.
     */
    default XmlCompoundElement removeChild(XmlElement toRemove) {
        
        return getChilds().remove(toRemove);
    }

    /**
     * A convenient Replaces the child element specified by the first parameter
     * with a new element as specified by the second parameter. If an element
     * specified by the first parameter cannot be found in the child list of
     * this element then the pom element specified by the second parameter is
     * just added to child list of this parent element.
     *
     * @param child the element to be replaced
     * @param newChild the element to replace an existing one
     * @return an object of type {@link XmlCompoundElement } which represents an
     * object which calls this method (parent element)
     */
    default XmlCompoundElement replaceChild(XmlElement child, XmlElement newChild) {
        return getChilds().replace(child, newChild);
    }

    /**
     * Returns a list of child elements that satisfies the given path.
     * The parameter specifies a relative path to this element.
     * 
     * The path parameter may take two forms. For example
     * <ul>
     * <li>
     * "p1/p2/p3". then all child elements with tag name "p3" which resides in
     * the owner "p1/p2" will be searched.
     * </li>
     * <li>
     * "p1/p2/*". then all child elements which resides in the owner "p1/p2"
     * will be searched regardless of tag names.
     * </li>
     * </ul>
     *
     * @param path the string value with items separated by slash.
     * @return Returns a list of child elements that satisfies the given path.
     * 
     * @see XmlChilds#findChildsByPath(java.lang.String)
     */
    default List<XmlElement> findElementsByPath(String path) {
        return getChilds().findChildsByPath(path);
    }
    /**
     * Returns a list of child elements that satisfies the given path.
     * The parameter specifies a relative path to this element.
     * 
     * The path parameter may take two forms. For example
     * <ul>
     * <li>
     * "p1/p2/p3". then all child elements with tag name "p3" which resides in
     * the owner "p1/p2" will be searched.
     * </li>
     * <li>
     * "p1/p2/*". then all child elements which resides in the owner "p1/p2"
     * will be searched regardless of tag names.
     * </li>
     * </ul>
     *
     * @param path the string value with items separated by slash.
     * @param predicate a condition for an element to be added to the result list
     * 
     * @return Returns a list of child elements that satisfies the given path.
     * @see XmlChilds#findChildsByPath(java.lang.String)
     */
    default List<XmlElement> findElementsByPath(String path, Predicate<XmlElement> predicate) {
        return getChilds().findChildsByPath(path,predicate);
    }

    /**
     * Returns a list of child elements that satisfies the given path.
     * The parameter specifies a relative path to this element.
     * 
     * The path parameter may take two forms. For example
     * <ul>
     * <li>
     * "p1/p2/p3". then all child elements with tag name "p3" which resides in
     * the owner "p1/p2" will be searched.
     * </li>
     * <li>
     * "p1/p2/*". then all child elements which resides in the owner "p1/p2"
     * will be searched regardless of tag names.
     * </li>
     * </ul>
     *
     * @param path the string value with items separated by slash.
     * 
     * @return Returns a list of child elements that satisfies the given path.
     * @see XmlChilds#findChildsByPath(java.lang.String)
     */
    default XmlElement findFirstElementByPath(String path) {
        List<XmlElement> list = findElementsByPath(path);
        return list == null || list.isEmpty() ? null : list.get(0);
    }

    /**
     * Returns a list of child elements that satisfies the given path.
     * The parameter specifies a relative path to this element.
     * 
     * The path parameter may take two forms. For example
     * <ul>
     * <li>
     * "p1/p2/p3". then all child elements with tag name "p3" which resides in
     * the owner "p1/p2" will be searched.
     * </li>
     * <li>
     * "p1/p2/*". then all child elements which resides in the owner "p1/p2"
     * will be searched regardless of tag names.
     * </li>
     * </ul>
     *
     * @param path the string value with items separated by slash.
     * @param predicate a condition for an element to be added to the result list
     * 
     * @return Returns a list of child elements that satisfies the given path.
     * @see XmlChilds#findChildsByPath(java.lang.String)
     */
    default XmlElement findFirstElementByPath(String path,Predicate<XmlElement> predicate) {
        List<XmlElement> list = findElementsByPath(path, predicate);
        return list == null || list.isEmpty() ? null : list.get(0);
    }
    
}
