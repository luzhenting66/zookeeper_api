package cn.pipilu.contorller;

import cn.pipilu.entity.ProductEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/products")
public class ProductController {

    @Value("${server.port}")
    private int port;

    @RequestMapping("/detail/{id}")
    public ProductEntity detail(@PathVariable String id){
        return new ProductEntity(id,"商品："+ port);
    }
}
