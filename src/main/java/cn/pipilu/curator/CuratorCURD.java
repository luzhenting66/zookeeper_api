package cn.pipilu.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.transaction.CuratorTransactionResult;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Curator操作CURD
 */
public class CuratorCURD {

    public static void main(String[] args) throws Exception {
        CuratorFramework instance = CuratorUtils.getInstance();

        /**
         * 创建节点
         */
        String result = instance.create()
                                .creatingParentsIfNeeded()
                                .withMode(CreateMode.PERSISTENT)
                                .forPath("/curator2/curator2/curator2_1", "123".getBytes());//只会在最后一级中，set value
        System.err.println("result="+result);

        /**
         * 删除节点
         */
        instance.delete()
                .deletingChildrenIfNeeded()
                .forPath("/curator2");

        /**
         * 查询
         */
        Stat stat = new Stat();
        byte[] bytes = instance.getData()
                               .storingStatIn(stat)
                               .forPath("/curator2/curator2/curator2_1");
        System.err.println("("+new String(bytes)+"),,,,stat="+stat);

        /**
         * 更新
         */
        Stat stat1 = instance.setData()
                             .forPath("/curator2", "中国".getBytes("UTF-8"));
        System.err.println("stat1 ="+stat1);

        /**
         * 异步操作
         */
        ExecutorService pool = Executors.newFixedThreadPool(1);
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            instance.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL)
                    .inBackground(new BackgroundCallback() {//inBackground 在后台运行
                             public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                                System.err.println(Thread.currentThread().getName()+" ::resultCode : "+ curatorEvent.getResultCode() +
                                " ::type : "+curatorEvent.getType() +"  :::: "+curatorEvent.getName());
                                countDownLatch.countDown();
                                }
                    },pool)
                    .forPath("/asy_curator/curator4","curator4".getBytes());
        }catch (Exception e){
            e.printStackTrace();
        }

        countDownLatch.await();
        TimeUnit.SECONDS.sleep(2);
        pool.shutdown();


        /**
         * 事务操作 curator独有的
         */
        try{
            Collection<CuratorTransactionResult> commit = instance.inTransaction()
                    .create()
                    .forPath("/curator_transa", "transa".getBytes())
                    .and()
                    .setData().forPath("/curator_transa", "222".getBytes())
                    .and()
                    .commit();

            for (CuratorTransactionResult r : commit) {
                System.err.println(r.getForPath() + " : "+r.getResultPath() + ": "+r.getType());
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
