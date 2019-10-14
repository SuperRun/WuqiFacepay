package com.wuqi.facepay.ui.result;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wuqi.facepay.R;
import com.wuqi.facepay.ui.carousel.CarouselActivity;

/**
 * @ClassName SuccessActivity
 * @Description 支付成功结果页
 * @Author Luo Yi
 * @Date 2019/10/7 20:03
 */
public class SuccessActivity extends AppCompatActivity {

    public static final int PICK_REG_VIDEO = 100;

    private TextView countTime = null;
    private TextView money = null;
    private ImageView cancel = null;
    private Intent intent = null;
    private CountDownTimer payCountDownTimer = null;
    private Button closeVocher = null;
    private Button getVocher = null;
    private Button openMemberDialog = null;
    private Button addMemberBtn = null;
    private ImageView closeMember = null;
    private AlertDialog vocherDialog = null;
    private AlertDialog memberDialog = null;
    private LinearLayout addMemberLayout = null;
    private CountDownTimer getVocherCountDownTimer = null;

    private String TAG = "SuccessActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);
        Log.d(TAG, "TaskID = " + getTaskId());
        Log.d(TAG, "lifecycle onCreate: ");
        countTime = findViewById(R.id.countTime);
        money = findViewById(R.id.money);
        cancel = findViewById(R.id.cancelPay);
        openMemberDialog = findViewById(R.id.addMember);

        Intent intentFromPay = getIntent();

        if(intentFromPay.hasExtra("money")){
            String msg = intentFromPay.getStringExtra("money");
            Log.d(TAG, msg);
            money.setText(intentFromPay.getStringExtra("money"));
        }

        // 支付完成结果页面倒计时
        long resultShowTime = Long.parseLong(getString(R.string.result_show_time));
        payCountDownTimer = new CountDownTimer(resultShowTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d(TAG, String.valueOf((int) (millisUntilFinished / 1000-1)));
                countTime.setText("("+(int) (millisUntilFinished / 1000-1)+"s)");
                if ((int) (millisUntilFinished)<2000){
                    intent = new Intent(SuccessActivity.this, CarouselActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                    startActivity(intent);
                    finishTasks();
                }
            }
            @Override
            public void onFinish() {

            }
        };

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "cancel | onClick");
                intent = new Intent(SuccessActivity.this, CarouselActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                startActivity(intent);
                finishTasks();
            }
        });

        // 自定义优惠券弹出框
        LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.vocher_layout, null);
        vocherDialog = new AlertDialog.Builder(this, R.style.VocherDialog).setView(linearLayout).create();

        closeVocher = linearLayout.findViewById(R.id.closeVocher);
        getVocher = linearLayout.findViewById(R.id.getVocher);

        // 绑定关闭dialog事件
        closeVocher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vocherDialog.dismiss();
            }
        });

        // 绑定领取优惠卷事件
        getVocher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vocherDialog.dismiss();
            }
        });

        // dialog关闭回调接口
        vocherDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                getVocherCountDownTimer.cancel();
                payCountDownTimer.start();
            }
        });

        // 自定义优惠券弹出框
        addMemberLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.add_member_layout, null);
        memberDialog = new AlertDialog.Builder(this, R.style.MemberDialog).setView(addMemberLayout).create();

        closeMember = addMemberLayout.findViewById(R.id.closeMember);
        addMemberBtn = addMemberLayout.findViewById(R.id.addMemberBtn);

        // 增加会员
        openMemberDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payCountDownTimer.cancel();
                Log.d(TAG, "==增加会员==");
                countTime.setText("(3s)");
                memberDialog.show();
            }
        });
        // 增加会员弹框关闭回调
        memberDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                 payCountDownTimer.start();
            }
        });

        // 点击增加会员按钮
        addMemberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memberDialog.dismiss();
            }
        });
    }

    protected void validateForm(){
//        String
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, String.valueOf(keyCode));
        Log.d(TAG, String.valueOf(event));

        // 回车
        if (keyCode==66){
            Intent intent = new Intent(this, CarouselActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            startActivity(intent);
            finishTasks();
            Log.d(TAG, "onKeyDown: ");
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStart() {
        super.onStart();
        vocherDialog.show();

        // 设定领取优惠卷倒计时
        long vocherShowTime = Long.parseLong(getString(R.string.vocher_show_time));
        Log.d(TAG, "onTick: " + vocherShowTime);
        getVocherCountDownTimer = new CountDownTimer(vocherShowTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d(TAG, "onTick: " + (int) (millisUntilFinished / 1000-1));
                getVocher.setText(getString(R.string.vocher_btn_text)+"("+(int) (millisUntilFinished / 1000-1)+"s)");
            }
            @Override
            public void onFinish() {
                vocherDialog.dismiss();
//                payCountDownTimer.start();
            }
        }.start();
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

    protected void finishTasks(){
        payCountDownTimer.cancel();
        getVocherCountDownTimer.cancel();
    }
}
