package org.vns.common.util.prefs.files;

/**
 *
 * @author Valery
 */
@FunctionalInterface

public interface TripleConsumer<T,E,V> {
    void accept(T first, E second, V third);
}
