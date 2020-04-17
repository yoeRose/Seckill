package com.yoe.service;

import com.yoe.dao.UserDao;
import com.yoe.exception.GlobalException;
import com.yoe.pojo.User;
import com.yoe.redis.UserKey;
import com.yoe.utils.CodeMsg;
import com.yoe.utils.MD5Util;
import com.yoe.utils.UUIDUtil;
import com.yoe.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;


@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private RedisService redisService;


    public static final String COOKIE_NAME_TOKEN = "token";

    /**
     * 通过Id获取用户
     * @param id
     * @return
     */
    public User getById(long id){

        //取缓存
        User user = redisService.get(UserKey.getById, "" + id, User.class);
        if(user != null){
            return user;
        }

        //从数据库中读取
        user = userDao.getById(id);
        if(user != null){
            redisService.set(UserKey.getById,""+id,user);
        }
        return user;
    }


    /**
     * 登录业务代码
     * @param loginVo
     * @return 返回值一般是具有代表业务方法含义的值
     */
    public boolean login(HttpServletResponse response,LoginVo loginVo){
        if(loginVo == null){
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }

        String mobile = loginVo.getMobile();
        String formPwd = loginVo.getPassword();

        //校验用户是否存在
        User user = getById(Long.parseLong(mobile));
        if(user == null){

            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }

        //验证密码
        String dbPwd = user.getPassword();

        //得到数据库的盐值
        String dbSalt = user.getSalt();

        //这里是第二次md5，第一次md5已经由前端做好
        String calsPwd = MD5Util.formPassToDBPass(formPwd, dbSalt);

        //密码错误
        if(!calsPwd.equals(dbPwd)){
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }

        //登录成功，生成cookie
        String token = UUIDUtil.uuid();
        addCookie(response,token,user);
        return true;
    }

    //通过token获取用户
    public User getByToken(HttpServletResponse response,String token){
        if(StringUtils.isEmpty(token)){
            return null;
        }
        User user = redisService.get(UserKey.token, token, User.class);
        //重新种cookie，延长存活时间
        addCookie(response,token,user);
        return user;
    }

    /**
     * 生成cookie：
     *  1.随机生成token
     *
     *  2.一个token对应一个用户信息，存放在redis中，方便读取
     *
     *  3.种cookie
     */
    public void addCookie(HttpServletResponse response,String token,User user){

        redisService.set(UserKey.token,token,user);

        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN,token);
        cookie.setMaxAge(UserKey.token.expireSeconds());
        cookie.setPath("/");
        //种cookie给客户端
        response.addCookie(cookie);
    }

    /**
     * 更新密码
     * @param token
     * @param id
     * @param newPassword
     * @return
     */
    public boolean updatePassword(String token,long id,String newPassword){
        User user = getById(id);
        if(user == null){
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //先更新数据库，再更新缓存
        User toBeUpdate = new User();
        toBeUpdate.setId(id);
        toBeUpdate.setPassword(MD5Util.formPassToDBPass(newPassword,user.getSalt()));
        userDao.update(toBeUpdate);

        //一旦涉及到缓存对象的修改，就必须要更新缓存
        redisService.delete(UserKey.getById,""+id);
        user.setPassword(toBeUpdate.getPassword());
        redisService.set(UserKey.token,token,user);
        return true;
    }

}


