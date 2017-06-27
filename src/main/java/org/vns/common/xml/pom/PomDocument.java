package org.vns.common.xml.pom;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.vns.common.xml.AbstractCompoundXmlElement;
import org.vns.common.xml.XmlChilds;
import org.vns.common.xml.XmlCompoundElement;
import org.vns.common.xml.XmlDocument;
import org.vns.common.xml.XmlElement;
import org.vns.common.xml.XmlTagMap;
import org.w3c.dom.Element;


/**
/**
 * A simple wrapper around a {@code org.w3c.dom.Document }.
 * The class inherits {@link XmlDocument } and contains methods to simplify
 * operations with {@code Maven pom} documents.
 * Provides methods for creating and saving objects of type 
 * {@link Document }. Also provides convenience methods to manipulate
 * the document's {@code  DOM Tree }. 
 * The class can be used independently of the XML API just to process only 
 * simple {@code DOM Documents } in more convenient way.
 * 
 * @author Valery Shyshkin
 */
public class PomDocument extends XmlDocument {

    private static final Logger LOG = Logger.getLogger(XmlDocument.class.getName());

    /**
     *
     */
    public final static List<String> DEPENDENCY_ARTIFACT = new ArrayList<>();

    static {
        DEPENDENCY_ARTIFACT.add("groupId");
        DEPENDENCY_ARTIFACT.add("artifactId");
        DEPENDENCY_ARTIFACT.add("version");
        DEPENDENCY_ARTIFACT.add("scope");
        DEPENDENCY_ARTIFACT.add("type");
        DEPENDENCY_ARTIFACT.add("classifier");
        DEPENDENCY_ARTIFACT.add("optional");
        DEPENDENCY_ARTIFACT.add("systemPath");
        DEPENDENCY_ARTIFACT.add("exclusions");
    }

    /**
     * Creates a new object with a simplest DOM document. The document contains
     * only those tags that are required for any pom-document:
     * <ul>
     * <li>project</li>
     * <li>modelVersion</li>
     * <li>groupId</li>
     * <li>artifactId</li>
     * <li>version</li>
     * <li>packaging</li>
     *
     * </ul>
     *
     * If the {@code basics} parameter is not specified then then the
     * following defaults are accepted:
     * <ul>
     * <li>modelVersion = 4.0.0</li>
     * <li>groupId = pom1.pom2.pom3</li>
     * <li>artifactId = poma1.poma2.poma3</li>
     * <li>version = 1.2.3</li>
     * <li>packaging = jar</li>
     *
     * </ul>
     *
     * If the specified parameter is not an empty array then :
     * <ul>
     * <li>index 0 corresponds to groupId</li>
     * <li>index 1 corresponds to artifactId</li>
     * <li>index 2 corresponds to version</li>
     * <li>index 3 corresponds to packaging</li>
     * <li>index 4 corresponds to modelVersion</li>
     * </ul>
     *  If some array elements are not specified then 
     * for them the default values are accepted.
     * 
     * @param basics a string array of tag values
     * 
     * @see #setModelVersion(java.lang.String) 
     * @see #setGroupId(java.lang.String) 
     * @see #setArtifactId(java.lang.String) 
     * @see #setVersion(java.lang.String) 
     * @see #setPackaging(java.lang.String) 
     */
    public PomDocument(String... basics) {
//        super();
        init(basics);
        //init(BaseUtil.getResourceAsStream("org/netbeans/modules/jeeserver/base/deployment/resources/pom-template.xml"));
    }
    /**
     * Creates a new instance with the specified basic pom tag values.
     * 
     * @param groutId the value of tag named {@code groutId }
     * @param artifactId the value of tag named {@code  artifactId }
     * @param version the value of tag named {@code  version }
     * @param packaging the value of tag named {@code  packaging}
     */
    public PomDocument(String groutId, String artifactId, String version, String packaging) {
        this(new String[]{groutId, artifactId, version, packaging, "4.0.0", });
    }

    /**
     * Creates an instance by the specified path.
     * 
     * @param pomXml a path to a file to create a new instance
     */
    public PomDocument(Path pomXml) {
        super(pomXml);
    }

    /**
     * Creates an instance by the specified {@code InputStream }.
     * @param pomXmlStream an object of type {@link java.io.InputStream } used to 
     * create an instance.
     */
    public PomDocument(InputStream pomXmlStream) {
        super(pomXmlStream);
    }

    /**
     * Creates an instance by the specified {@code  DOM Document} object.
     * @param doc an object of type {@link org.w3c.dom.Document }.
     */
    public PomDocument(Document doc) {
        super(doc);
    }

    private void init(String[] basics) {

        String[] pomMin = new String[]{
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
            "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" "
            + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
            + "xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">",
            "<modelVersion>${modelVersion}</modelVersion>",
            "<groupId>${groupId}</groupId>",
            "<artifactId>${artifactId}</artifactId>",
            "<version>${version}</version>",
            "<packaging>${packaging}</packaging>",
            "</project>"
        };

        StringBuilder sb = new StringBuilder();

        for (String str : pomMin) {
            sb.append(str);
        }

        String result = sb.toString();

        String groupId = "pom1.pom2.pom3";
        String artifactId = "poma1.poma2.poma3";
        String version = "1.2.3";
        String packaging = "jar";
        String modelVersion = "4.0.0";

        switch (basics.length) {
            case 0:
                break;
            case 1:
                groupId = basics[0];
                break;
            case 2:
                groupId = basics[0];
                artifactId = basics[1];
                break;
            case 3:
                groupId = basics[0];
                artifactId = basics[1];
                version = basics[2];
                break;
            case 4:
                groupId = basics[0];
                artifactId = basics[1];
                version = basics[2];
                packaging = basics[3];
                break;
            case 5:
                groupId = basics[0];
                artifactId = basics[1];
                version = basics[2];
                packaging = basics[3];
                modelVersion = basics[4];

        }

        result = result.replace("${groupId}", groupId);
        result = result.replace("${artifactId}", artifactId);
        result = result.replace("${version}", version);
        result = result.replace("${packaging}", packaging);
        result = result.replace("${modelVersion}", modelVersion);

        InputStream is = new ByteArrayInputStream(result.getBytes());
        init(is);
    }

    private void init(InputStream pomXmlStream) {
        document = parse(pomXmlStream);
    }
    /**
     * Sets the specified string value as a text content of the pom tag named 
     * {@code modelVersion }
     * @param modelVersion the value to be set.
     */
    public void setModelVersion(String modelVersion) {
        assert modelVersion != null;
        Element el = getFirstChildByTagName(getDocument().getDocumentElement(), "modelVersion");
        assert el != null;
        el.setTextContent(modelVersion);
    }

    /**
     * Sets the specified string value as a text content of the pom tag named 
     * {@code groupId }.
     * 
     * @param groupId the value to be set
     */
    public void setGroupId(String groupId) {
        assert groupId != null;
        Element el = getFirstChildByTagName(getDocument().getDocumentElement(), "groupId");
        assert el != null;
        el.setTextContent(groupId);
    }

    /**
     * Sets the specified string value as a text content of the pom tag named 
     * {@code artifactId }.
     * 
     * @param artifactId the value to be set
     */
    public void setArtifactId(String artifactId) {
        assert artifactId != null;
        Element el = getFirstChildByTagName(getDocument().getDocumentElement(), "artifactId");
        assert el != null;
        el.setTextContent(artifactId);
    }

    /**
     * Sets the specified string value as a text content of the pom tag named 
     * {@code version }.
     * 
     * @param version the value to be set
     */
    public void setVersion(String version) {
        assert version != null;
        Element el = getFirstChildByTagName(getDocument().getDocumentElement(), "version");
        assert el != null;
        el.setTextContent(version);
    }

    /**
     * Sets the specified string value as a text content of the pom tag named 
     * {@code packaging }.
     * 
     * @param packaging the value to be set
     */
    public void setPackaging(String packaging) {
        assert packaging != null;
        Element el = getFirstChildByTagName(getDocument().getDocumentElement(), "packaging");
        assert el != null;
        el.setTextContent(packaging);
    }
    
    /**
     * Returns  a {code DOM Element} with a tag named {@code dependencies}.
     * The parent Element of the returned value must be a {@code DOM Document's } element.
     * @return a DOM Element with a tag named 'dependencies'
     */
    public Element getDomDependencies() {
        Element element = null;
        NodeList nl = getRoot().getElement().getChildNodes();

        if (nl != null && nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {
                if ((nl.item(i) instanceof Element)) {
                    if ("dependencies".equals(((Element) nl.item(i)).getTagName())) {
                        element = (Element) nl.item(i);
                    }
                }
            }
        }
        return element;
    }

    /**
     * Returns a list of all elements whose name are {@code dependency}.
     * The returned list contains only elements which are child of the
     * {@code dependencies} tag ( path = "project/dependencies").
     * @return Returns a list of all elements whose name are {@code dependency}
     */
    public List<Element> getDomDependencyList() {
        List<Element> childs = new ArrayList<>();

        Element dependencies = getDomDependencies();
        if (dependencies == null) {
            return childs;
        } else {
            childs = getChildsByTagName(dependencies, "dependency");
        }

        return childs;
    }

    /**
     * Returns child elements of the {@code dependency}  element.
     * 
     * @param dependency The element from which to retrieve the child elements
     * @return child elements of the {@code dependency}  element
     */
    public List<Element> getDomDependencyChilds(Element dependency) {
        List<Element> dependencyArtifacts = new ArrayList<>();

        NodeList nl = dependency.getChildNodes();

        if (nl != null && nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {
                if ((nl.item(i) instanceof Element)) {
                    Element el = (Element) nl.item(i);
                    if (DEPENDENCY_ARTIFACT.contains(el.getTagName())) {
                        dependencyArtifacts.add(el);
                    }
                }
            }
        }
        return dependencyArtifacts;
    }

    /**
     * Return an instance of the {@link PomRoot } class.
     * If an instance doesn't exist then a new one is created.
     * @return an instance of the {@link PomRoot } class.
     */
    @Override
    public PomRoot getRoot() {
        assert document != null;
        if (this.xmlRoot == null) {
            xmlRoot = new PomRoot(this);
        }
        return (PomRoot) xmlRoot;
    }

}
