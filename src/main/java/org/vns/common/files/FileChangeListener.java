package org.vns.common.files;

import java.util.EventListener;

/**
 *
 * @author Valery Shyshkin
 */
public interface FileChangeListener extends EventListener {

    /**
     * Fired when a data file is modified.
     *
     * @param ev the event describing context where action has taken place
     */
    void fileModified(FileEvent ev);

    /**
     * Fired when a content of a folder is modified.
     *
     * @param ev the event describing context where action has taken place
     */
    void folderModified(FileEvent ev);

    /**
     * Fired when a new data file is created.
     *
     * @param ev the event describing context where action has taken place
     */
    void fileCreated(FileEvent ev);

    /**
     * Fired when a new folder is created.
     *
     * @param ev the event describing context where action has taken place
     */
    void folderCreated(FileEvent ev);

    /**
     * Fired when a data file is deleted.
     *
     * @param ev the event describing context where action has taken place
     */
    void fileDeleted(FileEvent ev);
    /**
     * Fired when a folder is deleted. 
     * @param ev the event describing context where action has taken place
     */
    void folderDeleted(FileEvent ev);
}
