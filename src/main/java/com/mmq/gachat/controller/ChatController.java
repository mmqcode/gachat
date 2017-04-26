package com.mmq.gachat.controller;

import com.google.gson.Gson;
import com.mmq.gachat.service.UserService;
import com.mmq.gachat.tool.Constants;
import com.mmq.gachat.tool.IoTool;
import com.mmq.gachat.tool.JsonTool;
import com.mmq.gachat.tool.Validate;
import com.mmq.gachat.vo.ChatMessage;
import com.mmq.gachat.vo.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2017/3/16.
 */
@Controller
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate template;

    @Autowired
    private UserService userService;

    @Autowired
    private JsonTool jsonTool;

    @Autowired
    private Gson gson;

    @Autowired
    private IoTool ioTool;

    private static final Logger logger = LogManager.getLogger(UserController.class);


    @RequestMapping(value = "/searching", method = RequestMethod.POST)
    public void searchingforChat(String username, HttpServletRequest request, HttpServletResponse response){

        long currentTime = System.currentTimeMillis();
        long timeoutTime = 4000l;

        SetOperations<String, String> userSet = template.opsForSet();

        //先将用户放入等待匹配的set中
        userSet.add(Constants.WAITINGUSER, username);
        logger.info("用户["+username+"]添加到等待匹配的set中。");


        ValueOperations<String, String> valueOperations = template.opsForValue();
        String targetName;
        //开始轮询匹配通知，一旦匹配成功则退出
        //如果匹配超过超时时间，也将退出。
        //退出后画面上将会继续迭代进入该方法
        boolean timeout = true;
        while(true){
            //匹配的消息模式
            //A:chatting:with -> B
            targetName = valueOperations.get(username+Constants.CHATTING_WITH);
            if(Validate.isString(targetName)){
                //找到匹配项
                timeout = false;
                break;
            }
            if(System.currentTimeMillis() - currentTime > timeoutTime){
                break;
            }
        }
        String resultJson;

        if(timeout){
            //制作响应消息
            Map<String,Object> resultMap = new HashMap<>();
            resultMap.put("code", "1");
            resultMap.put("message", "超时");
            resultJson = gson.toJson(resultMap);
        }else{
            //获取聊天对手的信息
            Map<String, Object> targetUser = this.userService.getUserInfoByName(targetName);

            //获得匹配成功后的hello消息并返回
            ListOperations<String, String> listOperations = template.opsForList();
            String receiveMessageKey = targetName+Constants.MESSAGE_DIRECTION+username;
            String chatMessageJson = listOperations.leftPop(receiveMessageKey);
            ChatMessage chatMessage = this.gson.fromJson(chatMessageJson, ChatMessage.class);

            //制作响应消息
            Map<String,Object> resultMap = new HashMap<>();
            resultMap.put("code", "0");
            resultMap.put("message", "匹配成功!");
            resultMap.put("chatMessage", chatMessage);
            resultMap.put("targetUser", targetUser);
            resultJson = gson.toJson(resultMap);
        }
        ioTool.writeMessageResponse(resultJson, response);
    }

    @RequestMapping(value = "/sendMessage", method = RequestMethod.GET)
    public void sendMessage(String message, String username, HttpServletResponse response){
        String responseJson;
        try{
            //确认聊天对象
            ValueOperations<String, String> valueOperations = template.opsForValue();
            String toUsername = valueOperations.get(username+Constants.CHATTING_WITH);
            if(!Validate.isString(toUsername)){

                throw new Exception("该用户无在聊对象");
            }
            //创建消息体,将消息至入聊天对象接收消息的list中。
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setSendTime(System.currentTimeMillis()+"");
            chatMessage.setTo(toUsername);
            chatMessage.setContent(message);
            chatMessage.setFrom(username);

            String chatMessageJson = gson.toJson(chatMessage);
            ListOperations<String,String> listOperations = template.opsForList();

            //username:to:tousername
            listOperations.rightPush(username+Constants.MESSAGE_DIRECTION+toUsername, chatMessageJson);
            responseJson = this.jsonTool.getSimpleMsgJson("操作成功!", "0");
        }catch (Exception e){
            e.printStackTrace();
            responseJson = this.jsonTool.getSimpleMsgJson(e.getMessage(), "1");
        }

        this.ioTool.writeMessageResponse(responseJson, response);
    }

    /**
     * 接收消息
     * @param username
     * @param response
     */
    @RequestMapping(value = "/receiveMessage", method = RequestMethod.POST)
    public void receiveMessage(String username, HttpServletResponse response){
        String responseJson;
        Map<String, Object> resultMap = new HashMap<>();
        ValueOperations<String, String> valueOperations = this.template.opsForValue();
        long startTime = System.currentTimeMillis();
        long timeoutTime = 6000L;//六秒
        try{
            String fromUsername = valueOperations.get(username+Constants.CHATTING_WITH);
            if(!Validate.isString(fromUsername)){
                //确认聊天对象是否结束了对话
                String toUsername = valueOperations.get(username+Constants.USER_CANCEL_CHATTING_WITH);
                if(Validate.isString(toUsername)){
                    //对方已经退出聊天
                    responseJson = this.jsonTool.getSimpleMsgJson("对方退出了聊天!", "2");
                    this.ioTool.writeMessageResponse(responseJson, response);
                    return;
                }
                throw new Exception("当前用户无在聊对象");
            }
            ListOperations<String,String> listOperations = this.template.opsForList();
            //在while循环中获取聊天信息
            while(true){
                //阻塞三秒钟获取
                String messageString = listOperations.leftPop(fromUsername+Constants.MESSAGE_DIRECTION+username, 2, TimeUnit.SECONDS);
                if(Validate.isString(messageString)){
                    ChatMessage chatMessage = gson.fromJson(messageString, ChatMessage.class);
                    resultMap.put("chatMessage", chatMessage);
                    resultMap.put("code","0");
                    resultMap.put("msg", "接收成功!");

                    break;
                }
                if(System.currentTimeMillis() - startTime > timeoutTime){
                    resultMap.put("code","1");
                    resultMap.put("msg", "超时!");
                    break;
                }
            }
            responseJson = gson.toJson(resultMap);

        }catch (Exception e){

            e.printStackTrace();
            responseJson = this.jsonTool.getSimpleMsgJson(e.getMessage(), "3");
        }
        this.ioTool.writeMessageResponse(responseJson, response);
    }

    /**
     * 用户取消聊天，可能是退出聊天界面也可能是点击了结束聊天按钮
     * @param username
     * @param response
     */
    @RequestMapping(value = "finishChatting", method = RequestMethod.POST)
    public void userFinishChatting(String username, HttpServletResponse response){

        String responseJson;

        ValueOperations<String, String> valueOperations = this.template.opsForValue();
        //聊天对象key
        String targetNameValueName = username+Constants.CHATTING_WITH;
        //聊天对象名称
        String targetName = valueOperations.get(targetNameValueName);
        //接收消息的list名称
        String receiveMessageListName = targetName+Constants.MESSAGE_DIRECTION+username;
        //发送消息的list名称
        String sendMessageListName = username+Constants.MESSAGE_DIRECTION+targetName;
        //聊天对方查询当前用户name的key
        String selfNameValueName = targetName+Constants.CHATTING_WITH;
        //hset chatting:couple中的key
        String chattingCoupleKey1 = username+":"+targetName;
        String chattingCoupleKey2 = targetName+":"+username;
        //将待删除的key放入一个set中
        Set<String> keysToDelete = new HashSet<>();
        keysToDelete.add(targetNameValueName);
        keysToDelete.add(receiveMessageListName);
        keysToDelete.add(sendMessageListName);
        keysToDelete.add(selfNameValueName);
        try{
            template.execute(new SessionCallback<Object>(){
                @Override
                public Object execute(RedisOperations operations) throws DataAccessException {
                    operations.multi();
                    operations.delete(keysToDelete);
                    HashOperations<String,String, List<String>> hashOperations = operations.opsForHash();
                    hashOperations.delete(Constants.CHATTING_COUPLE, chattingCoupleKey1, chattingCoupleKey2);
                    valueOperations.set(targetName+Constants.USER_CANCEL_CHATTING_WITH, username);
                    operations.expire(targetName+Constants.USER_CANCEL_CHATTING_WITH, 1, TimeUnit.MINUTES);
                    template.exec();
                    return null;
                }
            });
            responseJson  = this.jsonTool.getSimpleMsgJson("操作成功!", "0");
        }catch (Exception e){
            e.printStackTrace();
            responseJson = this.jsonTool.getSimpleMsgJson(e.getMessage(), "1");
        }
        this.ioTool.writeMessageResponse(responseJson, response);

    }


}
