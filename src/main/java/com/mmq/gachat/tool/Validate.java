package com.mmq.gachat.tool;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 检验一些数据的有效性
 */
public class Validate {

    /**
     * 判断对象为空
     * @param o
     * @return
     */
    public static boolean isObject(Object o) {
        if(null!=o) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否有效字符串
     * @param s
     * @return
     */
    public static boolean isString(String s) {
        if(null!=s) {
            s = s.trim();
            if(s.length()>0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否有效字符串
     * @param s object
     * @return
     */
    public static boolean isString(Object s) {
        if(null!=s) {
            String str = s.toString().trim();
            if(str.length()>0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断字符串长度
     * @param s
     * @param minLen 判断用到的长度
     * @return
     */
    public static boolean isString(String s,int minLen) {
        if(null!=s) {
            s = s.trim();
            if(s.length()>=minLen) {
                return true;
            }
        }
        return false;
    }

    /***
     *
     * @param s
     * @param maxLen
     * @return
     */
    public static boolean isLessString(String s,int maxLen) {
        if(null!=s && s.length()<=maxLen) {
            s = s.trim();
            if(s.length()>=maxLen) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断数组是否为空
     * @param array
     * @return
     */
    public static boolean isArray(String[] array) {
        if(array!=null && array.length>0) {
            return true;
        }
        return false;
    }

    /**
     * 判断List是否不为空
     * @param list
     * @return
     */
    public static<T> boolean isList(List<T> list){
        if (list!=null && list.size()>0){
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断是字母或者数字
     * @param s
     * @return
     */
    public static boolean isLetterOrNum(String s) {
        boolean flag = false;
        if(isString(s)) {
            if(s.contains("_")) {
                return flag;
            } else {
                if(s.matches("\\w+")) {
                    flag = true;
                }
            }
        }
        return flag;
    }

    public static boolean isLetterOrNum(String s,int min,int max) {
        boolean flag = false;
        if(isString(s)) {
            if(s.contains("_")) {
                return flag;
            } else {
                if(s.matches("\\w{"+ min +"," + max + "}")) {
                    flag = true;
                }
            }
        }
        return flag;
    }


    /**
     * 判断是Map对象
     * @param <K>
     * @param <T>
     * @param map
     * @return
     */
    public static<K,T> boolean isMap(Map<K, T> map) {
        if(map!=null && map.size()>0) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否包含html代码，是返回true，没有返回false
     * @param s
     * @return
     */
    public static boolean isContainHtml(String s) {
        boolean result = false;
        String regex = "[<>]+|(</)+";	//待完善
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(s);
        if(m.find()) {
            return true;
        }
        return result;
    }

    /**
     * 过滤html代码
     * @param s	传入String
     * @return	返回过滤好的String
     */
    public static String filterHtmlCode(String s) {
        String result = "";
        String regex = "[<>]+|(</)+";
        result = s.replaceAll(regex, "");
        return result;
    }

    /**
     * 去掉一些无用的空格
     * @param s
     * @return
     */
    public static String removeSpace(String s) {
        String r = "";
        if(s!=null){
            r = s.trim();
        }
        return r;
    }

    /**
     *	判断是否是正确的上传格式
     * @param suffix
     * @return
     */
    public static boolean isRightPicSuffix(String suffix) {
        if(Validate.isString(suffix)) {
            String suffixs = ".jpg,.jpeg,.gif";
            if(suffixs.contains(suffix)) {
                return true;
            }
        }
        return false;
    }

    public static boolean validateUrl(String webSite) {
        if(null!=webSite && !"".equals(webSite)) {
            String reg = "[\\w?&%=:@#!/\\.\\-\\\\]+";
            Pattern pattern = Pattern.compile(reg);
            Matcher matcher = pattern.matcher (webSite);
            if(matcher.matches()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否希腊数字
     * @param str
     * @return
     */
    public static boolean isAlphanumeric(String str)
    {
        if(isEmpty(str))
        {
            return false;
        } else
        {
            String regex = "[a-zA-Z0-9]+";
            return Pattern.matches(regex, str);
        }
    }

    /**
     * 是否空白
     * @param str
     * @return
     */
    public static boolean isBlank(String str)
    {
        int strLen;
        if(str == null || (strLen = str.length()) == 0)
            return true;
        for(int i = 0; i < strLen; i++)
            if(!Character.isWhitespace(str.charAt(i)))
                return false;

        return true;
    }

    /**
     * 是否移动手机号
     * @param str
     * @return
     */
    public static boolean isChinaMobile(String str)
    {
        if(isEmpty(str))
        {
            return false;
        } else
        {
            String regex = "1(3|5)[4-9]\\d{8}";
            return Pattern.matches(regex, str);
        }
    }

    /**
     * 是否07713568556类型的座机号
     * @param str
     * @return
     */
    public static boolean isChinaPAS(String str)
    {
        if(isEmpty(str))
            return false;
        if(str.startsWith("013") || str.startsWith("015"))
        {
            return false;
        } else
        {
            String regex = "0\\d{9,11}";
            return Pattern.matches(regex, str);
        }
    }

    /**
     * 是否联通手机号
     * @param str
     * @return
     */
    public static boolean isChinaUnicom(String str)
    {
        if(isEmpty(str))
        {
            return false;
        } else
        {
            String regex = "1(3|5)[0-3]\\d{8}";
            return Pattern.matches(regex, str);
        }
    }


    /**
     * 是否email
     * @param str
     * @return
     */
    public static boolean isEmail(String str)
    {
        return !isEmpty(str) && str.indexOf("@") > 0;
    }

    public static boolean isEmpty(Object args[])
    {
        return args == null || args.length == 0 || args.length == 1 && args[0] == null;
    }

    public static boolean isEmpty(String str)
    {
        return str == null || str.trim().length() == 0;
    }

    /**
     * 是否身份证
     * @param str
     * @return
     */
    public static boolean isIdCardNumber(String str)
    {
        if(isEmpty(str))
        {
            return false;
        } else
        {
            String regex = "(\\d{14}|\\d{17})(\\d|x|X)";
            return Pattern.matches(regex, str);
        }
    }

    /**
     * 是否手机号
     * @param str
     * @return
     */
    public static boolean isMobile(String str)
    {
        if(isEmpty(str))
            return false;
        String regex = "(13\\d{9})|(0\\d{9,11})|(15\\d{9})";
        return Pattern.matches(regex, str) && !str.startsWith("013") && !str.startsWith("015");
    }

    /**
     * 是否数字
     * @param str
     * @return
     */
    public static boolean isNumber(String str)
    {
        if(isEmpty(str))
            return false;
        for(int i = 0; i < str.length(); i++)
            if(str.charAt(i) > '9' || str.charAt(i) < '0')
                return false;

        return true;
    }

    /**
     * 是否指定范围内数字min<=str<=max
     * @param str
     * @param min
     * @param max
     * @return
     */
    public static boolean isNumber(String str, int min, int max)
    {
        if(!isNumber(str))
            return false;
        int number = Integer.parseInt(str);
        return number >= min && number <= max;
    }

    public static boolean isNumeric(String str)
    {
        if(isEmpty(str))
        {
            return false;
        } else
        {
            String regex = "(\\+|-){0,1}(\\d+)([.]?)(\\d*)";
            return Pattern.matches(regex, str);
        }
    }

    public static boolean isNumeric(String str, int fractionNum)
    {
        if(isEmpty(str))
        {
            return false;
        } else
        {
            String regex = (new StringBuilder("(\\+|-){0,1}(\\d+)([.]?)(\\d{0,")).append(fractionNum).append("})").toString();
            return Pattern.matches(regex, str);
        }
    }

    public static boolean isPhoneNumber(String str)
    {
        if(isEmpty(str))
        {
            return false;
        } else
        {
            String regex = "(([\\(（]\\d+[\\)）])?|(\\d+[-－]?)*)\\d+";
            return Pattern.matches(regex, str);
        }
    }

    /**
     * 是否邮编
     * @param str
     * @return
     */
    public static boolean isPostcode(String str)
    {
        if(isEmpty(str))
            return false;
        return str.length() == 6 && isNumber(str);
    }

    /**
     * 是否指定长度字符串minLength<=str.length()<=maxLength
     * @param str
     * @param minLength
     * @param maxLength
     * @return
     */
    public static boolean isString(String str, int minLength, int maxLength)
    {
        if(str == null)
            return false;
        if(minLength < 0)
            return str.length() <= maxLength;
        if(maxLength < 0)
            return str.length() >= minLength;
        return str.length() >= minLength && str.length() <= maxLength;
    }

    /**
     * 是否时间
     * @param str
     * @return
     */
    public static boolean isTime(String str)
    {
        if(isEmpty(str) || str.length() > 8)
            return false;
        String items[] = str.split(":");
        if(items.length != 2 && items.length != 3)
            return false;
        for(int i = 0; i < items.length; i++)
            if(items[i].length() != 2 && items[i].length() != 1)
                return false;

        return isNumber(items[0], 0, 23) && isNumber(items[1], 0, 59) && (items.length != 3 || isNumber(items[2], 0, 59));
    }

    /**
     * 是否包含中文
     * @param str
     * @return
     * lsm 2009-08-14
     */
    public static boolean isContainsChinese(String str){
        if(isString(str)){
            String regEx = "[\u4e00-\u9fa5]";
            Pattern pat = Pattern.compile(regEx);
            Scanner input = new Scanner(str);
            str=input.next();
            Matcher matcher = pat.matcher(str);
            boolean flg = false;
            if (matcher.find())
            {
                flg = true;
            }
            return flg;
        }
        return false;
    }

    public static String getString(Object object) {
        if (isEmpty(object)) {
            return "";
        }
        return (object.toString().trim());
    }

    public static boolean isEmpty(Object object) {
        if (object == null) {
            return (true);
        }
        if (object.equals("")) {
            return (true);
        }
        if (object.equals("null")) {
            return (true);
        }
        return (false);
    }

    // 检查是否是有效的手机号码
    public static void checkMobilePhone(String mobilephone)throws Exception{
        if(!Validate.isString(mobilephone)){
            throw new Exception("获取失败：手机号不能为空");
        }
        mobilephone = mobilephone.trim();
        Pattern p = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$");
        Matcher m = p.matcher(mobilephone);
        Boolean b = m.find();
        if(!b){
            throw new Exception("获取失败：不是有效的手机号");
        }
    }
}
