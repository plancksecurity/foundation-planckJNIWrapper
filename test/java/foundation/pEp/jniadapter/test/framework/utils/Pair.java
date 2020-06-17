package foundation.pEp.jniadapter.test.framework.utils;

public class Pair<K, V> {
    private K key = null;
    private V value = null;

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public Pair(K f, V s) {
        key = f;
        value = s;
    }
}

