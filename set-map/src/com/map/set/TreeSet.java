package com.map.set;

import com.map.map.Map;
import com.map.map.TreeMap;

/**
 * Description: TeeMap来实现Set
 *
 * @author guizy1
 * @date 2020/12/10 18:20
 */
public class TreeSet<E> implements Set<E> {

    Map<E, Object> map = new TreeMap<>();

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
                return  visitor.visit(key);
            }
        });
    }
}
