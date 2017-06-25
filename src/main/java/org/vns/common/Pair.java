package org.vns.common;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author Valery
 * @param <K>
 * @param <V>
 */
public class Pair<K,V> extends Object implements Serializable {
    private final K key;
    private final V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }
    public static Pair of(Object key,Object  value) {
        return new Pair(key, value);
    }
    public K key() {
        return key;
    }
    public V value() {
        return value;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.key);
        hash = 71 * hash + Objects.hashCode(this.value);
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
        final Pair<?, ?> other = (Pair<?, ?>) obj;
        if (!Objects.equals(this.key, other.key)) {
            return false;
        }
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        return true;
    }
    
}
