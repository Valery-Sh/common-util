/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.common.xml;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.vns.common.Util;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Valery
 */
public class XmlRootTest {

    private XmlRoot root;
    private XmlDocument xmlDocument;
    Map<String, String> rootMapping = new HashMap<>();
    Map<String, String> fullRootMapping = new HashMap<>();
    Map<String, String> patternRootMapping = new HashMap<>();

    public XmlRootTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        rootMapping = new HashMap<>();
        rootMapping.put("books", XmlDefaultElement.class.getName());
        rootMapping.put("books/pen", XmlDefaultElement.class.getName());
        rootMapping.put("books/book", XmlDefaultTextElement.class.getName());
        // 
        // fullRootMapping
        //
        fullRootMapping = new HashMap<>();
        fullRootMapping.put("books", XmlDefaultElement.class.getName());
        fullRootMapping.put("books/pen", XmlDefaultElement.class.getName());
        fullRootMapping.put("books/pen/ink-pen", XmlDefaultElement.class.getName());
        fullRootMapping.put("books/pen/ink-pen/color", XmlDefaultTextElement.class.getName());

        fullRootMapping.put("books/pen/ball-pen", XmlDefaultElement.class.getName());
        fullRootMapping.put("books/pen/ball-pen/color", XmlDefaultTextElement.class.getName());

        fullRootMapping.put("books/book", XmlDefaultTextElement.class.getName());
        // 
        // patternRootMapping
        //
        patternRootMapping = new HashMap<>();
        patternRootMapping.put("books", XmlDefaultElement.class.getName());
        patternRootMapping.put("books/pen", XmlDefaultElement.class.getName());
        patternRootMapping.put("books/pen/*", XmlDefaultElement.class.getName());
        patternRootMapping.put("books/pen/ink-pen/*", XmlDefaultTextElement.class.getName());
        patternRootMapping.put("books/pen/ball-pen/*", XmlDefaultTextElement.class.getName());

        patternRootMapping.put("books/book", XmlDefaultTextElement.class.getName());

        InputStream is = Util.getResourceAsStream("org/vns/common/resources/xml-shop-template.xml");
/*        InputStream is1 = Util.getResourceAsStream("/org/vns/common/xml-shop-template.xml");        
        InputStream is2 = Util.getResourceAsStream("org/vns/common/resources/xml-shop-template.xml");
        InputStream is3 = Util.getResourceAsStream("/org/vns/common/resources/xml-shop-template.xml");        
*/
        xmlDocument = new XmlDocument(is);
        root = new XmlRoot(xmlDocument);

    }

    @After
    public void tearDown() {
    }

    /**
     * Test of findXmlRoot method, of class XmlRoot.
     */
    @Test
    public void testFindXmlRoot() {
        System.out.println("findXmlRoot");

        XmlRoot expResult = root;
        XmlCompoundElement books = new XmlDefaultElement("books");
        root.addChild(books);

        XmlRoot result = (XmlRoot) XmlBase.findXmlRoot(books);
        assertEquals(expResult, result);

        XmlElement book = new XmlDefaultTextElement("book");
        books.addChild(book);

        result = (XmlRoot) XmlBase.findXmlRoot(book);
        assertEquals(expResult, result);

    }

    /**
     * Test of getTagMap method, of class XmlRoot.
     */
    @Test
    public void testGetPaths() {
        System.out.println("getPaths");
        XmlRoot instance = root;
        XmlTagMap expResult = null;
        XmlTagMap result = instance.getTagMap();
        assertNotEquals(expResult, result);

        expResult = new XmlTagMap();
        root.setTagMap(expResult);
        result = instance.getTagMap();
        assertEquals(expResult, result);

    }

    /**
     * Test of setPaths method, of class XmlRoot.
     */
    @Test
    public void testSetTagNap() {
        System.out.println("setPaths");
        XmlRoot instance = root;
        XmlTagMap expResult = new XmlTagMap();

        root.setTagMap(expResult);

        XmlTagMap result = instance.getTagMap();
        assertEquals(expResult, result);

    }

    /**
     * Test of getParentChainList method, of class XmlRoot.
     */
    @Test
    public void testGetParentChainList() {
        System.out.println("getParentChainList");

        XmlCompoundElement books = new XmlDefaultElement("books");
        root.addChild(books);

        List<XmlElement> result = XmlRoot.getParentChainList(books);
        assertNotNull(result);
        //
        // The list must contain two elements: 
        //   first - root tag with a name 'shop'
        //   second - with a name 'books'
        //
        assertEquals(result.size(), 2);
        assertEquals(root, result.get(0));
        assertEquals(books, result.get(1));

        //
        // add another element
        //
        XmlElement book = new XmlDefaultTextElement("book");
        books.addChild(book);

        result = XmlRoot.getParentChainList(books);
        assertNotNull(result);
        // The list must contain three elements: 
        //   first  - root tag with a name 'shop'
        //   second - with a name 'books'
        //   third  - with a name 'book'
        //
        result = XmlRoot.getParentChainList(book);
        assertNotNull(result);
        assertEquals(result.size(), 3);
        assertEquals(root, result.get(0));
        assertEquals(books, result.get(1));
        assertEquals(book, result.get(2));
    }

    /**
     * Test of getParentChainList method, of class XmlRoot. This is the case
     * when the XmlDocument tree is not full/ We create a 'books' element with a
     * 'book' child and we dont add 'books' to the root of the document..
     */
    @Test
    public void testGetParentChainList_not_full_tree() {
        System.out.println("getParentChainList");

        XmlCompoundElement books = new XmlDefaultElement("books");

        List<XmlElement> result = XmlRoot.getParentChainList(books);
        assertNotNull(result);
        //
        // The list must contain a single element with a name 'books'
        //
        assertEquals(result.size(), 1);
        assertEquals(books, result.get(0));

        //
        // add another element
        //
        XmlElement book = new XmlDefaultTextElement("book");
        books.addChild(book);

        // The list must contain three elements: 
        //   first  - root tag with a name 'shop'
        //   second - with a name 'books'
        //   third  - with a name 'book'
        //
        result = XmlRoot.getParentChainList(book);
        assertNotNull(result);
        assertEquals(result.size(), 2);
        assertEquals(books, result.get(0));
        assertEquals(book, result.get(1));
    }

    /**
     * Test of getElement method, of class XmlRoot.
     */
    @Test
    public void testGetElement() {
        System.out.println("getElement");
        XmlRoot instance = root;

        //
        // The expected result must be a DOM doocument element
        //
        Element expResult = xmlDocument.getDocument().getDocumentElement();

        Element result = instance.getElement();
        assertEquals(expResult, result);
    }

    /**
     * Test of createDOMElement method, of class XmlRoot. The method in the
     * class {@link XmlRoot} overrides the method in the base class and does
     * nothing.
     */
    @Test
    public void testCreateDOMElement() {
        System.out.println("createDOMElement");
        XmlRoot instance = root;
        Element expResult = root.getElement();
        //
        // createDOMElement must do nothing
        //
        instance.createDOMElement();
        Element result = root.getElement();
        assertEquals(expResult, result);
    }

    /**
     * Test of isChildSupported method, of class XmlRoot.
     */
    @Test
    public void testIsChildSupported() {
        System.out.println("isChildSupported");
        String tagName = "";
        XmlRoot instance = null;
        boolean expResult = false;
//        boolean result = instance.isChildSupported(tagName);
//        assertEquals(expResult, result);
    }

    /**
     * Test of commitUpdates method, of class XmlRoot.
     */
    @Test
    public void testCommitUpdates() {
        System.out.println("commitUpdates");
        XmlRoot instance = null;
    }

    /**
     * Test of addComment method, of class XmlRoot.
     */
    @Test
    public void testAddComment() {
        /*        System.out.println("addComment");
        XmlElement pen = XmlChilds.findElementsByPath(root, "books/pen").get(0);
        Node nlb = root.getDocument().getElementsByTagName("books").item(0);
        
        NodeList nl = nlb.getChildNodes();
        
        for ( int i=0; i < nl.getLength(); i++) {
            Node n = nl.item(i);
            String v = n.getNodeValue();
            char[] chars = v.toCharArray();
            String tx = n.getTextContent();
            String s = n.toString();
        }
        
        XmlRoot.addComment(pen, " I found the pen and add a comment. \r Multy Line");
        XmlRoot.insertComment(pen, " I found the pen and Insert a comment");        
        root.commitUpdates();
        xmlDocument.save(Paths.get("d:/0temp"), "shop_add_comment_01");        
         */
    }

    @Test
    public void testFindElementsByPath() {
        System.out.println("findChildsByPath");        
        List<XmlElement> list = root.findElementsByPath("books/pen/ball-pen",
                (el) -> {
                    return el.getAttributes().get("attr1") != null;
                }
        );

        assertEquals(1, list.size());
    }

    @Test
    public void testFindElementsByPath_1() {
        System.out.println("findChildsByPath");        

        List<XmlElement> list = root.findElementsByPath("books/pen/*",
                (el) -> {
                    return el.getAttributes().get("attr1") != null;
                }
        );

        assertEquals(1, list.size());
    }

}
