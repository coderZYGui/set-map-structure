package com.hashtable.set;

import com.hashtable.map.HashMap;
import com.hashtable.map.Map;
import com.map.set.Set;

/**
 * Description: 使用HashMap实现HashSet
 *
 * @author guizy1
 * @date 2020/12/24 12:58
 */
public class HashSet<E> implements Set<E> {

    private HashMap<E, Object> map = new HashMap<>();

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public boolean contains(E element) {
        return map.containsKey(element);
    }

    @Override
    public void add(E element) {
        map.put(element, null);
    }

    @Override
    public void remove(E element) {
        map.remove(element);
    }

    @Override
    public void traversal(Visitor<E> visitor) {
        map.traversal(new Map.Visitor<E, Object>() {
            @Override
            public boolean visit(E key, Object value) {
                return visitor.visit(key);
            }
        });
    }
}
