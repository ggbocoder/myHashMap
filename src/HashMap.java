import java.util.Arrays;

public class HashMap<K, V> {

    private static final int DEFAULT_CAPACITY = 16;
    private static final double LOAD_FACTOR = 0.75;

    private Entry<K, V>[] table;
    /**
     * 两张table，一张使用，另一张rehash才用到
     */
    private Entry<K, V>[][] tables = new Entry[2][];
    /**
     * 当前正在使用的table
     */
    private int idx = 0;
    /**
     * 渐进式rehash开始的数组下标
     */

    private int rehashedStartIndex = 0;
    /**
     * 新数组的entry数目
     */
    private int newSize=0;
    private int size;
    private int threshold;
    private int mask;
    private boolean rehashing;

    public HashMap() {
        this(DEFAULT_CAPACITY);
    }

    public HashMap(int capacity) {
        this.tables[idx] = new Entry[capacity];
        this.size = 0;
        this.threshold = (int) (capacity * LOAD_FACTOR);
        this.mask = capacity - 1;
        this.rehashing = false;
    }
    public void putInNewTable(K key,V value){
        int hash=hash(key);
        int index=findIndexInNewTable(hash,key);
        if(index!=-1){
            tables[idx^1][index].setValue(value);
            return;
        }
        addEntryInNewTable(hash,key,value);
    }


    public void put(K key, V value) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }
        if (rehashing) {
            //如果在插入的时候正在rehash，则在新的table也执行put操作
            putInNewTable(key,value);
            resize();
        }
        int hash = hash(key);
        int index = findIndex(hash, key);
        if (index != -1) {
            tables[idx][index].setValue(value);
            return;
        }
        addEntry(hash, key, value);
    }

    public V get(K key) {
        int hash = hash(key);
        int index = findIndex(hash, key);
        if (index != -1) {
            return tables[idx][index].getValue();
        }
        return null;
    }
    public V removeInNewTable(K key){
        int hash=hash(key);
        int index=findIndexInNewTable(hash,key);
        if(index!=-1){
            Entry<K,V> entry=tables[idx^1][index];
            tables[idx^1][index]=null;
            newSize--;
            return entry.getValue();
        }
        return  null;
    }

    public V remove(K key) {
        if(rehashing){
            //如果正在rehash，则对新数组也执行remove操作
            removeInNewTable(key);
        }
        int hash = hash(key);
        int index = findIndex(hash, key);
        if (index != -1) {
            Entry<K, V> entry = tables[idx][index];
            table[index] = null;
            size--;
            return entry.getValue();
        }
        return null;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }
    public void addEntryInNewTable(int hash, K key, V value){
        int newMask=((tables[idx].length<<1)-1);
        int index= hash & newMask;
        while (tables[idx^1][index]!=null){
            index=(index + 1) & newMask;
        }
        tables[idx^1][index] = new Entry<>(hash, key, value);
        newSize++;
    }

    private void addEntry(int hash, K key, V value) {
        if (size >= threshold) {
            resize();
        }
        int index = getIndex(hash);
        while (tables[idx][index] != null) {
            index = (index + 1) & mask;
        }
        tables[idx][index] = new Entry<>(hash, key, value);
        size++;
    }

    public int findIndexInNewTable(int hash,K key){
        int newMask=((tables[idx].length<<1)-1);
        int index= hash & newMask;
        int newIdx=idx^1;
        while(tables[newIdx][index]!=null){
            Entry<K, V> entry = tables[newIdx][index];
            if (entry.getHash() == hash && entry.getKey().equals(key)) {
                return index;
            }
            index = (index + 1) & newMask;
        }
        return -1;
    }
    private int findIndex(int hash, K key) {
        int index = getIndex(hash);
        while (tables[idx][index] != null) {
            Entry<K, V> entry = tables[idx][index];
            if (entry.getHash() == hash && entry.getKey().equals(key)) {
                return index;
            }
            index = (index + 1) & mask;
        }
        return -1;
    }

    private int getIndex(int hash) {
        return hash & mask;
    }

    private void resize() {
        int newCapacity = tables[idx].length << 1;
        int newIdx=idx ^ 1;
        if(rehashedStartIndex==0){
            tables[newIdx]= new Entry[newCapacity];
        }
        int newMask = newCapacity - 1;
        int rehashingIndex=rehashedStartIndex;
        //每次rehash八分之一的table
        int rehashedEndIndex=rehashedStartIndex+(tables[idx].length>>3);
        for (;rehashingIndex<rehashedEndIndex;rehashingIndex++) {
            Entry<K, V> entry = tables[idx][rehashingIndex];
            if (entry != null) {
                int index = getIndex(entry.getHash(), newMask);
                while (tables[newIdx][index] != null) {
                    index = (index + 1) & newMask;
                }
                tables[newIdx][index] = entry;
                newSize++;
            }
        }
        rehashedStartIndex=rehashedEndIndex;
        if(rehashedStartIndex>=tables[idx].length){
            rehashing = false;
            idx=newIdx;
            mask = newMask;
            threshold = (int) (newCapacity * LOAD_FACTOR);
            tables[idx^1]=null;
            size=newSize;
            newSize=0;
        }

    }

    private int getIndex(int hash, int mask) {
        return hash & mask;
    }

    private int hash(K key) {
        int h = key.hashCode();
        h ^= (h >>> 20) ^ (h >>> 12);
        return h ^ (h >>> 7) ^ (h >>> 4);
    }

    private static class Entry<K, V> {

        private final int hash;
        private final K key;
        private V value;

        public Entry(int hash, K key, V value) {
            this.hash = hash;
            this.key = key;
            this.value = value;
        }

        public int getHash() {
            return hash;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }
    }
}
