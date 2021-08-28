package com.javarush.task.task27.task2707;

/* 
Определяем порядок захвата монитора
*/

import java.util.concurrent.atomic.AtomicBoolean;

public class Solution {
    public void someMethodWithSynchronizedBlocks(Object obj1, Object obj2) {
        synchronized (obj1) {
            synchronized (obj2) {
                System.out.println(obj1 + " " + obj2);
            }
        }
    }


    public static boolean isLockOrderNormal(final Solution solution, final Object o1, final Object o2) throws Exception {
        AtomicBoolean o2Captured = new AtomicBoolean(false);
        boolean result;
        synchronized (o1){
            Thread testedMethodThread = new Thread(()->solution.someMethodWithSynchronizedBlocks(o1,o2));
            testedMethodThread.start();
            while (testedMethodThread.getState()!= Thread.State.BLOCKED);
            Thread o2Locker = new Thread(()->{
                synchronized (o2){
                    o2Captured.set(true);
                    synchronized (o1){}
                }
            });
            o2Locker.start();
            while (o2Locker.getState()!= Thread.State.BLOCKED);
            result = o2Captured.get();
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        final Solution solution = new Solution();
        final Object o1 = new Object();
        final Object o2 = new Object();

        System.out.println(isLockOrderNormal(solution, o1, o2));
    }
}
