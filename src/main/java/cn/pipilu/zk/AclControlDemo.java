package cn.pipilu.zk;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Zookeeper ACL权限控制
 */
public class AclControlDemo implements Watcher{
    public static final String CONNECTIONADDRESS="192.168.73.128";
    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    public void process(WatchedEvent watchedEvent) {
        if (Event.KeeperState.SyncConnected.equals(watchedEvent.getState())){
            if (Event.EventType.None.equals(watchedEvent.getType()) && null == watchedEvent.getPath()){
                countDownLatch.countDown();
                System.err.println(watchedEvent.getState()+"------>"+watchedEvent.getType());
            }
        }
    }

    public static void main(String[] args) throws Exception {
        ZooKeeper zooKeeper = new ZooKeeper(CONNECTIONADDRESS,5000,new AclControlDemo());
        countDownLatch.await();
        System.err.println("zookeeper连接状态---------->："+zooKeeper.getState());

         //public ACL(int perms, Id id)
        //public Id(String scheme, String id)
        ACL acl = new ACL(ZooDefs.Perms.ALL,
                new Id("digest", DigestAuthenticationProvider.generateDigest("root:root")));
        ACL acl1 = new ACL(ZooDefs.Perms.CREATE,new Id("ip","192.168.73.128"));

        List<ACL> aclList = new ArrayList<ACL>();
        aclList.add(acl);
        aclList.add(acl1);

        zooKeeper.create("/auth1","aclLzt".getBytes(),aclList, CreateMode.PERSISTENT);

        zooKeeper.addAuthInfo("digest","root:root".getBytes());
        zooKeeper.create("/auth1/auth1-1","auth1-1".getBytes(),ZooDefs.Ids.CREATOR_ALL_ACL,CreateMode.PERSISTENT);

        //----------------------------------------------------------------------------------------------------------
        ZooKeeper zooKeeper2 = new ZooKeeper(CONNECTIONADDRESS,5000,new AclControlDemo());

        countDownLatch.await();
        zooKeeper2.addAuthInfo("digest","root:root".getBytes());
        zooKeeper2.delete("/auth1/auth1-1",-1);

        //String s = DigestAuthenticationProvider.generateDigest("super:admin");
       // System.err.println("s --->"+s);
    }
}
