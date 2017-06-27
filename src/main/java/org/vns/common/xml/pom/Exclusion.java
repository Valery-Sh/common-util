package org.vns.common.xml.pom;

import java.util.List;
import org.vns.common.xml.AbstractCompoundXmlElement;
import org.vns.common.xml.XmlChilds;
import org.vns.common.xml.XmlCompoundElement;
import org.vns.common.xml.XmlElement;
import org.vns.common.xml.XmlTagMap;
import org.w3c.dom.Element;
/**
 * The class corresponds to the tag named {@code "exclusion"} of the 
 * {@code pom } document.  
 * The element is used as a child element of {@link Exclusions} element.
 * 
 * @see Exclusions
 * @see Dependency
 * @author Valery Shyshkin
 */
public class Exclusion extends AbstractCompoundXmlElement {

    /**
     * Creates a new instance of the class with a {@code tagName} property value
     * equals to {@code "exclusion"}. Puts two items to the
     * {@code tagMap}:
     *  <pre>
     *      map.put("groupId", DependencyArtifact.class.getName());
     *      map.put("artifactId", DependencyArtifact.class.getName());
     *  </pre>
     *
     */
    public Exclusion() {
        super("exclusion", null, null);
        init();
    }

    protected Exclusion(String tagName) {
        this();    
    }

    protected Exclusion(Element element, XmlCompoundElement parent) {
        super("exclusion", element, parent);
        init();
    }

    protected Exclusion(XmlCompoundElement parent) {
        super("exclusion", null, parent);
        init();
    }

    private void init() {
        XmlTagMap map = new XmlTagMap();
        map.put("groupId", DependencyArtifact.class.getName());
        map.put("artifactId", DependencyArtifact.class.getName());
        setTagMap(map);
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
        return getChildTagValue("type");
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

        Exclusion o = (Exclusion) other;

        boolean b = false;

        return XmlElement.equals(getGroupId(), o.getGroupId())
                && XmlElement.equals(getArtifactId(), o.getArtifactId());
    }


    /*    @Override
    public Exclusion getClone() {
        Exclusion clone = (Exclusion) newInstance();
        getChilds().list().forEach(el -> {
                clone.addChild(el.getClone());
        });
        return clone;
    }
     */
}
