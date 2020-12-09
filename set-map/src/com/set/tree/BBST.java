package com.set.tree;

import java.util.Comparator;

/**
 * @Description: 平衡二叉搜索树(BalanceBinarySearchTree)
 * @Reason
 *         由于RBTree需要对节点进行旋转操作; 此时就需要使用到AVLTree中的旋转代码,
 *         因为AVLTree和RBTree都是平衡二叉搜索树(BalanceBinarySearchTree),BBST在BST的基础上增加了旋转功能;
 *         为了程序的拓展性, 我们在创建一个BBST 继承 BST, AVLTree和RBTree再 继承 BBST
 * @author guizy
 * @date 2020/12/4 01:01
 */
public class BBST<E> extends BST<E> {

    public BBST() {
        this(null);
    }

    public BBST(Comparator<E> comparator) {
        super(comparator);
    }

    /**
     * 对node进行左旋转
     *
     * @param grand
     */
    protected void rotateLeft(Node<E> grand) {
        Node<E> parent = grand.right;
        Node<E> child = parent.left;
        grand.right = child;
        parent.left = grand;

        afterRotate(grand, parent, child);
    }

    /**
     * 对node进行右旋转
     *
     * @param grand
     */
    protected void rotateRight(Node<E> grand) {
        Node<E> parent = grand.left;
        Node<E> child = parent.right;
        grand.left = child;
        parent.right = grand;

        afterRotate(grand, parent, child);
    }

    /**
     * 旋转之后, 更新它们的parent; 并且更新旋转后的高度
     *
     * @param grand
     * @param parent
     * @param child
     */
    protected void afterRotate(Node<E> grand, Node<E> parent, Node<E> child) {
        // 让parent为子树的根节点
        parent.parent = grand.parent;
        // 如果grand是其父节点的left, 则将grand.parent.left = parent;
        if (grand.isLeftChild()) {
            grand.parent.left = parent;
        } else if (grand.isRightChild()) {
            grand.parent.right = parent;
            // grand是根节点
        } else {
            root = parent;
        }

        // 更新child的parent
        if (child != null)
            child.parent = grand;

        // 更新grand的parent
        grand.parent = parent;
    }

    /**
     * 统一旋转
     */
    protected void rotate(
            Node<E> r, // 子树的根节点
            Node<E> a, Node<E> b, Node<E> c,
            Node<E> d,
            Node<E> e, Node<E> f, Node<E> g) {
        // 让d成为这颗子树的根节点
        d.parent = r.parent;
        if (r.isLeftChild()) {
            r.parent.left = d;
        } else if (r.isRightChild()) {
            r.parent.right = d;
        } else {
            root = d;
        }

        // a-b-c
        b.left = a;
        if (a != null) a.parent = b;
        b.right = c;
        if (c != null) c.parent = b;
        // 更新b的高度, 因为b的左右子树都变了, 所以要更新高度

        // e-f-g
        f.left = e;
        if (e != null) e.parent = f;
        f.right = g;
        if (g != null) g.parent = f;

        // b-d-f
        d.left = b;
        d.right = f;
        b.parent = d;
        f.parent = d;
    }
}
