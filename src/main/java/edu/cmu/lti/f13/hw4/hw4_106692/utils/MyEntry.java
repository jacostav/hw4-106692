package edu.cmu.lti.f13.hw4.hw4_106692.utils;

import java.util.Map;

//http://stackoverflow.com/questions/3110547/java-how-to-create-new-entry-key-value
final public class MyEntry<K, V> implements Map.Entry<K, V> {
    private final K key;
    private V value;

    public MyEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public V setValue(V value) {
        V old = this.value;
        this.value = value;
        return old;
    }
}