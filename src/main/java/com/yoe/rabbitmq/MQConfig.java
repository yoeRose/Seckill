package com.yoe.rabbitmq;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Queue;


@Configuration
public class MQConfig {

    public static final String QUEUENAME = "queue";


    public static final String SECKILL_QUEUE = "seckillQueuue";
    //队列负责存放请求

    //Direct交换机模式
    @Bean
    public Queue queue(){
        return new Queue(SECKILL_QUEUE,true);
    }

}
