package com.trs.gfetch.utils;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class StringUtil {

    static final Base64.Encoder encoder = Base64.getEncoder();
    static final Base64.Decoder decoder = Base64.getDecoder();

    /**
     * 字符串base64,encoder
     * @param text
     * @return
     */
    public static String encoder(String text){
        try {
            final byte[] textByte = text.getBytes("UTF-8");
            //编码
            return encoder.encodeToString(textByte);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    /**
     * 字符串base64,decoder
     * @param encodedText
     * @return
     */
    public static String decoder(String encodedText){
        try {
            return new String(decoder.decode(encodedText), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    /**
     * url编码
     * @param str
     * @return
     */
    public static String getURLEncoderString(String str) {
        String result = "";
        if (null == str) {
            return "";
        }
        try {
            result = java.net.URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * url解码
     * @param str
     * @return
     */
    public static String URLDecoderString(String str) {
        String result = "";
        if (null == str) {
            return "";
        }
        try {
            result = java.net.URLDecoder.decode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

}
