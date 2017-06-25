
package org.vns.common.files;

import java.nio.file.WatchEvent;
import java.util.EventListener;
import org.vns.common.files.AbstractWatchRegistry.WatchableState;

/**
 * The implementations if the interface test whether the given listener 
 * can be processed and if so than the listener should be processed.
 * 
 * @author Valery Shyshkin
 */
@FunctionalInterface
public interface ListenerRecognizer {
    /**
     * Tests whether the given listener can be processed.
     * 
     * @param state an instance of {@link WatchableState} which corresponds to 
     *   a {@code Path} object which is registered with a {@code WatchService}.
     * @param event An event for an object that is registered with a WatchService.
     * @param listener an object of type EventListener to be tested
     * 
     * @return true if the given listener can be processed by this function
     */
    boolean test(WatchableState state, WatchEvent event, EventListener listener );
}
