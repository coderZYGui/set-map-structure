package com.set;

import com.set.file.FileInfo;
import com.set.file.Files;
import com.set.set.ListSet;
import com.set.set.Set;
import com.set.set.TreeSet;
import com.set.util.Times;
import org.junit.Test;

/**
 * Description:
 *
 * @author guizy
 * @date 2020/12/8 22:50
 */
public class Main {
    public static void main(String[] args) {

    }

    static void testSet(Set<String> set, String[] words) {
        for (int i = 0; i < words.length; i++) {
            set.add(words[i]);
        }
        for (int i = 0; i < words.length; i++) {
            set.contains(words[i]);
        }
        for (int i = 0; i < words.length; i++) {
            set.remove(words[i]);
        }
    }

    @Test
    public void testSrcWords() {
        FileInfo fileInfo = Files.read("C:\\Users\\guizy1\\Desktop\\src\\java\\util", new String[]{"java"});
        System.out.println("文件数量：" + fileInfo.getFiles());
        System.out.println("代码行数：" + fileInfo.getLines());
        String[] words = fileInfo.words();
        System.out.println("单词数量：" + words.length);

//        Times.test("ListSet", new Times.Task() {
//            @Override
//            public void execute() {
//                testSet(new ListSet<>(), words);
//            }
//        });

        Times.test("TreeSet", () -> testSet(new TreeSet<>(), words));
    }

    @Test
    public void testTreeSet() {
        Set<Integer> treeSet = new TreeSet<>();
        treeSet.add(12);
        treeSet.add(10);
        treeSet.add(11);
        treeSet.add(11);
        treeSet.add(10);

        treeSet.traversal(new Set.Visitor<Integer>() {
            @Override
            public boolean visit(Integer element) {
                System.out.println(element);
                return false;
            }
        });
    }

    @Test
    public void testListSet() {
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
