package com.wuqi.facepay.ui.logout;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wuqi.facepay.R;
import com.wuqi.facepay.ui.carousel.CarouselActivity;
import com.wuqi.facepay.ui.login.LoginActivity;
import com.wuqi.facepay.util.StringUtils;
import com.wuqi.facepay.util.ToastUtils;

/**
 * @ClassName LogoutActivity
 * @Description 退出登录页
 * @Author Luo Yi
 * @Date 2019/10/7 20:03
 */
public class LogoutActivity extends AppCompatActivity {

    TextView password = null;
    Button logoutBtn = null;
    ProgressBar loadingProgressBar = null;
    Intent intent = null;
    private String TAG = "LogoutActivity";

    private Handler mhandler = new Handler();

    // 退出登陆成功提示
    private Runnable runnabler1 =new Runnable() {

        public void run() {
            loadingProgressBar.setVisibility(View.INVISIBLE);
            intent = new Intent(LogoutActivity.this, LoginActivity.class);
            new ToastUtils().showToast(getApplicationContext(), getResources().getString(R.string.logout_success), intent);
        }

    };

    // 密码不正确提示
    private Runnable runnabler2 =new Runnable() {

        public void run() {
            loadingProgressBar.setVisibility(View.INVISIBLE);
            ToastUtils.showToast(getApplicationContext(), "密码不正确", 2000);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);
        Log.d(TAG, "lifecycle onCreate: ");
        logoutBtn = findViewById(R.id.logoutBtn);
        password = findViewById(R.id.password);

        // 退出登录
        logoutBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Log.d("LogoutActivity", String.valueOf(password.getText().toString().equals("123456")));
                if (StringUtils.isBlank(password.getText().toString())){
                    ToastUtils.showToast(getApplicationContext(), "请输入密码", 2000);
                }else if (password.getText().toString().equals("123456")){
                    ToastUtils.showToast(getApplicationContext(), getResources().getString(R.string.logout_success), 2000);
                    intent = new Intent(LogoutActivity.this, LoginActivity.class);
                    startActivity(intent);
                }else{
                    ToastUtils.showToast(getApplicationContext(), "密码错误", 2000);
                }
            }
        });
    }
}
