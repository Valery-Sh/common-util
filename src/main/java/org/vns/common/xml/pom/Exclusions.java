package org.vns.common.xml.pom;

import org.w3c.dom.Element;
import org.vns.common.xml.AbstractCompoundXmlElement;
import org.vns.common.xml.XmlChilds;
import org.vns.common.xml.XmlCompoundElement;
import org.vns.common.xml.XmlElement;
import org.vns.common.xml.XmlTagMap;
import org.w3c.dom.Element;

/**
 * The class  corresponds to the  tag named  "exclusions" of the {@code pom } document. 
 *
 * The element is used as a child element of {@link Dependency} element.
 * 
 * @author Valery Shyshkin
 * @see Dependency
 */
public class Exclusions extends AbstractCompoundXmlElement {
    /**
     * Creates a new instance of the class with a {@code tagName} property value
     * equals to {@code "exclusions"}.
     * Puts a single element to the {@code tagMap} with a key 
     * {@code "exclusion"} and value {@code Exclusion.class.getName()}
     * 
     */
    public Exclusions() {
        super("exclusions", null, null);
        init();
    }

    protected Exclusions(String tagName) {
        this();
    }

    protected Exclusions(Element element, XmlCompoundElement parent) {
        super("exclusions", element, parent);
        init();
    }

    protected Exclusions(XmlCompoundElement parent) {
        super("exclusions", null, parent);
        init();
    }

    private void init() {
        XmlTagMap map = new XmlTagMap();
        map.put("exclusion", Exclusion.class.getName());
        setTagMap(map);
        getTagMap().setDefaultClass(null);
    }


}//class Exclusions
