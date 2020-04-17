package com.yoe.redis;

public interface KeyPrefix {
    public int expireSeconds();

    public String getPrefix();
}
