package cn.pipilu.utils;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * Curator CuRD操作
 */
public class CuratorUtils {
    public static final String CONNECTIONADDRESS = "192.168.73.128:2181";

    private CuratorUtils(){}
    public static CuratorFramework getInstance() {

        CuratorFramework curatorFramework = CuratorFrameworkFactory
                .builder()
                .connectString(CONNECTIONADDRESS)
                .sessionTimeoutMs(5000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        curatorFramework.start();
        return curatorFramework;
    }
}