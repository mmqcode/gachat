package com.mmq.gachat.service;

import com.mmq.gachat.tool.Constants;
import com.mmq.gachat.tool.TimeTool;
import com.mmq.gachat.vo.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.hash.Jackson2HashMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * Created by Administrator on 2017/3/15.
 */
@Service
public class UserService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final Logger logger = LogManager.getLogger(UserService.class);

    //新用户注册
    public int regUser(User user){
        HashOperations<String, String, Object> hasOperation = stringRedisTemplate.opsForHash();

        Map<String, Object> userExists = hasOperation.entries(Constants.REG_USRE_KEY+user.getUsername());
        if(!userExists.isEmpty()){
            //用户名已经存在
            return 1;
        }else{
            Jackson2HashMapper mapper = new Jackson2HashMapper(false);
            user.setRegTime(TimeTool.getCurrentTime()+"");
            Map<String, Object> mappHash = mapper.toHash(user);
            hasOperation.putAll(Constants.REG_USRE_KEY+user.getUsername(),mappHash);
            logger.info("用户:["+user.getUsername()+"]注册成功!");
            return 0;
        }
    }

    /**
     * 用户登录过程
     * @param user
     * @return
     */
    public int userlogin(User user){

        HashOperations<String, String, Object> hasOperation = stringRedisTemplate.opsForHash();
        Map<String, Object> userExists = hasOperation.entries(Constants.REG_USRE_KEY+user.getUsername());
        if(userExists.isEmpty()){
            return 1;
        }else{
            String password = (String)userExists.get("password");
            if(password.equals(user.getPassword())){

                //将用户信息放入当前登录用户hset中。
                String token = user.getUsername()+System.currentTimeMillis();
                hasOperation.put(Constants.LOGIN_USER_KEY, user.getUsername(), token);
                logger.info("用户:["+user.getUsername()+"]登录成功!");
                return 0;
            }else{
                return 1;
            }
        }
    }

    /**
     * 根据用户名称获取用户信息
     * @param username
     * @return
     */
    public Map<String, Object> getUserInfoByName(String username){

        HashOperations<String, String, Object> hasOperation = stringRedisTemplate.opsForHash();
        Map<String, Object> map = hasOperation.entries(Constants.REG_USRE_KEY+username);
        if(!map.isEmpty()){
            return map;
        }else{
            return null;
        }
    }


}
