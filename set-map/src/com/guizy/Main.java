package com.guizy;

import com.guizy.set.ListSet;
import com.guizy.set.Set;

/**
 * Description:
 *
 * @author guizy
 * @date 2020/12/8 22:50
 */
public class Main {
    public static void main(String[] args) {

        Set<Integer> listSet = new ListSet<>();
        listSet.add(10);
        listSet.add(11);
        listSet.add(11);
        listSet.add(12);
        listSet.add(10);

        listSet.traversal(new Set.Visitor<Integer>() {
            @Override
            public boolean visit(Integer element) {
                System.out.println(element);
                return false;
            }
        });
    }
}
