package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

@Mapper
public interface OrderMapper {

    /**
     * 插入购买订单明细
     *
     * @param commodityId 秒杀到的商品ID
     * @param phone 用户电话
     */
    void insert(@Param("commodityId") long commodityId, @Param("money") BigDecimal money, @Param("phone") String phone);
}
