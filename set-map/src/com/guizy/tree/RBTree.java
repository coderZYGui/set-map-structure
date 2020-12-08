package com.guizy.tree;

import java.util.Comparator;

/**
 * Description: 红黑树
 *
 * @author guizy
 * @date 2020/12/3 22:49
 */
public class RBTree<E> extends BBST<E> {

    private static final boolean RED = false;
    private static final boolean BLACK = true;

    public RBTree() {
        this(null);
    }

    public RBTree(Comparator<E> comparator) {
        super(comparator);
    }

    @Override
    protected void afterAdd(Node<E> node) {
        Node<E> parent = node.parent;

        // 添加的是根节点(染成黑色)
        if (parent == null) {
            black(node);
            return;
        }

        // ------------- 一共 12 种情况--------------
        // 不需要处理的4种情况:  如果父节点是黑色, 直接返回
        if (isBlack(parent)) return;

        // 根据uncle节点的颜色来判断其他的各4种情况
        Node<E> uncle = parent.sibling();
        // 祖父节点
        Node<E> grand = parent.parent;

        // 需要处理的4种情况: 叔父节点是红色
        if (isRed(uncle)) {
            black(parent);
            black(uncle);
            // 把祖父节点染成红色, 当做新添加的节点处理(递归调用afterAdd)
            afterAdd(red(grand));
            return;
        }

        /*
            因为这4种情况, RBTree需要对节点进行旋转操作; 此时就需要使用到AVLTree中的旋转代码,
            因为AVLTree和RBTree都是平衡二叉搜索树(BalanceBinarySearchTree),BBST在BST的基础上增加了旋转功能;
            为了程序的拓展性, 我们在创建一个BBST 继承 BST, AVLTree和RBTree再 继承 BBST
        */
        // 需要处理的4种情况: 叔父节点不是红色(叔父节点为空)
        if (parent.isLeftChild()) { // L
            // LL,LR, grand都要染成红色
            red(grand);
            if (node.isLeftChild()) { // LL
                black(parent);
            } else { // LR
                black(node);
                rotateLeft(parent);
            }
            // LL,LR, grand最后都要右旋转
            rotateRight(grand);
        } else { // R
            red(grand);
            if (node.isLeftChild()) { // RL
                black(node);
                rotateRight(parent);
            } else { // RR
                black(parent);
            }
            rotateLeft(grand);
        }
        /*if (parent.isLeftChild()) { // L
            if (node.isLeftChild()) { // LL
                black(parent);
                red(grand);
                rotateRight(grand);
            } else { // LR
                black(node);
                red(grand);
                rotateLeft(parent);
                rotateRight(grand);
            }
        } else { // R
            if (node.isLeftChild()) { // RL
                black(node);
                red(grand);
                rotateRight(parent);
                rotateLeft(grand);
            } else { // RR
                black(parent);
                red(grand);
                rotateLeft(grand);
            }
        }*/
    }

    @Override
    protected void afterRemove(Node<E> node, Node<E> replacement) {
        // 删除的节点, 都是叶子节点

        // 如果删除的节点为红色,则不需要处理
        if (isRed(node)) return;

        // 用于取代node的节点replacement为红色
        if (isRed(replacement)) {
            // 将替代节点染为黑色
            black(replacement);
            return;
        }

        // 删除的是根节点
        Node<E> parent = node.parent;
        if (parent == null) return;

        // 删除黑色的叶子节点(肯定会下溢)
        // 判断被删除的node是左还是右(如果直接通过sibling()方法,拿到的不准确,因为在remove方法中已经将node置为null了,然后才调用的afterRemove
        boolean left = parent.left == null || node.isLeftChild();
        Node<E> sibling = left ? parent.right : parent.left;
        if (left) { // 被删除的节点在左边, 兄弟节点在右边
            if (isRed(sibling)) {
                black(sibling);
                red(parent);
                rotateLeft(parent);
                sibling = parent.right;
            }
            // 兄弟节点必然是黑色
            if (isBlack(sibling.left) && isBlack(sibling.right)) {  // 表示node的黑兄弟节点的left,right子节点都是黑节点
                boolean parentBlack = isBlack(parent);
                black(parent);
                red(sibling);
                if (parentBlack) {
                    afterRemove(parent, null);
                }
            } else { // 表示兄弟节点至少有一个红色子节点,可以向被删除节点的位置借一个节点
                if (isBlack(sibling.right)) {
                    rotateRight(sibling);
                    sibling = parent.right;
                }
                color(sibling, colorOf(parent));
                black(sibling.right);
                black(parent);
                rotateLeft(parent);
            }
        } else { // 被删除节点在右边, 兄弟节点在左边
            if (isRed(sibling)) { // 兄弟节点是红色
                black(sibling);
                red(parent);
                rotateRight(parent); // 旋转之后,改变兄弟节点,然后node的兄弟节点就为黑色了
                // 更换兄弟节点
                sibling = parent.left;
            }

            // 兄弟节点必然是黑色
            if (isBlack(sibling.left) && isBlack(sibling.right)) {  // 表示node的黑兄弟节点的left,right子节点都是黑节点
                // 兄弟节点没有一个红色子节点(不能借一个节点给你), 父节点要向下跟node的兄弟节点合并
                /*
                    首先这里要判断父节点parent的颜色(如果为parent为红色,则根据B树红色节点向其黑色父节点合并原则,parent向下合并,肯定不会
                    发生下溢; 如果parent为黑色,则说明parent向下合并后,必然也会发生下溢,这里我们当作移除一个叶子结点处理,复用afterRemove
                 */
                boolean parentBlack = isBlack(parent);
                // 下面两行染色的代码,是说明parent为红色的情况
                black(parent);
                red(sibling);
                if (parentBlack) {
                    afterRemove(parent, null);
                }

            } else { // 表示兄弟节点至少有一个红色子节点,可以向被删除节点的位置借一个节点
                // 兄弟节点的左边是黑色, 先将兄弟节点左旋转; 旋转完之后和后面两种的处理方式相同,都是再对父节点进行右旋转
                if (isBlack(sibling.left)) {
                    rotateLeft(sibling);
                    sibling = parent.left; // 因为旋转之后,要更改node的sibling,才能复用下面的染色代码.不然出现bug
                }
                // 旋转之后的中心节点继承parent的颜色; 旋转之后的左右节点染为黑色
                // 先染色,再旋转: 肯定要先对node的sibling先染色
                color(sibling, colorOf(parent));
                black(sibling.left);
                black(parent);
                rotateRight(parent);
            }
        }
    }

    /**
     * 将node染成color色
     *
     * @param node  需要染色的节点
     * @param color 染成的颜色
     * @return 返回染色的节点
     */
    private Node<E> color(Node<E> node, boolean color) {
        if (node == null) return node;
        ((RBNode<E>) node).color = color;
        return node;
    }

    /**
     * 传进来的节点染成黑色
     *
     * @param node
     * @return
     */
    private Node<E> black(Node<E> node) {
        return color(node, BLACK);
    }

    /**
     * 传进来的节点染成红色
     *
     * @param node
     * @return
     */
    private Node<E> red(Node<E> node) {
        return color(node, RED);
    }

    /**
     * 返回当前节点的颜色
     *
     * @param node
     * @return 如果传入的节点为空, 默认为黑色
     */
    private boolean colorOf(Node<E> node) {
        return node == null ? BLACK : ((RBNode<E>) node).color;
    }

    /**
     * 节点是否为黑色
     *
     * @param node
     * @return
     */
    private boolean isBlack(Node<E> node) {
        return colorOf(node) == BLACK;
    }

    /**
     * 节点是否为红色
     *
     * @param node
     * @return
     */
    private boolean isRed(Node<E> node) {
        return colorOf(node) == RED;
    }

    @Override
    protected Node<E> createNode(E element, Node<E> parent) {
        return new RBNode<>(element, parent);
    }

    private static class RBNode<E> extends Node<E> {

        boolean color;

        public RBNode(E element, Node<E> parent) {
            super(element, parent);
        }

        @Override
        public String toString() {
            String str = "";
            // 红色加 R_, 默认黑色
            if (color == RED) {
                str = "R_";
            }
            return str + element.toString();
        }
    }
}
