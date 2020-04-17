package com.yoe.service;

import com.yoe.dao.OrderDao;
import com.yoe.pojo.MiaoShaOrder;
import com.yoe.pojo.OrderInfo;
import com.yoe.pojo.User;
import com.yoe.redis.OrderKey;
import com.yoe.utils.OrderStatus;
import com.yoe.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class OrderService {


    @Autowired
    OrderDao orderDao;

    @Autowired
    RedisService redisService;


    public MiaoShaOrder getMiaoshaOrderByUserIdGoodsId(long userId, long goodsId) {
        return orderDao.getMiaoshaOrderByUserIdGoodsId(userId,goodsId);
    }

    @Transactional
    public OrderInfo createOrder(User user, GoodsVo goodsVo) {
        //1.写订单
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goodsVo.getId());
        orderInfo.setGoodsName(goodsVo.getGoodsName());
        orderInfo.setGoodsPrice(goodsVo.getMiaoshaPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(OrderStatus.CREATE.getCode());
        orderInfo.setUserId(user.getId());
        orderDao.createOrder(orderInfo);


        //这里为null
        System.out.println("普通订单Id"+orderInfo.getId());
        //写秒杀订单
        MiaoShaOrder miaoShaOrder = new MiaoShaOrder();
        miaoShaOrder.setGoodsId(goodsVo.getId());
        miaoShaOrder.setOrderId(orderInfo.getId());
        miaoShaOrder.setUserId(user.getId());
        orderDao.createMiaoshaOrder(miaoShaOrder);

        System.out.println("秒杀orderId:"+miaoShaOrder.getId());

        //秒杀订单写入缓存
        redisService.set(OrderKey.getSeckillOrderByUserIdGoodsId, ""+user.getId()+"_"+goodsVo.getId(),miaoShaOrder);
        return orderInfo;
    }

    public OrderInfo getOrderById(Long orderId) {
        return orderDao.getOrderById(orderId);
    }
}
