package com.map;

import com.map.file.FileInfo;
import com.map.file.Files;
import com.map.map.Map;
import com.map.map.TreeMap;
import com.map.set.Set;
import com.map.set.TreeSet;
import org.junit.Test;

/**
 * Description:
 *
 * @author guizy1
 * @date 2020/12/9 15:43
 */
public class Main {

    // 测试TreeMap来实现TreeSet
    @Test
    public void test3() {
        Set<Integer> treeSet = new TreeSet<>();
        treeSet.add(1);
        treeSet.add(7);
        treeSet.add(3);
        treeSet.add(7);
        treeSet.add(0);

        treeSet.traversal(new Set.Visitor<Integer>() {
            @Override
            public boolean visit(Integer element) {
                System.out.println(element);
                return false;
            }
        });
    }

    @Test
    public void test1() {
        Map<String, Integer> map = new TreeMap<>();
        map.put("c", 2);
        map.put("a", 5);
        map.put("b", 6);
        map.put("a", 8);

        map.traversal(new Map.Visitor<String, Integer>() {
            @Override
            public boolean visit(String key, Integer value) {
                System.out.println(key + "_" + value);
                return false;
            }
        });
    }

    @Test
    public void test2() {
        FileInfo fileInfo = Files.read("D:\\tree-structure\\tree\\src",
                new String[]{"java"});

        System.out.println("文件数量：" + fileInfo.getFiles());
        System.out.println("代码行数：" + fileInfo.getLines());
        String[] words = fileInfo.words();
        System.out.println("单词数量：" + words.length);

        Map<String, Integer> map = new TreeMap<>();
        for (int i = 0; i < words.length; i++) {
            Integer count = map.get(words[i]);
            // count = count == null ? 0 : count + 1;
            // map.put(words[i], count + 1);
            count = (count == null) ? 1 : (count + 1);
            map.put(words[i], count);
        }

        map.traversal(new Map.Visitor<String, Integer>() {
            public boolean visit(String key, Integer value) {
                System.out.println(key + "_" + value);
                return false;
            }
        });
    }

}
