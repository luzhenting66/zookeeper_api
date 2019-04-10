package cn.pipilu.server;

import cn.pipilu.MicroserviceProductApplication;
import cn.pipilu.utils.CuratorUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

/**
 * 商品服务注册实现
 */
@Component
public class ProductRegisteServiceImpl implements ProductRegisteService {

    private Logger logger = LoggerFactory.getLogger(MicroserviceProductApplication.class);

    @Value("${micro.service}")
    private String MICRO_SERVICE;
    @Value("${products.service}")
    private String PRODUCTS_SERVICE;
    @Value("${server.port}")
    private int port;


    @Override
    public void registeService(String ip, int port) {
        String servicePath = MICRO_SERVICE +PRODUCTS_SERVICE;

        try {
            CuratorFramework curatorFramework = CuratorUtils.getInstance();
            //检查节点是否存在
            Stat stat = curatorFramework.checkExists().forPath(servicePath);
            if (Objects.isNull(stat)){
                String createResult = curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT)
                        .forPath(servicePath, "".getBytes());
                logger.debug("---------------createResult = "+createResult);
            }
            //注册节点,临时有序节点
            String ipAddress = ip +":"+port;
            curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                    .forPath(servicePath+"/"+ip,ipAddress.getBytes());

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @PostConstruct
    public void registeProductService(){
        String ip ;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();

            logger.debug("ip ={},port={}",ip,port);
            registeService(ip,port);
            logger.debug("商品注册服务成功：{}",ip +":"+port);
        } catch (UnknownHostException e) {
            logger.debug("注册失败",e);
        }
    }
}
