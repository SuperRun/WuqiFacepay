package com.wuqi.facepay.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.wuqi.facepay.R;

import java.util.Map;

public class SharedPreferencesUtils {

    public static void editSharedPreferences(Context context, Map map){
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
    }
}
