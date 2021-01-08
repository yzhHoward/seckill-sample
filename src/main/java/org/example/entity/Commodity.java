package org.example.entity;

import java.math.BigDecimal;

public class Commodity {

    private final long id;
    private final String title; // 商品标题
    private final BigDecimal price; // 价格
    private final long stock; // 剩余库存数量

    public Commodity(long id, String title, BigDecimal price, long stock) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.stock = stock;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public long getStock() {
        return stock;
    }
}
