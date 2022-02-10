package com.ownimage.midi2key.util;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CyclicBarrier;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WaitForNextValueTest {

    WaitForNextValue<Integer> underTest;

    @BeforeEach
    public void setUp() {
        underTest = new WaitForNextValue();
    }

    @Test
    public synchronized void testSetAndGet() throws InterruptedException {
        // given
        Integer[] actual = new Integer[1];
        var newValue = Integer.valueOf(4);
        var barrier = new CyclicBarrier(2);
        // when
        var thread = new Thread(() -> {
            await(barrier);
            actual[0] = underTest.nextValue();
        });
        thread.start();
        await(barrier);
        underTest.value(newValue);
        thread.join();
        // then
        assertEquals(newValue, actual[0]);
    }

    @SneakyThrows
    private void await(CyclicBarrier barrier) {
        barrier.await();
    }

}