package com.mmq.gachat.tool;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * Created by Administrator on 2016/9/27.
 */
@Component
public class IoTool {

    /**
     *
     *
     * @param stream
     * @return
     */
    public String getStringFromInputStream(InputStream stream){
        BufferedInputStream bis = new BufferedInputStream(stream);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            byte[] data = new byte[8];
            int count = -1;
            while((count = bis.read(data,0,8)) != -1) {
                bos.write(data,0,count);
            }
            data = null;
            return new String(bos.toByteArray(),"UTF-8");
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }finally{
            if(null != bis){
                try{
                    bis.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            if(null != bos){
                try{
                    bos.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        }
    }

    public byte[] getByteArrayFromInputStream(InputStream stream){
        BufferedInputStream bis = new BufferedInputStream(stream);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try{
            byte[] data = new byte[8];
            int count = -1;
            while((count = bis.read(data,0,8)) != -1) {
                bos.write(data,0,count);
            }
            data = null;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(null != bis){
                try{
                    bis.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            if(null != bos){
                try{
                    bos.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return bos.toByteArray();
    }

    public void writeMessageResponse(String resultData, HttpServletResponse response){

        PrintWriter printWriter = null;
        try{
            response.setCharacterEncoding("utf-8");
            printWriter = response.getWriter();
            printWriter.write(resultData);
            printWriter.flush();
        }catch (Exception e){
            e.printStackTrace();

        }finally {
            if(null != printWriter){
                printWriter.close();
                printWriter = null;
            }

        }
    }

    public void writeJsonPMessageResponse(String resultData, HttpServletResponse response, String callBackString){
        PrintWriter printWriter = null;
        try{
            response.setContentType("text/javascript");
            printWriter = response.getWriter();
            printWriter.write(callBackString+"("+resultData+");");
            printWriter.flush();
        }catch (Exception e){
            e.printStackTrace();

        }finally {
            if(null != printWriter){
                printWriter.close();
                printWriter = null;
            }
        }
    }

    /**
     * 数组转对象
     * @param bytes
     * @return
     */
    public Object toObject (byte[] bytes) {
        Object obj = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream (bis);
            obj = ois.readObject();
            ois.close();
            bis.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return obj;
    }

    /**
     * 对象转数组
     * @param obj
     * @return
     */
    public byte[] toByteArray (Object obj) {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray();
            oos.close();
            bos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return bytes;
    }
}
