package cn.pipilu.controller;

import cn.pipilu.entity.OrderEntity;
import cn.pipilu.entity.ProductEntity;
import cn.pipilu.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private OrderService orderService;

    @RequestMapping("/detail/{id}")
    public OrderEntity detail(@PathVariable String id){
        ResponseEntity<ProductEntity> product = restTemplate.getForEntity("http://" + orderService.randomProductIpAndPort() + "/products/detail/" + id, ProductEntity.class);
        ProductEntity productBody = product.getBody();

        return new OrderEntity(id,productBody);
    }
}
