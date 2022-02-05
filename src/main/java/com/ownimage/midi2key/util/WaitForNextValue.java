package com.ownimage.midi2key.util;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class WaitForNextValue<T> {

    private T lastValue;
    private ReentrantLock lock = new ReentrantLock();
    private Condition newValue = lock.newCondition();

    public void value(T value) {
        try {
            lock.lock();
            lastValue = value;
            newValue.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public T nextValue() {
        boolean failed = false;
        do {
            try {
                lock.lock();
                newValue.await();
                return lastValue;
            } catch (InterruptedException e) {
                e.printStackTrace();
                failed = true;
            } finally {
                lock.unlock();
            }
        } while (failed);
        return lastValue;
    }
}


