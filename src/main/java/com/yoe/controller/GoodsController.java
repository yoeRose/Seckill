package com.yoe.controller;

import com.yoe.pojo.User;
import com.yoe.redis.GoodsKey;
import com.yoe.service.GoodsService;
import com.yoe.service.RedisService;
import com.yoe.utils.Result;
import com.yoe.vo.GoodsDetailVo;
import com.yoe.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    RedisService redisService;

    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;


    @Autowired
    ApplicationContext applicationContext;

    private static Logger log = LoggerFactory.getLogger(LoginController.class);

    /**
     * 进行页面缓存改造
     * @param model
     * @param user
     * @return
     */
    @RequestMapping(value = "/to_list",produces = "text/html")
    @ResponseBody
    public String toList(HttpServletRequest request, HttpServletResponse response, Model model, User user){

        model.addAttribute("user",user);

        List<GoodsVo> GoodsVos = goodsService.listGoodsVo();
        model.addAttribute("goodsList",GoodsVos);

        //从缓存中获取页面
        String html = redisService.get(GoodsKey.getGoodsList,"",String.class);
        if(!StringUtils.isEmpty(html)){//缓存不为空,直接返回页面
            return html;
        }


        //手动渲染
        SpringWebContext ctx = new SpringWebContext(request,response,
                request.getServletContext(),request.getLocale(), model.asMap(), applicationContext );
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list", ctx);

        //没有缓存就种缓存
        if(!StringUtils.isEmpty(html)) {
            redisService.set(GoodsKey.getGoodsList, "", html);
        }
        return html;
    }

    /**
     * 2020年4月7日20:55:37 进行页面缓存优化
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/to_detail2/{goodsId}",produces = "text/html")
    @ResponseBody
    public String toDetail2(HttpServletRequest request, HttpServletResponse response,Model model, User user, @PathVariable(value = "goodsId")Long goodsId){
        model.addAttribute("user",user);

        //从缓存中获取页面
        String html = redisService.get(GoodsKey.getGoodsDetail,""+goodsId,String.class);
        if(!StringUtils.isEmpty(html)){//缓存不为空,直接返回页面
            return html;
        }

        //手动渲染
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods",goods);
        //活动开始时间
        long startAt = goods.getStartDate().getTime();

        //活动结束时间
        long endAt = goods.getEndDate().getTime();

        //当前时间
        long now = System.currentTimeMillis();

        //活动状态
        int miaoshaStatus = 0;

        //距离秒杀开始时间
        int remainSeconds = 0;

        if(now < startAt){//秒杀未开始
            miaoshaStatus = 0;
            remainSeconds = (int)((startAt - now)/1000);
        }else if(now > endAt){//秒杀已经结束
            miaoshaStatus = 2;
            remainSeconds = -1;
        }else{//秒杀进行中
            miaoshaStatus = 1;
            remainSeconds = 0;
        }

        model.addAttribute("miaoshaStatus",miaoshaStatus);
        model.addAttribute("remainSeconds",remainSeconds);



        //手动渲染
        SpringWebContext ctx = new SpringWebContext(request,response,
                request.getServletContext(),request.getLocale(), model.asMap(), applicationContext );
        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", ctx);

        //没有缓存就种缓存
        if(!StringUtils.isEmpty(html)) {
            redisService.set(GoodsKey.getGoodsDetail, ""+goodsId, html);
        }
        return html;
    }

    /**
     * 2020年4月8日09:25:02 页面静态化,不用模板，直接传json格式
     * @param request
     * @param response
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/detail/{goodsId}")
    @ResponseBody
    public Result<GoodsDetailVo> toDetail(HttpServletRequest request, HttpServletResponse response, Model model, User user, @PathVariable(value = "goodsId")Long goodsId){

        //手动渲染
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);

        //活动开始时间
        long startAt = goods.getStartDate().getTime();

        //活动结束时间
        long endAt = goods.getEndDate().getTime();

        //当前时间
        long now = System.currentTimeMillis();

        //活动状态
        int miaoshaStatus = 0;

        //距离秒杀开始时间
        int remainSeconds = 0;

        if(now < startAt){//秒杀未开始
            miaoshaStatus = 0;
            remainSeconds = (int)((startAt - now)/1000);
        }else if(now > endAt){//秒杀已经结束
            miaoshaStatus = 2;
            remainSeconds = -1;
        }else{//秒杀进行中
            miaoshaStatus = 1;
            remainSeconds = 0;
        }

        GoodsDetailVo goodsDetailVo = new GoodsDetailVo();
        goodsDetailVo.setGoods(goods);
        goodsDetailVo.setMiaoshaStatus(miaoshaStatus);
        goodsDetailVo.setRemainSeconds(remainSeconds);
        goodsDetailVo.setUser(user);

        return Result.success(goodsDetailVo);
    }

}
