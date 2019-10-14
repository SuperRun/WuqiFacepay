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
import com.wuqi.facepay.ui.logout.LogoutActivity;
import com.wuqi.facepay.ui.pay.PayActivity;
import com.wuqi.facepay.util.CommonUtils;
import com.wuqi.facepay.util.DateUtils;
import com.wuqi.facepay.util.FacePay;
import com.wuqi.facepay.util.StringUtils;

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

//        Log.d(TAG, String.valueOf(MIN_MONEY));
        Log.d(TAG, "lifecycle onCreate: ");

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String expiresIn = sharedPref.getString(getString(R.string.expires_in), null);
        String saveTime = sharedPref.getString(getString(R.string.saved_time), null);
//        Log.d(TAG, "onCreate saveTime= " + saveTime);
//        Log.d(TAG, "onCreate expiresIn= " + expiresIn);

        if (saveTime == null || expiresIn == null){
            // 第一次需要初始化
            FacePay.initPayFace(getApplicationContext());

        } else if (saveTime != null && expiresIn != null){
            // 如果authInfo过期需要重新初始化
            if (DateUtils.getSecondPoor(saveTime) > Long.parseLong(expiresIn)){
                FacePay.initPayFace(getApplicationContext());
            }
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
        String expiresIn = sharedPref.getString(getString(R.string.expires_in), null);
        String saveTime = sharedPref.getString(getString(R.string.saved_time), null);

        if (saveTime == null || expiresIn == null){
            // 第一次需要初始化
            new FacePay().initPayFace(getApplicationContext());

        } else if (saveTime != null && expiresIn != null){
            // 如果authInfo过期需要重新初始化
            if (DateUtils.getSecondPoor(saveTime) > Long.parseLong(expiresIn)){
                new FacePay().initPayFace(getApplicationContext());
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
