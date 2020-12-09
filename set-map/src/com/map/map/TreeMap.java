package com.map.map;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;

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
        Node<K, V> node = node(key);
        return node != null ? node.value : null;
    }

    @Override
    public V remove(K key) {
        return remove(node(key));
    }

    @Override
    public boolean containsKey(K key) {
        return node(key) != null;
    }

    @Override
    public boolean containsValue(V value) {
        if (root == null) return false;

        Queue<Node<K, V>> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            Node<K, V> node = queue.poll();
            if (valEquals(value, node.value))
                return true; // 说明存在

            if (node.left != null)
                queue.offer(node.left);

            if (node.right != null)
                queue.offer(node.right);
        }
        return false;
    }

    @Override
    public void traversal(Visitor<K, V> visitor) {

    }

    private boolean valEquals(V v1, V v2) {
        // 如果v1==null,说明为true, 走v2==null, v2等于null的话, 说明v1和v2相等, v2不等于空的话, 返回false
        // 如果v1!=null, 直接判断v1.equals(v2)是否相等
        return v1 == null ? v2 == null : v1.equals(v2);
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

    private void afterRemove(Node<K, V> node, Node<K, V> replacement) {
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
        Node<K, V> parent = node.parent;
        if (parent == null) return;

        // 删除黑色的叶子节点(肯定会下溢)
        // 判断被删除的node是左还是右(如果直接通过sibling()方法,拿到的不准确,因为在remove方法中已经将node置为null了,然后才调用的afterRemove
        boolean left = parent.left == null || node.isLeftChild();
        Node<K, V> sibling = left ? parent.right : parent.left;
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

    private V remove(Node<K, V> node) {
        if (node == null) return null;
        // node 不为空, 必然要删除结点, 先size--;
        size--;

        V oldValue = node.value;

        // 删除node是度为2的结点
        if (node.hasTwoChildren()) {
            /*//1 找到后继(也可以找到前驱)
            Node<E> successor = successor(node);
            //2 用后继结点的值覆盖度为2结点的值
            node.element = successor.element;
            //3 删除后继节点
            node = successor;*/

            //1、找到前驱
            Node<K, V> predecessor = predecessor(node);
            //2、用前驱节点的值覆盖度为2节点的值
            node.key = predecessor.key;
            node.value = predecessor.value;
            //3、删除前驱节点
            node = predecessor;
        }
        // 删除node,即删除后继节点 (node节点必然是度为1或0)
        // 因为node只有一个子节点/0个子节点, 如果其left!=null, 则用node.left来替代, node.left==null, 用node.right来替代,
        // 若node为叶子节点, 说明, node.left==null, node.right也为null, 则replacement==null;
        Node<K, V> replacement = node.left != null ? node.left : node.right;

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
        return oldValue;
    }

    /**
     * 根据传入的节点, 返回该节点的前驱节点 (中序遍历)
     *
     * @param node
     * @return
     */
    private Node<K, V> predecessor(Node<K, V> node) {
        if (node == null) return node;

        // (中序遍历)前驱节点在左子树当中(node.left.right.right.right...)
        Node<K, V> p = node.left;
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
    private Node<K, V> successor(Node<K, V> node) {
        if (node == null) return node;

        Node<K, V> p = node.right;
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


    /**
     * 传入key找到对应红黑树对应的结点, 然后取出k红黑树节点的value
     *
     * @param key
     * @return
     */
    private Node<K, V> node(K key) {
        Node<K, V> node = root;
        while (node != null) {
            int cmp = compare(key, node.key);
            if (cmp == 0) return node;
            if (cmp > 0) {  // 说明key对应的结点, 比node的key大, 所以去它的右子树找
                node = node.right;
            } else {
                node = node.left;
            }
        }
        return null; // 没有找到key对应的结点
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
