package com.yoe.dao;

import com.yoe.pojo.MiaoShaOrder;
import com.yoe.pojo.OrderInfo;
import org.apache.ibatis.annotations.*;


/**
 * 负责对订单的操作，包括秒杀订单
 */
@Mapper
public interface OrderDao {

    /**
     * 查询秒杀订单
     * @param userId
     * @param goodsId
     * @return
     */
    @Select("select * from miaosha_order where user_id = #{userId} and goods_id = #{goodsId}")
    MiaoShaOrder getMiaoshaOrderByUserIdGoodsId(@Param("userId") long userId, @Param("goodsId")long goodsId);


    /**
     * 插入订单
     */
    @Insert("insert into order_info(user_id,goods_id,delivery_addr_id,goods_name,goods_count,goods_price,order_channel,status,created_date)"+
    "values(#{userId},#{goodsId},#{deliveryAddrId},#{goodsName},#{goodsCount},#{goodsPrice},#{orderChannel},#{status},#{createDate})")
    @SelectKey(keyColumn="id", keyProperty="id", resultType=long.class, before=false, statement="select last_insert_id()")
    long createOrder(OrderInfo orderInfo);

    @Insert("insert into miaosha_order (user_id, goods_id, order_id)values(#{userId}, #{goodsId}, #{orderId})")
    long createMiaoshaOrder(MiaoShaOrder miaoShaOrder);

    @Select("select * from order_info where id = #{orderId}")
    OrderInfo getOrderById(@Param("orderId") Long orderId);
}
