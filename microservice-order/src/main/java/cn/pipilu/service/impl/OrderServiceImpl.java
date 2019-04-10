package cn.pipilu.service.impl;

import cn.pipilu.service.OrderService;
import cn.pipilu.utils.CuratorUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    private static volatile List<String> productServiceList;
    private Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Value("${micro.service}")
    private String MICRO_SERVICE;
    @Value("${products.service}")
    private String PRODUCTS_SERVICE;

    @Override
    public List<String> getProductServices() {
        CuratorFramework curatorFramework = CuratorUtils.getInstance();
        PathChildrenCache cache=new PathChildrenCache(curatorFramework,MICRO_SERVICE+PRODUCTS_SERVICE,true);
        try {
            cache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
            cache.getListenable().addListener((curator,pathChildrenCacheEvent)->updateProductServiceList(curator));
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateProductServiceList(curatorFramework);
        return productServiceList;
    }

    @Override
    public String randomProductIpAndPort() {
        if (CollectionUtils.isEmpty(productServiceList))
            return "";
        int index = new Random().nextInt(productServiceList.size());
        return productServiceList.get(index);
    }


    @PostConstruct
    public void initProductServices(){
        logger.debug("------------拉取productService---开始---------");
        productServiceList = getProductServices();
        logger.debug("------------拉取productService---完成---------{}",productServiceList);
    }

    public void updateProductServiceList(CuratorFramework curatorFramework){
        try {
            List<String> pathList = curatorFramework.getChildren().forPath(MICRO_SERVICE + PRODUCTS_SERVICE);
            productServiceList = pathList.stream().map(s -> {
                byte[] bytes = null;
                try {
                    bytes = curatorFramework.getData().storingStatIn(new Stat()).forPath(MICRO_SERVICE + PRODUCTS_SERVICE + "/" + s);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (Objects.isNull(bytes))
                    return "";
                return new String(bytes);
            }).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

//     PathChildrenCacheListener plis = new PathChildrenCacheListener(){
//        @Override
//        public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
//            updateProductServiceList(curatorFramework);
//        }
//    };
}
