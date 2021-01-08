package org.example.service.impl;

import org.example.dto.OrderDto;
import org.example.entity.Commodity;
import org.example.mapper.CommodityMapper;
import org.example.service.SecKillService;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
public class SecKillServiceImpl implements SecKillService {

    private final StringRedisTemplate stringRedisTemplate;
    private final CommodityMapper commodityMapper;
    private final DefaultRedisScript<Boolean> redisScript;

    public SecKillServiceImpl(StringRedisTemplate stringRedisTemplate, CommodityMapper commodityMapper) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.commodityMapper = commodityMapper;
        redisScript = new DefaultRedisScript<>();
        redisScript.setResultType(Boolean.class);
        String script = "if tonumber(redis.call('HGET', KEYS[1], 'stock')) <= 0 then return false; " +
                "else redis.call('HINCRBY', KEYS[1], 'stock', -1); return true; " +
                "end;";
        redisScript.setScriptText(script);
    }

    @Override
    public List<Commodity> findAll() {
        BoundSetOperations<String, String> boundSetOperations = stringRedisTemplate.boundSetOps("commodity");
        HashOperations<String, String, String> hashOperations = stringRedisTemplate.opsForHash();
        Set<String> commoditySet = boundSetOperations.members();
        List<Commodity> commodityList;
        if (commoditySet == null || commoditySet.isEmpty()) {
            // 说明缓存中没有商品列表数据
            // 查询数据库中商品数据，并将列表数据循环放入redis缓存中
            commodityList = commodityMapper.findAll();
            for (Commodity commodity : commodityList) {
                // 将商品数据依次放入redis缓存中，key:商品的ID值
                String id = String.valueOf(commodity.getId());
                boundSetOperations.add(id);
                hashOperations.putIfAbsent("commodity: " + id, "title", commodity.getTitle());
                hashOperations.putIfAbsent("commodity: " + id, "price", commodity.getPrice().toString());
                hashOperations.putIfAbsent("commodity: " + id, "stock", String.valueOf(commodity.getStock()));
            }
        } else {
            commodityList = new ArrayList<>();
            Commodity commodity;
            for (String id : commoditySet) {
                String title = hashOperations.get("commodity: " + id, "title");
                String price = hashOperations.get("commodity: " + id, "price");
                String stock = hashOperations.get("commodity: " + id, "stock");
                if (title == null || price == null || stock == null) {
                    commodity = commodityMapper.findById(Long.parseLong(id));
                    hashOperations.putIfAbsent("commodity: " + id, "title", commodity.getTitle());
                    hashOperations.putIfAbsent("commodity: " + id, "price", commodity.getPrice().toString());
                    hashOperations.putIfAbsent("commodity: " + id, "stock", String.valueOf(commodity.getStock()));
                } else {
                    commodity = new Commodity(Long.parseLong(id), title, new BigDecimal(price), Long.parseLong(stock));
                }
                commodityList.add(commodity);
            }
        }
        return commodityList;
    }

    @Override
    public Commodity findById(Long commodityId) {
        Commodity commodity;
        BoundHashOperations<String, String, String> boundHashOperations = stringRedisTemplate.boundHashOps("commodity: " + commodityId);
        String title = boundHashOperations.get("title");
        String price = boundHashOperations.get("price");
        String stock = boundHashOperations.get("stock");
        if (title == null || price == null || stock == null) {
            commodity = commodityMapper.findById(commodityId);
            boundHashOperations.putIfAbsent("title", commodity.getTitle());
            boundHashOperations.putIfAbsent("price", commodity.getPrice().toString());
            boundHashOperations.putIfAbsent("stock", String.valueOf(commodity.getStock()));
        } else {
            commodity = new Commodity(commodityId, title, new BigDecimal(price), Long.parseLong(stock));
        }
        return commodity;
    }

    @Override
    public boolean buy(long commodityId, BigDecimal money, String phone) {
        BoundHashOperations<String, String, String> boundHashOperations = stringRedisTemplate.boundHashOps("commodity: " + commodityId);
        String stockString = boundHashOperations.get("stock");
        long stock;
        if (stockString == null) {
            Commodity commodity = commodityMapper.findById(commodityId);
            stock = commodity.getStock();
            boundHashOperations.putIfAbsent("title", commodity.getTitle());
            boundHashOperations.putIfAbsent("price", commodity.getPrice().toString());
            boundHashOperations.putIfAbsent("stock", String.valueOf(stock));
        } else {
            stock = Long.parseLong(stockString);
        }
        // 提前做出判断
        if (stock <= 0) {
            return false;
        } else {
            Boolean result = stringRedisTemplate.execute(redisScript, Collections.singletonList("commodity: " + commodityId));
            if (result == null || !result) {
                return false;
            }
            // 秒杀成功
            stringRedisTemplate.convertAndSend("mq", "{" +
                    "commodityId:" + commodityId +
                    ", money:" + money +
                    ", phone:'" + phone + '\'' +
                    '}');
            return true;
        }
    }
}
