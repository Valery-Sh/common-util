package org.vns.common.xml.pom;

import java.util.List;
import org.w3c.dom.Element;
import org.vns.common.xml.AbstractCompoundXmlElement;
import org.vns.common.xml.XmlChilds;
import org.vns.common.xml.XmlCompoundElement;
import org.vns.common.xml.XmlElement;
import org.vns.common.xml.XmlTagMap;
import org.w3c.dom.Element;


/**
 * The class that corresponds to the tag named 
 * {@code "dependency"} of the {@code pom } document. 
 *
 * The element is used as a child element of {@link Dependency} element.
 *  
 * @see Dependencise
 * @author Valery Shyshkin
 */
public class Dependency extends AbstractCompoundXmlElement {
    /**
     * Creates a new instance of the class with a {@code tagName} property value
     * equals to {@code "dependency"}. Puts the following items to the
     * {@code tagMap}:
     *  <pre>
     * map.put("groupId", DependencyArtifact.class.getName());
     *   map.put("artifactId", DependencyArtifact.class.getName());
     *   map.put("version", DependencyArtifact.class.getName());
     *   map.put("scope", DependencyArtifact.class.getName());
     *   map.put("type", DependencyArtifact.class.getName());
     *   map.put("optional", DependencyArtifact.class.getName());
     *   map.put("systemPath", DependencyArtifact.class.getName());
     *   map.put("exclusions", Exclusions.class.getName());     *  
     * </pre>
     *
     */

    public Dependency() {
        super("dependency", null, null);
        init();
    }

    protected Dependency(String tagName) {
        this();
    }

    protected Dependency(Element element, XmlCompoundElement parent) {
        super("dependency", element, parent);
        init();
    }

    protected Dependency(XmlCompoundElement parent) {
        super("dependency", null, parent);
        init();
    }

    private void init() {
        XmlTagMap map = new XmlTagMap();
        //map.put("dependency", Dependency.class.getName());
        map.put("groupId", DependencyArtifact.class.getName());
        map.put("artifactId", DependencyArtifact.class.getName());
        map.put("version", DependencyArtifact.class.getName());
        map.put("scope", DependencyArtifact.class.getName());
        map.put("type", DependencyArtifact.class.getName());
        map.put("optional", DependencyArtifact.class.getName());
        map.put("systemPath", DependencyArtifact.class.getName());
        map.put("exclusions", Exclusions.class.getName());

        setTagMap(map);
        getTagMap().setDefaultClass(null);
    }


    public DependencyArtifact findByTagName(String tagName) {
        List<XmlElement> list = getChilds().list();
        DependencyArtifact result = null;
        for (XmlElement el : list) {
            if ((el instanceof DependencyArtifact) && tagName.equals(el.getTagName())) {
                result = (DependencyArtifact) el;
                break;
            }
        }
        return result;
    }

    private String getChildTagValue(String tagName) {
        List<XmlElement> list = getChilds().list();
        String value = null;
        for (XmlElement pe : list) {
            if (tagName.equals(pe.getTagName())) {
                value = ((DependencyArtifact) pe).getTextContent();
            }
        }
        return value;
    }

    public String getGroupId() {
        return getChildTagValue("groupId");
    }

    public String getType() {
        String type = getChildTagValue("type");
        return type == null ? "jar" : type;
    }

    public String getArtifactId() {
        return getChildTagValue("artifactId");
    }

    public String getVersion() {
        return getChildTagValue("version");
    }

    @Override
    public boolean weakEquals(Object other) {
        if (!super.weakEquals(other)) {
            return false;
        }

        Dependency o = (Dependency) other;

        boolean b = false;
        String thisType = getType();
        if (thisType == null) {
            thisType = "jar"; //default type
        }
        String otherType = o.getType();
        if (otherType == null) {
            otherType = "jar"; //default type
        }

        return XmlElement.equals(getGroupId(), o.getGroupId())
                && XmlElement.equals(getArtifactId(), o.getArtifactId())
                && thisType.equals(otherType);
    }

}
