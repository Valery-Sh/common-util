package org.vns.common.util.prefs.files;

import java.util.prefs.AbstractPreferences;
import java.util.prefs.Preferences;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.vns.common.PathObject;
import org.vns.common.util.prefs.Storage;

/**
 *
 * @author Valery
 */
public class FilePreferencesTest {
    
    public FilePreferencesTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getRoot method, of class FilePreferences.
     */
    @Test
    public void testGetRoot() {
        System.out.println("getRoot");
        FilePreferences instance = null;
        Preferences expResult = null;
        Preferences result = instance.getRoot();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of storageType method, of class FilePreferences.
     */
    @Test
    public void testStorageType() {
        System.out.println("storageType");
        FilePreferences instance = null;
        String expResult = "";
        String result = instance.storageType();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of typeExtention method, of class FilePreferences.
     */
    @Test
    public void testTypeExtention() {
        System.out.println("typeExtention");
        FilePreferences instance = null;
        String expResult = "";
        String result = instance.typeExtention();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getStorage method, of class FilePreferences.
     */
    @Test
    public void testGetStorage_String() {
        System.out.println("getStorage");
        String absolutePath = "";
        FilePreferences instance = null;
        Storage expResult = null;
        Storage result = instance.getStorage(absolutePath);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getStorage method, of class FilePreferences.
     */
    @Test
    public void testGetStorage_FilePreferences_String() {
        System.out.println("getStorage");
        FilePreferences parent = null;
        String absolutePath = "";
        FilePreferences instance = null;
        Storage expResult = null;
        Storage result = instance.getStorage(parent, absolutePath);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of childSpi method, of class FilePreferences.
     */
    @Test
    public void testChildSpi() {
        System.out.println("childSpi");
        String name = "";
        FilePreferences instance = null;
        AbstractPreferences expResult = null;
        AbstractPreferences result = instance.childSpi(name);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of node method, of class FilePreferences.
     */
    @Test
    public void testNode() {
        System.out.println("node");
        String path = "";
        FilePreferences instance = null;
        FilePreferences expResult = null;
        FilePreferences result = instance.node(path);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of storage method, of class FilePreferences.
     */
    @Test
    public void testStorage() {
        System.out.println("storage");
        FilePreferences instance = null;
        Storage expResult = null;
        Storage result = instance.storage();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of sfsRoot method, of class FilePreferences.
     */
    @Test
    public void testSfsRoot() {
        System.out.println("sfsRoot");
        FilePreferences instance = null;
        PathObject expResult = null;
        PathObject result = instance.sfsRoot();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of properties method, of class FilePreferences.
     */
    @Test
    public void testProperties() {
        System.out.println("properties");
        FilePreferences instance = new FilePreferences("d:/0temp/WDIR");
        FilePreferences p1 = instance.node("fio");
        String s = p1.absolutePath();
        
        p1.put("valery", "shyshkin");
        p1.get("valery");
        
//        PropertiesExt result = instance.properties();
//+        assertEquals(expResult, result);
    }

    /**
     * Test of sectionName method, of class FilePreferences.
     */
    @Test
    public void testSectionName() {
        System.out.println("sectionName");
        FilePreferences instance = null;
        String expResult = "";
        String result = instance.sectionName();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setProperties method, of class FilePreferences.
     */
    @Test
    public void testSetProperties() {
        System.out.println("setProperties");
        String key = "";
        String[] values = null;
        FilePreferences instance = null;
        String expResult = "";
        String result = instance.setProperties(key, values);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAsArray method, of class FilePreferences.
     */
    @Test
    public void testGetAsArray() {
        System.out.println("getAsArray");
        String key = "";
        FilePreferences instance = null;
        String[] expResult = null;
        String[] result = instance.getAsArray(key);
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of join method, of class FilePreferences.
     */
    @Test
    public void testJoin() {
        System.out.println("join");
        String key = "";
        String def = "";
        FilePreferences instance = null;
        String expResult = "";
        String result = instance.join(key, def);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of childrenNamesSpi method, of class FilePreferences.
     */
    @Test
    public void testChildrenNamesSpi() throws Exception {
        System.out.println("childrenNamesSpi");
        FilePreferences instance = null;
        String[] expResult = null;
        String[] result = instance.childrenNamesSpi();
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removeNodeSpi method, of class FilePreferences.
     */
    @Test
    public void testRemoveNodeSpi() throws Exception {
        System.out.println("removeNodeSpi");
        FilePreferences instance = null;
        instance.removeNodeSpi();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removeSpi method, of class FilePreferences.
     */
    @Test
    public void testRemoveSpi() {
        System.out.println("removeSpi");
        String key = "";
        FilePreferences instance = null;
        instance.removeSpi(key);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of keysSpi method, of class FilePreferences.
     */
    @Test
    public void testKeysSpi() throws Exception {
        System.out.println("keysSpi");
        FilePreferences instance = null;
        String[] expResult = null;
        String[] result = instance.keysSpi();
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSpi method, of class FilePreferences.
     */
    @Test
    public void testGetSpi() {
        System.out.println("getSpi");
        String key = "";
        FilePreferences instance = null;
        String expResult = "";
        String result = instance.getSpi(key);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of get method, of class FilePreferences.
     */
    @Test
    public void testGet() {
        System.out.println("get");
        String key = "";
        FilePreferences instance = null;
        String[] expResult = null;
        String[] result = instance.get(key);
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of put method, of class FilePreferences.
     */
    @Test
    public void testPut() {
        System.out.println("put");
        String key = "";
        String[] values = null;
        FilePreferences instance = null;
        instance.put(key, values);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getProperty method, of class FilePreferences.
     */
    @Test
    public void testGetProperty() {
        System.out.println("getProperty");
        String key = "";
        FilePreferences instance = null;
        String expResult = "";
        String result = instance.getProperty(key);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setProperty method, of class FilePreferences.
     */
    @Test
    public void testSetProperty() {
        System.out.println("setProperty");
        String key = "";
        String value = "";
        FilePreferences instance = null;
        instance.setProperty(key, value);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addProperty method, of class FilePreferences.
     */
    @Test
    public void testAddProperty() {
        System.out.println("addProperty");
        String key = "";
        String value = "";
        FilePreferences instance = null;
        int expResult = 0;
        int result = instance.addProperty(key, value);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addProperties method, of class FilePreferences.
     */
    @Test
    public void testAddProperties() {
        System.out.println("addProperties");
        String key = "";
        String[] values = null;
        FilePreferences instance = null;
        instance.addProperties(key, values);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of putSpi method, of class FilePreferences.
     */
    @Test
    public void testPutSpi() {
        System.out.println("putSpi");
        String key = "";
        String value = "";
        FilePreferences instance = null;
        instance.putSpi(key, value);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of asyncInvocationOfFlushSpi method, of class FilePreferences.
     */
    @Test
    public void testAsyncInvocationOfFlushSpi() {
        System.out.println("asyncInvocationOfFlushSpi");
        FilePreferences instance = null;
        instance.asyncInvocationOfFlushSpi();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of asyncInvocationOfFlushSpi_1 method, of class FilePreferences.
     */
    @Test
    public void testAsyncInvocationOfFlushSpi_1() {
        System.out.println("asyncInvocationOfFlushSpi_1");
        FilePreferences instance = null;
        instance.asyncInvocationOfFlushSpi_1();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of flush method, of class FilePreferences.
     */
    @Test
    public void testFlush() throws Exception {
        System.out.println("flush");
        FilePreferences instance = null;
        instance.flush();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of flushSpi method, of class FilePreferences.
     */
    @Test
    public void testFlushSpi() throws Exception {
        System.out.println("flushSpi");
        FilePreferences instance = null;
        instance.flushSpi();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of sync method, of class FilePreferences.
     */
    @Test
    public void testSync() throws Exception {
        System.out.println("sync");
        FilePreferences instance = null;
        instance.sync();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of syncSpi method, of class FilePreferences.
     */
    @Test
    public void testSyncSpi() throws Exception {
        System.out.println("syncSpi");
        FilePreferences instance = null;
        instance.syncSpi();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of headerComments method, of class FilePreferences.
     */
    @Test
    public void testHeaderComments() {
        System.out.println("headerComments");
        FilePreferences instance = null;
        String[] expResult = null;
        String[] result = instance.headerComments();
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setHeaderComments method, of class FilePreferences.
     */
    @Test
    public void testSetHeaderComments() {
        System.out.println("setHeaderComments");
        String[] comments = null;
        FilePreferences instance = null;
        instance.setHeaderComments(comments);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of bottomComments method, of class FilePreferences.
     */
    @Test
    public void testBottomComments() {
        System.out.println("bottomComments");
        FilePreferences instance = null;
        String[] expResult = null;
        String[] result = instance.bottomComments();
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setBottomComments method, of class FilePreferences.
     */
    @Test
    public void testSetBottomComments() {
        System.out.println("setBottomComments");
        String[] comments = null;
        FilePreferences instance = null;
        instance.setBottomComments(comments);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of sectionComments method, of class FilePreferences.
     */
    @Test
    public void testSectionComments() {
        System.out.println("sectionComments");
        FilePreferences instance = null;
        String[] expResult = null;
        String[] result = instance.sectionComments();
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setSectionComments method, of class FilePreferences.
     */
    @Test
    public void testSetSectionComments() {
        System.out.println("setSectionComments");
        String[] comments = null;
        FilePreferences instance = null;
        instance.setSectionComments(comments);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of propertyComments method, of class FilePreferences.
     */
    @Test
    public void testPropertyComments() {
        System.out.println("propertyComments");
        String key = "";
        int idx = 0;
        FilePreferences instance = null;
        String[] expResult = null;
        String[] result = instance.propertyComments(key, idx);
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setPropertyComments method, of class FilePreferences.
     */
    @Test
    public void testSetPropertyComments() {
        System.out.println("setPropertyComments");
        String key = "";
        int idx = 0;
        String[] comments = null;
        FilePreferences instance = null;
        instance.setPropertyComments(key, idx, comments);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of remove method, of class FilePreferences.
     */
    @Test
    public void testRemove() {
        System.out.println("remove");
        String key = "";
        String value = "";
        FilePreferences instance = null;
        instance.remove(key, value);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of comment method, of class FilePreferences.
     */
    @Test
    public void testComment_String_String() {
        System.out.println("comment");
        String key = "";
        String value = "";
        FilePreferences instance = null;
        instance.comment(key, value);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of uncomment method, of class FilePreferences.
     */
    @Test
    public void testUncomment_String_String() {
        System.out.println("uncomment");
        String key = "";
        String value = "";
        FilePreferences instance = null;
        instance.uncomment(key, value);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of comment method, of class FilePreferences.
     */
    @Test
    public void testComment_String() {
        System.out.println("comment");
        String key = "";
        FilePreferences instance = null;
        instance.comment(key);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of uncomment method, of class FilePreferences.
     */
    @Test
    public void testUncomment_String() {
        System.out.println("uncomment");
        String key = "";
        FilePreferences instance = null;
        instance.uncomment(key);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
