package com.yoe.controller;

import com.yoe.pojo.User;
import com.yoe.rabbitmq.MQReceiver;
import com.yoe.rabbitmq.MQSender;
import com.yoe.redis.UserKey;
import com.yoe.service.RedisService;
import com.yoe.service.UserService;
import com.yoe.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/demo")
public class SampleController {

    @Autowired
    private UserService userService;


    @Autowired
    private RedisService redisService;

    @Autowired
    private MQSender mqSender;

    @Autowired
    private MQReceiver mqReceiver;

    @RequestMapping("/thymeleaf")
    public String thymeleaf(Model model){
        model.addAttribute("name","yoe");
        return "hello";
    }

    @RequestMapping("/db/get")
    @ResponseBody
    public User dbGet(){
        User user = userService.getById(1L);
        return user;
    }


    @RequestMapping("/redis/get")
    @ResponseBody
    public User redisGet(){
        User v1 = redisService.get(UserKey.getById,"key2", User.class);
        return v1;
    }

//    @RequestMapping("/mq")
//    @ResponseBody
//    public Result<String> mq(){
//        mqSender.send("hello,yoe");
//        return Result.success("Hello yoe");
//    }


}
