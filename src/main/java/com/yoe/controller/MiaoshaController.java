package com.yoe.controller;

import com.sun.tools.javac.jvm.Code;
import com.yoe.access.AccessLimit;
import com.yoe.pojo.MiaoShaOrder;
import com.yoe.pojo.OrderInfo;
import com.yoe.pojo.User;
import com.yoe.rabbitmq.MQSender;
import com.yoe.rabbitmq.MiaoshaRequest;
import com.yoe.redis.AccessKey;
import com.yoe.redis.GoodsKey;
import com.yoe.redis.SeckillKey;
import com.yoe.service.GoodsService;
import com.yoe.service.MiaoshaService;
import com.yoe.service.OrderService;
import com.yoe.service.RedisService;
import com.yoe.utils.CodeMsg;
import com.yoe.utils.Result;
import com.yoe.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.List;


@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean {

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;


    @Autowired
    MiaoshaService miaoshaService;

    @Autowired
    MQSender mqSender;

    /**
     * 系统初始化:预装载秒杀商品库存到redis
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        if(goodsList == null){
            return;
        }
        for(GoodsVo goods : goodsList) {
            redisService.set(GoodsKey.getMiaoshaGoodsStock,""+goods.getId(),goods.getStockCount());
        }
    }


    /**
     * 秒杀功能
     * @param
     * @param user
     * @return
     */
    @PostMapping("/{path}/do_miaosha")
    @ResponseBody
    public Result<Integer> miaosha(User user, @RequestParam(value = "goodsId")long goodsId
            , @PathVariable("path")String path){
        if(user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        boolean res = miaoshaService.checkPath(path, user, goodsId);
        if(!res){
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }

        //预减库存
        long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock,""+goodsId);

        if (stock < 0){
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }

        //判断是否已经秒杀到了，防止重复秒杀
        MiaoShaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(),goodsId);
        if(order != null){//重复秒杀
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }

        //封装秒杀请求
        MiaoshaRequest miaoshaRequest = new MiaoshaRequest();
        miaoshaRequest.setGoodsId(goodsId);
        miaoshaRequest.setUser(user);

        //然后把秒杀请求发送到mq中
        mqSender.sendMiaoshaRequest(miaoshaRequest);

        //返回排队中的标识
        return Result.success(0);
    }

    /**
     * 获取秒杀结果
     * @param user
     * @param goodsId
     * @return
     */
    @GetMapping("/result")
    @ResponseBody
    public Result<Long> getSeckillResult(User user,long goodsId){
        if(user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        long result = miaoshaService.getSeckillResult(user.getId(), goodsId);
        //这里 result = -1
        System.out.println("result："+result);

        return Result.success(result);
    }


    /**
     * 秒杀接口隐藏,生成path
     * @param user
     * @param goodsId
     * @return
     */
    @AccessLimit(seconds=5,maxCount=5,needLogin=true)
    @GetMapping("/path")
    @ResponseBody
    public Result<String> getPath(HttpServletRequest request,User user, @RequestParam("goodsId") long goodsId, @RequestParam(value = "verifyCode",defaultValue = "0")int verifyCode){
        if(user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        //验证验证码
        boolean check = miaoshaService.checkVerifyCode(user,goodsId,verifyCode);

        System.out.println("check"+check);
        if(!check){
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }
        String path = miaoshaService.createPath(user, goodsId);
        return Result.success(path);
    }


    @GetMapping("/verifyCode")
    @ResponseBody
    public Result<String> getSeckillVerifyCode(HttpServletResponse response, User user,
                                               @RequestParam("goodsId")long goodsId){
        if(user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        try {
            BufferedImage image  = miaoshaService.createVerifyCode(user, goodsId);
            OutputStream out = response.getOutputStream();
            ImageIO.write(image, "JPEG", out);
            out.flush();
            out.close();
            return null;
        }catch(Exception e) {
            e.printStackTrace();
            return Result.error(CodeMsg.MIAOSHA_FAIL);
        }
    }

}
