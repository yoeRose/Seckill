package com.yoe.pojo;

import java.util.Date;

/**
 * 秒杀活动的商品
 */
public class MiaoShaGoods {
    private Long id;

    //商品id
    private Long goodsId;

    //秒杀价格
    private Double miaoshaPrice;

    //该商品在秒杀活动中的库存
    private Integer stockCount;

    //秒杀开始时间
    private Date startDate;

    //秒杀关闭时间
    private Date endDate;

    public MiaoShaGoods() {
    }

    public MiaoShaGoods(Long id, Long goodsId, Double miaoshaPrice, Integer stockCount, Date startDate, Date endDate) {
        this.id = id;
        this.goodsId = goodsId;
        this.miaoshaPrice = miaoshaPrice;
        this.stockCount = stockCount;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getStockCount() {
        return stockCount;
    }

    public void setStockCount(Integer stockCount) {
        this.stockCount = stockCount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Double getMiaoshaPrice() {
        return miaoshaPrice;
    }

    public void setMiaoshaPrice(Double miaoshaPrice) {
        this.miaoshaPrice = miaoshaPrice;
    }
}
