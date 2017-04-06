package com.mmq.gachat.tool;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.UUID;

/**封装一些方法
 * Created by Administrator on 2016/9/27.
 */
@Component
public class SystemUtils {

    private static final Logger logger = LogManager.getLogger(SystemUtils.class);


    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param byteArray
     * @return
     */
    private static String byteToStr(byte[] byteArray) {
        String strDigest = "";
        for (int i = 0; i < byteArray.length; i++) {
            strDigest += byteToHexStr(byteArray[i]);
        }
        return strDigest;
    }

    /**
     * 将字节转换为十六进制字符串
     *
     * @param mByte
     * @return
     */
    private static String byteToHexStr(byte mByte) {
        char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        char[] tempArr = new char[2];
        tempArr[0] = Digit[(mByte >>> 4) & 0X0F];
        tempArr[1] = Digit[mByte & 0X0F];
        String s = new String(tempArr);
        return s;
    }

    public static String base64Encode(String bstr){
        byte[] obj = null;
        try{
           obj = bstr.getBytes("utf-8");
        }catch (Exception e){
            e.printStackTrace();
        }
        return new BASE64Encoder().encode(obj);
    }

    public static String base64Decode(String str) {
        byte[] bt = null;
        String result = null;
        try {
            BASE64Decoder decoder = new BASE64Decoder();
            bt = decoder.decodeBuffer(str);
            result = new String(bt,"utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String CommonsBase64Encode(String bstr){
        String resultstring = null;
        try{
            byte[] result = Base64.encodeBase64(bstr.getBytes("utf-8"));
            resultstring = new String(result,"utf-8");
        }catch (Exception e){
            e.printStackTrace();
        }
        return resultstring;
    }

    public static String CommonsBase64Decode(String bstr){
        String resultstring = null;
        try{
            byte[] result = Base64.decodeBase64(bstr.getBytes("utf-8"));
            resultstring = new String(result,"utf-8");
        }catch (Exception e){
            e.printStackTrace();
        }
        return resultstring;
    }

    public static String md5(String content){
        char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        try {
            byte[] btInput = content.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static String createUUID(){
        String uuid = UUID.randomUUID().toString(); //获取UUID并转化为String对象
        uuid = uuid.replace("-", "");//因为UUID本身为32位只是生成时多了“-”，所以将它们去点就可
        return uuid;
    }


    /**
     * 还原被编码成base64的图片
     * @param base64info
     *
     * @return
     */
    public static boolean base64ToImage(String base64info, String fileName, String directory){
        if(null == base64info || null == fileName || null == directory){
            return false;
        }
        try{
            base64info = base64info.substring(base64info.indexOf(",")+",".length());
            byte[] result = Base64.decodeBase64(base64info.getBytes("utf-8"));
            for(byte b:result){
                if(b < 0){
                    b += 256;
                }
            }
            File file = new File(directory);
            if(!file.exists()){
                file.mkdirs();
            }
            File imgFile = new File(directory+File.separator+fileName);
            if(!imgFile.exists()){
                imgFile.createNewFile();
            }
            OutputStream out = new FileOutputStream(imgFile);
            out.write(result);
            out.flush();
            out.close();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }



}
