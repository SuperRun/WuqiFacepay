package com.wuqi.facepay.util;

import android.util.Log;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * @ClassName CommonUtils
 * @Description 通用工具类
 * @Author Luo Yi
 * @Date 2019/10/7 20:03
 */
public class CommonUtils {

  /**
   * double类型数据保留小数点后两位
   * @param doubleMoney
   * @return
   */
  public static String keepTwo (double doubleMoney) {
    DecimalFormat formater = new DecimalFormat("0.00");
    formater.setMaximumFractionDigits(2);
    formater.setRoundingMode(RoundingMode.DOWN);
    return formater.format(doubleMoney);
  }
}
