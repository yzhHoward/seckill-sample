package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.entity.Commodity;

import java.util.List;


@Mapper
public interface CommodityMapper {

    /**
     * 查询所有秒杀商品的记录信息
     */
    List<Commodity> findAll();

    /**
     * 根据主键查询当前秒杀商品的数据
     */
    Commodity findById(long commodityId);

    /**
     * 减库存
     */
    void reduceStock(long commodityId);
}
