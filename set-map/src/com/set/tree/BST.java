package com.set.tree;

import java.util.Comparator;

/**
 * Description: 二叉搜索树
 *
 * @author guizy
 * @date 2020/4/15 21:08
 */
public class BST<E> extends BinaryTree<E> {

    private Comparator<E> comparator; // 定义一个比较器

    public BST() {
        this(null);
    }

    public BST(Comparator<E> comparator) {
        this.comparator = comparator;
    }

    public void add(E element) {
        elementNotNullCheck(element);

        // 添加第一个节点
        if (root == null) {
            // 给根节点赋值,且根节点没有父节点
            root = createNode(element, null);
            size++;

            // 添加节点之后的处理
            afterAdd(root);
            return;
        }

        // 添加的不是第一个节点
        Node<E> parent = root; // 这个是第一次比较的父节点
        Node<E> node = root;
        int cmp = 0;
        while (node != null) {
            cmp = compare(element, node.element);   // 两者具体比较的方法
            parent = node; // 记录其每一次比较的父节点
            if (cmp > 0) {
                // 插入的元素大于根节点的元素,插入到根节点的右边
                node = node.right;
            } else if (cmp < 0) {
                // 插入的元素小于根节点的元素,插入到根节点的左边
                node = node.left;
            } else { // 相等
                node.element = element;
                return;
            }
        }
        // 看看插入到父节点的哪个位置
        Node<E> newNode = createNode(element, parent);
        if (cmp > 0) {
            parent.right = newNode;
        } else {
            parent.left = newNode;
        }
        size++;

        // 添加节点之后的逻辑
        afterAdd(newNode);
    }

    /**
     * 添加node之后的调整
     *
     * @param node 新添加的结点
     */
    protected void afterAdd(Node<E> node) {
    }

    /**
     * 删除node之后的调整
     *
     * @param node        被删除的结点
     * @param replacement   用于替代被删除的节点
     */
    protected void afterRemove(Node<E> node, Node<E> replacement) {
    }

    public void remove(E element) {
        remove(node(element));
    }

    public boolean contains(E element) {
        return node(element) != null;
    }

    /**
     * 删除结点的逻辑
     *
     * @param node
     */
    private void remove(Node<E> node) {
        if (node == null) return;
        // node 不为空, 必然要删除结点, 先size--;
        size--;
        // 删除node是度为2的结点
        if (node.hasTwoChildren()) {
            /*//1 找到后继(也可以找到前驱)
            Node<E> successor = successor(node);
            //2 用后继结点的值覆盖度为2结点的值
            node.element = successor.element;
            //3 删除后继节点
            node = successor;*/

            //1、找到前驱
            Node<E> predecessor = predecessor(node);
            //2、用前驱节点的值覆盖度为2节点的值
            node.element = predecessor.element;
            //3、删除前驱节点
            node = predecessor;
        }
        // 删除node,即删除后继节点 (node节点必然是度为1或0)
        // 因为node只有一个子节点/0个子节点, 如果其left!=null, 则用node.left来替代, node.left==null, 用node.right来替代,
        // 若node为叶子节点, 说明, node.left==null, node.right也为null, 则replacement==null;
        Node<E> replacement = node.left != null ? node.left : node.right;

        // 删除node是度为1的结点
        if (replacement != null) {
            // 更改parent
            replacement.parent = node.parent;
            // 更改parent的left、right的指向
            if (node.parent == null) {  // node是度为1且是根节点
                root = replacement;
            } else if (node == node.parent.left) {
                node.parent.left = replacement;
            } else if (node == node.parent.right) {
                node.parent.right = replacement;
            }
            // 删除结点之后的处理
            afterRemove(node, replacement);
            // 删除node是叶子节点, 且是根节点
        } else if (node.parent == null) {
            root = null;
            // 删除结点之后的处理
            afterRemove(node, null);
        } else { // node是叶子结点, 且不是根节点
            if (node == node.parent.left) {
                node.parent.left = null;
            } else {  // node == node.parent.right
                node.parent.right = null;
            }
            // 删除结点之后的处理
            afterRemove(node, null);
        }
    }

    /**
     * 传入element找到对应element对应的结点
     *
     * @param element
     * @return
     */
    private Node<E> node(E element) {
        Node<E> node = root;
        while (node != null) {
            int cmp = compare(element, node.element);
            if (cmp == 0) return node;
            if (cmp > 0) {  // 说明element对应的结点, 比node的element大, 所以去它的右子树找
                node = node.right;
            } else {
                node = node.left;
            }
        }
        return null; // 没有找到element对应的结点
    }

    /**
     * @return 返回值等于0, 代表e1=e2
     * 大于0,代表e1>e2
     * 小于0,代表e1<e2
     */
    private int compare(E e1, E e2) {
        if (comparator != null) { // 这里表示传入了比较器
            // 优先使用比较器
            return comparator.compare(e1, e2);
        }
        // 这里表示没有使用比较器,此时再强制将传入的元素实现Comparable接口,并重写接口中的方法
        //return ((Comparable<E>) e1).compareTo(e2);
        return ((Comparable<E>) e1).compareTo(e2);
    }

    /**
     * 二叉排序树结点内容不能为空(否则怎么排序呢?)
     *
     * @param element 结点内容
     */
    private void elementNotNullCheck(E element) {
        if (element == null) {
            // 手动抛出异常对象
            throw new IllegalArgumentException("element must not be null");
        }
    }
}
