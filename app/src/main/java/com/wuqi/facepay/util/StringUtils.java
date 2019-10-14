package com.wuqi.facepay.util;

import android.util.Log;

/**
 * @ClassName StringUtils
 * @Description 字符串工具类
 * @Author Luo Yi
 * @Date 2019/10/7 20:03
 */
public class StringUtils {
    /**
     * 判断str是否为空
     *
     * @param str String
     * @return boolean true:空;false:非空
     */
    public static boolean isBlank(String str) {
        return ((str == null) || (str.trim().equals("")));
    }

}
