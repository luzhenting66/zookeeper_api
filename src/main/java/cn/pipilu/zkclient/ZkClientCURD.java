package cn.pipilu.zkclient;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * ZkClient客户端操作CURD
 */
public class ZkClientCURD {

    public static final String CONNECTIONADDRESS="192.168.73.128:2181";

    public static void main(String[] args) throws InterruptedException {
        final ZkClient zkClient = new ZkClient(CONNECTIONADDRESS,5000);
        System.err.println(zkClient+"--------->success");
        /**
         * 创建节点，递归创建，
         */
        zkClient.createPersistent("/zkclient/zkclient_sub1/zkclient_sub1_1",true);
        List<String> children = zkClient.getChildren("/zkclient");
        System.err.println(children);

        /**
         * 级联删除
         */
        boolean result = zkClient.deleteRecursive("/zkclient");
        System.err.println(result);

        /**
         * 注册数据变化监听
         */
        zkClient.subscribeDataChanges("/zkclient", new IZkDataListener() {
            public void handleDataChange(String s, Object o) throws Exception {
                System.err.println("s="+s);
                System.err.println("o ="+o.toString());
            }
            public void handleDataDeleted(String s) throws Exception {

            }
        });

        zkClient.writeData("/zkclient","zkclientvalue");
        TimeUnit.SECONDS.sleep(2);

        /**
         * 注册子节点变化监听
         */
        zkClient.subscribeChildChanges("/zkclient", new IZkChildListener() {
            public void handleChildChange(String s, List<String> list) throws Exception {
                System.err.println("children change..s ="+s);
                System.err.println(list);
            }
        });

        zkClient.delete("/zkclient/zkclient_sub1");
        TimeUnit.SECONDS.sleep(2);
    }
}
