package com.util;/**
 * 描述：
 * Created by Liuyg on 2018-12-28 10:10
 */

import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * @program: mmqbb
 * @description:
 * @author: Liuyg
 * @create: 2018-12-28 10:10
 **/

public class Base64Util {

    private static final Logger logger = LoggerFactory.getLogger(Base64Util.class);

    private static final String UTF_8 = "UTF-8";

    /**
     * 对给定的字符串进行base64解码操作
     */
    public static String decodeData(String inputData) {
        if (null == inputData) {
            return null;
        }
        return new String(Base64.decodeBase64(inputData.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);

        return null;
    }

    /**
     * 对给定的字符串进行base64加密操作
     */
    public static String encodeData(String inputData) {
        if (null == inputData) {
            return null;
        }
        return new String(Base64.encodeBase64(inputData.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);

        return null;
    }

}
