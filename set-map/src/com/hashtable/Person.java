package com.hashtable;

/**
 * Description: 怎么计算及定义对象的哈希值
 *
 * @author guizy
 * @date 2020/12/11 23:33
 */
public class Person {
    private int age;
    private float height;
    private String name;

    public Person(int age, float height, String name) {
        this.age = age;
        this.height = height;
        this.name = name;
    }

    // Person对象的age,height,name相同, 就认为他们是同一个对象(放到hashMap里面同一个key)
    @Override
    public boolean equals(Object obj) {
        // 内存地址相同
        if (this == obj) return true;
        // 当前对象肯定不为空, 因为要调用equals方法
        if (obj == null || obj.getClass() != this.getClass()) return false;
        // 比较成员变量
        Person person = (Person) obj;
        return person.age == age
                && person.height == height
                && (person.name == null ? name == null : person.name.equals(name));
    }

    // 这样只要Person对象的age,height,name相同, 它们的hashCode值必然相同
    @Override
    public int hashCode() {
        int hash = Integer.hashCode(age);
        hash = hash * 31 + Float.hashCode(height);
        hash = hash * 31 + (name != null ? name.hashCode() : 0);
        return hash;
    }
}
