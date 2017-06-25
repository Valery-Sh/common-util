package org.vns.common.files;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

/**
 * Event for listening on file system changes which are watching by an object of type
 * {@code WatchService}.
 * The class is used with objects of type {@link FileChangeListener}.
 * 
 * @author Valery Shyshkin
 */
public class FileEvent {
    private final Path watchable;
    private final Path context;
    private final WatchEvent.Kind kind;
    /**
     * Creates an instance of the class for the given {@code watchable path, context}
     *  and event {@code kind}.
     * 
     * @param watchable the path which corresponds to the registered path with 
     * a {@code WatchService}.
     * @param context the path of a folder or a file which 
     * is the relative path between the specified {@code watchable}, and the 
     * entry that is created, deleted, or modified.
     * 
     * @param kind may be one of ENTRY_CREATE, ENTRY_DELETE, and ENTRY_MODIFY
     */
    public FileEvent(Path watchable, Path context, WatchEvent.Kind kind) {
        this.watchable = watchable;
        this.context = context;
        this.kind = kind;
    }
    /**
     * Returns the path which is registered with a {@code WatchService}.
     * @return the path which is registered with a {@code WatchService}.
     */
    public Path getWatchable() {
        return watchable;
    }
    /**
     * Returns the path of a folder or a file which 
     * is the relative path between the {@code watchable} path and the 
     * entry that is created, deleted, or modified.
     * 
     * @return the path of a folder or a file which is the relative path 
     *    between the {@code watchable} path and the entry that is created,
     *    deleted, or modified.
     */
    public Path getContext() {
        return context;
    }
    /**
     * Returns one of the values: ENTRY_CREATE, ENTRY_DELETE, and ENTRY_MODIFY
     * @return one of the values: ENTRY_CREATE, ENTRY_DELETE, and ENTRY_MODIFY
     */
    public WatchEvent.Kind getKind() {
        return kind;
    }
    
}
