package cn.pipilu.zk;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * zookeeper curd操作
 */
public class ZookeeperCURD {
    public static final String CONNECTIONADDRESS="192.168.73.128:2181";
    private static CountDownLatch countDownLatch = new CountDownLatch(1);
    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {

        /**
         * 1.第一个参数：连接地址
         * 2.第二个参数：连接超时时间
         * 3.监听事件，当状态是已连接时，countDownLatch减1
         * public ZooKeeper(String connectString, int sessionTimeout, Watcher watcher)
         */
        ZooKeeper zooKeeper = new ZooKeeper(CONNECTIONADDRESS, 5000, new Watcher() {
            public void process(WatchedEvent watchedEvent) {
                if (Event.KeeperState.SyncConnected.equals(watchedEvent.getState())){
                    countDownLatch.countDown();
                }
                if (Event.EventType.NodeDataChanged.equals(watchedEvent.getType())){
                    System.err.println("数据节点发生了变化"+watchedEvent.getPath()+",类型："+watchedEvent.getType());
                }
            }
        });

//        ZooKeeper zooKeeper = new ZooKeeper(CONNECTIONADDRESS,5000,(event)->{
//            if (Watcher.Event.KeeperState.SyncConnected.equals(event.getState())){
//                countDownLatch.countDown();
//            }
//        });

        //确保zookeeper连接通，再操作增删改查，所以此处先阻塞一会
        countDownLatch.await();
        System.err.println("zookeeper已经连接上。。。status = "+zooKeeper.getState());

        /**
         * 创建节点
         * String create(String path, byte[] data, List<ACL> acl, CreateMode createMode)
         *
         * 1.第一个参数：路径
         * 2.第二个参数：value
         * 3.第三个参数：权限
         * 4.第四个参数：节点类型，此处持久节点
         *
         */
        String result = zooKeeper.create("/lzt", "lzt".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        System.err.println("result = "+result);

        /**
         * 帧数
         public class Stat implements Record {
         private long czxid;
         private long mzxid;
         private long ctime;
         private long mtime;
         private int version;
         private int cversion;
         private int aversion;
         private long ephemeralOwner;
         private int dataLength;
         private int numChildren;
         private long pzxid;
         */
        Stat stat = new Stat();

        /**
         * 如果要监听节点事件，需要 设置watch：true
         */
        zooKeeper.getData("/lzt",true,stat);
        /**
         * 查询数据
         * public byte[] getData(String path, boolean watch, Stat stat)
         *
         * 1.路径
         * 2.是否监听
         * 3.帧数
         *
         */
        byte[] data = zooKeeper.getData("/lzt", true, stat);
        System.err.println(new String(data));
        System.err.println("stat ="+stat);

        /**
         * 修改数据
         * public Stat setData(String path, byte[] data, int version)
         *
         * 1.路径
         * 2.新value
         * 3.是否做版本控制，-1：不做版本控制
         *
         */
        Stat setData = zooKeeper.setData("/lzt", "pipilu".getBytes(), -1);
        System.err.println("setData = "+setData);

        /**
         * 删除节点
         * public void delete(String path, int version)
         *
         * 1.节点路径
         * 2.是否版本控制，-1：不做版本控制
         */
        zooKeeper.delete("/lzt/zhenting",-1);

        /**
         * 查看子节点
         * public List<String> getChildren(String path, boolean watch)
         */
        List<String> list = zooKeeper.getChildren("/lzt", true);
        System.err.println("list ="+list); //list =[a, b, c]
    }
}
