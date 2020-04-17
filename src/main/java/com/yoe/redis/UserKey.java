package com.yoe.redis;


public class UserKey extends BasePrefix {

    public static final int TOKEN_EXPIRE= 3600*24*2;

    public UserKey(String prefix) {
        super(prefix);
    }

    public UserKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    //token有效期为2天
    public static UserKey token = new UserKey(TOKEN_EXPIRE,"token");

    //对象缓存设置为永久有效，除非有改动
    public static UserKey getById = new UserKey(0,"id");

}
