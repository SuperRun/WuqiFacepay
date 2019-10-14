package com.wuqi.facepay.ui.result;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wuqi.facepay.R;
import com.wuqi.facepay.ui.carousel.CarouselActivity;

/**
 * @ClassName FailedActivity
 * @Description 支付失败结果页
 * @Author Luo Yi
 * @Date 2019/10/7 20:03
 */
public class FailedActivity extends AppCompatActivity {

    private TextView countTime = null;
    private TextView money = null;
    private ImageView cancel = null;
    private Intent intent = null;
    private CountDownTimer timer = null;

    private String TAG = "FailedActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_failed);
        Log.d(TAG, "TaskID = " + getTaskId());
        Log.d(TAG, "lifecycle onCreate: ");
        countTime = findViewById(R.id.countTime);
        money = findViewById(R.id.money);
        cancel = findViewById(R.id.cancelPay);
        intent = new Intent(FailedActivity.this, CarouselActivity.class);

        // 启动倒计时
        long resultTime = Long.parseLong(getString(R.string.result_show_time));
        timer = new CountDownTimer(resultTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                countTime.setText((int) (millisUntilFinished / 1000-1)+"s");
                Log.d(TAG, String.valueOf(millisUntilFinished / 1000-1));
                if ((int) (millisUntilFinished)<2000){
                    startActivity(intent);
                    onDestroy();
                }
            }
            @Override
            public void onFinish() {

            }
        }.start();

        // 关闭页面
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");
                startActivity(intent);
                onDestroy();
            }
        });
    }
}
