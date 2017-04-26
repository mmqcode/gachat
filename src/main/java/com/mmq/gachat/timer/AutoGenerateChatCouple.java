package com.mmq.gachat.timer;

import com.google.gson.Gson;
import com.mmq.gachat.controller.UserController;
import com.mmq.gachat.tool.Constants;
import com.mmq.gachat.vo.ChatMessage;
import com.mmq.gachat.vo.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.SystemClock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**匹配聊天对象
 * Created by Administrator on 2017/3/17.
 */
public class AutoGenerateChatCouple {



    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate redisTemplate;

    @Autowired
    private Gson gson;

    private static final Logger logger = LogManager.getLogger(AutoGenerateChatCouple.class);

    public void generateChattingCouple(){


        try{
            SetOperations<String, String> setOperations = this.redisTemplate.opsForSet();
            Set<String> couple = setOperations.distinctRandomMembers(Constants.WAITINGUSER, 2);
            if(couple.isEmpty() || couple.size() < 2){
                //等待的人数不足两个人
                return;
            }
            List<String> coupleList = new ArrayList<>();
            for(String username:couple){
                coupleList.add(username);
            }
            couple = null;
            logger.info("人员匹配成功：{}", coupleList);
            redisTemplate.execute(new SessionCallback() {
                @Override
                public Object execute(RedisOperations operations) throws DataAccessException {
                    operations.multi();
                    SetOperations<String, String> setOperations = operations.opsForSet();
                    //先从等待列表中移除被选中的两个人
                    setOperations.remove(Constants.WAITINGUSER, coupleList.get(0), coupleList.get(1));
                    //以及username2:to:username1
                    String key1 = coupleList.get(0)+Constants.MESSAGE_DIRECTION+coupleList.get(1);
                    String key2 = coupleList.get(1)+Constants.MESSAGE_DIRECTION+coupleList.get(0);
                    //为随机挑选出来的两个等待用户各自生成一个接受消息的(list)，并将该聊天组记录到hset中。
                    //并生成一个hello确认消息
                    ListOperations<String, String> listOperations = operations.opsForList();
                    ChatMessage chatMessage = getHelloMessage();
                    String chatMessageJson = gson.toJson(chatMessage);
                    listOperations.rightPush(key1, chatMessageJson);
                    listOperations.rightPush(key2, chatMessageJson);
                    HashOperations<String, String, List<String>> hashOperations = operations.opsForHash();
                    hashOperations.put(Constants.CHATTING_COUPLE, coupleList.get(0)+":"+coupleList.get(1), coupleList);

                    //为匹配成功的两个用户生成匹配通知
                    // A:chatting:with -> B
                    // B:chatting:with -> A
                    ValueOperations<String, String> valueOperations = operations.opsForValue();
                    valueOperations.set(coupleList.get(0)+Constants.CHATTING_WITH, coupleList.get(1));
                    valueOperations.set(coupleList.get(1)+Constants.CHATTING_WITH, coupleList.get(0));

                    //添加到各自的历史匹配人员中。
                    ZSetOperations<String, String> zSetOperations = operations.opsForZSet();
                    zSetOperations.add(coupleList.get(0)+Constants.USER_HISTORY_MATCH, coupleList.get(1),
                            System.currentTimeMillis());
                    zSetOperations.add(coupleList.get(1)+Constants.USER_HISTORY_MATCH, coupleList.get(0),
                            System.currentTimeMillis());


                    operations.exec();
                    return null;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            logger.error("匹配时出现异常:{}", e.getMessage());
        }
    }

    public ChatMessage getHelloMessage(){
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent("已经和*匹配成功啦，可以开始聊天了。");
        chatMessage.setFrom("system");
        chatMessage.setTo("users");
        chatMessage.setSendTime(System.currentTimeMillis()+"");

        return chatMessage;
    }


}
