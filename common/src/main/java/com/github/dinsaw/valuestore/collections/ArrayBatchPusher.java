package com.github.dinsaw.valuestore.collections;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ArrayBatchPusher<T> implements BatchPusher<T> {

    private List<T> elements;
    private Integer batchSize;
    private Consumer<List<T>> consumer;

    public ArrayBatchPusher(Integer batchSize, Consumer<List<T>> consumer) {
        this.batchSize = batchSize;
        this.consumer = consumer;
        elements = new ArrayList<T>(batchSize);
    }

    @Override
    public void add(T element) {
        if (elements.size() == (batchSize-1)) {
            elements.add(element);
            flush();
        }
        elements.add(element);
    }

    @Override
    public int size() {
        return elements.size();
    }

    @Override
    public void flush() {
        if (elements.isEmpty()) { return; }
        consumer.accept(elements);
        elements = new ArrayList<T>(batchSize);
    }
}
