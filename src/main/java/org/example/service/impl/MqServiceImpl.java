package org.example.service.impl;

import com.alibaba.fastjson.JSON;
import org.example.dto.OrderDto;
import org.example.mapper.CommodityMapper;
import org.example.mapper.OrderMapper;
import org.example.service.MqService;
import org.springframework.stereotype.Service;

@Service
public class MqServiceImpl implements MqService {

    private final CommodityMapper commodityMapper;
    private final OrderMapper orderMapper;

    public MqServiceImpl(CommodityMapper commodityMapper, OrderMapper orderMapper) {
        this.commodityMapper = commodityMapper;
        this.orderMapper = orderMapper;
    }

    public void receiveMessage(String message) {
        OrderDto orderDto = JSON.parseObject(message, OrderDto.class);
        commodityMapper.reduceStock(orderDto.getCommodityId());
        orderMapper.insert(orderDto.getCommodityId(), orderDto.getMoney(), orderDto.getPhone());
    }
}
