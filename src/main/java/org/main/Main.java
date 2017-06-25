/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.main;

import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;
import org.vns.common.PathObject;
import org.vns.common.files.FilePathObject;

/**
 *
 * @author Valery
 */
public class Main {
    public static void main(String[] args) throws IOException {
        File TEMP = new File("d:/0temp/todel");
        PathObject toDel = FilePathObject.createDirectories(TEMP.toPath());
        toDel.delete();
/*        String sfsPath = TEMP.getPath() + "/a/b";
        //Preferences expResult = null;
        Preferences result = FilePreferences.fileUserRoot(sfsPath);
        Preferences propNode = result.node("C").node("D/E");
        Preferences propNode1 = result.node("C").node("D/E/M");
        
        propNode.put("key-test", "key-value");
        propNode1.put("key-test01", "key-value01");
        String str = propNode.get("key-test", null);
        String str1 = propNode.get("key-test01", null);        
        int i = 0;
        //assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
*/        
    }
}
