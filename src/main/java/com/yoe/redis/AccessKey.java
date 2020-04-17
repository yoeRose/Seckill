package com.yoe.redis;

public class AccessKey extends BasePrefix{

    public AccessKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    //public static AccessKey access = new AccessKey(5,"access");

    public static AccessKey withExpire(int seconds){
        return new AccessKey(seconds,"access");
    }
}
