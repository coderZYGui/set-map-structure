package com.map.map;

import java.util.Comparator;

/**
 * Description: TreeMap使用红黑树来实现, 红黑树的节点中存储的是key-value键值对
 *
 * @author guizy1
 * @date 2020/12/9 15:48
 */
public class TreeMap<K, V> implements Map<K, V> {

    private static final boolean RED = false;
    private static final boolean BLACK = true;

    private int size;

    // 定义根节点
    private Node<K, V> root;

    private Comparator<K> comparator; // 定义一个比较器

    public TreeMap() {
        this(null);
    }

    public TreeMap(Comparator<K> comparator) {
        this.comparator = comparator;
    }

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
     * 想TreeMap中添加一个结点
     *
     * @param key   键
     * @param value 值
     * @return 返回之前被覆盖的值
     */
    @Override
    public V put(K key, V value) {
        keyNotNullCheck(key);

        // 添加第一个节点
        if (root == null) {
            // 给根节点赋值,且根节点没有父节点
            root = new Node<>(key, value, null);
            size++;

            // 添加节点之后的处理
            afterPut(root);
            return null;
        }

        // 添加的不是第一个节点
        Node<K, V> parent = root; // 这个是第一次比较的父节点
        Node<K, V> node = root;
        int cmp = 0;
        while (node != null) {
            cmp = compare(key, node.key);   // 两者具体比较的方法
            parent = node; // 记录其每一次比较的父节点
            if (cmp > 0) {
                // 插入的元素大于根节点的元素,插入到根节点的右边
                node = node.right;
            } else if (cmp < 0) {
                // 插入的元素小于根节点的元素,插入到根节点的左边
                node = node.left;
            } else { // 相等
                node.key = key;
                V oldValue = node.value;
                node.value = value;
                return oldValue; // 返回之前node的value
            }
        }
        // 看看插入到父节点的哪个位置
        Node<K, V> newNode = new Node<>(key, value, parent);
        if (cmp > 0) {
            parent.right = newNode;
        } else {
            parent.left = newNode;
        }
        size++;

        // 添加节点之后的逻辑
        afterPut(newNode);
        // 这里的key是第一次加进去的, 之前没有值, 所以返回null
        return null;
    }

    @Override
    public V get(K key) {
        return null;
    }

    @Override
    public V remove(K key) {
        return null;
    }

    @Override
    public boolean containsKey(K key) {
        return false;
    }

    @Override
    public boolean containsValue(V value) {
        return false;
    }

    @Override
    public void traversal(Visitor<K, V> visitor) {

    }

    private void afterPut(Node<K, V> node) {
        Node<K, V> parent = node.parent;

        // 添加的是根节点(染成黑色)
        if (parent == null) {
            black(node);
            return;
        }

        // ------------- 一共 12 种情况--------------
        // 不需要处理的4种情况:  如果父节点是黑色, 直接返回
        if (isBlack(parent)) return;

        // 根据uncle节点的颜色来判断其他的各4种情况
        Node<K, V> uncle = parent.sibling();
        // 祖父节点
        Node<K, V> grand = parent.parent;

        // 需要处理的4种情况: 叔父节点是红色
        if (isRed(uncle)) {
            black(parent);
            black(uncle);
            // 把祖父节点染成红色, 当做新添加的节点处理(递归调用afterAdd)
            afterPut(red(grand));
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

    }

    /**
     * @return 返回值等于0, 代表e1=e2
     * 大于0,代表e1>e2
     * 小于0,代表e1<e2
     */
    private int compare(K k1, K k2) {
        if (comparator != null) { // 这里表示传入了比较器
            // 优先使用比较器
            return comparator.compare(k1, k2);
        }
        // 这里表示没有使用比较器,此时再强制将传入的元素实现Comparable接口,并重写接口中的方法
        //return ((Comparable<E>) k1).compareTo(k2);
        return ((Comparable<K>) k1).compareTo(k2);
    }

    /**
     * 对node进行左旋转
     *
     * @param grand
     */
    private void rotateLeft(Node<K, V> grand) {
        Node<K, V> parent = grand.right;
        Node<K, V> child = parent.left;
        grand.right = child;
        parent.left = grand;

        afterRotate(grand, parent, child);
    }

    /**
     * 对node进行右旋转
     *
     * @param grand
     */
    private void rotateRight(Node<K, V> grand) {
        Node<K, V> parent = grand.left;
        Node<K, V> child = parent.right;
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
    private void afterRotate(Node<K, V> grand, Node<K, V> parent, Node<K, V> child) {
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
     * 将node染成color色
     *
     * @param node  需要染色的节点
     * @param color 染成的颜色
     * @return 返回染色的节点
     */
    private Node<K, V> color(Node<K, V> node, boolean color) {
        if (node == null) return node;
        node.color = color;
        return node;
    }

    /**
     * 传进来的节点染成黑色
     *
     * @param node
     * @return
     */
    private Node<K, V> black(Node<K, V> node) {
        return color(node, BLACK);
    }

    /**
     * 传进来的节点染成红色
     *
     * @param node
     * @return
     */
    private Node<K, V> red(Node<K, V> node) {
        return color(node, RED);
    }

    /**
     * 返回当前节点的颜色
     *
     * @param node
     * @return 如果传入的节点为空, 默认为黑色
     */
    private boolean colorOf(Node<K, V> node) {
        return node == null ? BLACK : node.color;
    }

    /**
     * 节点是否为黑色
     *
     * @param node
     * @return
     */
    private boolean isBlack(Node<K, V> node) {
        return colorOf(node) == BLACK;
    }

    /**
     * 节点是否为红色
     *
     * @param node
     * @return
     */
    private boolean isRed(Node<K, V> node) {
        return colorOf(node) == RED;
    }

    private void keyNotNullCheck(K key) {
        if (key == null) {
            // 手动抛出异常对象
            throw new IllegalArgumentException("key must not be null");
        }
    }

    private static class Node<K, V> {
        K key;
        V value;
        boolean color = RED;
        Node<K, V> left;
        Node<K, V> right;
        Node<K, V> parent;

        public Node(K key, V value, Node<K, V> parent) {
            this.key = key;
            this.value = value;
            this.parent = parent;
        }

        public boolean isLeaf() {
            return left == null && right == null;
        }

        public boolean hasTwoChildren() {
            return left != null && right != null;
        }

        public boolean isLeftChild() {
            return parent != null && this == parent.left;
        }

        public boolean isRightChild() {
            return parent != null && this == parent.right;
        }

        public Node<K, V> sibling() {
            if (isLeftChild()) {
                return parent.right;
            }

            if (isRightChild()) {
                return parent.left;
            }
            return null;
        }
    }
}
