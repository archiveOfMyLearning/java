package com.example;
//guaranteed deadlock, without using Thread.sleep()
public class Deadlock {
    public static void main(String[] args) {
        Object outerLock = new Object();
        Object lock1 = new Object();
        Object lock2 = new Object();
        synchronized (outerLock){
            Thread t1 = new Thread(()->{
                synchronized (lock1){
                    synchronized (outerLock){
                        synchronized (lock2){}
                    }
                }
            });
            t1.start();
            while (t1.getState()!= Thread.State.BLOCKED);
            Thread t2 = new Thread(()->{
                synchronized (lock2){
                    synchronized (lock1){}
                }
            });
            t2.start();
            while (t2.getState()!= Thread.State.BLOCKED);
        }
        System.out.println("Deadlock obtained");
    }
}
