package org.example.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

public class Order {

    private long id;  // 订单ID
    private BigDecimal money;  // 支付金额
    private long phone;  // 秒杀用户的手机号
    private long commodityId;  // 秒杀到的商品ID

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;  // 创建时间
}
