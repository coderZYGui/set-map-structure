package com.hashtable;

import com.hashtable.map.HashMap;
import com.hashtable.model.Key;
import com.hashtable.model.Person;
import org.junit.Test;

/**
 * Description: 测试哈希表
 *
 * @author guizy
 * @date 2020/12/11 23:17
 */
public class Main {
    @Test
    public void test5() {
        HashMap<Object, Integer> map = new HashMap<>();
        for (int i = 1; i <= 19; i++) {
            map.put(new Key(i), i);
        }
        map.put(new Key(4), 100);
        Assert.test(map.size() == 19);
        Assert.test(map.get(new Key(4)) == 100);
        Assert.test(map.get(new Key(18)) == 18);
        // map.print();
    }

    @Test
    public void test4() {
        HashMap<Object, Integer> map = new HashMap<>();
        for (int i = 1; i <= 19; i++) {
            map.put(new Key(i), i);
        }
        System.out.println(map.get(new Key(1))); // null
        map.print();
        // System.out.println(map.size()); // 19
    }

    @Test
    public void test3() {
        Person p1 = new Person(10, 1.4f, "jack");
        Person p2 = new Person(10, 1.4f, "jack");
        HashMap<Object, Integer> map = new HashMap<>();
        map.put(p1, 1);
        map.put(p2, 2);
        map.put("jack", 3);
        map.put("rose", 4);
        map.put("jack", 5);
        map.put(null, 6);
        //System.out.println(map.size()); //  4

//        System.out.println(map.get("jack"));
//        System.out.println(map.get("rose"));
//        System.out.println(map.get(null));
//        System.out.println(map.get(p1));

        // 测试remove, get
//        System.out.println(map.size());
//        System.out.println(map.remove("jack"));
//        System.out.println(map.get("jack"));
//        System.out.println(map.size());

        // 测试遍历
//        map.traversal(new Map.Visitor<Object, Integer>() {
//            @Override
//            public boolean visit(Object key, Integer value) {
//                System.out.println(key + "_" + value);
//                return false;
//            }
//        });

        // 测试containsKey, containsValue
        System.out.println(map.containsKey(p1));
        System.out.println(map.containsKey(null));
        System.out.println(map.containsValue(6));
        System.out.println(map.containsValue(1));
    }

    @Test
    public void test2() {
        String str1 = "NB";
        String str2 = "Ma";
        boolean b = str1.hashCode() == str2.hashCode();
        boolean c = str1.equals(str2);

        System.out.println("b = " + b); // truq
        System.out.println("c = " + c); // false
    }

    @Test
    public void test1() {
        Person p1 = new Person(10, 1.67f, "jack");
        Person p2 = new Person(10, 1.67f, "jack");

//        Map<Object, Object> map = new HashMap<>();
//        map.put(p1, "abc");
//        map.put("test", "ccc");
//        map.put(p2, "bcd");
//        System.out.println(map.size());

        /*
        现在的需求: 当Person的age,height,name都相同的时候, 就认定p1,p2是同一个key, 进行hash(p1), hash(p2)生成的hash值
          就是相同的, 所以它们在哈希表中的索引位置也是相同的. 此时他们就出现了哈希冲突, 将p2的"bcd"覆盖掉p1的"abc"
          想要解决"当Person的age,height,name都相同的时候, 就认定p1,p2是同一个key", 我们就要在Person类中重写hashCode方法,
          然后在该方法中对Person的属性进行充分利用. 通过"计算字符串的方式计算"
         */
    }

    @Test
    public void test() {
        String str = "jack";
        int length = str.length();
        int hashCode = 0;
        for (int i = 0; i < length; i++) {
            char c = str.charAt(i);
            hashCode = hashCode * 31 + c;
        }
        // hashCode = ((j * 31 + a) * 31 + c) * 31 + k
        System.out.println(hashCode);   // 3254239

        // Java官方String的hashCode
        int hashCode2 = str.hashCode();
        System.out.println(hashCode2);  // 3254239
    }
}
