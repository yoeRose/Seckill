package com.yoe.Interceptor;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.AMQP;
import com.yoe.access.AccessLimit;
import com.yoe.context.UserContext;
import com.yoe.pojo.User;
import com.yoe.redis.AccessKey;
import com.yoe.service.RedisService;
import com.yoe.service.UserService;
import com.yoe.utils.CodeMsg;
import com.yoe.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

@Service
public class AccessInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod){

            User user = getUser(request, response);
            UserContext.setUser(user);

            HandlerMethod handlerMethod = (HandlerMethod)handler;

            //获取注解
            AccessLimit accessLimit = handlerMethod.getMethodAnnotation(AccessLimit.class);
            if(accessLimit == null){
                return true;
            }
            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();

            String key = request.getRequestURI();
            if(needLogin){//需要登录
                if(user == null){
                    render(response,CodeMsg.SESSION_ERROR);
                    return false;
                }

                key += "_"+user.getId();
            }else{

            }

            AccessKey accessKey = AccessKey.withExpire(seconds);
            Integer accessCount = redisService.get(accessKey, "" + key, Integer.class);
            if(accessCount == null){
                redisService.set(accessKey,key,1);
            }else if(accessCount < maxCount){
                redisService.incr(accessKey,key);
            }else{
                render(response,CodeMsg.ACCESS_LIMIT_REACHED);
                return false;
            }

        }
        return true;
    }


    private User getUser(HttpServletRequest request, HttpServletResponse response){
        //参数cookie
        String paramToken = request.getParameter(UserService.COOKIE_NAME_TOKEN);

        //请求体中的cookie
        String cookieToken = getCookieValue(request,UserService.COOKIE_NAME_TOKEN);

        if(StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)){
            return null;
        }
        String token = StringUtils.isEmpty(paramToken)? cookieToken : paramToken;
        User user = userService.getByToken(response,token);
        return user;
    }

    private String getCookieValue(HttpServletRequest request, String cookieNameToken) {
        Cookie[] cookies = request.getCookies();
        if(cookies == null || cookies.length <= 0){
            return null;
        }
        for(Cookie cookie : cookies){
            if(cookie.getName().equals(cookieNameToken)){
                return cookie.getValue();
            }
        }
        return null;
    }

    private void render(HttpServletResponse response, CodeMsg cm)throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        OutputStream out = response.getOutputStream();
        String str  = JSON.toJSONString(Result.error(cm));
        out.write(str.getBytes("UTF-8"));
        out.flush();
        out.close();
    }

}
