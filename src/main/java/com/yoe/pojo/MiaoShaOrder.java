package com.yoe.pojo;


/**
 * 秒杀订单
 * MiaoshaOrder 用于判断该用户是否已经进行过秒杀活动
 */
public class MiaoShaOrder {

    private Long Id;

    private Long userId;

    private Long orderId;

    private Long goodsId;

    public MiaoShaOrder() {
    }

    public MiaoShaOrder(Long id, Long userId, Long orderId, Long goodsId) {
        Id = id;
        this.userId = userId;
        this.orderId = orderId;
        this.goodsId = goodsId;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }
}
