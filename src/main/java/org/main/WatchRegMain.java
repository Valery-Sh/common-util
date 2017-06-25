/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.main;

import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import java.nio.file.WatchEvent.Kind;
import org.vns.common.PathObject;
import org.vns.common.files.FilePathObject;
import org.vns.common.files.WatchRegistry;

/**
 *
 * @author Valery
 */
public class WatchRegMain {
    
    public static void main(String[] args) throws Exception {
        PathObject poC = FilePathObject.createDirectories(Paths.get("d:/0temp/WDIR/C"));
        PathObject poC_c1 = FilePathObject.createDirectories(Paths.get("d:/0temp/WDIR/C/c1"));        
        PathObject poD = FilePathObject.createDirectories(Paths.get("d:/0temp/WDIR/D"));
        PathObject poD_d1 = FilePathObject.createDirectories(Paths.get("d:/0temp/WDIR/D/d1"));
        PathObject poD_d2 = FilePathObject.createDirectories(Paths.get("d:/0temp/WDIR/D/d2"));

        WatchRegistry wr = WatchRegistry.getInstance();
        Path path = Paths.get("d:/0temp/WDIR/C");
        
        //wr.register(path, new Kind[] {ENTRY_MODIFY,  ENTRY_CREATE, ENTRY_DELETE});
        //wr.register(path, new Kind[] {});        
        //wr.processEvents(true);
        
        //WatchRegistry wr1 = new WatchRegistry();
        WatchRegistry wr1 = WatchRegistry.getInstance();
        Path path1 = Paths.get("d:/0temp/WDIR/D");
        Path path2 = Paths.get("d:/0temp/WDIR/D/d2");
        
        wr1.register(path1, new Kind[] {ENTRY_MODIFY,  ENTRY_CREATE, ENTRY_DELETE});
        wr1.register(path, new Kind[] {ENTRY_MODIFY,  ENTRY_CREATE, ENTRY_DELETE});        
        //wr1.register(path2, new Kind[] {ENTRY_MODIFY,  ENTRY_CREATE, ENTRY_DELETE});                
        wr1.processEvents();
        
        System.out.println("AFTER new WatchRegistry()");
        Thread.sleep(3000);
        poD.delete();
        poC.delete();
        System.out.println("AFTER AFTERALL DELETE");
        
        //po = FilePathObject.createDirectories(Paths.get("d:/0temp/WDIR/D"));
        //po.delete();
        while(true) {
            Thread.sleep(100);
        }
        
            //System.out.println("1  AFTER new WatchRegistry()");
        
    }    
}
