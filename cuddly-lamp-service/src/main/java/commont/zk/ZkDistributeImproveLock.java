package commont.zk;

import com.alibaba.dubbo.common.utils.StringUtils;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class ZkDistributeImproveLock implements Lock {

    /*
     * 利用临时顺序节点来实现分布式锁
     * 获取锁：取排队号（创建自己的临时顺序节点），然后判断自己是否是最小号，如是，则获得锁；不是，则注册前一节点的watcher,阻塞等待
     * 释放锁：删除自己创建的临时顺序节点
     */

    //  上锁的路径
    private String lockPath;

    //  zk客户端
    private ZkClient zkClient;

    private ThreadLocal<String> currentPath = new ThreadLocal<>();

    private ThreadLocal<String> beforePath = new ThreadLocal<>();

    private ThreadLocal<Integer> reenterCount = ThreadLocal.withInitial(() -> 0);


    public ZkDistributeImproveLock(String lockPath){
        if(StringUtils.isBlank(lockPath)){
            throw new IllegalArgumentException("zk锁路径不能为空");
        }

        this.lockPath = lockPath;

        zkClient = new ZkClient("172.16.150.195:2181");

        zkClient.setZkSerializer(new MyZkSerializer());

        if(!this.zkClient.exists(lockPath)){

            try{
                zkClient.createPersistent(lockPath, true);
            }catch (ZkNodeExistsException e){
                e.printStackTrace();
            }
        }
    }


    @Override
    public void lock() {
        if(!tryLock()){

            //  阻塞等待
            waitForLock();
            //  再次尝试加锁
            lock();
        }
    }

    @Override
    public boolean tryLock() {
        System.out.println(Thread.currentThread().getName() + " -------> 尝试获取分布式锁");

        //  尝试创建 临时顺序节点
        if(this.currentPath.get() == null || zkClient.exists(this.currentPath.get())){
            String node = zkClient.createEphemeralSequential(lockPath+"/", "locked");
            currentPath.set(node);
            reenterCount.set(0);
        }

        //  获取所有的号
        List<String> childrenList = zkClient.getChildren(lockPath);

        Collections.sort(childrenList);

        if(currentPath.get().equals(lockPath + "/" + childrenList.get(0))){

            reenterCount.set(reenterCount.get() + 1);
            System.out.println(Thread.currentThread().getName() + " -------> 成功获得分布式锁");
            return true;
        }
        else {
           int curIndex = childrenList.indexOf(currentPath.get().substring(lockPath.length() + 1));
           String node = lockPath + "/" + childrenList.get(curIndex - 1);
           beforePath.set(node);
        }

        System.out.println(Thread.currentThread().getName() + ">>>>>>>>>>>> 加锁失败 >>>>>>>>>>>");
        return false;
    }

    private void waitForLock(){
        final CountDownLatch cdl = new CountDownLatch(1);

        IZkDataListener listener = new IZkDataListener() {
            @Override
            public void handleDataChange(String s, Object o) throws Exception {

            }

            @Override
            public void handleDataDeleted(String s) throws Exception {
                System.out.println(Thread.currentThread().getName() + " ------> 监听到节点被删除，分布式锁被释放");
                cdl.countDown();
            }
        };

        //  注册 watcher
        zkClient.subscribeDataChanges(beforePath.get(), listener);

        if(zkClient.exists(beforePath.get())){
            try {
                System.out.println(Thread.currentThread().getName() + " ---- > 分布式锁没抢到，进入阻塞状态");
                cdl.await();
                System.out.println(Thread.currentThread().getName() + "------ > 释放分布式锁，被唤醒");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        zkClient.unsubscribeDataChanges(beforePath.get(), listener);
    }

    @Override
    public void unlock() {
        System.out.println(Thread.currentThread().getName() + " ------ > 释放分布式锁");

        if(reenterCount.get() > 1){
            reenterCount.set(reenterCount.get() - 1);
            return;
        }

        if(currentPath.get() != null){

            zkClient.delete(currentPath.get());

            currentPath.set(null);

            reenterCount.set(0);
        }
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
