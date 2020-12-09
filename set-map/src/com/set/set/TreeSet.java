package com.set.set;

import com.set.tree.BinaryTree;
import com.set.tree.RBTree;

import java.util.Comparator;

/**
 * Description: 使用红黑树实现Set
 *
 * @author guizy
 * @date 2020/12/8 23:33
 */
public class TreeSet<E> implements Set<E> {

    private RBTree<E> tree;

    public TreeSet() {
        this(null);
    }

    public TreeSet(Comparator<E> comparator) {
        tree = new RBTree<>(comparator);
    }

    @Override
    public int size() {
        return tree.size();
    }

    @Override
    public boolean isEmpty() {
        return tree.isEmpty();
    }

    @Override
    public void clear() {
        tree.clear();
    }

    @Override
    public boolean contains(E element) {
        return tree.contains(element);
    }

    @Override
    public void add(E element) {
        // RBTree中默认就是去重的
        tree.add(element);
    }

    @Override
    public void remove(E element) {
        tree.remove(element);
    }

    @Override
    public void traversal(Visitor<E> visitor) {
        // 中序遍历
        tree.inorderTraversal(new BinaryTree.Visitor<E>() {
            @Override
            public boolean visit(E element) {
                return visitor.visit(element);
            }
        });
    }
}
