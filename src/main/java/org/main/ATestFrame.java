/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import java.nio.file.WatchEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.vns.common.PathObject;
import org.vns.common.RequestExecutor;
import org.vns.common.files.DefaultWatchRegistry;
import org.vns.common.files.FileChangeAdapter;
import org.vns.common.files.FileEvent;
import org.vns.common.files.FilePathObject;
import org.vns.common.files.WatchRegistry;

/**
 *
 * @author Valery
 */
public class ATestFrame extends javax.swing.JFrame {

    
    /**
     * Creates new form ATestFrame
     */
    public ATestFrame() {
        initComponents();
    }
    DefaultWatchRegistry instance = null;
    RequestExecutor.Task standaloneTask = null;

    DefaultWatchRegistry instanceAllKinds = null;
    RequestExecutor.Task standaloneTaskAllKinds = null;
    
    PathObject poC;
    PathObject poC_c1;
    PathObject poC_c2;

    PathObject poD;
    PathObject poD_d1;
    PathObject poD_d2;

    Path pathC = Paths.get("d:/0temp/WDIR/C");
    Path pathC_c1 = Paths.get("d:/0temp/WDIR/C/c1");
    Path pathC_c2 = Paths.get("d:/0temp/WDIR/C/c2");

    Path pathD = Paths.get("d:/0temp/WDIR/D");
    Path pathD_d1 = Paths.get("d:/0temp/WDIR/D/d1");
    Path pathD_d2 = Paths.get("d:/0temp/WDIR/D/d2");

    private void registerAllDefault() throws IOException {
        poC = FilePathObject.createDirectories(Paths.get("d:/0temp/WDIR/C"));
        poC_c1 = FilePathObject.createDirectories(Paths.get("d:/0temp/WDIR/C/c1"));
        poC_c2 = FilePathObject.createDirectories(Paths.get("d:/0temp/WDIR/C/c2"));
        poD = FilePathObject.createDirectories(Paths.get("d:/0temp/WDIR/D"));
        poD_d1 = FilePathObject.createDirectories(Paths.get("d:/0temp/WDIR/D/d1"));
        poD_d2 = FilePathObject.createDirectories(Paths.get("d:/0temp/WDIR/D/d2"));

        //WatchRegistry wr = new WatchRegistry();
        //wr.register(pathC, new Kind[] {ENTRY_MODIFY,  ENTRY_CREATE, ENTRY_DELETE});
        //wr.register(pathC, new Kind[] {});        
        //wr.processEvents(true);
        //WatchRegistry wr1 = new WatchRegistry();
        pathC = Paths.get("d:/0temp/WDIR/C");
        pathD = Paths.get("d:/0temp/WDIR/D");
        pathC_c1 = Paths.get("d:/0temp/WDIR/C/c1");
        pathD_d2 = Paths.get("d:/0temp/WDIR/D/d2");

        WatchRegistry.getInstance().register(pathD, new WatchEvent.Kind[]{ENTRY_MODIFY, ENTRY_CREATE, ENTRY_DELETE})
                .lock()
                .addListeners(createAdapter());
        WatchRegistry.getInstance().setVerbose(true);
                
        WatchRegistry.getInstance().register(pathC, new WatchEvent.Kind[]{ENTRY_MODIFY, ENTRY_CREATE, ENTRY_DELETE})
                .lock();
        //WatchRegistry.getInstance().register(pathD, new WatchEvent.Kind[]{ENTRY_MODIFY, ENTRY_CREATE, ENTRY_DELETE})
        //        .lock();

        WatchRegistry.getInstance().register(pathD_d2, new WatchEvent.Kind[]{ENTRY_MODIFY, ENTRY_CREATE, ENTRY_DELETE})
                .lock();
        WatchRegistry.getInstance().register(pathC, new WatchEvent.Kind[]{ENTRY_MODIFY, ENTRY_CREATE, ENTRY_DELETE})
                .lock();
        WatchRegistry.getInstance().register(pathC_c1, new WatchEvent.Kind[]{ENTRY_MODIFY, ENTRY_CREATE, ENTRY_DELETE})
                .lock();
        
        WatchRegistry.getInstance().processEvents();

    }

    private void deleteAll() {
        try {
            poD.delete();
            poC.delete();
            System.out.println("AFTER AFTERALL DELETE");
        } catch (IOException ex) {
            //Logger.getLogger(NewJFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    private void registerTest() throws IOException, InterruptedException {

        deleteAll();
    }
    Path registerPath(Path path, DefaultWatchRegistry reg, FileChangeAdapter adapter) throws IOException {
        //WatchEvent.Kind[] kind = new WatchEvent.Kind[]{ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY, OVERFLOW};
        WatchEvent.Kind[] kind = new WatchEvent.Kind[]{ENTRY_CREATE, ENTRY_MODIFY,OVERFLOW};
        reg.register(path, kind).addListeners(adapter);

        return path;
    }
    Path registerPathAllKinds(Path path, DefaultWatchRegistry reg, FileChangeAdapter adapter) throws IOException {
        //WatchEvent.Kind[] kind = new WatchEvent.Kind[]{ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY, OVERFLOW};
        WatchEvent.Kind[] kind = new WatchEvent.Kind[]{ENTRY_CREATE, ENTRY_MODIFY,ENTRY_DELETE,OVERFLOW};
        reg.register(path, kind).addListeners(adapter);

        return path;
    }

    

    FileChangeAdapter createAdapter() {
        FileChangeAdapter adapter = new FileChangeAdapter() {
            @Override
            public void fileCreated(FileEvent ev) {
                setResult("FILE_ENTRY_CREATE");
                System.out.println("FileChangeAdapter: RESULT:" + getResult());

            }

            @Override
            public void fileDeleted(FileEvent ev) {
                setResult("FILE_ENTRY_DELETE");
                System.out.println("FileChangeAdapter: RESULT:" + getResult());

            }

            @Override
            public void folderCreated(FileEvent ev) {
                setResult("FOLDER_ENTRY_CREATE");
                System.out.println("FileChangeAdapter: RESULT:" + getResult());

            }

            @Override
            public void folderDeleted(FileEvent ev) {
                setResult("FOLDER_ENTRY_DELETE");
                System.out.println("FileChangeAdapter: RESULT:" + getResult());
            }

        };
        return adapter;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        registerPathButton = new javax.swing.JButton();
        registerPathTextField = new javax.swing.JTextField();
        unregisterPathButton = new javax.swing.JButton();
        unregisterPathTextField = new javax.swing.JTextField();
        doStandaloneButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        deleteOnlyButton = new javax.swing.JButton();
        closeStandaloneButton = new javax.swing.JButton();
        doStandaloneButton1 = new javax.swing.JButton();
        deleteOnlyButton1 = new javax.swing.JButton();
        registerAllDefaultButton = new javax.swing.JButton();
        registerAllNoDelButton = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        printButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        registerPathButton.setText("register path");

        unregisterPathButton.setText("unregisterPath");

        doStandaloneButton.setText("do Standalone");
        doStandaloneButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doStandaloneButtonActionPerformed(evt);
            }
        });

        deleteButton.setText("delete(true)");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        deleteOnlyButton.setText("register D");
        deleteOnlyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteOnlyButtonActionPerformed(evt);
            }
        });

        closeStandaloneButton.setText("close");
        closeStandaloneButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeStandaloneButtonActionPerformed(evt);
            }
        });

        doStandaloneButton1.setText("do Standalone fileOnly");
        doStandaloneButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doStandaloneButton1ActionPerformed(evt);
            }
        });

        deleteOnlyButton1.setText("register D fileOnly");
        deleteOnlyButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteOnlyButton1ActionPerformed(evt);
            }
        });

        registerAllDefaultButton.setText("register All & delete");
        registerAllDefaultButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                registerAllDefaultButtonActionPerformed(evt);
            }
        });

        registerAllNoDelButton.setText("register All & no delete");
        registerAllNoDelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                registerAllNoDelButtonActionPerformed(evt);
            }
        });

        jButton2.setText("close service");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        printButton.setText("print Waychables");
        printButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(registerPathButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(unregisterPathButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(registerAllDefaultButton)
                            .addComponent(doStandaloneButton)
                            .addComponent(jButton2))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(registerPathTextField)
                                            .addComponent(unregisterPathTextField))
                                        .addContainerGap())
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(124, 124, 124)
                                        .addComponent(deleteOnlyButton)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(closeStandaloneButton)
                                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(registerAllNoDelButton)
                                .addContainerGap())))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(doStandaloneButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(deleteButton)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(deleteOnlyButton1)
                                .addGap(18, 18, 18)
                                .addComponent(printButton)))
                        .addGap(0, 24, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(registerPathButton)
                    .addComponent(registerPathTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(unregisterPathButton)
                    .addComponent(unregisterPathTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(doStandaloneButton)
                    .addComponent(deleteOnlyButton)
                    .addComponent(closeStandaloneButton))
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(doStandaloneButton1)
                    .addComponent(deleteOnlyButton1)
                    .addComponent(printButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(registerAllDefaultButton)
                    .addComponent(registerAllNoDelButton)
                    .addComponent(deleteButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton2)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void doStandaloneButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doStandaloneButtonActionPerformed
        try {
            System.out.println("doProcessEvents");

            File folderD = new File("d:/0temp/D");
            Path pathD = folderD.toPath();
            Path pathC = Paths.get("d:/0temp/C/c1");
            Path pathE = Paths.get("d:/0temp/E");
            FilePathObject foE = (FilePathObject) FilePathObject.createDirectories(pathE);
            if (!Files.exists(Paths.get("d:/0temp/E/e.txt"))) {
                foE.createData("e.txt");
            }

            Path pathD_d1 = Paths.get(pathD.toString(), "d1");
            PathObject fo = FilePathObject.createDirectories(pathD_d1);

            if (!Files.exists(Paths.get("d:/0temp/D/.meme.txt"))) {
                ((FilePathObject) fo.getParent()).createData(".meme.txt");
            }

            PathObject foc = FilePathObject.createDirectories(pathC.getParent());

            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
            }

            System.out.println("2222222222 " + fo.isFolder());

            instance = new DefaultWatchRegistry();

            FileChangeAdapter adapter = createAdapter();

            registerPath(pathD, instance, adapter);
            registerPath(pathC.getParent(), instance, adapter);
            //registerPath(pathE, instance, adapter);

            //
            // FOLDERS
            //
            //fo.delete();
            Thread.sleep(1000);
            String result = adapter.getResult();
            String expResult = "FOLDER_ENTRY_DELETE";
            System.out.println("  --- ADAPTER getResult()=" + result);
            //instance.closeService();
            //task.shutdownNow(0);

            System.out.println("--- END -----");
        } catch (IOException ex) {
        } catch (InterruptedException ex) {
        }

    }//GEN-LAST:event_doStandaloneButtonActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        try {
            File folderD = new File("d:/0temp/WDIR/D");
            Path pathD = folderD.toPath();

            Path pathD_d1 = Paths.get(pathD.toString(), "d1");
            PathObject fo = FilePathObject.createDirectories(pathD);

            //fo.delete(false);
            //instance.unregisterAndDeleteFolder(pathD);
            //instance.unregisterAndDeleteFolder(Paths.watchable("d:/0temp/E"));
            //WatchRegistry.getInstance().releaseLock(pathD);
            //fo.delete();
            String lockFileName = ".lock-a6882a7b-343f-4c75-a96e-2d0d3a927b48";            
            Path lockPath = pathD.resolve(Paths.get(lockFileName));
            //System.out.println("lockPath = " + lockPath);
            fo.delete();
        } catch (IOException ex) {
            System.out.println("!!! DELETE ERROR");
        }
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void deleteOnlyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteOnlyButtonActionPerformed
        try {
            System.out.println("REGISTER D");

            File folderD = new File("d:/0temp/D");
            Path pathD = folderD.toPath();
            Path pathD_d1 = Paths.get(pathD.toString(), "d1");
            PathObject fo = FilePathObject.createDirectories(pathD_d1);
            if (!Files.exists(Paths.get("d:/0temp/D/.meme.txt"))) {
                ((FilePathObject) fo.getParent()).createData(".meme.txt");
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
            }

            System.out.println("33333333333333 " + fo.isFolder());

            FileChangeAdapter adapter = createAdapter();

            registerPath(pathD, instance, adapter);
        } catch (IOException ex) {
        }
    }//GEN-LAST:event_deleteOnlyButtonActionPerformed

    private void closeStandaloneButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeStandaloneButtonActionPerformed
        try {
            standaloneTask.shutdownNow(100);
            instance.close();
            instance = null;
            standaloneTask = null;
            
            
        } catch (Exception ex) {
            System.out.println("close standolone EXCEPTION");
        }
    }//GEN-LAST:event_closeStandaloneButtonActionPerformed

    private void doStandaloneButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doStandaloneButton1ActionPerformed
        try {

            File folderD = new File("d:/0temp/D");
            Path pathD = folderD.toPath();

            PathObject fo = FilePathObject.createDirectories(pathD);

            if (!Files.exists(Paths.get("d:/0temp/D/.meme.txt"))) {
                ((FilePathObject) fo).createData(".meme.txt");
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
            }

            instance = new DefaultWatchRegistry();

            FileChangeAdapter adapter = createAdapter();

            
            instanceAllKinds = new DefaultWatchRegistry();

            FileChangeAdapter adapterAllKind = createAdapter();


            registerPathAllKinds(pathD, instanceAllKinds, adapterAllKind);
            
            registerPath(pathD, instance, adapter);
            
            
            //
            // FOLDERS
            //
            //fo.delete();
            Thread.sleep(1000);
            String result = adapter.getResult();
            String expResult = "FOLDER_ENTRY_DELETE";
            System.out.println("  --- ADAPTER getResult()=" + result);
            //instance.closeService();
            //task.shutdownNow(0);

            System.out.println("--- END -----");
        } catch (IOException ex) {
        } catch (InterruptedException ex) {
        }
    }//GEN-LAST:event_doStandaloneButton1ActionPerformed

    private void deleteOnlyButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteOnlyButton1ActionPerformed
        try {
            System.out.println("REGISTER D");

            File folderD = new File("d:/0temp/D");
            Path pathD = folderD.toPath();
            PathObject fo = FilePathObject.createDirectories(pathD);
            if (!Files.exists(Paths.get("d:/0temp/D/.meme.txt"))) {
                ((FilePathObject) fo).createData(".meme.txt");
            } 

            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
            }

            System.out.println("4444444444444 " + fo.isFolder());

            FileChangeAdapter adapter = createAdapter();

            registerPath(pathD, instance, adapter);
        } catch (IOException ex) {
        }
    }//GEN-LAST:event_deleteOnlyButton1ActionPerformed

    private void registerAllDefaultButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_registerAllDefaultButtonActionPerformed
        try {
            registerAllDefault();
            deleteAll();
        } catch (IOException ex) {
        }
    }//GEN-LAST:event_registerAllDefaultButtonActionPerformed

    private void registerAllNoDelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_registerAllNoDelButtonActionPerformed
        try {
            registerAllDefault();
        } catch (IOException ex) {
        }
    }//GEN-LAST:event_registerAllNoDelButtonActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        try {
            WatchRegistry.getInstance().close();
        } catch (IOException ex) {
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void printButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printButtonActionPerformed
        System.out.println(instance.stringView());
    }//GEN-LAST:event_printButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ATestFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ATestFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ATestFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ATestFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ATestFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeStandaloneButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton deleteOnlyButton;
    private javax.swing.JButton deleteOnlyButton1;
    private javax.swing.JButton doStandaloneButton;
    private javax.swing.JButton doStandaloneButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton printButton;
    private javax.swing.JButton registerAllDefaultButton;
    private javax.swing.JButton registerAllNoDelButton;
    private javax.swing.JButton registerPathButton;
    private javax.swing.JTextField registerPathTextField;
    private javax.swing.JButton unregisterPathButton;
    private javax.swing.JTextField unregisterPathTextField;
    // End of variables declaration//GEN-END:variables
}
