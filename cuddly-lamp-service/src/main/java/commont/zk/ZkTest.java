package commont.zk;

import java.util.concurrent.CountDownLatch;

public class ZkTest implements Runnable {

    static int inventory = 1;

    private static final int num = 10;

    private static CountDownLatch cdl = new CountDownLatch(num);

//    private ZkDistributeImproveLock zkDistributeImproveLock = new ZkDistributeImproveLock("/zookeeper/zkLock");
    private ZkDistributeLock zkDistributeImproveLock = new ZkDistributeLock("/zookeeper/zkLock");

    @Override
    public void run() {
        try {
            zkDistributeImproveLock.lock();
//            cdl.await();
            if(inventory > 0){
                Thread.sleep(10);
                inventory--;
            }
            System.out.println(inventory);
            zkDistributeImproveLock.unlock();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    //  场景模拟 come let's go !!!
    public static void main(String[] args) {
        for(int i=1; i<=num; i++){
            new Thread(new ZkTest()).start();
//            cdl.countDown();
        }
    }
}
