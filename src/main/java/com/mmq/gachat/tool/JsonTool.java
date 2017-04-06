package com.mmq.gachat.tool;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/27.
 */
@Component
public class JsonTool {

    private Gson gson = new Gson();

    public Map<String,String> jsonToSimpleMap(String jsonData){
        try{
            Map<String,String> jsonMap = this.gson.fromJson(jsonData, new TypeToken<Map<String,String>>(){}.getType());
            return jsonMap;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }

    }

    public String mapToJsonString(Map<String,String>  map){
        try{
            String resultString = this.gson.toJson(map);
            return resultString;
        }catch(Exception e){
            e.printStackTrace();
            return  null;

        }
    }

    public String getSimpleMsgJson(String msg, String code){
        Map<String,String> map = new HashMap<String, String>();
        map.put("msg",msg);
        map.put("code",code);
        return gson.toJson(map);
    }

    public String getSuccessDataJson(Map<String,String> map,String successMsg){
        if(null == map){
            return null;
        }
        map.put("code","1");
        map.put("msg",successMsg);
        return gson.toJson(map);
    }

}
