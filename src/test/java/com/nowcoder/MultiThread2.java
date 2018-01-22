package com.nowcoder;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiThread2 {
    static class Comsumer implements  Runnable {

        private BlockingQueue<Integer> queue ;

        Comsumer(BlockingQueue<Integer> queue)
        {
            this.queue = queue;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    //一直在等待从阻塞队列中取出
                    System.out.println(Thread.currentThread().getName()+":"+queue.take());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    static class Producer implements  Runnable {

        private  BlockingQueue<Integer> queue ;
        Producer(BlockingQueue queue)
        {
            this.queue = queue;
        }


        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                try {
                    Thread.sleep(1000);  //1s放一个进阻塞队列
                    queue.put(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void testBlockingQueue()
    {
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<Integer>(10);
        new Thread(new Producer(queue)).start();
        new Thread(new Comsumer(queue),"thread-A").start();
        new Thread(new Comsumer(queue),"thread-B").start();


    }

    public static AtomicInteger atomicInteger = new AtomicInteger(0);
    public static   int counter = 0;

    public static void testAtomic()
    {
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    for (int i1 = 0; i1 < 10; i1++) {
                        System.out.println(Thread.currentThread().getName()+":"+ atomicInteger.incrementAndGet());
                    }
                }
            }).start();
        }
    }

    public static void testWithoutAtomic()
    {
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    for (int i1 = 0; i1 < 10; i1++) {
                        counter++;
                        System.out.println(Thread.currentThread().getName()+":"+counter);
                    }
                }
            }).start();
        }
    }

    public static void testExecutor()
    {
        ExecutorService service= Executors.newSingleThreadExecutor();
        //ExecutorService service= Executors.newFixedThreadPool(3);
        service.submit(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    System.out.println("executor A"+i);
                }
            }
        });
        service.submit(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("executor B"+i);
                }
            }
        });

        service.shutdown();

        //轮询
        while (!service.isTerminated()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Wait for termination.");
        }

    }

    public static void main(String[] args) {
        //testBlockingQueue();
        //testAtomic();
        //testWithoutAtomic();
        testExecutor();
    }

}
