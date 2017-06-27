/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.common.xml;

import java.io.InputStream;
import java.nio.file.Path;
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
public class XmlDocumentTest {
    private XmlRoot root;
    private XmlDocument xmlDocument;
    Map<String, String> rootMapping = new HashMap<>();
    Map<String, String> fullRootMapping = new HashMap<>();
    Map<String, String> patternRootMapping = new HashMap<>();
    
    public XmlDocumentTest() {
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

        //InputStream is = Util.getResourceAsStream("org/vns/common/resources/xml-shop-template.xml");
        InputStream is = Util.getResourceAsStream("org/vns/common/resources/FXMLDocument01.fxml");   
                                                                           
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
     * Test of getDocument method, of class XmlDocument.
     */
    @Test
    public void testGetDocument() {
        System.out.println("getDocument");
        XmlDocument instance = new XmlDocument();
        Document expResult = null;
        Document result = instance.getDocument();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of parse method, of class XmlDocument.
     */
    @Test
    public void testParse_0args() {
        System.out.println("parse");
        XmlDocument instance = new XmlDocument();
        Document expResult = null;
        Document result = instance.parse();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of parse method, of class XmlDocument.
     */
    @Test
    public void testParse_InputStream() {
        System.out.println("parse");
        InputStream is = null;
        XmlDocument instance = new XmlDocument();
        Document expResult = null;
        Document result = instance.parse(is);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of save method, of class XmlDocument.
     */
    @Test
    public void testSave_0args() throws Exception {
        System.out.println("save");
        XmlDocument instance = xmlDocument;
        root.addChild(new XmlDefaultElement("empty"));
        XmlDefaultElement el = new XmlDefaultElement("DockPane");
        root.addChild(el);
        el.addChild(new XmlDefaultElement("DockNode"));
        root.commitUpdates();
        long t1 = System.currentTimeMillis();
        instance.save();
        long t2 = System.currentTimeMillis();
        System.err.println("TIME=" +(t2-t1));
    }

    /**
     * Test of saveSpec method, of class XmlDocument.
     */
    @Test
    public void testSaveSpec() {
        System.out.println("saveSpec");
        XmlDocument instance = new XmlDocument();
        instance.saveSpec();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of save method, of class XmlDocument.
     */
    @Test
    public void testSave_Path_String() {
        System.out.println("save");
        Path targetDir = null;
        String newFileName = "";
        XmlDocument instance = new XmlDocument();
        instance.save(targetDir, newFileName);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of save method, of class XmlDocument.
     */
    @Test
    public void testSave_Path() {
        System.out.println("save");
        Path target = null;
        XmlDocument instance = new XmlDocument();
        instance.save(target);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getRoot method, of class XmlDocument.
     */
    @Test
    public void testGetRoot() {
        System.out.println("getRoot");
        XmlDocument instance = new XmlDocument();
        XmlRoot expResult = null;
        XmlRoot result = instance.getRoot();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of createElement method, of class XmlDocument.
     */
    @Test
    public void testCreateElement() {
        System.out.println("createElement");
        String tagName = "";
        XmlDocument instance = new XmlDocument();
        Element expResult = null;
        Element result = instance.createElement(tagName);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of existsInDOM method, of class XmlDocument.
     */
    @Test
    public void testExistsInDOM() {
        System.out.println("existsInDOM");
        Element element = null;
        boolean expResult = false;
        boolean result = XmlDocument.existsInDOM(element);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of hasChildElements method, of class XmlDocument.
     */
    @Test
    public void testHasChildElements() {
        System.out.println("hasChildElements");
        Element parent = null;
        boolean expResult = false;
        boolean result = XmlDocument.hasChildElements(parent);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getChildElements method, of class XmlDocument.
     */
    @Test
    public void testGetChildElements() {
        System.out.println("getChildElements");
        Element parent = null;
        List<Element> expResult = null;
        List<Element> result = XmlDocument.getChildElements(parent);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getParentChainList method, of class XmlDocument.
     */
    @Test
    public void testGetParentChainList() {
        System.out.println("getParentChainList");
        Element element = null;
        List<Element> expResult = null;
        List<Element> result = XmlDocument.getParentChainList(element);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getParentElement method, of class XmlDocument.
     */
    @Test
    public void testGetParentElement() {
        System.out.println("getParentElement");
        Element el = null;
        Element expResult = null;
        Element result = XmlDocument.getParentElement(el);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of hasParentElement method, of class XmlDocument.
     */
    @Test
    public void testHasParentElement() {
        System.out.println("hasParentElement");
        Element el = null;
        boolean expResult = false;
        boolean result = XmlDocument.hasParentElement(el);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFirstChildByTagName method, of class XmlDocument.
     */
    @Test
    public void testGetFirstChildByTagName() {
        System.out.println("getFirstChildByTagName");
        Element parent = null;
        String tagName = "";
        Element expResult = null;
        Element result = XmlDocument.getFirstChildByTagName(parent, tagName);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getChildsByTagName method, of class XmlDocument.
     */
    @Test
    public void testGetChildsByTagName() {
        System.out.println("getChildsByTagName");
        Element parent = null;
        String tagName = "";
        List<Element> expResult = null;
        List<Element> result = XmlDocument.getChildsByTagName(parent, tagName);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isRootElement method, of class XmlDocument.
     */
    @Test
    public void testIsRootElement() {
        System.out.println("isRootElement");
        Element element = null;
        boolean expResult = false;
        boolean result = XmlDocument.isRootElement(element);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
