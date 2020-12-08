package com.guizy.set;

import com.guizy.tree.BinaryTree;
import com.guizy.tree.RBTree;

/**
 * Description:
 *
 * @author guizy
 * @date 2020/12/8 23:33
 */
public class TreeSet<E> implements Set<E> {

    private RBTree<E> tree = new RBTree<>();

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
            public void visit(E element) {
                visitor.visit(element);
            }
        });
    }
}
