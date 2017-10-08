package com.wite.positionerwear.utils;

/**
 * Created by Administrator on 2017/9/11.
 */

public class UnicodeUtil {

    private static String TAG;



    /**
     * 将unicode字节=utf-8的汉字转换成unicode格式汉字码
     *
     * @param string
     * @return
     */
    public static String UNstringToUnicode(String string) {
        String test;
        StringBuffer stringBuffer = new StringBuffer();

        for (int i = 0; i < string.length(); i = i + 4) {
            test = string.substring(i, i + 4);
            stringBuffer.append("\\u"+test);

        }
        return    unicodeToString(stringBuffer.toString());
    }




    //转Unicode
    public static String string2Unicode(String string) {

        StringBuffer unicode = new StringBuffer();

        for (int i = 0; i < string.length(); i++) {

            // 取出每一个字符
            char c = string.charAt(i);

            // 转换为unicode
            unicode.append("\\u" + Integer.toHexString(c));
        }

        return unicode.toString();
    }


    public static String unicodeToString(String unicode) {

        String str = unicode.replace("0x", "\\");

        StringBuffer string = new StringBuffer();
        String[] hex = str.split("\\\\u");
        for (int i = 1; i < hex.length; i++) {
            int data = Integer.parseInt(hex[i], 16);
            string.append((char) data);
        }
        return string.toString();
    }



    /**
     * 将字符串转成unicode
     * @param str 待转字符串
     * @return unicode字符串
     */
    public String convert(String str)
    {
        str = (str == null ? "" : str);
        String tmp;
        StringBuffer sb = new StringBuffer(1000);
        char c;
        int i, j;
        sb.setLength(0);
        for (i = 0; i < str.length(); i++)
        {
            c = str.charAt(i);
            sb.append("\\u");
            j = (c >>>8); //取出高8位
            tmp = Integer.toHexString(j);
            if (tmp.length() == 1)
                sb.append("0");
            sb.append(tmp);
            j = (c & 0xFF); //取出低8位
            tmp = Integer.toHexString(j);
            if (tmp.length() == 1)
                sb.append("0");
            sb.append(tmp);

        }
        return (new String(sb));
    }

}