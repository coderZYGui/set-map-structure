package com.hashtable.map;

import com.hashtable.printer.BinaryTreeInfo;
import com.hashtable.printer.BinaryTrees;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

/**
 * Description: 使用哈希表来实现一个HashMap
 *
 * @author guizy
 * @date 2020/12/14 22:00
 */
public class HashMap<K, V> implements Map<K, V> {

    private static final boolean RED = false;
    private static final boolean BLACK = true;
    private static final int DEFAULT_CAPACITY = 1 << 4;

    private int size;   // HashMap的容量(哈希表容量), 用来记录存放多少个entry(键值对)

    // 存放 红黑树根节点 的数组(哈希表底层就是数组), 也就是说哈希表中每一个桶中都是一颗红黑树. 桶数组
    private Node<K, V>[] table;

    public HashMap() {
        table = new Node[DEFAULT_CAPACITY];
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        // 这里的判断是因为, 如果size==0的时候, 调用clear, 还是会进入下面的循环,进行清空,数组只是开辟了16个空的位置,本来就是空的.
        if (size == 0) return;
        size = 0;
        // 将哈希表中每一个桶(红黑树的根节点)都清空.
        for (int i = 0; i < table.length; i++) {
            table[i] = null;
        }
    }

    /**
     * 往哈希表数组中(桶中)添加
     *
     * @param key
     * @param value
     * @return 返回是被替代的value
     */
    @Override
    public V put(K key, V value) {
        // 哈希表中的索引
        int index = index(key);
        // 取出index位置(数组中)的红黑树根节点(因为哈希表中存储的就是红黑树的根节点(键值对))
        Node<K, V> root = table[index];
        if (root == null) {
            root = new Node<>(key, value, null);
            table[index] = root;
            size++;
            // 修复红黑树性质
            afterPut(root);
            return null;
        }
        // 出现hash冲突, 说明table[index]表中的位置不为空,已经有红黑树的根节点了
        // 添加的不是第一个节点
        Node<K, V> parent = root; // 这个是第一次比较的父节点
        Node<K, V> node = root;
        int cmp = 0;
        int h1 = key == null ? 0 : key.hashCode();
        do {
            cmp = compare(key, node.key, h1, node.hash);   // 两者具体比较的方法
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
        } while (node != null);
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
        if (size == 0) return false;
        Queue<Node<K, V>> queue = new LinkedList<>();
        // 遍历哈希表中的所有的桶, 然后根据每个桶中的红黑树根节点, 层序遍历, 看看是否存在value
        for (int i = 0; i < table.length; i++) {
            // 说明哈希表中的table[i]的位置没有红黑树根节点, 也就是为空, 此时不用遍历比较.跳过
            if (table[i] == null) continue;
            queue.offer(table[i]);
            while (!queue.isEmpty()) {
                Node<K, V> node = queue.poll();
                if (Objects.equals(value, node.value)) return true;
                if (node.left != null) {
                    queue.offer(node.left);
                }
                if (node.right != null) {
                    queue.offer(node.right);
                }
            }
        }
        return false;
    }

    @Override
    public void traversal(Visitor<K, V> visitor) {
        if (size == 0 || visitor == null) return;
        Queue<Node<K, V>> queue = new LinkedList<>();
        // 遍历哈希表中的所有的桶, 然后根据每个桶中的红黑树根节点, 层序遍历, 看看是否存在value
        for (int i = 0; i < table.length; i++) {
            // 说明哈希表中的table[i]的位置没有红黑树根节点, 也就是为空, 此时不用遍历比较.跳过
            if (table[i] == null) continue;
            queue.offer(table[i]);
            while (!queue.isEmpty()) {
                Node<K, V> node = queue.poll();
                // 返回为true, 就停止遍历
                if (visitor.visit(node.key, node.value)) return;
                if (node.left != null) {
                    queue.offer(node.left);
                }
                if (node.right != null) {
                    queue.offer(node.right);
                }
            }
        }
    }

    public void print() {
        if (size == 0) return;
        for (int i = 0; i < table.length; i++) {
            final Node<K, V> root = table[i];
            System.out.println("[index = " + i + "]");
            BinaryTrees.println(new BinaryTreeInfo() {
                @Override
                public Object root() {
                    return root;
                }

                @Override
                public Object left(Object node) {
                    return ((Node<K, V>) node).left;
                }

                @Override
                public Object right(Object node) {
                    return ((Node<K, V>) node).right;
                }

                @Override
                public Object string(Object node) {
                    return node;
                }
            });
            System.out.println("----------------------------------");
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
        // 获取要删除节点的索引(就是红黑树所在桶的索引)
        int index = index(node);
        // 删除node是度为1的结点
        if (replacement != null) {
            // 更改parent
            replacement.parent = node.parent;
            // 更改parent的left、right的指向
            if (node.parent == null) {  // node是度为1且是根节点
                table[index] = replacement;
            } else if (node == node.parent.left) {
                node.parent.left = replacement;
            } else if (node == node.parent.right) {
                node.parent.right = replacement;
            }
            // 删除结点之后的处理
            afterRemove(node, replacement);
            // 删除node是叶子节点, 且是根节点
        } else if (node.parent == null) {
            table[index] = null;
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
     * 根据一个key, 找到对应的节点
     *
     * @param key
     * @return
     */
    private Node<K, V> node(K key) {
        // 确定key在哪个桶里面(索引)
        int index = index(key);
        // 根据key所在桶的索引, 找到该桶的红黑树根节点
        Node<K, V> node = table[index];
        // 再根据红黑树的根节点, 去找到key所对应的节点, 拿到value返回
        int h1 = key == null ? 0 : key.hashCode();
        while (node != null) {
            int cmp = compare(key, node.key, h1, node.hash);
            if (cmp == 0) return node;
            if (cmp > 0) {
                node = node.right;
            } else if (cmp < 0) {
                node = node.left;
            }
        }
        return null;
    }

    /**
     * 计算key的索引(在哈希表(数组)的哪个索引位置)
     *
     * @param key
     * @return
     */
    private int index(K key) {
        if (key == null) return 0; // 如果key为空, 插入到哈希表的0位置
        int hash = key.hashCode();
        // 同Double,Long的hashCode类似实现,因为key.hashCode()是我们自己实现的,在JDK底层又作了一次混合运算
        // 拿到我们自己实现的hash值, 将hash值和hash值无符号右移16位再做一次运算
        hash = hash ^ (hash >>> 16);
        return hash & (table.length - 1);
    }

    /**
     * 根据传入的node,计算它在数组中的哪个索引位置(即使它不是根节点)
     *
     * @param node
     * @return
     */
    private int index(Node<K, V> node) {
        return (node.hash ^ (node.hash >>> 16)) & (table.length - 1);
    }

    /**
     * 比较key的大小
     *
     * @param k1
     * @param k2
     * @param h1 h1的哈希值
     * @param h2 h2的哈希值
     * @return
     */
    private int compare(K k1, K k2, int h1, int h2) {
        // 根据哈希值来判断: 比较哈希值
        int result = h1 - h2;
        // 表示哈希值不同
        if (result != 0) return result;
        // 哈希值相同,不代表它们就是同一个对象,不能直接覆盖. 要判断它们的equals方法
        // equals相同, 返回0(说明相同是同一个对象), 进行覆盖
        if (Objects.equals(k1, k2)) return 0;
        // 哈希值相同,equals不同
        // 比较key的类名(因为红黑树中的节点类型是各种类型)
        if (k1 != null && k2 != null) {
            String k1Class = k1.getClass().getName();
            String k2Class = k2.getClass().getName();
            result = k1Class.compareTo(k2Class);
            // 不同类型
            if (result != 0) return result;
            // 同一种类型, 并且k1,k2的类型都实现了Comparable接口(具备可比较性)
            if (k1 instanceof Comparable) {
                // 走k1内部的比较逻辑
                return ((Comparable) k1).compareTo(k2);
            }
        }
        // 同一种类型, 哈希值相同, 但不具备可比较性
        // k1,k2都为null, 在Objects.equals方法中作了判断
        // k1不为null, k2为null
        // k1为null, k2不为null
        return System.identityHashCode(k1) - System.identityHashCode(k2); // 根据内存地址来生成一个hashCode
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
                    发生下溢; 如果parent为黑色,则说明parent向下合并后,必然也会发生下溢,这里我们当作移除一个叶子节点处理,复用afterRemove
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
            // 更改原来的红黑树根节点(就是哪个桶的位置)
            //table[index(grand.key)] = parent;
            table[index(grand)] = parent;
        }

        // 更新child的parent
        if (child != null)
            child.parent = grand;

        // 更新grand的parent
        grand.parent = parent;
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

    private static class Node<K, V> {
        int hash;
        K key;
        V value;
        boolean color = RED;
        Node<K, V> left;
        Node<K, V> right;
        Node<K, V> parent;

        public Node(K key, V value, Node<K, V> parent) {
            this.key = key;
            this.hash = key == null ? 0 : key.hashCode();
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

        @Override
        public String toString() {
            return "Node_" + key + "_" + value;
        }
    }
}
