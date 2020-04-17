package com.yoe.redis;

public class SeckillKey extends BasePrefix{

    public SeckillKey(String prefix) {
        super(prefix);
    }

    public SeckillKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static SeckillKey isGoodsOver = new SeckillKey("");

    public static SeckillKey getSeckillPath = new SeckillKey(60,"seckillPath");

    public static SeckillKey getSeckillVerifyCode = new SeckillKey(300,"verifyCode");
}
