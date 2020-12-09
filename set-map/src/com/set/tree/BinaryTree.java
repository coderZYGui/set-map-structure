package com.set.tree;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Description: 普通二叉树
 *
 * @author guizy
 * @date 2020/11/16 22:34
 */
public class BinaryTree<E>{

    protected int size;
    // 定义根节点
    protected Node<E> root;

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        root = null;
        size = 0;
    }

    /**
     * 前序遍历(根左右)
     */
    public void preorderTraversal(BST.Visitor<E> visitor) {
        this.preorderTraversal(root, visitor); // 从根节点开始遍历
    }

    private void preorderTraversal(Node<E> node, BST.Visitor<E> visitor) {
        if (node == null || visitor == null) return;

        visitor.visit(node.element);
        preorderTraversal(node.left, visitor);
        preorderTraversal(node.right, visitor);
    }

    /**
     * 中序遍历(左根右 / 右根左), 相当于升序/降序
     */
    public void inorderTraversal(BST.Visitor<E> visitor) {
        this.inorderTraversal(root, visitor); // 从根节点开始遍历
    }

    private void inorderTraversal(Node<E> node, BST.Visitor<E> visitor) {
        if (node == null || visitor == null) return;

        inorderTraversal(node.left, visitor);
        visitor.visit(node.element);
        inorderTraversal(node.right, visitor);
    }

    /**
     * 后序遍历(左右根/右左根)
     */
    public void postorderTraversal(BST.Visitor<E> visitor) {
        this.postorderTraversal(root, visitor); // 从根节点开始遍历
    }

    private void postorderTraversal(Node<E> node, BST.Visitor<E> visitor) {
        if (node == null || visitor == null) return;

        postorderTraversal(node.left, visitor);
        postorderTraversal(node.right, visitor);
        visitor.visit(node.element);
    }

    // 层序遍历
    public void levelOrder(BST.Visitor<E> visitor) {
        if (root == null || visitor == null) return;

        Queue<Node<E>> queue = new LinkedList<>();
        queue.offer(root); // 将根节点先入队

        while (!queue.isEmpty()) {
            Node<E> node = queue.poll(); // 取出队头节点

            visitor.visit(node.element);

            if (node.left != null) {    // 如果该节点左节点不为空,则将该左节点入队
                queue.offer(node.left);
            }
            if (node.right != null) {   // 如果该节点右节点不为空,则将该右节点入队
                queue.offer(node.right);
            }
        }
    }

    public interface Visitor<E> {
        boolean visit(E element);
    }

    // 计算二叉树排序树的高度
    // 方式一: 递归的方式
    public int height() {
        return height(root);
    }

    public int height(Node<E> node) {
        if (node == null) return 0;
        // 左子节点或右子节点中高度最高的值 + 1
        return 1 + Math.max(height(node.left), height(node.right));
    }

    protected Node<E> createNode(E element, Node<E> parent) {
        return new Node<>(element, parent);
    }

    // 判断一棵二叉树是否为完全二叉树(推荐方式二)
    // 方式二
    public boolean isCompleteTree() {
        if (root == null) return false;

        Queue<Node<E>> queue = new LinkedList<>();
        queue.offer(root); // 将根节点先入队

        boolean leaf = false;
        while (!queue.isEmpty()) {
            Node<E> node = queue.poll(); // 取出队头节点

            if (leaf && !node.isLeaf()) return false;

            if (node.left != null) {    // 如果该节点左节点不为空,则将该左节点入队
                queue.offer(node.left);
            } else if (node.right != null) {
                // node.left == null && node.right != null
                return false;
            }

            if (node.right != null) {   // 如果该节点右节点不为空,则将该右节点入队
                queue.offer(node.right);
            } else {
                // 能来到这里, 说明后面的节点都是叶子节点
                // node.left == null && node.right == null
                // node.left != null && node.right == null
                leaf = true;
            }
        }
        return true;
    }

    /**
     * 根据传入的节点, 返回该节点的前驱节点 (中序遍历)
     *
     * @param node
     * @return
     */
    protected Node<E> predecessor(Node<E> node) {
        if (node == null) return node;

        // (中序遍历)前驱节点在左子树当中(node.left.right.right.right...)
        Node<E> p = node.left;
        // 左子树存在
        if (p != null) {
            while (p.right != null) {
                p = p.right;
            }
            return p;
        }

        // 程序走到这里说明左子树不存在; 从父节点、祖父节点中寻找前驱节点
        /*
         * node的父节点不为空 && node是其父节点的左子树时. 就一直往上寻找它的父节点
         *  因为node==node.parent.right, 说明你在你父节点的右边, 那么node.parent就是其node的前驱节点
         */
        while (node.parent != null && node == node.parent.left) {
            node = node.parent;
        }

        // 能来到这里表示: 两种情况如下
        // node.parent == null 表示没有父节点(根节点),返回空 ==> return node.parent;
        // node==node.parent.right 说明你在你父节点的右边, 那么node.parent就是其node的前驱节点 ==> return node.parent;
        return node.parent;
    }

    /**
     * 根据传入的节点, 返回该节点的后驱节点 (中序遍历)
     *
     * @param node
     * @return
     */
    protected Node<E> successor(Node<E> node) {
        if (node == null) return node;

        Node<E> p = node.right;
        if (p != null) {
            while (p.left != null) {
                p = p.left;
            }
            return p;
        }

        // node.right为空
        while (node.parent != null && node == node.parent.right) {
            node = node.parent;
        }

        return node.parent;
    }

    protected static class Node<E> {
        E element;
        Node<E> left;
        Node<E> right;
        Node<E> parent;

        // 这里不初始化左,右结点,因为不常用,比如所叶子结点,就灭有左右结点
        // 父节点,除了根节点外,都有父节点
        public Node(E element, Node<E> parent) {
            this.element = element;
            this.parent = parent;
        }

        /**
         * 判断是否为叶子节点
         *
         * @return
         */
        public boolean isLeaf() {
            return left == null && right == null;
        }

        /**
         * 判断是否为左右节点都不为空的节点
         *
         * @return
         */
        public boolean hasTwoChildren() {
            return left != null && right != null;
        }

        /**
         * 判断自己是否左子树
         *
         * @return
         */
        public boolean isLeftChild() {
            return parent != null && this == parent.left;
        }

        /**
         * 判断自己是否左子树
         *
         * @return
         */
        public boolean isRightChild() {
            return parent != null && this == parent.right;
        }

        /**
         * 返回节点的兄弟节点. 注: 节点的叔父节点,即parent.sibling(), 父节点的兄弟节点
         *
         * @return
         */
        public Node<E> sibling() {
            // 自己是父节点的左子树, 返回父节点的右子树(即兄弟节点)
            if (isLeftChild()) {
                return parent.right;
            }
            // 自己是父节点的右子树, 返回父节点的左子树(即兄弟节点)
            if (isRightChild()) {
                return parent.left;
            }
            // 没有父节点, 即没有兄弟节点
            return null;
        }
    }
}
