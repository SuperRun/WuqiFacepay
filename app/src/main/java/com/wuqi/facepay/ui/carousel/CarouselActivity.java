package com.wuqi.facepay.ui.carousel;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ViewFlipper;

import com.wuqi.facepay.R;
import com.wuqi.facepay.service.AuthService;
import com.wuqi.facepay.service.FacePayService;
import com.wuqi.facepay.ui.logout.LogoutActivity;
import com.wuqi.facepay.ui.pay.PayActivity;
import com.wuqi.facepay.util.CommonUtils;
import com.wuqi.facepay.util.DateUtils;

public class CarouselActivity extends AppCompatActivity {
    private String money = "";
    private String TAG = "CarouselActivity";
    private double MAX_MONEY = 50000;
    private double MIN_MONEY = 0.01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carousel);
        Log.d(TAG, "TaskID = " + getTaskId());
        ViewFlipper viewFlipper = findViewById(R.id.viewFlipper);
        viewFlipper.setFlipInterval(4000);

        Log.d(TAG, "lifecycle onCreate: ");

        // 获取微信刷脸支付的authToken
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        long expiresIn = sharedPref.getLong(getString(R.string.authinfo_expires_in), 0);
        String authInfo = sharedPref.getString(getString(R.string.saved_auth_info), null);
        long saveTime = sharedPref.getLong(getString(R.string.auth_saved_time), 0);
        Log.d(TAG, "==onCreate expiresIn== " + expiresIn);
        Log.d(TAG, "==onCreate authInfo== " + authInfo);
        Log.d(TAG, "==onCreate saveTime== " + saveTime);
        if (authInfo == null){
            // 第一次需要初始化
            FacePayService.initPayFace(getApplicationContext());

        } else {
            // 如果authInfo过期需要重新初始化
            if (saveTime == 0 || DateUtils.getSecondPoor(saveTime) > expiresIn){
                FacePayService.initPayFace(getApplicationContext());
            }
        }

        // 启动新线程网络请求

        String token = sharedPref.getString(getString(R.string.token_access), null);
        long tokenSaveTime = sharedPref.getLong(getString(R.string.token_saved_time), 0);
        long tokenExpiresIn = sharedPref.getLong(getString(R.string.token_expires_in), 0);
        Log.d(TAG, "==onCreate token== " + token);
        Log.d(TAG, "==onCreate tokenSaveTime== " + tokenSaveTime);
        Log.d(TAG, "==onCreate tokenExpiresIn== " + tokenExpiresIn);
        if (token == null || tokenSaveTime==0 || tokenExpiresIn==0 || DateUtils.getSecondPoor(tokenSaveTime) > tokenExpiresIn){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 获取百度刷脸的access_token
                    Log.d(TAG, "== 重新获取token ==");
                    String token = AuthService.getAuth();
                    SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(getString(R.string.token_access), token);
                    editor.putLong(getString(R.string.token_expires_in), 2592000);
                    editor.putLong(getString(R.string.token_saved_time), System.currentTimeMillis());
                    editor.commit();
                }
            }).start();
        }
    }

    @SuppressLint("LongLogTag")
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, String.valueOf(keyCode));
        Log.d(TAG, String.valueOf(event));

        // 数字
        if (keyCode>=7 && keyCode<=16 || keyCode>=144 && keyCode<=153 || keyCode == 158){
            money += event.getNumber();
        }

        // 回车
        if (keyCode==66){
//            Log.d("CarouselActivity money=", money);
            double moneyDouble = Double.parseDouble(money);
//            Log.d("CarouselActivity moneyFloat=", String.valueOf(Double.parseDouble(money)));
            if ( moneyDouble >= MIN_MONEY && moneyDouble <= MAX_MONEY){
                Intent intent = new Intent(this, PayActivity.class);
                intent.putExtra("money", CommonUtils.keepTwo(moneyDouble));
                startActivity(intent);
            }
            money = "";
        }

        // esc
        if (keyCode==111){

            Intent intent = new Intent(this, LogoutActivity.class);
            startActivity(intent);
            money = "";
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "lifecycle onStart: ");

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "lifecycle onRestart: ");
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        long expiresIn = sharedPref.getLong(getString(R.string.authinfo_expires_in), 0);
        long saveTime = sharedPref.getLong(getString(R.string.auth_saved_time), 0);

        if (saveTime == 0 || expiresIn == 0){
            // 第一次需要初始化
            FacePayService.initPayFace(getApplicationContext());

        } else if (saveTime != 0 && expiresIn != 0){
            // 如果authInfo过期需要重新初始化
            if (DateUtils.getSecondPoor(saveTime) > expiresIn){
                FacePayService.initPayFace(getApplicationContext());
            }
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "lifecycle onResume: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "lifecycle onStop: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "lifecycle onPause: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "lifecycle onDestroy: ");
    }
}
