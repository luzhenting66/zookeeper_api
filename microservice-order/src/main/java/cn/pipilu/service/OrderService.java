package cn.pipilu.service;

import java.util.List;

public interface OrderService {

    /**
     * 获取产品服务
     * @return
     */
   List<String> getProductServices();

   String randomProductIpAndPort();
}
