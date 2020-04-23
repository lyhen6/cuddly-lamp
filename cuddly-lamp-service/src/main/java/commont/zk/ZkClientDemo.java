package commont.zk;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;

import java.util.List;

public class ZkClientDemo {

    public static void main(String[] args) {

        //  创建zk客户端 链接
        ZkClient zkClient = new ZkClient("172.16.150.195:2181");

        //  实现序列化
        zkClient.setZkSerializer(new MyZkSerializer());

        //  创建 子节点路径 并复制

        //持久节点（PERSISTENT）：节点创建后，就一直存在，直到有删除操作来主动清除这个节点

        //持久顺序节点（PERSISTENT_SEQUENTIAL）：保留持久节点的特性，额外的特性是，每个节点会为其第一层子节点维护一个顺序，
        //记录每个子节点创建的先后顺序，ZK会自动为给定节点名加上一个数字后缀（自增的），作为新的节点名。

        //临时节点（EPHEMERAL）：和持久节点不同的是，临时节点的生命周期和客户端会话绑定，当然也可以主动删除。

        //临时顺序节点（EPHEMERAL_SEQUENTIAL）：保留临时节点的特性，额外的特性如持久顺序节点的额外特性。

        zkClient.create("/zookeeper/lamp1", "cuddly-lamp-1", CreateMode.PERSISTENT);

        zkClient.subscribeChildChanges("/zookeeper/lamp1", new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                System.out.println(parentPath + "子节点发生变化" + currentChilds);
            }
        });


        //这里开始是创建一个watch，但是为什么这个方法会命名为subscribeDataChanges()呢，原因是:
        //原本watch的设置然后获取是仅一次性的，现在我们使用subscribe这个英文，代表订阅，代表这个watch一直存在
        //使用这个方法我们可以轻易实现持续监听的效果，比原生zookeeper方便

        zkClient.subscribeDataChanges("/zookeeper/lamp1", new IZkDataListener() {
            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {
                System.out.println(dataPath + "发生变化" + data);
            }

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                System.out.println(dataPath + "被删除");
            }
        });

        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
