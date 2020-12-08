package com.guizy.set;

/**
 * Description: Set集合
 *
 *  这里为Set集合, 提供了遍历集合的接口, 为什么我们之前的动态数组,链表没有提供呢, 因为它们有索引的概念, 可以通过for就可以遍历。
 *  然而Set集合, 没有索引的概念, 所以要提供遍历接口。
 *
 * @author guizy
 * @date 2020/12/8 22:27
 */
public interface Set<E> {

    int size();

    boolean isEmpty();

    void clear();

    boolean contains(E element);

    void add(E element);

    void remove(E element);

    void traversal(Visitor<E> visitor); //遍历集合

    public static abstract class Visitor<E> {
        boolean stop;
        public abstract boolean visit(E element);
    }
}


