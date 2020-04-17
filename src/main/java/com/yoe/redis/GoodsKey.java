package com.yoe.redis;

public class GoodsKey extends BasePrefix {

    private static final int GOODS_LIST_EXPIRE = 60;//商品列表缓存过期时间（单位：秒）

    private static final int GOODS_DETAIL_EXPIRE = 60;//商品细节过期时间（单位：秒）

    public GoodsKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public GoodsKey(String prefix) {
        super(prefix);
    }

    public static GoodsKey getGoodsList= new GoodsKey(GOODS_LIST_EXPIRE,"goodsList");

    public static GoodsKey getGoodsDetail = new GoodsKey(GOODS_DETAIL_EXPIRE,"goodsDetail");

    public static GoodsKey getMiaoshaGoodsStock = new GoodsKey("goodsStock");
}
