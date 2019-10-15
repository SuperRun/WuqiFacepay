package com.wuqi.facepay.util;

import java.util.Date;

/**
 * @ClassName DateUtils
 * @Description 日期转化工具类
 * @Author Luo Yi
 * @Date 2019/10/7 20:03
 */
public class DateUtils {

    /**
     * 得到与当前时间的秒数差
     * @param savedTime
     * @return
     */
    public static long getSecondPoor(long savedTime) {

        // 获得两个时间的毫秒时间差异
        long diff = System.currentTimeMillis() - savedTime;
        return diff / 1000;
    }
}
