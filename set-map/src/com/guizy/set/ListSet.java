package com.guizy.set;

import com.guizy.list.DoubleLinkedList;
import com.guizy.list.List;

/**
 * Description:
 *
 * @author guizy
 * @date 2020/12/8 22:32
 */
public class ListSet<E> implements Set<E> {

    private List<E> list = new DoubleLinkedList<>();

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.inEmpty();
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public boolean contains(E element) {
        return list.contains(element);
    }

    @Override
    public void add(E element) {
        // 方式1: 不添加重复的元素
        // if (list.contains(element)) return;
        // list.add(element);

        // 方式2: 新元素覆盖掉旧的
        int index = list.indexOf(element);
        if (index != list.ELEMENT_NOT_FOUNT) {  // 存在就覆盖
            list.set(index, element);
        } else { // 不存在就添加
            list.add(element);
        }
    }

    @Override
    public void remove(E element) {
        int index = list.indexOf(element);
        if (index != list.ELEMENT_NOT_FOUNT) {
            list.remove(index);
        }
    }

    @Override
    public void traversal(Visitor<E> visitor) {
        if (visitor == null) return;

        int size = list.size();
        for (int i = 0; i < size; i++) {
            if (visitor.visit(list.get(i))) return;
        }
    }
}
