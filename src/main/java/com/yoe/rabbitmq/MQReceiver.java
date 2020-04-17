package com.yoe.rabbitmq;

import com.yoe.pojo.MiaoShaOrder;
import com.yoe.pojo.OrderInfo;
import com.yoe.pojo.User;
import com.yoe.service.GoodsService;
import com.yoe.service.MiaoshaService;
import com.yoe.service.OrderService;
import com.yoe.service.RedisService;
import com.yoe.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQReceiver {

    private static Logger log = LoggerFactory.getLogger(MQReceiver.class);

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;

//    @RabbitListener(queues = MQConfig.QUEUENAME)
//    public void receive(String msg){
//        log.info("receive message:"+msg);
//    }

    @RabbitListener(queues = MQConfig.SECKILL_QUEUE)
    public void receiveSeckillRequest(String msg){
        log.info("receive msg:" + msg);
        MiaoshaRequest request = redisService.StringToBean(msg, MiaoshaRequest.class);

        User user = request.getUser();
        long goodsId = request.getGoodsId();

        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);

        //判断库存
        Integer stock = goods.getStockCount();

        if(stock <= 0){//库存不足直接跳转到失败页面
            return;
        }

        //判断是否已经秒杀到了，防止重复秒杀
        MiaoShaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(),goodsId);
        if(order != null){//重复秒杀
            return;
        }
        System.out.println("判断是否会执行到miaosha方法");
        miaoshaService.miaosha(user,goods);
    }
}
