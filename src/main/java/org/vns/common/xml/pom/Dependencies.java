package org.vns.common.xml.pom;

import java.util.List;
import org.vns.common.xml.AbstractCompoundXmlElement;
import org.vns.common.xml.XmlChilds;
import org.vns.common.xml.XmlCompoundElement;
import org.vns.common.xml.XmlElement;
import org.vns.common.xml.XmlTagMap;
import org.w3c.dom.Element;

/**
 * The class corresponds to the tag named "dependencies" of the {@code pom }
 * document.
 *
 * The element is used as a child element of the pom root {@code "project"}
 * element.
 *
 * @author Valery Shyshkin
 * @see Dependency
 * @see PomRoot
 */
public class Dependencies extends AbstractCompoundXmlElement {

    /**
     * Creates a new instance of the class with a {@code tagName} property value
     * equals to {@code "dependencies"}. Puts a single element to the
     * {@code tagMap} with a key {@code "dependency"} and value
     * {@code Dependency.class.getName()}
     */
    public Dependencies() {
        super("dependencies", null, null);
        init();
    }

    protected Dependencies(String tagName) {
        this();
    }

    protected Dependencies(Element element, XmlCompoundElement parent) {
        super("dependencies", element, parent);
        init();
    }

    protected Dependencies(XmlCompoundElement parent) {
        super("dependencies", null, parent);
        init();
    }

    private void init() {
        XmlTagMap map = new XmlTagMap();
        map.put("dependency", Dependency.class.getName());
        setTagMap(map);
        getTagMap().setDefaultClass(null);
    }

    /**
     * Finds an element of type {@link Dependency} for the given
     * {@code groupId, artifactId } and {@code type } values.
     *
     * @param groupId the value of the {@code text} property of the
     * {@code groupId} tag.
     * @param artifactId the value of the {@code text} property of the
     * {@code artifactId} tag.
     * @param type the value of the {@code text} property of the {@code type}
     * tag. The method takes into account that the tag type may be omitted. Then
     * the default value "jar" is accepted.
     *
     * @return an instance of the Dependency class. If the tag can't be found
     * then null value returns.
     */
    public Dependency findDependency(String groupId, String artifactId, String type) {
        Dependency result = null;
        List<XmlElement> list = getChilds().list();
        for (XmlElement e : list) {
            if (e instanceof Dependency) {
                Dependency d = (Dependency) e;
                if (!groupId.equals(d.getGroupId()) || !artifactId.equals(d.getArtifactId())) {
                    continue;
                }
                String t0 = type != null ? type : "jar";
                String t1 = d.getType() != null ? d.getType() : "jar";
                if (t0.equalsIgnoreCase(t1)) {
                    result = d;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Merges each element of type {@link Dependency} of the immediate child
     * elements from the specified source element to the collection of
     * {@code Dependency} objects of this element. The source element which is
     * specified by the parameter does not have to be of type
     * {@link Dependencies} but can be of any type that implements
     * {@link XmlCompoundElement}.
     * <p>
     * For each element of the source child collection of elements the method
     * searches for an element of type {@code Dependency} in this child element
     * collection. The search is successful if the element with the same values
     * as {@code groupId}, {@code artifactId} and {@code type} has been found.
     * In this case the found element is replaced with the one used for
     * searching. You should know that the {@code type} value may be omitted and
     * the default value {@code "jar" ) is considered.
     * If the element is not found then the elemeent from source collection
     * is added to this child collection.
     *
     * @param source the element which child elements of type {@code Dependency}
     * are to be copied.
     *
     * @return this element
     */
    protected Dependencies mergeDependencies(XmlCompoundElement source) {
        List<XmlElement> list = source.findElementsByPath("dependency");

        if (!list.isEmpty()) {
            getChilds().merge(list, (el1, el2) -> {
                return ((Dependency) el1).weakEquals(el2);
            });
        }
        return this;
    }

    /**
     * Merge the child collection of this element with another collection that
     * can be found by the given value in the given root. The value specified by
     * the first parameter corresponds to the attribute named {@code "api"} and
     * is used to find a collection of the root element specified by the second
     * parameter.
     * <p>
     * Searches an element of type {@link Dependencies} in the specified {@link XmlBase
     * } with an attribute named {@code api} and the value equals to the given
     * {@code apiName} value.
     *
     * If found then the child collection of this element is merged with the
     * child collection of the found element.
     * <p>
     * We'll reference the found collection as {@code merge collection} This
     * means that for each child {@code dependency } element of the merge
     * collection replaces an element with the same {@code groupId, artifactId}
     * and {@code type} values in the child collection of this element. If the
     * corresponding {@code dependency} is not found the the element from the
     * merge collection is added to the child collection of this element.
     * <p>
     * We should know that an element that is used as a replacement or addition
     * is not an original element of the merge collection but a clone of the
     * original element as specified by the {@link #getClone() } method. This
     * allows to keep a source collection immutable since merging.
     *
     *
     * @param apiName the value of the attribute named "api" of the
     * "dependencies" element.
     *
     * @param fromRoot the root of the xml document
     * @return this element
     * @see Dependency
     * @see XmlBase
     */
    public Dependencies mergeAPI(String apiName, XmlCompoundElement fromRoot) {
        if (fromRoot == null) {
            return null;
        }
        List<XmlElement> api = fromRoot.findElementsByPath("dependencies", el -> {
            String attrValue = el.getAttributes().get("api");
            return attrValue != null && apiName.equals(el.getAttributes().get("api"));
        });

        if (!api.isEmpty()) {
            XmlChilds tomerge = ((XmlCompoundElement) api.get(0)).getChilds();
            getChilds().mergeClones(tomerge, (el1, el2) -> {
                Dependency dp;
                Dependency dp1;
                Dependency clone;
                if (el1 != null) {
                    dp = (Dependency) el1;
                    return dp.weakEquals(el2) ? el2 : null;
                } else {
                    clone = (Dependency) el2.getClone();
                    return clone;
                }
            });
        }
        return this;
    }

    public Dependencies merge(Dependencies fromDependencies) {
        if (fromDependencies == null) {
            return null;
        }
        
        getChilds().mergeClones(fromDependencies.getChilds(), (el1, el2) -> {
            Dependency dp;
            Dependency dp1;
            Dependency clone;
            if (el1 != null) {
                dp = (Dependency) el1;
                return dp.weakEquals(el2) ? el2 : null;
            } else {
                clone = (Dependency) el2.getClone();
                return clone;
            }
        });
        return this;
    }

}
