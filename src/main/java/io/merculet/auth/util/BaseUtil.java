package io.merculet.auth.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Base64;
import java.util.Objects;

/**
 * Base64编码通用处理
 * @author zhou liming
 * @package cn.merculet.auth.util
 * @date 2018/9/26 15:01
 * @description
 */
@Slf4j
public class BaseUtil {

    public static final Base64.Decoder DECODER = Base64.getDecoder();
    public static final Base64.Encoder ENCODER = Base64.getEncoder();

    /**
     * 解码
     * @param content
     * @return
     */
    public static String decode(String content){
        if(StringUtils.isEmpty(content)){
            return content;
        }

        try {
            return new String(DECODER.decode(content), "UTF-8");
        } catch (Exception e) {
            log.error("decode error,msg={} content={}",e.getMessage(),content);
            return content;
        }
    }

    /**
     * 编码
     * @param content
     */
    public static String encode(String content){
        if(StringUtils.isEmpty(content)){
            return content;
        }

        try {
            byte[] textByte = content.getBytes("UTF-8");
            return ENCODER.encodeToString(textByte);
        } catch (Exception e) {
            log.error("encode error,msg={} content={}",e.getMessage(),content);
            return content;
        }

    }

    /**
     * 判断字符串是否已经base编码
     * @param content
     * @return
     */
    public static boolean isBase64(String content) {
        String decode = decode(content);

        String encode = encode(decode);

        if(Objects.equals(encode,content)){
            return true;
        }
        return false;
    }

}
