package org.vns.common.xml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * A simple wrapper around a {@code org.w3c.dom.Document }. Provides methods for
 * creating and saving objects of type {@link Document }. Also provides
 * convenience methods to manipulate the document's {@code  DOM Tree }. The class
 * can be used independently of the XML API just to process only simple 
 * {@code DOM Documents } in more convenient way.
 *
 */
public class XmlDocument {

    private static final Logger LOG = Logger.getLogger(XmlDocument.class.getName());

    private Path xmlPath;
    protected Document document;
    protected XmlRoot xmlRoot;

    protected XmlDocument() {
    }

    /**
     * Creates an new instance of the class by the given root name.
     *
     * @param rootName the root tag name of the DOM Document to be created.
     */
    public XmlDocument(String rootName) {
        //document = XMLUtil.createDocument(rootName, null, null, null);
        XmlDocBuilder.createDocument(rootName);
    }

    /**
     * Creates an new instance from the given object of type {@code InputStream
     * }.
     *
     * @param inputStream the source of the DOM Document as InputStream..
     */
    public XmlDocument(InputStream inputStream) {
        init(inputStream);
    }

    /**
     * Creates an instance by the specified {@code  DOM Document} object.
     *
     * @param doc an object of type {@link org.w3c.dom.Document }.
     */
    public XmlDocument(Document doc) {
        this.document = doc;
        Element e;
    }

    /**
     * Creates an instance by the specified path.
     *
     * @param xmlPath the path to a file to create a new instance
     */
    public XmlDocument(Path xmlPath) {
        this.xmlPath = xmlPath;
        init();
    }

    /**
     * Returns a {@link org.w3c.dom.Document } this object represents.
     *
     * @return
     */
    public Document getDocument() {
        return document;
    }

    private void init(InputStream is) {
        document = parse(is);
    }

    private void init() {
        document = parse();
    }

    /**
     * Creates and returns a new instance of {@link Document}. To create the
     * instance the method uses a file defined by null     {@link #XmlDocument(java.nio.file.Path) constructor.
     *
     * @return a new instance of {@link Document}.
     */
    protected Document parse() {
        return XmlDocBuilder.parse(xmlPath);


        /*      
            Document d = null;                
            try {
            FileObject pomFo = FileUtil.toFileObject(xmlPath.toFile());
            InputSource source = new InputSource(pomFo.getInputStream());
            d = XMLUtil.parse(source, false, false, null, new ParseEntityResolver());

        } catch (IOException | DOMException | SAXException ex) {
            LOG.log(Level.INFO, ex.getMessage());
        }
        return d;
         */
    }

    /**
     * Creates and returns a new instance of {@link Document} by the the
     * specified input stream.
     *
     * @return a new instance of {@link Document}.
     */
    protected Document parse(InputStream is) {
        return XmlDocBuilder.parse(is);
        /*        Document d = null;
        try {
            InputSource source = new InputSource(is);
            d = XMLUtil.parse(source, false, false, null, new ParseEntityResolver());
        } catch (IOException | DOMException | SAXException ex) {
            LOG.log(Level.INFO, ex.getMessage());
        }
        return d;
         */
    }

    /**
     * Saves the DOM Document object. To save the document the method uses
     * {@link TransformerFactory}
     *
     * @throws TransformerConfigurationException
     * @throws TransformerException
     */
    public void save() throws TransformerConfigurationException, TransformerException {

        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer();
        // !!! iFactory.setAttribute("indent-number", 4)
        DOMSource source = new DOMSource(document);
        StringWriter w;
        
        StreamResult result = new StreamResult(new StringWriter());
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATIO‌​N, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "yes");
        //StreamResult result = new StreamResult(new StringWriter());
        transformer.transform(source, result);
        String xmlString = result.getWriter().toString();
        System.err.println(xmlString);

    }

    public String saveSpec() {
        String xmlStr = null;
        try {
            DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
            DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
            LSSerializer writer = impl.createLSSerializer();
            writer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);
            LSOutput output = impl.createLSOutput();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            output.setByteStream(out);
            writer.write(document, output);
            xmlStr = new String(out.toByteArray());
            
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | ClassCastException ex) {
            Logger.getLogger(XmlDocument.class.getName()).log(Level.SEVERE, null, ex);
        }
        return xmlStr;
    }

    /**
     * Save the DOM Document to the file specified by the given directory and a
     * file name.
     *
     * @param targetDir the target directory where save the document
     * @param newFileName the file name to save the document to
     */
    public void save(Path targetDir, String newFileName) {
        try {
            Path p = Paths.get(targetDir.toString(), newFileName);
            Files.deleteIfExists(p);
            if (!Files.exists(p)) {
                Files.createDirectories(targetDir);
                p = Files.createFile(p);
            }
            save(p);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage());
        }
    }

    /**
     * Save the DOM Document to the file specified by the given path.
     *
     * @param target the target path where save the document
     */
    public synchronized void save(Path target) {
        XmlDocBuilder.save(document, target);
        /*        FileObject pomFo = FileUtil.toFileObject(target.toFile());
        try (OutputStream os = pomFo.getOutputStream()) {
            String encoding = document.getXmlEncoding();
            if (encoding == null) {
                encoding = "UTF-8";
            }
            XMLUtil.write(document, os, encoding);

        } catch (IOException ex) {
            LOG.log(Level.INFO, ex.getMessage());
        }
         */
    }

    /**
     * Return an instance of the {@link XmlRoot } class. If an instance doesn't
     * exist then a new one is created.
     *
     * @return an instance of the {@link XmlRoot } class.
     */
    public XmlRoot getRoot() {
        assert document != null;
        if (xmlRoot == null) {
            xmlRoot = new XmlRoot(this);
        }
        return xmlRoot;
    }

    /**
     * Convenient method to create a DOM Element. Useful for test purpose.
     *
     * @param tagName a tag name to create element
     * @return a new element created
     */
    public Element createElement(String tagName) {

        return getDocument().createElement(tagName);
    }

    //
    // --------- Static Methods -----------
    //
    /**
     * Checks whether the given DOM element is already added to the DOM Tree.
     *
     * @param element the element to be checked
     *
     * @return
     */
    public static boolean existsInDOM(Element element) {
        NodeList nl = element.getOwnerDocument().getElementsByTagName(element.getTagName());
        if (nl.getLength() == 0) {
            return false;
        }
        boolean found = false;
        for (int i = 0; i < nl.getLength(); i++) {
            if (nl.item(i) == element) {
                found = true;
                break;
            }
        }
        return found;
    }

    public static boolean hasChildElements(Element parent) {
        NodeList nl = parent.getChildNodes();
        boolean result = false;
        if (nl != null && nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {
                if ((nl.item(i) instanceof Element)) {
                    result = true;
                    break;
                }
            }
        }
        return result;

    }

    public static List<Element> getChildElements(Element parent) {
        List<Element> list = new ArrayList<>();

        NodeList nl = parent.getChildNodes();

        if (nl != null && nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {
                if ((nl.item(i) instanceof Element)) {
                    Element el = (Element) nl.item(i);
                    list.add(el);
                }
            }
        }
        return list;
    }

    public static List<Element> getParentChainList(Element element) {
        List<Element> list = new ArrayList<>();

        Element el = element;
        list.add(el);

        while (true) {

            if (XmlDocument.isRootElement(el)) {
                break;
            }
            if (!XmlDocument.hasParentElement(el)) {
                break;
            }
            el = XmlDocument.getParentElement(el);
            list.add(0, el);
        }
        return list;
    }

    public static Element getParentElement(Element el) {

        Element result = null;
        if (el.getParentNode() != null && (el.getParentNode() instanceof Element)) {
            result = (Element) el.getParentNode();
        }
        return result;

    }

    public static boolean hasParentElement(Element el) {
        return getParentElement(el) != null;

    }

    public static Element getFirstChildByTagName(Element parent, String tagName) {
        Element child = null;

        NodeList nl = parent.getChildNodes();

        if (nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {
                if ((nl.item(i) instanceof Element)) {
                    Element el = (Element) nl.item(i);
                    if (tagName.equals(el.getTagName())) {
                        child = el;
                        break;
                    }
                }
            }
        }
        return child;
    }

    public static List<Element> getChildsByTagName(Element parent, String tagName) {
        List<Element> childs = new ArrayList<>();

        NodeList nl = parent.getChildNodes();

        if (nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {
                if ((nl.item(i) instanceof Element)) {
                    Element el = (Element) nl.item(i);
                    if (tagName.equals(el.getTagName())) {
                        childs.add(el);
                    }
                }
            }
        }
        return childs;

    }

    public static boolean isRootElement(Element element) {
        return element.getOwnerDocument().getDocumentElement() == element;
    }

    /*    public static void disconnect(XmlElement el) {
        Element domEl = el.getElement();
        if ( domEl == null ) {
            return;
        }
        if ( domEl.getParentNode() != null ) {
            domEl.getParentNode().removeChild(domEl);
        }
        el.nullElement();
        if ( el instanceof XmlCompoundElement) {
            ((XmlCompoundElement) el).getChilds().list().forEach(e ->{
                disconnect(e);
            });
        }
    }
     */
}
