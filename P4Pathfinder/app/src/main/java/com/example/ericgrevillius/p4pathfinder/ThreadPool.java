package com.example.ericgrevillius.p4pathfinder;

import java.util.LinkedList;

public class ThreadPool {
    private Thread [] threads;
    private Buffer<Runnable> buffer = new Buffer<>();
    private boolean running = false;
    private int n;

    public ThreadPool(int nThreads){
        n = nThreads;
    }

    public void start(){
        if (threads == null){
            threads = new Thread[n];
            for (int i = 0; i < n; i++){
                threads[i] = new Thread() {
                    public void run() {
                        Runnable task;
                        while(!Thread.interrupted()) {
                            try {
                                task = buffer.get();
                                task.run();
                            } catch (InterruptedException e) {
                                try {
                                    join();
                                } catch (InterruptedException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                    }
                };
                threads[i].start();
            }
        }
    }

    public void stop(){
        if(threads != null){
            for (int i = 0; i < n; i++){
                execute(new StopThread());
            }
            threads = null;
        }
    }

    public synchronized void execute(Runnable task){
        buffer.put(task);
    }

    private class Buffer<T> {
        private LinkedList<T> buffer = new LinkedList<T>();

        /**
         * Adds given object of the given type to the the buffer.
         *
         * @param obj
         *            Object of the given type.
         */
        public synchronized void put(T obj) {
            buffer.addLast(obj);
            notifyAll();
        }

        /**
         * Returns an object of the given type.
         *
         * @return Object of given type.
         * @throws InterruptedException
         */
        public synchronized T get() throws InterruptedException {
            while (buffer.isEmpty()) {
                wait();
            }
            return buffer.removeFirst();
        }
    }

    private class StopThread implements Runnable {
        public void run() {
            Thread.currentThread().interrupt();
        }

        public String toString() {
            return "Closing down " + Thread.currentThread();
        }
    }
}
