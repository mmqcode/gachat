package com.mmq.gachat.vo;

import java.io.Serializable;

/**聊天内容
 * Created by Administrator on 2017/3/17.
 */
public class ChatMessage implements Serializable {

    private static final long serialVersionUID = 1l;

    private String content;

    private String sendTime;

    private String from;

    private String to;

    private String type;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }
}
