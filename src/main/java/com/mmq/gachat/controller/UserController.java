package com.mmq.gachat.controller;

import com.google.common.util.concurrent.ExecutionError;
import com.google.gson.Gson;
import com.mmq.gachat.service.UserService;
import com.mmq.gachat.tool.*;
import com.mmq.gachat.vo.User;
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;

/**
 * Created by Administrator on 2017/3/15.
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JsonTool jsonTool;

    @Autowired
    private IoTool ioTool;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private Gson gson;

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate template;

    private static final Logger logger = LogManager.getLogger(UserController.class);

    @RequestMapping(value = "/reg", method = RequestMethod.GET)
    public void regUser(User user, HttpServletResponse response, HttpServletRequest request){
        String responseJson;
        String callBack = request.getParameter("jsoncallback");
        try{
            if(null == user.getUsername()){
                throw new Exception("无有效信息!");
            }
            int flg = this.userService.regUser(user);
            if(flg == 1){
                throw new Exception("用户名已经被使用啦!");
            }else{
                responseJson = this.jsonTool.getSimpleMsgJson("注册成功!", "0");
            }
        }catch (Exception e){
            e.printStackTrace();
            responseJson = this.jsonTool.getSimpleMsgJson(e.getMessage(), "1");
        }
        this.ioTool.writeJsonPMessageResponse(responseJson, response, callBack);
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public void userlogin(User user, HttpServletResponse response, HttpServletRequest request){
        String responseJson;
        String callBack = request.getParameter("jsoncallback");
        Map<String,String> resultMap;
        try{
            if(null == user.getUsername()){
                throw new Exception("无有效信息!");
            }
            int flg = this.userService.userlogin(user);
            if(flg == 1){
                throw new Exception("用户名密码错误!");
            }else{
                resultMap = new HashMap<>();
                resultMap.put("code", "0");
                resultMap.put("msg", "登录成功");
                HashOperations<String, String, Object> hasOperation = stringRedisTemplate.opsForHash();
                resultMap.put("token", (String)hasOperation.get(Constants.LOGIN_USER_KEY, user.getUsername()));
                responseJson = gson.toJson(resultMap);

            }
        }catch (Exception e){
            e.printStackTrace();
            responseJson = this.jsonTool.getSimpleMsgJson(e.getMessage(), "1");
        }
        this.ioTool.writeJsonPMessageResponse(responseJson, response, callBack);
    }

    /**
     * 获取用户信息
     * @param username
     * @param request
     * @param response
     */
    @RequestMapping(value = "/getUser", method = RequestMethod.POST)
    public void getUserInfoByUserName(String username, HttpServletRequest request, HttpServletResponse response){
        String responseJson;
        try{
            HashOperations<String, String, Object> hasOperation = stringRedisTemplate.opsForHash();
            Map<String, Object> map = hasOperation.entries(Constants.REG_USRE_KEY+username);
            if(!map.isEmpty()){
                map.put("code","0");
                map.put("msg","操作成功!");
                responseJson = this.gson.toJson(map);
                logger.info("获取用户信息:"+map.get("username"));
            }else{
                throw new Exception("没有该用户信息。");
            }
        }catch (Exception e){
            e.printStackTrace();
            responseJson = this.jsonTool.getSimpleMsgJson(e.getMessage(), "1");
        }
        this.ioTool.writeMessageResponse(responseJson, response);
    }

    /**
     * 更新用户信息
     * @param username
     * @param nickName
     * @param request
     * @param response
     */
    @RequestMapping(value = "/updateUser", method = RequestMethod.POST)
    public void saveUserInfo(String username,String nickName,String imgBase64 ,HttpServletRequest request, HttpServletResponse response){
        String responseJson;
        try{
            HashOperations<String, String, Object> hasOperation = stringRedisTemplate.opsForHash();
            hasOperation.put(Constants.REG_USRE_KEY+username,"nickName", nickName);
            if(Validate.isString(imgBase64)){
                hasOperation.put(Constants.REG_USRE_KEY+username, "headImage", imgBase64);
            }
            String headImgName = SystemUtils.createUUID()+".png";
            String fileStoreDirectoryString = System.getProperty("gachat") + File.separator+"staticResources"
                    +File.separator+"image"+File.separator+"headImg";
            //String imagePath = fileStoreDirectoryString+headImgName;
            SystemUtils.base64ToImage(imgBase64, headImgName, fileStoreDirectoryString);

            hasOperation.put(Constants.REG_USRE_KEY+username, "headImageName", headImgName);
            responseJson = this.jsonTool.getSimpleMsgJson("操作成功!", "0");
        }catch (Exception e){
            e.printStackTrace();
            responseJson = this.jsonTool.getSimpleMsgJson(e.getMessage(), "1");
        }
        this.ioTool.writeMessageResponse(responseJson, response);
    }

    @RequestMapping(value = "/exit", method = RequestMethod.POST)
    public void userExitApp(String username, HttpServletRequest request, HttpServletResponse response){
        String responseJson;
        try{
            HashOperations<String, String, Object> hasOperation = stringRedisTemplate.opsForHash();
            hasOperation.delete(Constants.LOGIN_USER_KEY, username);
            responseJson = this.jsonTool.getSimpleMsgJson("用户登录数据清除成功!", "0");
            logger.info("用户:["+username+"]退出了软件。");
        }catch (Exception e){
            e.printStackTrace();
            responseJson = this.jsonTool.getSimpleMsgJson(e.getMessage(), "1");
        }
        this.ioTool.writeMessageResponse(responseJson, response);
    }

    /**
     * 用户取消匹配
     * @param username
     * @param response
     */
    @RequestMapping(value = "/userCancelMatching", method = RequestMethod.POST)
    public void userCancelMatching(String username, HttpServletResponse response){

        String responseJson;
        try{
            SetOperations<String, String> setOperations = template.opsForSet();
            long result = setOperations.remove(Constants.WAITINGUSER, username);
            if(result == 1){
                responseJson = this.jsonTool.getSimpleMsgJson("操作成功!","0");
                logger.info("用户["+username+"]取消了匹配!");
            }else{
                ValueOperations<String,String> valueOperations = template.opsForValue();
                String chatWithName = valueOperations.get(username+Constants.CHATTING_WITH);
                if(Validate.isString(chatWithName)){
                    //已经匹配成功
                    responseJson = this.jsonTool.getSimpleMsgJson("已经匹配成功了,不能取消匹配", "1");
                }else{
                    responseJson = this.jsonTool.getSimpleMsgJson("您已经不再等待匹配的队伍里了","1");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            responseJson = this.jsonTool.getSimpleMsgJson("程序异常:"+e.getMessage(), "1");
        }
        this.ioTool.writeMessageResponse(responseJson, response);
    }

    /**
     * 获取当前已经登录的用户数量
     * @param response
     */
    @RequestMapping(value = "/getCurrentUsersNumber", method = RequestMethod.POST)
    public void getCurrentUsersNumber(HttpServletResponse response){
        String resultJson;
        Map<String, String> resultMap = new HashMap<>();
        HashOperations<String, String, Object> hasOperation = stringRedisTemplate.opsForHash();
        Long number = hasOperation.size(Constants.LOGIN_USER_KEY);
        resultMap.put("code", "0");
        resultMap.put("number", number+"");
        resultJson = this.jsonTool.mapToJsonString(resultMap);
        this.ioTool.writeMessageResponse(resultJson, response);
        //logger.info("获取了登录人数:"+number);
    }

    /**
     * 获取用户的匹配历史人员
     * @param response
     * @param username
     */
    @RequestMapping(value = "/getHistoryMatch", method = RequestMethod.POST)
    public void getHistoryMatch(HttpServletResponse response, String username){

        Map<String, Object> resultMap;
        List<Map<String,String>> matchsList;
        String resultJson;

        ZSetOperations<String, String> zSetOperations = template.opsForZSet();
        long currentTime = System.currentTimeMillis();
        long sevenDaysAgo = TimeTool.getDateByDifferFromCertainDate(new Date(), -7).getTime();
        Set<ZSetOperations.TypedTuple<String>> zsetData = zSetOperations.reverseRangeByScoreWithScores(username+Constants.USER_HISTORY_MATCH,
                sevenDaysAgo, currentTime);
        if(!zsetData.isEmpty()){
            resultMap = new HashMap<>();
            matchsList = new ArrayList<>();
            HashMap<String, String> map;
            for(ZSetOperations.TypedTuple<String> historyMatch:zsetData){
                map = new HashMap<>();
                map.put("value", historyMatch.getValue());
                Date matchDate = TimeTool.getCurrentTimeBySeconds(historyMatch.getScore().longValue());
                String matchDateString = TimeTool.getFormatTime(matchDate);
                map.put("time", matchDateString);
                matchsList.add(map);
            }
            resultMap.put("matches", matchsList);
            resultMap.put("code", "0");
            resultJson = gson.toJson(resultMap);
        }else{
            resultJson = this.jsonTool.getSimpleMsgJson("无数据", "1");
        }
        this.ioTool.writeMessageResponse(resultJson, response);
    }
}
