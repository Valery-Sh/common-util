package org.vns.common.xml;

import java.util.HashMap;
import java.util.Map;

/**
 * Class allows to define permissible tag names for elements and corresponding 
 * names of the classes that implement the interface {@link XmlElement}.
 * Every object of type {@link XmlCompoundElement} contains a read-only property
 * with a  name {@code  tagMap}. You can specify one or more key/value pairs
 * and put them into the internal collection of the {@code  XmlTag} object. 
 * Suppose we create an instance of the {@code  XmlCompoundElement}: <p>
 * <pre>
 *   XmlCompoundXmlImpl books = new XmlCompoundElement("books");
 * </pre>
 * And now you can add child to the {@code books} element: 
 * <pre>
 *   BookElement book = new BookElementElement("book");
 *   books.addChild(book);
 * </pre> 
 * This way you can add an element of any type and with any tag name. But what 
 * if you want to restrict the number of element types and there tag names? 
 * To achieve this goal you can specify: <p>
 * <pre>
 *   XmlCompoundXmlImpl books = new XmlCompoundElement("books");
 *   books.getTagMap()
 *       .put("book", BookElementElement.class.getName()
 * 
 *   BookElement book = new BookElement("book");
 *   books.addChild(book);
 * </pre> <p>
 * After this you can't execute the code below:
 * <p>
 * <pre>
 *    NodepadElement notepad = new NodepadElement("book");
 *    books.addChild(nodepad);
 * </pre>
 * An exception will be thrown because the class {@code NodepadElement} can't be 
 * found in the {@code tagMap} of the {@code books} element. The exception 
 * appears and if we try to execute the code : <p>
 * <pre>
 *    BookElementElement book01 = new BookElementElement("booooook");
 *    books.addChild(book01);
 * </pre>
 * <p>
 * This happens because there is no tag with a name {@code "booooook"} defined
 * in the {@tagMap} of the {@code books}.
 * 
 * @see XmlCompoundElement#getTagMap() 
 * @author Valery Shishkin
 */
public class XmlTagMap {

    private final Map<String, String> map;
    private String defaultClass;
    
    public XmlTagMap() {
        this(new HashMap<>());
    }

    public XmlTagMap(Map<String, String> map) {
        
        if ( map == null ) {
            this.map = new HashMap<>();
        } else {
            this.map = map;
        }
    }
    
    public boolean isTagPathSupported(String tagPath) {
        
        if (getDefaultClass() != null) {
            return true;
        }

        return null != get(tagPath);
    }
    
    public XmlTagMap put(String path, String clazz) {
        map.put(path, clazz);
        return this;
    }
    public XmlTagMap putAll(XmlTagMap other) {
        map.putAll(other.map);
        return this;
    }

    protected String remove(String path) {
        return map.remove(path);
    }

    public String get(String path) {
        return map.get(path);
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public int size() {
        return map.size();
    }

    public boolean containsPath(String path) {
        return map.containsKey(path);
    }

    public boolean containsClass(String clazz) {
        return map.containsValue(clazz);
    }

    public String getDefaultClass() {
        return defaultClass;
    }

    public void setDefaultClass(String defaultClass) {
        this.defaultClass = defaultClass;
    }
            
}
