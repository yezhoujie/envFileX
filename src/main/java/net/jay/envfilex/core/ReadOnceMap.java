package net.jay.envfilex.core;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

public class ReadOnceMap<K, V> extends AbstractMap<K, V> implements Map<K, V> {

    private final Map<K, V> firstRead;
    private final Map<K, V> otherReads;

    private boolean wasRead = false;

    public ReadOnceMap(Map<K, V> first, Map<K, V> rest) {
        firstRead = first;
        otherReads = rest;
    }

    @Override
    public int size() {
        return getSource(false).size();
    }

    @Override
    public @NotNull Set<Entry<K, V>> entrySet() {
        return getSource(true).entrySet();
    }

    private Map<K, V> getSource(boolean consume) {
        Map<K ,V> source;
        synchronized (this) {
            if (wasRead) {
                source = otherReads;
            } else {
                if (consume) {
                    wasRead = true;
                }
                source = firstRead;
            }
        }
        return source == null ? Collections.emptyMap() : source;
    }
}
