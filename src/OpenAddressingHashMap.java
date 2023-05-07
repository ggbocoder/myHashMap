import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class OpenAddressingHashMap<K, V> implements Map<K, V> {

    /**
     * 默认初始容量
     */
    private static final int DEFAULT_CAPACITY = 16;

    /**
     * 最大容量
     */
    private static final int MAXIMUM_CAPACITY = 1 << 30;

    /**
     * 负载因子
     */
    private static final float LOAD_FACTOR = 0.75f;

    /**
     * 哈希表数组
     */
    private Entry<K, V>[] table;

    /**
     * 元素数量
     */
    private int size;

    /**
     * 当前容量
     */
    private int capacity;

    public OpenAddressingHashMap() {
        this(DEFAULT_CAPACITY);
    }

    @SuppressWarnings("unchecked")
    public OpenAddressingHashMap(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
        }
        if (initialCapacity > MAXIMUM_CAPACITY) {
            initialCapacity = MAXIMUM_CAPACITY;
        }
        // 找到大于等于初始容量的最小 2^n 数
        int n = Integer.highestOneBit(initialCapacity - 1) << 1;
        capacity = n;
        table = (Entry<K, V>[]) new Entry[capacity];
    }

    @Override
    public V put(K key, V value) {
        if (size >= capacity * LOAD_FACTOR) {
            resize();
        }
        int index = hash(key);
        Entry<K, V> entry = table[index];
        int i = 0;
        while (entry != null && !entry.getKey().equals(key)) {
            i++;
            index = (index + i) % capacity;
            entry = table[index];
        }
        if (entry == null) {
            entry = new Entry<>(key, value);
            table[index] = entry;
            size++;
            return null;
        } else {
            V oldValue = entry.getValue();
            entry.setValue(value);
            return oldValue;
        }
    }

    @Override
    public V remove(Object key) {
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {

    }

    @Override
    public void clear() {

    }

    @Override
    public Set<K> keySet() {
        return null;
    }

    @Override
    public Collection<V> values() {
        return null;
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return null;
    }

    @Override
    public V get(Object key) {
        int index = findIndex(key);
        Entry<K, V> entry = table[index];
        return entry == null ? null : entry.getValue();
    }

    @Override
    public boolean containsKey(Object key) {
        return findIndex(key) >= 0;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    private int findIndex(Object key) {
        int index = hash(key);
        Entry<K, V> entry = table[index];
        int i = 0;
        while (entry != null && !entry.getKey().equals(key)) {
            i++;
            index = (index + i) % capacity;
            entry = table[index];
        }
        return entry == null ? -1 : index;
    }

    private void resize() {
        int newCapacity = capacity << 1;
        if (newCapacity > MAXIMUM_CAPACITY) {
            return;
        }
        Entry<K, V>[] newTable = (Entry<K, V>[]) new Entry[newCapacity];
        for (Entry<K, V> entry : table) {
            if (entry != null) {
                int index = hash(entry.getKey(), newCapacity);
                int i = 0;
                while (newTable[index] != null) {
                    i++;
                    index = (index + i) % newCapacity;
                }
                newTable[index] = entry;
            }
        }
        table = newTable;
        capacity = newCapacity;
    }

    private int hash(Object key) {
        return hash(key, capacity);
    }

    private int hash(Object key, int tableLength) {
        int h = key == null ? 0 : key.hashCode();
        return (tableLength - 1) & h;
    }

    private static class Entry<K, V> implements Map.Entry<K, V> {

        private final K key;
        private V value;

        public Entry(K key, V value) {
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
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }
    }
}
