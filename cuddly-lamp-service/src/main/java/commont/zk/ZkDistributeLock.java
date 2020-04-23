package commont.zk;

import com.alibaba.dubbo.common.utils.StringUtils;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

//  简单的分布式zk锁 会造成惊群效应
public class ZkDistributeLock implements Lock {

    //  上锁的路径
    private String lockPath;

    //  zk客户端
    private ZkClient zkClient;


    public ZkDistributeLock(String lockPath){
        if(StringUtils.isBlank(lockPath)){
            throw new IllegalArgumentException("zk锁路径不能为空");
        }
        this.lockPath = lockPath;
        zkClient = new ZkClient("172.16.150.195:2181");
        zkClient.setZkSerializer(new MyZkSerializer());
    }


    @Override
    public void lock() {

        //  获取不到锁 阻塞等待
        if(!tryLock()){

            //  等待获取
            waitForLock();

            //  再次尝试
            lock();
        }
    }


    // tryLock方法 会尝试创建一个临时节点
    @Override
    public boolean tryLock() {

        try{
            zkClient.createEphemeral(lockPath);
        }catch (ZkNodeExistsException e){
            System.out.println(">>>>>>>>>>>> 加锁失败 >>>>>>>>>>>");
            return false;
        }
        System.out.println(">>>>>>>>>>>> 加锁成功 >>>>>>>>>>>");
        return true;
    }


    @Override
    public void unlock() {
        zkClient.delete(lockPath);
    }

    //  等待锁
    private void waitForLock(){
        //  令枪
        final CountDownLatch cdl = new CountDownLatch(1);

        //  监听
        IZkDataListener listener = new IZkDataListener() {

            @Override
            public void handleDataChange(String s, Object o) throws Exception {

            }

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                System.out.println(dataPath + " --->>> 监听到节点被删除");
                //  唤醒阻塞线程
                cdl.countDown();
            }
        };

        zkClient.subscribeDataChanges(lockPath, listener);

        //  阻塞自己
        if(zkClient.exists(lockPath)){
            try{
                cdl.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //  取消阻塞
        zkClient.unsubscribeDataChanges(lockPath, listener);
    }


    @Override
    public void lockInterruptibly() throws InterruptedException {

    }


    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
