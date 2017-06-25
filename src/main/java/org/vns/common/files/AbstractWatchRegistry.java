package org.vns.common.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EventListener;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The class is a wrapper around the {@code java.nio.file.WatchService} class.
 * Allow to register one or more objects of type {@code java.nio.file.Path} to a
 * {@code WatchService} instance. Event processing for each registered
 * {@code watchable} object is performed in the same thread as the calling code.
 * When registered the {@code watchable} object remains active until is
 * explicitly unregistered or the corresponding {code WatchService ) is closed.
 * Any number of listeners of type {@link FileChangeListener} can be added to a
 * registered {@code watchable} object.
 *
 * @author Valery Shyshkin
 */
public class AbstractWatchRegistry {

    private WatchService watchService;

    private final Map<Path, WatchableState> stateMap = new ConcurrentHashMap<>();
    private final Map<Path, WatchableState> suspendMap = new ConcurrentHashMap<>();
    private BiConsumer<WatchableState, Path> createConsumer;

    private final List<ListenerRecognizer> slistenerRecognizers = new ArrayList<>();
    private final List<ListenerRecognizer> listenerRecognizers = Collections.synchronizedList(slistenerRecognizers);

    protected String id = "id01";
    /**
     * Prints detail info when process events
     */
    protected boolean verbose;

    /**
     * Creates a new instance of the class. The processing of events doesn't
     * begin when an instance created.
     */
    protected AbstractWatchRegistry() {
        init();
    }

    private void init() {
        initService();
        if (getDefaultListenerRecognizer() != null) {
            listenerRecognizers.add(getDefaultListenerRecognizer());
        }
    }

    /**
     * For this class the method does nothing. In subclasses may be overridden
     * to implement some actions to perform initialization of the new instance.
     */
    protected void initService() {
    }

    /**
     * Returns the number of registered objects of type {@code java.nio.Path}.
     *
     * @return the number of registered objects of type {@code java.nio.Path}.
     */
    public int count() {
        return stateMap.size();
    }

    public ListenerRecognizer[] getListenerRecognizers() {
        return listenerRecognizers.toArray(new ListenerRecognizer[0]);
    }

    /**
     * Add a function than is used as a handler for event listeners whose type
     * is not {@link FileChangeListener}.
     *
     *
     * @param recognizers an array of the functions to be used as a handler for
     * listeners other than FileChangeListener
     */
    public void addListenerRecognizers(ListenerRecognizer... recognizers) {
        listenerRecognizers.addAll(Arrays.asList(recognizers));
    }

    /**
     * Closes the {@code WatchService} of this object
     *
     * @throws IOException if the close operation cannot be performed.
     */
    protected void closeService() throws IOException {
        if (watchService == null) {
            return;
        }
        try {
            watchService().close();
            watchService = null;
        } finally {
            releaseLocks();
        }
    }

    /**
     * Releases locks of all registered paths.
     */
    public void releaseLocks() {
        stateMap.keySet().forEach(p -> {
            try {
                releaseLock(p);
            } catch (IOException ex) {
                Logger.getLogger(AbstractWatchRegistry.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    /**
     * The user can set a string identifier for this object in order to make
     * various messages recognizable. No restrictions are applied on the contend
     * of the string.
     *
     * @return an identifier of the object
     */
    public String getId() {
        return id;
    }

    /**
     * The user can set a string identifier for this object in order to make
     * various messages recognizable. No restrictions are applied on the contend
     * of the string.
     *
     * @param id an identifier to be set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Sets the value of property that specifies how and what messages are send
     * to the system console or log file.
     *
     * @param verbose if true than detailed information about events processing
     * is displayed on the system console and log file.
     */
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    /**
     * Tests whether an instance of type {@code WatchService} is assigned to
     * this object an is not null.
     *
     * @return true if the WatchService is assigned
     */
    public boolean isServiceAvailable() {
        return watchService != null;
    }

    /**
     * Returns an object of type {@code WatchService} that this object is
     * wrapped around.
     *
     * @return an object of type WatchService
     */
    protected WatchService watchService() throws IOException {
        if (watchService == null) {
            watchService = FileSystems.getDefault().newWatchService();
        }

        return watchService;
    }

    /**
     * Registers a given path as a {@code watchable} for the given event kinds.
     *
     * @param path the path that corresponds to an existing folder which becomes
     * a {@code watchable}.
     *
     * @param kind an array of event kinds to be watched
     * @return an object of type {@link WatchableState } that keeps all needed
     * information about the current state of the {@code watchable} object
     *
     * @throws IllegalStateException if the specified path is not a folder or
     * doesn't exist
     */
    public WatchableState register(Path path, WatchEvent.Kind<?>... kind) {
        if (!Files.isDirectory(path)) {
            return null;
        }
        if (isRegistered(path)) {
            return watchable(path);
        }
        WatchableState retval = null;
        try {
            WatchEvent.Kind<?>[] newKind = kind;
            if (kind.length == 0) {
                newKind = new WatchEvent.Kind<?>[]{ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE, OVERFLOW};
            }
            WatchKey key = path.register(watchService(), newKind);
            retval = new WatchableState(this, path, newKind, key);
            stateMap.put(path, retval);
        } catch (IOException ex) {
            Logger.getLogger(AbstractWatchRegistry.class.getName()).log(Level.SEVERE, null, ex);
            throw new IllegalStateException(ex);
        }
        return retval;
    }

    public void registerRecursively(Path path, Consumer<WatchableState> consumer) {
        if (!Files.isDirectory(path)) {
            return;
        }

        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    if (!isRegistered(dir)) {
                        register(dir, consumer);
                    }
                    return FileVisitResult.CONTINUE;
                }
            }
            );
        } catch (IOException ex) {
            Logger.getLogger(AbstractWatchRegistry.class.getName()).log(Level.SEVERE, null, ex);
            throw new IllegalStateException(ex);
        }
    }

    public WatchableState register(Path path, Consumer<WatchableState> consumer) {
        if (!Files.isDirectory(path)) {
            return null;
        }

        WatchableState defaultState = new WatchableState(path);

        consumer.accept(defaultState);

        if (isRegistered(path)) {
            return watchable(path);
        }
        if (defaultState.isSkipped()) {
            return null;
        }

        if (defaultState.getKind().length == 0) {
            defaultState.setKind(ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE, OVERFLOW);
        }

        WatchableState wp = register(path, defaultState.getKind());
        wp.addListeners(defaultState.getListeners());

        if (defaultState.needsLock()) {
            wp.lock();
        }
        return wp;

    }

    /**
     * Registers the specified function that should be executed when a new file
     * or folder is created. The function accepts two parameters:
     * <ul>
     * <li>
     * An object of type {@link WatchableState} that corresponds to a directory
     * for which an event of type {@code ENTRY_CREATE} has been fired.
     * </li>
     * <li>
     * An object of type {@code Path} which represents a relative path to the
     * {@code watchable} path. The absolute path may be built with a code:
     *      {@code watchableState.getPath().resolve(context) } where the
     * {@code watchableState} is a value of the first parameter and the
     * {@code context} is a value of the second parameter.
     * </li>
     * </ul>
     * The method performs no code except it saves a function in the class
     * variable. The parameter can be {@code null} causing no actions when a
     * folder or a file is created.
     * <p>
     * Any method of class {@code WatchableState} may be applied in the body of
     * the function specified by the parameter. But only the methods null null
     * null null null null null null null null null null null null null null null     {@link WatchableState#addListeners(java.util.EventListener...) and 
     * {@link WatchableState#suspendListeners() } will take effect. Actually
     * the function deals with a copy of the original object of type
     * {@code WatchableState}. The listeners that can be added in the function
     * are not added to the original fileListener collection and are executed prior
     * to the listeners of the original.
     * <p>
     * The method {@code suspendListeners() } affects only on the original
     * listeners and if applied then the original listeners are not executed.
     *
     * @param consumer a function that is used as a handler of the event fired
     * when a folder or a file is created. Can be null.
     */
    public void onEntryCreate(BiConsumer<WatchableState, Path> consumer) {
        this.createConsumer = consumer;
    }

    protected void onCreate(WatchableState state, Path context) {
        if (createConsumer == null) {
            return;
        }
        createConsumer.accept(state, context);
    }

    /**
     * Tests whether the given path is registered with a {@code WatchService)
     *
     * @param path an object to be checked
     * @return true if the path is already registered
     */
    public boolean isRegistered(Path path) {
        if (!isServiceAvailable()) {
            return false;
        }
        return stateMap.containsKey(path);
    }

    /**
     * Returns an object of type {@link WatchableState} for the given path.
     *
     * @param path the path for which the method returns a state
     * @return an object of type {@code WatchableState} for the given path.
     */
    public WatchableState watchable(Path path) {
        return stateMap.get(path);
    }

    /**
     * Returns a collection of all registered {@link WatchableState} objects.
     *
     * @return a collection of type {@code Collection<WatchableState>} for the
     * given path.
     */
    public Collection<WatchableState> watchables() {
        return stateMap.values();
    }

    /**
     * Unregisters the specified path. The method first applies a method
     * {@code cancel()} to the corresponding object of type {
     *
     * @WatchKey}. Then releases the lock that may be created for this path and
     * then removes the state of the path from the {@code watchable} state
     * store.
     *
     * @param path the path to be unregistered
     */
    public void unregister(Path path) {
        if (!isServiceAvailable() || !isRegistered(path)) {
            return;
        }
        watchable(path).unregister();
        stateMap.remove(path);
    }

    /**
     * Locks the specified folder from being deleted.
     *
     * A folder may be locked from being deleted by creating a data file with a
     * name specified by the constant {@link WatchableState#lockFileName} and
     * then applying a {@code java.nio.channels.FileLock } to that file.
     *
     * @param path an object to be locked
     * @return an object of type {@link WatchableState} of the given
     * {@code path}.
     */
    public WatchableState lock(Path path) {
        return isRegistered(path) ? watchable(path).lock() : null;
    }

    /**
     * Releases the file lock of the specified folder. The lock to be released
     * could be applied to the folder by the method
     * {@link #lock(java.nio.file.Path)}
     *
     * @param path a path to the file whose lock must be released
     */
    public void releaseLock(Path path) throws IOException {
        if (isRegistered(path)) {
            watchable(path).releaseLock();
        }
    }

    /**
     * Adds listeners of type {@link FileChangeListener} to the given path. If a
     * fileListener already added that it is replaced with a new one.
     *
     * @param path a file the listeners are added to
     * @param listeners listeners to be added
     */
    public void addListeners(Path path, EventListener... listeners) {
        if (listeners.length == 0 || !isRegistered(path)) {
            return;
        }
        watchable(path).addListeners(listeners);
    }

    /**
     * Removes the specified listeners of type {@link FileChangeListener} that
     * may be assigned to the given path.
     *
     * @param path a file the listeners are removed from
     * @param listeners listeners to be removed
     */
    public void removeListeners(Path path, FileChangeListener... listeners) {
        if (listeners.length == 0 || !isRegistered(path)) {
            return;
        }
        watchable(path).removeListeners(listeners);
    }

    /**
     * Return listeners of type {@link FileChangeListener} as an array
     *
     * @param path the path which listeners a to be returned
     * @return an array of listeners
     */
    protected EventListener[] getListeners(Path path) {
        if (!isRegistered(path)) {
            return new EventListener[0];
        }
        return watchable(path).getListeners();
    }

    protected void print(String str) {
        if (verbose) {
            System.out.println(str);
        }
    }

    protected void printErr(String str) {
        if (verbose) {
            System.err.println(str);
        }
    }

    /**
     * Starts a cycle to process events
     *
     * @throws java.io.IOException if an operation fails
     */
    protected void doProcessEvents() throws IOException {

        WatchKey key = null;
        while (true) {
            // wait for key to be signalled
            try {
                printErr("id= " + id + "; BEFORE take() path=" + key);
                key = watchService().take();
                printErr("id= " + id + ";AFTER take() path=" + key.watchable());

            } catch (InterruptedException x) {
                return;
            }

            WatchEvent<?> lastEvent = null;
            for (WatchEvent<?> event : key.pollEvents()) {
                printErr("id= " + id + "; CYCLE kind=" + event.kind() + "; context=" + event.context());
                lastEvent = event;
                WatchEvent.Kind kind = event.kind();
                //
                // notifies listeners
                //
                notify((Path) key.watchable(), event);

                if (kind == OVERFLOW) {
                    System.out.println("!!!!!!!  OVERFLOW !!!!!!!!!!=" + key.watchable());
                }
            }
            // do printing if only  verbose field equals to true
            printNotifyInfo("id= " + id, key, lastEvent);

            if (!Files.exists((Path) key.watchable())) {
                unregister((Path) key.watchable());
                continue;
            }
            key.reset();

        }//while

    }

    protected void printNotifyInfo(String msg, WatchKey key, WatchEvent<?> lastEvent) {
        if (!verbose) {
            return;
        }
        Path watchable = (Path) key.watchable();
        System.out.println("===================================");
        System.out.println("   ---  NOTIFY " + msg);
        System.out.println("   ---  watchable=" + watchable);
        System.out.println("   ---  exists==" + Files.exists(watchable));
        System.out.println("   ---  notExists==" + Files.notExists(watchable));

        System.out.println("   ---  key.isValid=" + key.isValid());
        if (lastEvent == null) {
            System.out.println("   ---  lastEvent=" + null);
        } else {
            System.out.println("   ---  lastEvent.context()=" + lastEvent.context());
            System.out.println("   ---  lastEvent.kind()=" + lastEvent.kind());
        }
        System.out.println("===================================");
        if ("afterReset".equals(msg)) {
            try {
                Thread.sleep(500);

            } catch (InterruptedException ex) {
                Logger.getLogger(AbstractWatchRegistry.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("   ---  !!!! exists==" + Files.exists(watchable));
            if (!Files.exists(watchable)) {
                key.cancel();
            }
        }
    }

    /**
     * Decodes events and notifies all listeners registered to the given path.
     *
     * If the listeners are suspend that they are not notified. This may happen
     * in case of applying the method {@link #suspendListeners(java.nio.file.Path...)
     *
     * @param watchablePath a Path object whose listeners are to be notified.
     *
     * @param event an event polled in the event cycle for the given path
     */
    protected void notify(Path watchablePath, WatchEvent<?> event) {

        if (verbose && watchable(watchablePath).areListenersSuspend()) {
            printErr("id= " + id + "; LISTENERS suspended = " + watchablePath);
        }

        EventListener[] listenerList = watchable(watchablePath).getListeners();

        Path context = (Path) event.context();
        Path eventSource = watchablePath.resolve(context);

        WatchEvent.Kind kind = event.kind();

        FileEvent fe = new FileEvent(watchablePath, context, kind);

        WatchableState watchableState = watchable(watchablePath);

        boolean tempListenersSuspend = watchableState.areListenersSuspend();

        if (createConsumer != null) {
            tempListenersSuspend = doOnCreate(watchableState, event)
                    .areListenersSuspend();
        }

//        if (tempCreateConsumer != null && kind == ENTRY_CREATE) {
        if (kind == ENTRY_CREATE) {
            watchableState.addChildren(eventSource);
        }

        if (!tempListenersSuspend) {
            for (EventListener el : listenerList) {
                for (ListenerRecognizer r : getListenerRecognizers()) {
                    if (notify(watchableState, event, el, r)) {
                        break;
                    }
                }
            }
        }

        if (kind == ENTRY_DELETE) {
            watchableState.removeChildren(eventSource);
        }

    }

    protected ListenerRecognizer getDefaultListenerRecognizer() {
        return null;
/*        return (state, event, listener) -> {

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
        */
    }

    protected WatchableState doOnCreate(WatchableState state, WatchEvent<?> event) {

        WatchableState temp = new WatchableState(state.getPath());

        temp.kind = state.getKind();

        FileEvent fe = new FileEvent(state.getPath(), (Path) event.context(), event.kind());
        //
        // Execute BiConsumer function
        //
        onCreate(temp, (Path) event.context());

        Path eventSource = state.getPath().resolve((Path) event.context());

        for (EventListener el : temp.listeners) {
            for (ListenerRecognizer r : getListenerRecognizers()) {
                if (notify(state, event, el, r)) {
                    break;
                }
            }
        }

        return temp;

    }

    protected boolean notify(WatchableState state, WatchEvent<?> event, EventListener listener, ListenerRecognizer recognizer) {
        return recognizer.test(state, event, listener);
    }

    /**
     * Deletes a folder and all it's descendents recursively.
     *
     * The given folder is not required to be registered as {@code watchable}
     * but if it does and the value of the parameter {@code unregister }
     * is true than the method first unregister it and than try to delete. This
     * is a safe method to perform deletion of the registered folder.
     *
     * @param path specifies a folder to be deleted
     * @param unregister unregister folders before delete
     *
     * @throws IOException if the specified folder cannot be deleted/
     */
    protected void deleteFolder(Path path, boolean unregister) throws IOException {
        Files.walkFileTree(path, new FileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                if (unregister && isRegistered(dir)) {
                    unregister(dir);
                    return FileVisitResult.CONTINUE;
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                if (exc == null) {
                    try {
                        Files.deleteIfExists(dir);
                    } catch (IOException ex) {
                        System.out.println("FileVisitResult EXCEPTION postVisitDir = " + dir);
                    }
                    return FileVisitResult.CONTINUE;
                } else {
                    // directory iteration failed
                    throw new IOException("");
                }
            }
        });
    }

    /**
     * Marks all listeners for each path in the given array as suspended. This
     * means that when an event occurs no listeners should be notified/
     *
     * @param paths an array of paths whose listeners are to be suspend.
     */
    public synchronized void suspendListeners(Path... paths) {
        for (Path path : paths) {
            if (!isRegistered(path)) {
                continue;
            }
            watchable(path).suspendListeners();
        }
    }

    /**
     * Marks all listeners for each path in the given array as suspended. This
     * means that when an event occurs no listeners should be notified/
     *
     * @param paths an array of paths whose listeners are to be suspend.
     */
    public synchronized void suspend(Path... paths) {
        for (Path path : paths) {
            if (!isRegistered(path)) {
                continue;
            }
            suspendMap.put(path, watchable(path));
            watchable(path).getKey().cancel();
        }
    }

    /**
     * Marks all listeners for each path in the given array as ready to be
     * notified.
     *
     * @param paths an array of paths whose listeners are to be resumed.
     */
    public synchronized void resume(Path... paths) throws IOException {
        for (Path path : paths) {
            if (!isRegistered(path)) {
                continue;
            }
            WatchableState state = suspendMap.get(path);
            if (state == null) {
                continue;
            }
            WatchKey key = path.register(watchService(), state.getKind());
            state.setKey(key);
            stateMap.put(path, state);

        }

    }

    /**
     * Marks all listeners for each path in the given array as ready to be
     * notified.
     *
     * @param paths an array of paths whose listeners are to be resumed.
     */
    public synchronized void resumeListeners(Path... paths) {
        for (Path path : paths) {
            if (!isRegistered(path)) {
                continue;
            }
            watchable(path).resumeListeners();
        }
    }

    public String stringView() {
        StringBuilder sb = new StringBuilder();
        String sep = System.lineSeparator();
        sb.append("=== WatchableStore ===")
                .append(sep)
                .append("   --- ")
                .append("Item count = ")
                .append(stateMap.size())
                .append(sep);

        if (stateMap.size() == 0) {
            sb.append(sep)
                    .append("======================================");
        } else {
            stateMap.values().forEach(v -> {
                sb.append("--------------------------")
                        .append(sep)
                        .append(v.toString())
                        .append(sep)
                        .append("--------------------------")
                        .append(sep);
            });
        }

        return sb.toString();

    }

    public static class WatchableState {

        private final Path path;
        private final AbstractWatchRegistry registry;

        private WatchEvent.Kind[] kind;
        private WatchKey key;
        private final List<EventListener> slisteners = new ArrayList();
        private final List<EventListener> listeners = Collections.synchronizedList(slisteners);
        /**
         * When an object of this class created we put an item with a key that
         * equals to the path and value {@code Files.isDirectory(path).
         */
        private final Map<Path, Boolean> childrens = new ConcurrentHashMap<>();
        private FileLock lock;
        private RandomAccessFile randomFile;
        protected String lockFileName = ".lock-a6882a7b-343f-4c75-a96e-2d0d3a927b48";

        private boolean listenersSuspend;

        private boolean skipped;
        private boolean needsLock;

        public WatchableState(AbstractWatchRegistry registry, Path path, WatchEvent.Kind[] kind, WatchKey key, FileChangeListener... listeners) {
            this.registry = registry;
            this.path = path;
            this.kind = kind;
            this.key = key;
            File[] files = path.toFile().listFiles();
            for (File f : files) {
                childrens.put(f.toPath(), f.isDirectory());
            }

            Collections.addAll(this.listeners, (listeners == null || listeners.length == 0) ? new FileChangeListener[0] : listeners);
        }

        protected WatchableState(Path path) {
            registry = null;
            this.path = path;
            this.kind = new WatchEvent.Kind<?>[]{ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY, OVERFLOW};
            this.key = null;
        }

        protected WatchableState getCopy() {
            WatchableState ws = new WatchableState(getPath());
            ws.kind = getKind();
            ws.childrens.putAll(childrens);

            ws.listeners.addAll(listeners);
            ws.listenersSuspend = listenersSuspend;
            return ws;
        }

        public Path getPath() {
            return path;
        }
        /**
         * Causes the code to skip this object from performing some operations.
         * It's the code specific how to use this method. 
         */
        public void skip() {
            this.skipped = true;
        }
        /**
         * Tests whether the object is skipped.
         * @return true if earlier the method skip() was invoked
         */
        public boolean isSkipped() {
            return skipped;
        }

        protected boolean needsLock() {
            return needsLock;
        }
        /**
         * Unregistered this object.
         * First applies the method {@code WatchKey.cancel()} and
         * then invokes the method {@link #releaseLock() }.
         */
        public void unregister() {
            try {
                getKey().cancel();
                releaseLock();
            } catch (IOException ex) {
                Logger.getLogger(AbstractWatchRegistry.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        /**
         * Releases the file lock of the file that resides in the
         * {@code watchable} folder and is used to lock the folder from being
         * deleted.
         */
        public void releaseLock() throws IOException {
            if (lock != null) {
                //
                // we use try(..) to close file automatically
                //
                try (RandomAccessFile f = randomFile) {
                    lock.release();
                }

            }
        }

        /**
         * Locks the specified folder from being deleted.
         *
         * A folder may be locked from being deleted by creating a data file
         * with a name specified by the constant
         * {@link WatchableState#lockFileName} and then applying a {@code java.nio.channels.FileLock
         * } to that file.
         *
         * @return an object of type {@link WatchableState} of the given
         * {@code watchable}.
         */
        public WatchableState lock() {
            if (key == null) {
                needsLock = true;
                return this;
            }
            if (lock != null) {
                return this;
            }
            Path lockPath = path.resolve(Paths.get(lockFileName));

            if (!Files.exists(lockPath)) {
                try {
                    registry.suspend(getPath());
                    Files.createFile(path.resolve(Paths.get(lockFileName)));
                } catch (IOException ex) {
                    Logger.getLogger(AbstractWatchRegistry.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        registry.resume(getPath());
                    } catch (IOException ex) {
                        Logger.getLogger(AbstractWatchRegistry.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            try {
                RandomAccessFile raf = new RandomAccessFile(lockPath.toFile(), "rw");
                FileChannel channel = raf.getChannel();
                randomFile = raf;
                FileLock newlock = channel.tryLock();
                this.lock = newlock;
            } catch (FileNotFoundException ex) {
                Logger.getLogger(AbstractWatchRegistry.class.getName()).log(Level.INFO, null, ex);
            } catch (IOException | OverlappingFileLockException ex) {
                Logger.getLogger(AbstractWatchRegistry.class.getName()).log(Level.INFO, null, ex);
            }
            return this;
        }

        public FileLock getLock() {
            return lock;
        }

        public boolean hasLock() {
            return lock != null;
        }

        public boolean isDirectory(Path path) {
            return childrens.get(path);
        }

        protected void addChildren(Path newPath) {
            if (path.equals(newPath.getParent())) {
                childrens.put(newPath, Files.isDirectory(newPath));
            }
        }

        public void removeChildren(Path toRemove) {
            if (path.equals(toRemove.getParent())) {
                childrens.remove(toRemove);
            }

        }

        public WatchEvent.Kind[] getKind() {
            return kind;
        }

        public void setKind(WatchEvent.Kind... kind) {
            if (registry == null || this.key == null) {
                this.kind = kind;
            }
        }

        public WatchKey getKey() {
            return key;
        }

        protected void setKey(WatchKey key) {
            if (this.key == null) {
                this.key = key;
            }
        }

        public EventListener[] getListeners() {
            return listeners.toArray(new EventListener[0]);
        }
        /**
         * Adds each item of the specified array of listeners to the end of an internal collection.
         * The method doesn't add a listener if it already exists in the 
         * internal collection.
         * 
         * @param listeners the array of listeners to be added
         * @return the target object of type WatchableState. Just to organize
         *   a method call chain.
         */
        public synchronized WatchableState addListeners(EventListener... listeners) {
            removeListeners(listeners);
            Collections.addAll(this.listeners, listeners);
            return this;
        }
        /**
         * Removes each item of the given array of listeners from an internal collection.
         * 
         * @param listeners the array of listeners to be removed
         * @return the target object of type WatchableState. Just to organize
         *   a method call chain.
         */
        public synchronized WatchableState removeListeners(EventListener... listeners) {
            for (EventListener l : listeners) {
                int idx = this.listeners.indexOf(l);
                if (idx < 0) {
                    continue;
                }
                this.listeners.remove(idx);
            }
            return this;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            String ex = Files.exists(path) ? " exists " : "not exists";
            String notex = Files.notExists(path) ? " NOT exists " : " exists";

            String sep = System.lineSeparator();
            sb.append("   ")
                    .append("path: ").append(path)
                    .append(" ")
                    .append(ex)
                    .append(";")
                    .append(sep)
                    .append("   ")
                    .append("notExists = ")
                    .append(notex)
                    .append(sep)
                    .append("   ")
                    .append("key.isValid() = ")
                    .append(key.isValid())
                    .append(sep)
                    .append("   ")
                    .append("children count = ")
                    .append(childrens.size())
                    .append(sep)
                    .append("   ")
                    .append("listener count = ")
                    .append(listeners.size());

            return sb.toString();
        }

        public void suspendListeners() {
            this.listenersSuspend = true;
        }

        public void resumeListeners() {
            this.listenersSuspend = false;
        }

        public boolean areListenersSuspend() {
            return listenersSuspend;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 23 * hash + Objects.hashCode(this.path);
            hash = 23 * hash + Objects.hashCode(this.registry);
            hash = 23 * hash + Arrays.deepHashCode(this.kind);
            hash = 23 * hash + Objects.hashCode(this.key);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }

            final WatchableState other = (WatchableState) obj;

            if (this.registry != other.registry) {
                return false;
            }

            if (!Objects.equals(this.key, other.key)) {
                return false;
            }
            if (this.key != null) {
                return true;  // registered paths 
            }
            //
            // Only whan this.key == null && other.key == null. Is not registered
            //
            if (this.listenersSuspend != other.listenersSuspend) {
                return false;
            }
            if (this.skipped != other.skipped) {
                return false;
            }
            if (!Objects.equals(this.path, other.path)) {
                return false;
            }
            if (!Objects.equals(this.registry, other.registry)) {
                return false;
            }
            if (!Arrays.deepEquals(this.kind, other.kind)) {
                return false;
            }
            if (!Objects.equals(this.listeners, other.listeners)) {
                return false;
            }
            return true;
        }

    }//class WatchableState
}
