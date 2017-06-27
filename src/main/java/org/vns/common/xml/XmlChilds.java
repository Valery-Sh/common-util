package org.vns.common.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.vns.common.xml.XmlErrors.XmlError;
import org.vns.common.xml.XmlErrors.XmlResult;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Represents child elements of an object of type {@code XmlCompoundElement}.
 *
 * @see XmlCompoundElement
 * @author Valery Shyshkin
 */
public class XmlChilds {

    private final List<XmlElement> childs;
    private final XmlCompoundElement owner;

    /**
     * Creates an instance of the class for the given element of type
     * {@link XmlCompoundElement}. 
     * If the parameter {@code owner } is already in the {@code DOM Tree} 
     * i.e the {@code ownwer.getElement() } returns a not null value of type 
     * {@code org.w3c.dom.Element } then the constructor  
     * then creates {@code xml} elements for each
     * immediate child element of the created instance by its DOM element of
     * type {@code org.w3c.dom.Element}.
     *
     * @param owner an object of type XmlCompoundElement}.
     */
    public XmlChilds(XmlCompoundElement owner) {
        childs = new ArrayList<>();
        this.owner = owner;
        init();
    }

    private void init() {
        List<Element> domList = getChildDomElements();
        if (this instanceof XmlTextElement) {
            String text = ((XmlTextElement) this).getTextContent();
            if (!domList.isEmpty() && text != null) {
                throw new IllegalStateException(
                        " XmlChilds.getChilds(): Can't get child elements since the element has not null text property.");
            }
        }

        domList.forEach(el -> {
            String s = el.getTagName();
            
            XmlChildElementFactory f = new XmlChildElementFactory(owner);
            XmlElement xmlEl = f.createXmlElement(el);
            if ((xmlEl instanceof XmlTextElement)) {
                String content = xmlEl.getElement().getTextContent();
                if (!XmlDocument.hasChildElements(xmlEl.getElement()) && content.length() > 0) {
                    ((XmlTextElement) xmlEl).setText(content);
                }
            }
            xmlEl.getAttributes().copyFrom(el);
            childs.add(xmlEl);
        });

    }

    protected List<Element> getChildDomElements() {

        List<Element> list = new ArrayList<>();
        if (owner.getElement() != null) {
            NodeList nl = owner.getElement().getChildNodes();
            if (nl != null && nl.getLength() > 0) {
                for (int i = 0; i < nl.getLength(); i++) {
                    if ((nl.item(i) instanceof Element)) {
                        Element el = (Element) nl.item(i);
                        //if (!owner.isChildTagNameSupported(el.getTagName())) {
                        if (!isChildTagNameSupported(owner, el.getTagName())) {
                            continue;
                        }
                        list.add(el);
                    }
                }
            }
        }
        return list;
    }

    public XmlCompoundElement getOwner() {
        return owner;
    }

    /**
     * Creates a slash separated string representation of given list of
     * {@code xml} elements. Each item in the result string is a tag name of the
     * corresponding element. If the {@code list } is empty then an empty string
     * returns. The first character of the result can't be a slash.
     *
     * @param list a list to be converted
     * @return a string of slash separated tag names
     */
    protected String relativePath(List<XmlElement> list) {

        StringBuilder pathBuilder = new StringBuilder();
        String slash = "/";
        for (int i = 0; i < list.size(); i++) {
            if (i == list.size() - 1) {
                slash = "";
            }
            pathBuilder
                    .append(list.get(i).getTagName());
            pathBuilder.append(slash);
        }
        return pathBuilder.toString();
    }

    /**
     * Returns a path of the specified element relative to the owner of this
     * object.
     *
     * @param element an object for which to calculate the relative path to the
     * owner element of this object
     * @return a relative path of the given element
     */
    public String relativePath(XmlElement element) {
        StringBuilder sb = new StringBuilder();
        ParentVisitor v = new ParentVisitor();
        v.visit(this.owner, element, el -> {
            if (sb.length() != 0) {
                sb.insert(0, "/");
            }
            sb.insert(0, el.getTagName());
        });
        return sb.toString();
    }

    /**
     * Searches permissable class name of the given element. The {code
     * permissable class name} is the one that is specified by a {@code tagMap}
     * in some parent of the given element. Scans a parent chain list of
     * elements starting from the immediate parent of the given element. The
     * element specified by the parameter may not has a parent element i.e {@code tocheck.getParent()
     * } returns {@code null}. In this case the method considers the owner of
     * this object to be a parent of the element to be checked. The method
     * doesn't modify an existing {@code parent} property of the element to be
     * checked.
     *
     * <p>
     * The following algorithm is used to create an object of type
     * {@link XmlResult}: Let there is element of type MyElement. Since each
     * element can have a parent element then a chain of it's parent elements
     * can be constructed as shown below:
     * <pre>
     *  parent_N
     *      …..................
     *      …..................
     *          parent_0
     * </pre>
     *
     * for example {@code parent_0.getParent() } gives {@code parent_1 } etc.
     * <p>
     * Suppose we execute the code:
     * <pre>
     *      MyElement myElement = new MyElement(ʺbookʺ);
     *      XmlResult result = parent_0.getChilds().findElementClass(myElement);
     * </pre>
     *
     * Parent elements tested one by one, starting with parent_0 element, that
     * is the immediate parent. Testing continues for as long as for some
     * element with the name {@code parent_K} one of the following conditions is
     * satisfied:
     * <ul>
     * <li>
     * <pre>parent_K.getTagMap.get(ʺparent_K-1/parent_K-2/.../parent_0/bookʺ)  != null</pre>
     * </li>
     * <li>
     * <pre>
     *          parent_K.getTagMap.get(ʺparent_K-1/parent_K-2/.../parent_0/bookʺ)
     *              and
     *          parent_K.getTagMap().getDefaultClass() != null;
     * </pre>
     *
     * </li>
     * <li>
     * <pre>K == N</pre> and none of the above conditions is satisfied
     * </li>
     * </ul>
     * If {@code condition 1} is determined then a class name is assigned to the
     * property of the {@code result} object:
     * <pre>
     *      result.setElementClass(parent_K.getTagMap.get(ʺparent_K-1/.../parent_0/bookʺ)));
     * </pre>
     * <p>
     * If {@code condition 2} is satisfied then:
     * <pre>
     *    result.setElementClass(parent_K.getTagMap.getDefaultClass());
     *    result.setDefaultClass(true);
     * </pre> The boolean property {@code defaultClass } in the above code is
     * set to {@code true}. The subsequent code can recognize how the class name
     * was determined.
     * <p>
     * In case when the {@code condition 3} is satisfied then the method
     * returns.
     * <p>
     * Additionally the result will contains a list of elements of type
     * {@code XmlElement}. The element {@code parent_K} has the index 0 in the
     * list and the last element is {@code tocheck} specified as a parameter.
     *
     * @param tocheck the element used to search for a permissable class name.
     * @return an object of type XmlResult where all necessary information is
     * gathered.
     *
     * @see
     * #checkElement(org.netbeans.modules.jeeserver.base.deployment.xml.XmlElement)
     * @see XmlResult
     * @see XmlTagMap
     */
    public XmlResult findElementClass(XmlElement tocheck) {
        XmlResult result = new XmlResult(tocheck);
        List<XmlElement> list = new ArrayList<>();

        XmlCompoundElement saveParent = tocheck.getParent();
        if (tocheck.getParent() == null) {
            // This happens when an element still is not added to the childs coollectio
            tocheck.setParent(owner);
        }
        ParentVisitor v = new ParentVisitor();
        v.visit(tocheck, el -> {
            if (el.getParent() != null && !(el instanceof XmlBase)) {
                XmlTagMap map = el.getParent().getTagMap();
                //String path = XmlChilds.this.relativePath(list);
                String path = el.getParent().getChilds().relativePath(tocheck);
                if (map.get(path) != null) {
                    // found class
                    result.setElementClass(map.get(path));

                    // include root
                    list.add(0, el.getParent());
                    v.stop();
                    return;
                } else if (map.getDefaultClass() != null) {
                    // found default class
                    result.setDefaultClass(true);
                    result.setElementClass(map.getDefaultClass());
                    // include root
                    list.add(0, el.getParent());
                    v.stop();
                    return;

                }

                list.add(0, el);
            }

        });

        tocheck.setParent(saveParent);

        result.getParentList().addAll(list);
        return result;
    }

    /**
     * Checks all child elements recursively. Each child element of this object
     * is checked and recursively all child elements of the elements mentioned
     * above are checked.
     *
     * Doesn't throw exceptions but provides the opportunity to developer.
     *
     * @return an object of type XmlErros that contains a list of errors (may be
     * empty).
     */
    public XmlErrors check() {
        XmlErrors errors = new XmlErrors();
        Visitor visitor = new Visitor();
        visitor.visit(this, el -> {
            errors.merge(checkElement(el));
        });
        return errors;
    }

    /**
     * Checks whether the specified element has a valid class name and a valid
     * tag name. The element to be checked may not have a parent element. In
     * this case the checking will be executed against this object (of type
     * {@code  XmlChilds} ) as a parent.
     * <p>
     * The method calls {@code findElementClass} , analyzes the result and
     * creates an object of type {@link XmlError } if an error is determined.
     * <p>
     * To decide whether the element specified as parameter in valid or not the
     * method use the result object obtained by calling the method
     * {@code findElementClass}. The following values are taken into account:
     *
     * <ul>
     * <li>
     * The string value of the parameter's class name. We refer it head as
     * {@code tocheckClassName}
     * </li>
     * <li>The string value of the property {@link XmlResult#elementClass}  </li>
     * <li>The boolean value of the property {@link XmlResult#defaultClass}</li>
     * </ul>
     *
     * <pre>
     *   If {@code elementClass != null
     *           and
     *      tocheckClassName.equals(elementClasName))}
     * </pre> then no errors fount and the method returns.
     * <p>
     * <
     * pre>
     * If null null null null null null null null null null null null     {@code elementClass != null
     *          and
     *      not  tocheckClassName.equals(elementClasName)
     *          and
     *      defaultClass == true }
     * </pre> then the method creates {@code XmlError } objects and marks the
     * error as warning. This error does not affect to such methods as
     * {@code add(XmlElement)} and {@code replace(XmlElement,XmlElement)} and
     * doesn't results in exception. The method returns.
     * <p>
     * <
     * pre>
     * If {@code elementClass != null
     *          and
     *     not tocheckClassName.equals(elementClasName) }
     * </pre> then the method creates {@code XmlError } with error code "210".
     * This error affects to such methods such as {@code add } and
     * {@code replace} and causes them to throw an exception of type {@link InvalidClassNameException
     * }
     * <p>
     * <
     * pre>
     * If {@code elementClass == null }
     * </pre> then the method creates {@code XmlError } with error code "200".
     * This error affects to such methods such as {@code add } and
     * {@code replace} and causes them to throw an exception of type {@link InvalidTagNameException
     * }
     *
     * @param tocheck an element to be checked
     * @return an object that contains a collection of (possibly empty) of
     * detected errors
     * @see
     * #findElementClass(org.netbeans.modules.jeeserver.base.deployment.xml.XmlElement)
     */
    public XmlErrors checkElement(XmlElement tocheck) {
        XmlErrors errors = new XmlErrors();

        XmlResult checkResult = findElementClass(tocheck);

        String tocheckClass = tocheck.getClass().getName();
        String clazz = checkResult.getElementClass();

        if (clazz != null && clazz.equals(tocheckClass)) {
            return errors;
        } else if (clazz != null && !clazz.equals(tocheckClass) && checkResult.isDefaultClass()) {
            // wanning
            checkResult.setWarning(true);
            checkResult.setErrorCode("100");
            errors.addError(new XmlError(checkResult));
        } else if (clazz != null && !clazz.equals(tocheckClass)) {
            // error
            checkResult.setErrorCode("210");
            errors.addError(new XmlError(checkResult));
        } else if (clazz == null && XmlBase.findXmlRoot(tocheck) != null) {
            checkResult.setErrorCode("200");
            errors.addError(new XmlError(checkResult));
        } else if (clazz == null) {
            // wanning
            checkResult.setWarning(true);
            checkResult.setErrorCode("100");
            errors.addError(new XmlError(checkResult));
        }

        return errors;
    }

    protected boolean isChildTagNameSupported(XmlCompoundElement xmlElement, String tagName) {
        boolean result = xmlElement.getTagMap().isTagPathSupported(tagName);
        if (result) {
            return true;
        }
        if (xmlElement.getParent() == null) {
            return false;
        }
        String path = xmlElement.getTagName() + "/" + tagName;
        return isChildTagNameSupported(xmlElement.getParent(), path);
    }

    public List<XmlElement> list() {
        return new ArrayList<>(childs);
    }

    public XmlElement get(int index) {
        return childs.get(index);
    }

    public boolean isEmpty() {
        return childs.isEmpty();
    }

    public int size() {
        return childs.size();
    }

    public int indexOf(XmlElement element) {
        return childs.indexOf(element);
    }

    public boolean contains(XmlElement e) {
        return childs.contains(e);
    }

    /**
     * Appends the specified {@code  xml element } to the end of an internal
     * collection.
     *
     * <ul>
     * <li>
     * If the child is already in the internal collection then the method does
     * nothing.
     * </li>
     * <li>
     * If no error found then sets the {@code 'parent'} property of the 
     *        {@code child } element to the {@link #owner ) of this object.
     * </li>
     * <li>
     * The method doesn't change the {@code DOM Tree} and doesn't create
     *       {@code DOM Elements}.
     * </li>
     * </ul>
     *
     * <p>
     * The method may throw an {@link IllegalStateException } when the
     * conditions below are satisfied:
     * <ul>
     * <li>
     * an owner element is an instance of {@link XmlTextElement }
     *        and the owner's 'text' property is not {@code null }.
     * </li>
     * </ul>
     *
     * <p>
     * The method may throw an {@link IllegalArgumentException }
     * when {@code  child.getElement() != null } and
     * {@code  child.getElement() } is already in {@code DOM Tree}.
     *
     * <p>
     * To make additional checking the method invokes {@code CheckElement(XmlElement) }
     * with the {@code child} as an argument. The mentioned method returns an object of type
     * {@link XmlErrors }. If the error list is not empty then the method throws
     * an exception for the first error in the list.
     *
     * @param child the element to be added
     * @return a owner {@code  xml element}.
     * @see XmlErrors
     * @see XmlError
     * @see #checkElement(org.netbeans.modules.jeeserver.base.deployment.xml.XmlElement)
     */
    public XmlCompoundElement add(XmlElement child) {
        if (owner instanceof XmlTextElement) {
            if (((XmlTextElement) owner).getTextContent() != null) {
                throw new IllegalStateException(
                        "XmlCompoundElement.addChild(): can't add child since the element has not emty text content");
            }
        }

        if (child.getElement() != null && XmlDocument.existsInDOM(child.getElement())) {
            throw new IllegalArgumentException(" The parameter is already in DOM tree and cannot be added ");
        }
        if (contains(child)) {
            return owner;
        }
        XmlErrors errors = checkElement(child);

        for (XmlError error : errors.getErrorList()) {
            if (error.isWarning()) {
                continue;
            }
            throw error.getException();
        }

        owner.beforeAddChild(child);
        child.setParent(owner);
        childs.add(child);
        return owner;

    }

    /**
     * Deletes a given element from an internal collection. If the internal
     * collections already contains such an element then the method does
     * nothing.
     *
     * @param child an object to be deleted.
     *
     * @return a owner object of type {@link XmlCompoundElement }
     * of the element to be deleted.
     */
    public XmlCompoundElement remove(XmlElement child) {
        if (child == null || !contains(child)) {
            return owner;
        }
        if (child.getElement() != null && child.getElement().getParentNode() != null) {
            XmlDocument.getParentElement(child.getElement()).removeChild(child.getElement());
        }
        childs.remove(child);

        return owner;
    }

    /**
     * Replaces the child element specified by the first parameter with a new
     * element as specified by the second parameter. If an element specified by
     * the first parameter cannot be found in the child list of the owner
     * (element then the newChild specified by the second parameter is just
     * added to the child list.
     * <p>
     * The method may throw an {@link IllegalArgumentException }
     * when {@code  newChild.getElement() is set to null }
     * <p>
     * The method may throw an {@link IllegalArgumentException }
     * when {@code  newChild.getElement() != null } and  
     * {@code  newChild.getElement() } is already in {@code DOM Tree}.
     * <p>
     * To make additional checking the method invokes {@code checkElement(XmlElement)
     * }
     * with the {@code newChild} as an argument. The mentioned method returns an
     * object of type {@link XmlErrors }. If the error list is not empty then
     * the method throws an exception for the first error in the list.
     *
     *
     * @param child the element to be replaced
     * @param newChild the element to replace an existing one
     * @return an object of type {@link XmlCompoundElement } which represents an
     * object which calls this method (owner element)
     */
    public XmlCompoundElement replace(XmlElement child, XmlElement newChild) {
        if (newChild == null) {
            throw new IllegalArgumentException(
                    " XmlChilds.replaceChild: The second parameter of the method can't be null");
        }
        if (child != null && contains(child)) {
            remove(child);
        }
        if (newChild.getElement() != null && XmlDocument.existsInDOM(newChild.getElement())) {
            throw new IllegalStateException("XmlChilds.replaceChild: The newChild parameter is already in DOM tree and cannot be added ");
        }

        XmlErrors errors = checkElement(newChild);

        for (XmlError error : errors.getErrorList()) {
            if (error.isWarning()) {
                continue;
            }
            throw error.getException();
        }

        owner.getChilds().add(newChild);
        return owner;

    }

    /**
     * Returns a list of child elements that satisfies the given path. The
     * parameter specifies a relative path to the {@link #owner } of this
     * object.
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
     */
    public List<XmlElement> findChildsByPath(final String path) {
        List<XmlElement> list = new ArrayList<>();
        Visitor v = new Visitor();
        v.visit(this, el -> {
            String p = relativePath(el);
            if (path.equals(p)) {
                list.add(el);
            } else if (path.endsWith("*")) {
                if (p.lastIndexOf("/") < 0) {
                    p = "*";
                } else {
                    p = p.substring(0, p.lastIndexOf("/")) + "/*";
                }
                if (path.equals(p)) {
                    list.add(el);
                }
            }
        });
        return list;
    }

    /**
     * Returns a list of child elements that satisfies the given path. The
     * parameter specifies a relative path to the {@link #owner } of this
     * object.
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
     * @param predicate a condition for an element to be added to the result
     * list
     *
     * @return Returns a list of child elements that satisfies the given path.
     */
    public List<XmlElement> findChildsByPath(final String path, Predicate<XmlElement> predicate) {
        //return getChilds().findChildsByPath(path);
        List<XmlElement> list = findChildsByPath(path);
        List<XmlElement> result = new ArrayList<>();
        list.forEach(el -> {
            if (predicate.test(el)) {
                result.add(el);
            }
        });
        return result;
    }

    /**
     *
     * @param tomergeList
     * @param predicate
     * @return
     */
    public XmlChilds merge(List<XmlElement> tomergeList, BiPredicate<XmlElement, XmlElement> predicate) {
        tomergeList.forEach(mel -> {
            boolean replaced = false;
            //XmlElement clone = mel.getClone();
            for (XmlElement el : childs) {
                if (predicate.test(el, mel)) {
                    mel.nullElement();
                    replace(el, mel);
                    replaced = true;
                    break;
                }
            }
            if (!replaced) {
                mel.nullElement();
                add(mel);
            }
        });
        return this;
    }

    public XmlChilds mergeClones(XmlChilds tomerge, Converter<XmlElement, XmlElement> converter) {
        List<XmlElement> tomergeList = tomerge.list();
        tomergeList.forEach(mel -> {
            XmlElement clone = null;
            XmlElement found = null;
            for (XmlElement el : childs) {
                if (converter.test(el, mel)) {
                    clone = converter.convert(mel);
                }
                if (clone != null) {
                    found = el;
                    break;
                }
            }
            if (found != null) {
                replace(found, clone);
            } else {
                clone = converter.convert(mel);
                if (clone != null) {
                    add(clone);
                }
            }
        });
        return this;
    }

    @FunctionalInterface
    public static interface Converter<T, V> {

        default boolean test(T el1, T el2) {
            return convert((T) el1, (T) el2) != null;
        }

        /**
         * Accepts one or two parameters
         *
         * @param el1
         * @param el2
         * @return
         */
        V convert(T el1, T el2);

        default V convert(T el) {
            return convert(null, el);
        }
    }

    public static class Visitor {

        public Visitor() {
        }

        public void visit(XmlChilds c, Consumer<XmlElement> consumer) {
            c.list().forEach(e -> {
                consumer.accept(e);
                if (e instanceof XmlCompoundElement) {
                    visit(((XmlCompoundElement) e).getChilds(), consumer);
                }
            });
        }
    }//class Visitor

    public static class ParentVisitor {

        private boolean stop;

        public ParentVisitor() {
            //this.rootChilds = childs;
        }

        /**
         *
         */
        public void stop() {
            this.stop = true;
        }

        public void visit(XmlElement el, Consumer<XmlElement> consumer) {
            if (stop) {
                // allows reuse the same ParentVisitor
                stop = false;
            }

            consumer.accept(el);
            if (stop) {
                return;
            }
            if (el.getParent() == null || (el instanceof XmlBase)) {
                return;
            }
            visit(el.getParent(), consumer);
        }

        public void visit(XmlCompoundElement upperBoundary, XmlElement el, Consumer<XmlElement> consumer) {
            if (stop) {
                // allows reuse the same ParentVisitor
                stop = false;
            }

            consumer.accept(el);
            if (stop) {
                return;
            }
            if (el.getParent() == null || el.getParent() == upperBoundary) {
                return;
            }
            visit(upperBoundary, el.getParent(), consumer);
        }

    }//class ParentVisitor

}//class
