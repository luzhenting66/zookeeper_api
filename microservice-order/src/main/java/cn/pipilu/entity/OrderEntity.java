package cn.pipilu.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity {
    private String id;
    private ProductEntity productEntity;
}
