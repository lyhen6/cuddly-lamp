package com.ly.lamp.commont.threadLocal;

import java.util.concurrent.CountDownLatch;

public class ThreadLocalUtil {



    public static class InnerClass{

        public void add(String str){
//            Counter counter = new Counter();
            StringBuilder sb = Counter.tl.get();
            Counter.tl.set(sb.append(str));
        }

        public void print(){
            System.out.printf("ThreadName :: %s , ThreadLocalHashCode :: %s , Instance HashCode :: %s , Value :: %s\n",
                    Thread.currentThread().getName(),
                    Counter.tl.hashCode(),
                    Counter.tl.get().hashCode(),
                    Counter.tl.get().toString());
        }

        public void set(String words){
            Counter.tl.set(new StringBuilder(words));
            System.out.printf("Set, ThreadName :: %s , ThreadLocalHashCode :: %s , Instance HashCode :: %s , Value :: %s\n",
                    Thread.currentThread().getName(),
                    Counter.tl.hashCode(),
                    Counter.tl.get().hashCode(),
                    Counter.tl.get().toString());
        }
    }


    public static class Counter{

        private static ThreadLocal<StringBuilder> tl = ThreadLocal.withInitial(() -> new StringBuilder("---------"));

//        private Counter(){
//            tl.set(new StringBuilder());
//        }

//        public static void set(){
//            tl.set(new StringBuilder());
//        }
//
//        public static void remove(){
//            tl.remove();
//        }
    }


    public static void main(String[] args) {
        int threadCount = 3;
        CountDownLatch cdl = new CountDownLatch(threadCount);
        InnerClass innerClass = new InnerClass();
        for (int i=1; i<=threadCount; i++){
            new Thread(
                    () -> {
                        for (int j=1; j<=4; j++){
                            innerClass.add(String.valueOf(j));
                            innerClass.print();
                        }
                        innerClass.set(" Hello word !");
                        cdl.countDown();
                    },
                    "thread - " + i
            ).start();
        }
        try {
            cdl.await();
            System.out.println("run finished ~~~~");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        System.out.println("<<<<<<" + Counter.tl.get().append(">>>>>>>"));
    }
}
