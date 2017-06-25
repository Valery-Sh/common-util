package org.vns.common.files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import java.nio.file.WatchEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.vns.common.RequestExecutor;

/**
 * The class is a default implementation of the {@link AbstractWatchRegistry}
 * class. Allow to register one or more objects of type
 * {@code java.nio.file.Path} to a {@code WatchService} instance. Event
 * processing for each registered {@code watchable} object is performed in a
 * single separate thread. When registered the {@code watchable} object remains
 * active until is explicitly unregistered or the corresponding {code
 * WatchService ) is closed. Any number of listeners of type
 * {@link FileChangeListener} can be added to a registered {@code watchable}
 * object.
 *
 * @author Valery Shyshkin
 */
public class DefaultWatchRegistry extends AbstractWatchRegistry {

    protected RequestExecutor.Task task;

    /**
     * Creates a new instance of the class. The processing of events doesn't
     * begin when an instance created.
     */
    public DefaultWatchRegistry() {
        super();
    }
    /**
     * Closes the {@code WatchService} of this object.
     * First the method shutdowns the task where the events are processing. 
     *
     * @throws IOException if the close operation cannot be performed.
     */
    public void close() throws IOException {
        try {
            task.shutdownNow(100);
            if (isServiceAvailable()) {
                closeService();
            }
        } catch (Exception ex) {
            System.out.println("@@ CLOSE EXCEPTION");
        }
    }
    /**
     * Deletes a folder and all it's descendents recursively.
     *
     * The given folder is not required to be registered as {@code watchable}
     * but if it does than the method first unregister it and than try to
     * delete. This is a safe method to perform deletion of the registered
     * folder.
     *
     * @param path specifies a folder to be deleted
     * @throws IOException if the specified folder cannot be deleted/
     */
    public void deleteFolder(Path path) throws IOException {
        super.deleteFolder(path, true);
    }
/*    @Override
    public DefaultWatchRegistry.WatchableState register(Path path, Kind<?>[] kind) throws IOException {
        return super.register(path, kind);
    }
*/    
    /**
     * Starts a cycle to process events
     */
    public void processEvents() throws IOException {
        task = RequestExecutor.createTask(() -> {
            try {
                doProcessEvents();
            } catch (IOException ex) {
                Logger.getLogger(DefaultWatchRegistry.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        );//task

        task.schedule(0);
        task.shutdown();

    }
    @Override
    protected ListenerRecognizer getDefaultListenerRecognizer() {
        return (state, event, listener) -> {

            if (!(listener instanceof FileChangeListener)) {
                return false;
            }

            boolean retval = true;
            FileChangeListener fileListener = (FileChangeListener) listener;

            WatchEvent.Kind<?> kind = event.kind();
            FileEvent fe = new FileEvent(state.getPath(), (Path) event.context(), kind);

            Path eventSource = state.getPath().resolve((Path) event.context());

            if (event.kind() == ENTRY_CREATE) {
                if (Files.isDirectory(eventSource)) {
                    fileListener.folderCreated(fe);
                } else {
                    fileListener.fileCreated(fe);
                }

            } else if (kind == ENTRY_DELETE) {
                boolean isDirectory = state.isDirectory(eventSource);
                if (isDirectory) {
                    fileListener.folderDeleted(fe);
                } else {
                    fileListener.fileDeleted(fe);
                }
            } else if (kind == ENTRY_MODIFY) {
                boolean isDirectory = state.isDirectory(eventSource);
                if (isDirectory) {
                    fileListener.folderModified(fe);
                } else {
                    fileListener.fileModified(fe);
                }
            }

            return retval;
        };
    }

}
