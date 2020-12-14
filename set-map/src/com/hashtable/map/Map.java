package com.hashtable.map;

/**
 * Description: Map接口
 *
 * @author guizy1
 * @date 2020/12/9 15:38
 */
public interface Map<K, V> {
    int size();

    boolean isEmpty();

    void clear();

    V put(K key, V value); //添加元素

    V get(K key);

    V remove(K key);

    boolean containsKey(K key); //查找key是否存在

    boolean containsValue(V value); //查找value是否存在

    void traversal(Visitor<K, V> visitor); //元素遍历

    public static abstract class Visitor<K, V> {
        boolean stop;

        public abstract boolean visit(K key, V value);
    }
}
