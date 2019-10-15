package com.wuqi.facepay.util;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.wuqi.facepay.R;

/**
 * @ClassName ToastUtils
 * @Description Toast工具类
 * @Author Luo Yi
 * @Date 2019/10/7 20:03
 */
public class ToastUtils {
    private static Toast toast;
    private static Boolean isShowing=false;
    private static Intent intent=null;
    private static Context context=null;

    private static Handler handler = new Handler(Looper.getMainLooper());
    private static Handler mhandler = new Handler();
    private static Runnable runnable =new Runnable() {
        public void run() {
            Log.d("Runnable", "run: ");
            isShowing=false;
            toast.cancel();
            // 跳转页面
            if (intent!=null){
                context.startActivity(intent);
            }
        }

    };

    /**
     * 自定义Toast文本
     * @param context
     * @param msg
     * @param time
     */
    public static void showToast(Context context, String msg, long time) {

        if (!isShowing){

            if (toast ==null)
                toast = new Toast(context);
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.toast_layout, null);
            TextView toast_msg = view.findViewById(R.id.toastMsg);
            toast_msg.setText(msg);
            toast.setView(view);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            isShowing=true;
            mhandler.postDelayed(runnable, time);
        }

    }

    /**
     * Toast出现之后页面跳转
     * @param context
     * @param msg
     * @param intent
     */
    public  void showToast(Context context, String msg, Intent intent) {
        this.context = context;
        this.intent = intent;
        if (!isShowing){
            if (toast ==null)
                toast = new Toast(context);
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.toast_layout, null);
            TextView toast_msg = view.findViewById(R.id.toastMsg);
            toast_msg.setText(msg);
            toast.setView(view);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            isShowing=true;
            mhandler.postDelayed(runnable, 2000);
        }

    }

    /**
     * Toast 传递延迟处理事件
     * @param context
     * @param msg
     * @param time
     * @param runnable
     */
    public  void showToast(Context context, String msg, long time, Runnable runnable) {
        this.context = context;
        this.intent = intent;
        if (!isShowing){
            if (toast ==null)
                toast = new Toast(context);
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.toast_layout, null);
            TextView toast_msg = view.findViewById(R.id.toastMsg);
            toast_msg.setText(msg);
            toast.setView(view);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            isShowing=true;
            mhandler.postDelayed(runnable, time);
        }

    }

    public static void toast(final Context context, final String text) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
