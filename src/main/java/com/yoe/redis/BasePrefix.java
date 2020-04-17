package com.yoe.redis;

public abstract class BasePrefix implements KeyPrefix{


    //key的过期时间
    private int expireSeconds;

    private String prefix;

    public BasePrefix() {
    }

    //key为0表示key永不过期
    public BasePrefix(String prefix){
        this(0,prefix);
    }

    public BasePrefix(int expireSeconds, String prefix) {
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }


    /**
     * 设置key的过期时间
     *  0：表示永不过期
     * @return
     */
    @Override
    public int expireSeconds() {
        return expireSeconds;
    }

    @Override
    public String getPrefix() {
        String className = getClass().getSimpleName();
        return className+":"+ prefix;
    }
}
