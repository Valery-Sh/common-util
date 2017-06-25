package org.main;

import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.*;
import static java.nio.file.LinkOption.*;
import java.nio.file.attribute.*;
import java.io.*;
import java.util.*;

/**
 * Example to watch a directory (or tree) for changes to files.
 */
public class WatchDir {

    private final WatchService watchService;
    private final Map<WatchKey, Path> keys;
    private final boolean recursive;
    private boolean trace = false;

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>) event;
    }

    /**
     * Register the given directory with the WatchService
     */
    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        if (trace) {
            Path prev = keys.get(key);
            if (prev == null) {
                System.out.format("register: %s\n", dir);
            } else if (!dir.equals(prev)) {
                System.out.format("update: %s -> %s\n", prev, dir);
            }
        }
        keys.put(key, dir);
    }

    /**
     * Register the given directory, and all its sub-directories, with the
     * WatchService.
     */
    private void registerAll(final Path start) throws IOException {
        // register directory and sub-directories
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                    throws IOException {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Creates a WatchService and registers the given directory
     */
    WatchDir(Path dir, boolean recursive) throws IOException {
        this.watchService = FileSystems.getDefault().newWatchService();

        this.keys = new HashMap<WatchKey, Path>();
        this.recursive = recursive;

        if (recursive) {
            System.out.format("Scanning %s ...\n", dir);
            registerAll(dir);
            System.out.println("Done.");
        } else {
            register(dir);
        }

        // enable trace after initial registration
        this.trace = true;
    }

    /**
     * Process all events for keys queued to the watchService
     */
    void processEvents() {
        WatchKey key = null;
        for (;;) {

            // wait for key to be signalled
            try {
                System.out.println("BEFORE take() path=" + key);
                key = watchService.take();
                System.err.println("AFTER take() path=" + key.watchable());
            } catch (InterruptedException x) {
                return;
            }

            Path dir = keys.get(key);
            if (dir == null) {
                System.err.println("WatchKey not recognized!!");
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                System.out.println("Cycle FOR pollEvents path=" + key.watchable() + "; context()=" + event.context() + "; kind=" + event.kind());

                WatchEvent.Kind kind = event.kind();

                // TBD - provide example of how OVERFLOW event is handled
                if (kind == OVERFLOW) {
                    continue;
                }

                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);

                // print out event
                System.out.format("%s: %s\n", event.kind().name(), child);
                System.out.println("  ---- event.count()=" + event.count());
                // if directory is created, and watching recursively, then
                // register it and its sub-directories
                if (recursive && (kind == ENTRY_CREATE)) {
                    try {
                        if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                            registerAll(child);
                        }
                    } catch (IOException x) {
                        // ignore to keep sample readbale
                    }
                }
            }

            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            System.out.println("AFTER keyreset path=" + key.watchable());

            if (!valid) {
                keys.remove(key);
                System.out.println("INVALID path=" + key.watchable());

                // all directories are inaccessible
                if (keys.isEmpty()) {
                    break;
                }
            } else {
                Path p = (Path) key.watchable();
                if (!Files.exists(p)) {
                    keys.remove(key);
                    System.out.println("INVALID path=" + key.watchable());

                    // all directories are inaccessible
                    if (keys.isEmpty()) {
                        break;
                    }

                }
                System.out.println("VALID path=" + key.watchable());
            }
        }
    }

    static void usage() {
        System.err.println("usage: java WatchDir [-r] dir");
        //System.exit(-1);
    }

    public static void main(String[] args) throws IOException {
        // parse arguments
        if (args.length == 0 || args.length > 2) {
            usage();
        }
        boolean recursive = false;
        /*        int dirArg = 0;
        if (args[0].equals("-r")) {
            if (args.length < 2)
                usage();
            recursive = true;
            dirArg++;
        }
         */
        recursive = false;
        // register directory and process its events
        //Path dir = Paths.get(args[dirArg]);
        //Path dir = Paths.get("d:/0temp/WDIR");
        Path dir = Paths.get("d:/0temp/WDIR/C");
        new WatchDir(dir, recursive).processEvents();
        System.out.println("AFTER new WatchDir()");
    }
}
