package com.yoe.utils;

public enum OrderStatus {
    CREATE(0,"订单创建未支付"),
    PAY(1,"已支付"),
    DELIVERY(2,"已经发货"),
    RECEIVE(3,"已经收货"),
    REFOUND(4,"已退款"),
    FINISH(5,"已完成");

    private Integer code;
    private String msg;

    OrderStatus(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
