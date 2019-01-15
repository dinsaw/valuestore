package com.github.dinsaw.valuestore.collections;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Slf4j
@RunWith(JUnit4.class)
public class ArrayBatchPusherTest {

    private BatchPusher<String> pusher = new ArrayBatchPusher<>(3, (a) -> consume(a));

    @Before
    public void setUp() {
        pusher = new ArrayBatchPusher<>(3, (a) -> consume(a));
    }

    @Test
    public void addShouldIncreaseSizeOfBuffer() {
        pusher.add("e1");
        pusher.add("e2");

        assertEquals(2, pusher.size());
    }

    @Test
    public void addShouldFlushElementsToConsumerWhenSizeMatchesBatchSize() {
        pusher.add("e1");
        pusher.add("e2");
        pusher.add("e3");

        assertEquals(0, pusher.size());
    }

    private void consume(List<String> a) {
        log.debug("Consumed elements : {} ", a);
    }

    @Test
    public void flushShouldFlushAllElementsToConsumerIrrespectiveOfBatchSize() {
        pusher.add("e1");
        pusher.flush();

        assertEquals(0, pusher.size());
    }

    @Test
    public void getFlushCountShouldReturnTotalElementsFlushCount() {
        pusher.add("e1");
        pusher.add("e2");
        pusher.add("e3");
        pusher.add("e4");
        pusher.flush();

        assertTrue(4 == pusher.getFlushCount());

    }
}