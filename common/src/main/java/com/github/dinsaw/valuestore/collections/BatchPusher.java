package com.github.dinsaw.valuestore.collections;

public interface BatchPusher<T> {
    void add(T input);
    int size();
    void flush();
}
