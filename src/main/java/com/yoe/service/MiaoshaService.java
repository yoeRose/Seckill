package com.yoe.service;

import com.yoe.pojo.MiaoShaOrder;
import com.yoe.pojo.OrderInfo;
import com.yoe.pojo.User;
import com.yoe.redis.SeckillKey;
import com.yoe.utils.MD5Util;
import com.yoe.utils.Result;
import com.yoe.utils.UUIDUtil;
import com.yoe.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.jws.soap.SOAPBinding;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * 秒杀业务
 */
@Service
public class MiaoshaService {

    @Autowired
    OrderService orderService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    RedisService redisService;

    private static char[] ops = new char[] {'+', '-', '*'};

    @Transactional
    public OrderInfo miaosha(User user, GoodsVo goodsVo) {
        /**
         * 1.减库存 -- goodService
         * 2.下订单 --orderService
         * 3.写入秒杀订单 --- orderService
         */
        //1.减库存
        boolean res = goodsService.reduceStock(goodsVo);
        System.out.println("减库存执行结果:"+res);

        if(res){//减库存成功
            return orderService.createOrder(user, goodsVo);
        }else{//减库存失败，表明已经失败
            setGoodsOver(goodsVo.getId());
            return null;
        }
    }


    /**
     * 获取秒杀结果
     * @param userId
     * @param goodsId
     * @return
     */
    public long getSeckillResult(long userId, long goodsId) {

        MiaoShaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(userId, goodsId);

        System.out.println("秒杀结果order："+order);

        if(order != null){//秒杀成功
            return order.getOrderId();
        }else{
            boolean isOver = getGoodsOver(goodsId);
            if(isOver){
                return -1;
            }else{
                return 0;
            }
        }


    }

    private void setGoodsOver(Long goodsId) {
        redisService.set(SeckillKey.isGoodsOver,""+goodsId,true);
    }

    /**
     * 判断是否没有库存
     * @param goodsId
     * @return
     */
    private boolean getGoodsOver(long goodsId) {
        return redisService.exists(SeckillKey.isGoodsOver,""+goodsId);
    }


    /**
     * 验证path
     * @param path
     * @param user
     * @param goodsId
     * @return
     */
    public boolean checkPath(String path,User user,long goodsId) {
        if(user == null || path == null){
            return false;
        }
        String str = redisService.get(SeckillKey.getSeckillPath, "" +user.getId()+"_"+goodsId, String.class);
        return path.equals(str);
    }

    /**
     * 创建接口隐藏路径
     * @param user
     * @param goodsId
     * @return
     */
    public String createPath(User user,long goodsId) {
        String str = MD5Util.md5(UUIDUtil.uuid()+"123456");
        redisService.set(SeckillKey.getSeckillPath,""+user.getId()+"_"+goodsId,str);
        return str;
    }

    /**
     * 生成验证码
     * @param user
     * @param goodsId
     * @return
     */
    public BufferedImage createVerifyCode(User user, long goodsId) {
        if(user == null || goodsId <=0) {
            return null;
        }
        int width = 80;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        //把验证码存到redis中
        int rnd = calc(verifyCode);
        redisService.set(SeckillKey.getSeckillVerifyCode, user.getId()+"_"+goodsId, rnd);
        //输出图片
        return image;
    }

    private int calc(String exp) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            return (Integer)engine.eval(exp);
        }catch(Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private String generateVerifyCode(Random rdm) {
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3 = rdm.nextInt(10);
        char op1 = ops[rdm.nextInt(3)];
        char op2 = ops[rdm.nextInt(3)];
        String exp = ""+ num1 + op1 + num2 + op2 + num3;
        return exp;
    }

    public boolean checkVerifyCode(User user, long goodsId, int verifyCode) {
        if(user == null || goodsId <=0){
            return false;
        }

        Integer codeOld = redisService.get(SeckillKey.getSeckillVerifyCode,""+user.getId()+"_"+goodsId,Integer.class);
        if(codeOld == null || codeOld - verifyCode != 0){
            return false;
        }
        redisService.delete(SeckillKey.getSeckillVerifyCode,""+user.getId()+goodsId);
        return true;
    }
}
