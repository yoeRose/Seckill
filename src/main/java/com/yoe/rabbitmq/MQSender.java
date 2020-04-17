package com.yoe.rabbitmq;

import com.yoe.service.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class MQSender {

    @Autowired
    AmqpTemplate amqpTemplate;

    @Autowired
    RedisService redisService;

    private static Logger log = LoggerFactory.getLogger(MQSender.class);

//    public void send(Object message){
//        String msg = redisService.beanToString(message);
//        log.info("send message:"+msg);
//        amqpTemplate.convertAndSend(MQConfig.QUEUENAME,msg);
//    }

    /**
     * 发送秒杀请求
     * @param request
     */
    public void sendMiaoshaRequest(MiaoshaRequest request) {
        String msg =  redisService.beanToString(request);
        log.info("send message:"+msg);
        amqpTemplate.convertAndSend(MQConfig.SECKILL_QUEUE, msg);
    }
}
