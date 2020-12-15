package com.hashtable.model;

/**
 * Description:
 *
 * @author guizy
 * @date 2020/12/16 01:04
 */
public class Key {
    private int value;

    public Key(int value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return value / 5;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != getClass()) return false;
        return ((Key) obj).value == value;
    }

    @Override
    public String toString() {
        return "v(" + value + ")";
    }
}