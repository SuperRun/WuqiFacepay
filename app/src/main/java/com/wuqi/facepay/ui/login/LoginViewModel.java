package com.wuqi.facepay.ui.login;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.wuqi.facepay.R;
import com.wuqi.facepay.data.LoginRepository;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();

    public LoginViewModel(LoginRepository instance) {
    }


    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public int login(String username, String password) {
        // can be launched in a separate asynchronous job
        return loginDataChanged(username, password);
    }

    public int loginDataChanged(String username, String password) {
        Log.d("LoginViewModel", String.valueOf(username.equals("1586714785")));
        Log.d("LoginViewModel", String.valueOf(password.equals("123456")));
        if (!isUserNameValid(username)) {
            return R.string.invalid_username;
        } else if (!isPasswordValid(password)) {
            return R.string.invalid_password;
        } else if (username.equals("15868174785") && password.equals("123456")){
            return R.string.login_success;
        }
        return R.string.login_failed;
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }

        String regex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[013678])|(18[0,5-9]))\\d{8}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(username);

        return m.matches();
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}
