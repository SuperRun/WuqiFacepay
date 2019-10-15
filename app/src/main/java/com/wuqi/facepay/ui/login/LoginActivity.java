package com.wuqi.facepay.ui.login;

import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.wuqi.facepay.R;
import com.wuqi.facepay.service.FacePayService;
import com.wuqi.facepay.ui.carousel.CarouselActivity;
import com.wuqi.facepay.util.StringUtils;
import com.wuqi.facepay.util.ToastUtils;

/**
 * @ClassName LoginActivity
 * @Description 登录页
 * @Author Luo Yi
 * @Date 2019/10/7 20:03
 */
public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText = null;
    private EditText passwordEditText = null;
    private ImageView userIconImageView = null;
    private ImageView clearUserIconImageView = null;
    private ImageView pwdIconImageView = null;
    private ImageView ifShowImageView = null;
    private Button loginButton = null;
    private LinearLayout usernameLinearLayout = null;
    private LinearLayout pwdLinearLayout = null;

    private LoginViewModel loginViewModel;
    private boolean ifShowPwd = false;
    private Intent intent = null;
    private int result = 0;
    private ProgressBar loadingProgressBar = null;
    private String TAG = "LoginActivity";

    private Handler mhandler = new Handler();
    private Runnable runnable1 =new Runnable() {

        public void run() {
            loadingProgressBar.setVisibility(View.INVISIBLE);
            intent = new Intent(LoginActivity.this, CarouselActivity.class);
            new ToastUtils().showToast(getApplicationContext(), getResources().getString(result), intent);
            onDestroy();
        }

    };
    private Runnable runnable2 =new Runnable() {

        public void run() {
            loadingProgressBar.setVisibility(View.INVISIBLE);
            ToastUtils.showToast(getApplicationContext(), getResources().getString(result), 1000);
        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "TaskID = " + getTaskId());
        Log.d(TAG, "lifecycle onCreate: ");
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        userIconImageView = findViewById(R.id.userIcon);
        clearUserIconImageView = findViewById(R.id.clearUserIcon);
        pwdIconImageView = findViewById(R.id.pwdIconImageView);
        ifShowImageView = findViewById(R.id.ifShowImageView);
        loginButton = findViewById(R.id.loginBtn);
        usernameLinearLayout = findViewById(R.id.usernameLinearLayout);
        pwdLinearLayout = findViewById(R.id.pwdLinearLayout);
        loadingProgressBar = findViewById(R.id.loading);

        // 文本发生变化时判断两个input是否都输入文本 是：显示按钮背景色 否：不显示
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!StringUtils.isBlank(usernameEditText.getText().toString()) &&
                        !StringUtils.isBlank(passwordEditText.getText().toString())){
                    loginButton.setBackground(getResources().getDrawable(R.drawable.shape_btn_pressed));
                    loginButton.setEnabled(true);
                }else{
                    loginButton.setBackground(getResources().getDrawable(R.drawable.shape_btn_unpressed));
                    loginButton.setEnabled(false);
                }
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);

        // 文本框聚焦改变外观颜色
        usernameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.i("LoginActivity", "onFocus");
                if (hasFocus){
                    usernameLinearLayout.setBackground(getResources().getDrawable(R.drawable.shape_form_item_focused));
                    userIconImageView.setImageDrawable(getResources().getDrawable(R.drawable.user_active));
                }else if (StringUtils.isBlank(usernameEditText.getText().toString())){
                    usernameLinearLayout.setBackground(getResources().getDrawable(R.drawable.shape_form_item));
                    userIconImageView.setImageDrawable(getResources().getDrawable(R.drawable.user_icon));
                }
            }
        });

        // 清除用户名文本框内容
        clearUserIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameEditText.setText("");
            }
        });

        // 文本框聚焦改变外观颜色
        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    pwdLinearLayout.setBackground(getResources().getDrawable(R.drawable.shape_form_item_focused));
                    pwdIconImageView.setImageDrawable(getResources().getDrawable(R.drawable.pwd_active));
                }else if (StringUtils.isBlank(passwordEditText.getText().toString())){
                    pwdLinearLayout.setBackground(getResources().getDrawable(R.drawable.shape_form_item));
                    pwdIconImageView.setImageDrawable(getResources().getDrawable(R.drawable.pwd_icon));
                }
            }
        });

        // 是否显示密码
        ifShowImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ifShowPwd = !ifShowPwd;
                int type = passwordEditText.getInputType();
                if (ifShowPwd){
                    ifShowImageView.setImageDrawable(getResources().getDrawable(R.drawable.show_pwd));
                    passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else {
                    ifShowImageView.setImageDrawable(getResources().getDrawable(R.drawable.hide_pwd));
                    passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        // 登录
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 隐藏软键盘
                hideKeyBoard();
                if (loginButton.isEnabled()){
                    loadingProgressBar.setVisibility(View.VISIBLE);
                    result = loginViewModel.login(usernameEditText.getText().toString(), passwordEditText.getText().toString());
                    if (result == R.string.login_success){
                        // 登录成功后初始化微信支付app
                        FacePayService.initPayFace(getApplicationContext());
                        mhandler.postDelayed(runnable1, 1000);
                    }else {
                        mhandler.postDelayed(runnable2, 1000);
                    }
                }else{
                    ToastUtils.showToast(getApplicationContext(), "请输入用户名和密码", 1000);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "lifecycle onDestroy: ");
        super.onDestroy();
    }

    /**
     * 隐藏软键盘
     */
    protected void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
    }
}
