package com.yoe.redis;


public class OrderKey extends BasePrefix{

    public OrderKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public OrderKey(String prefix) {
        super(prefix);
    }

    public static OrderKey getSeckillOrderByUserIdGoodsId = new OrderKey("seckillUserIdGoodsId");
}
