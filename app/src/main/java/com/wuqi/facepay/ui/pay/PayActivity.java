package com.wuqi.facepay.ui.pay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tencent.wxpayface.IWxPayfaceCallback;
import com.tencent.wxpayface.WxPayFace;
import com.tencent.wxpayface.WxfacePayCommonCode;
import com.wuqi.facepay.R;
import com.wuqi.facepay.service.FacePayService;
import com.wuqi.facepay.ui.carousel.CarouselActivity;
import com.wuqi.facepay.ui.result.FailedActivity;
import com.wuqi.facepay.ui.result.SuccessActivity;
import com.wuqi.facepay.util.CommonUtils;
import com.wuqi.facepay.util.ToastUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName PayActivity
 * @Description 支付页
 * @Author Luo Yi
 * @Date 2019/10/7 20:03
 */
public class PayActivity extends AppCompatActivity {

    public static final String TAG = "PayActivity";

    private TextView countTime = null;
    private TextView money = null;
    private ImageView cancel = null;
    private Button wxFacePayBtn = null;

    private Intent intent = null;
    private CountDownTimer timer =null;
    private String moneyVal ="";

    private static final String PARAMS_FACE_AUTHTYPE = "face_authtype";
    private static final String PARAMS_APPID = "appid";
    private static final String PARAMS_SUB_APPID = "sub_appid";
    private static final String PARAMS_MCH_ID = "mch_id";
    private static final String PARAMS_MCH_NAME = "mch_name";
    private static final String PARAMS_SUB_MCH_ID = "sub_mch_id";
    private static final String PARAMS_STORE_ID = "store_id";
    private static final String PARAMS_AUTHINFO = "authinfo";
    private static final String PARAMS_OUT_TRADE_NO = "out_trade_no";
    private static final String PARAMS_TOTAL_FEE = "total_fee";
    private static final String PARAMS_TELEPHONE = "telephone";
    private static final String PARAMS_FACTORY = "factory";

    private static final String PARAMS_REPORT_ITEM = "item";
    private static final String PARAMS_REPORT_ITEMVALUE = "item_value";

    private static final String PARAMS_REPORT_MCH_ID = "mch_id";
    private static final String PARAMS_REPORT_SUT_MCH_ID = "sub_mch_id";
    private static final String PARAMS_REPORT_OUT_TRADE_NO = "out_trade_no";
    private static final String PARAMS_BANNER_STATE = "banner_state";


    public static final String RETURN_CODE = "return_code";
    public static final String RETURN_MSG = "return_msg";

    private static final int TEST_TYPE_NONE = 0;
    private static final int TEST_TYPE_OBO = 1;
    private static final int TEST_TYPE_OBN = 2;
    private static final int TEST_TYPE_OBN_ONCE = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        Log.d(TAG, "TaskID = " + getTaskId());
        Log.d(TAG, "lifecycle onCreate: ");
        countTime = findViewById(R.id.countTime);
        cancel = findViewById(R.id.cancelPay);
        money = findViewById(R.id.money);
        wxFacePayBtn = findViewById(R.id.wx_face_pay_btn);
        long showTime = Long.parseLong(getString(R.string.pay_show_time));
        Intent intentFromCarousel = getIntent();

        // 获取页面传值过来的金额
        if(intentFromCarousel.hasExtra("money")){
            moneyVal = intentFromCarousel.getStringExtra("money");
            money.setText(intentFromCarousel.getStringExtra("money"));
        }

        // 启动倒计时
        timer = new CountDownTimer(showTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                countTime.setText((int) (millisUntilFinished / 1000-1)+"s");
                if ((int) (millisUntilFinished)<2000){
                    ToastUtils.showToast(getApplicationContext(), "支付失败", 1000);
                }
            }
            @Override
            public void onFinish() {
                startActivity(intent);
            }
        }.start();

        // 刷脸支付
        wxFacePayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Log.d("PayActivity", "onClick: wxFacePayBtn");
            wxFacePayBtn.setEnabled(false);
            doPay();
            }
        });

        // 关闭页面
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Log.d(TAG, "cancel | onClick");
            intent = new Intent(PayActivity.this, CarouselActivity.class);
            startActivity(intent);
            finishTask();
//            onDestroy();
            }
        });
    }


    /**
     * 返回刷脸支付结果
     * @return
     */
    private boolean doPay() {
        Log.d(TAG, "enter | doPay ");
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String authInfo = sharedPref.getString(getString(R.string.saved_auth_info), null);
        Log.d(TAG, "doPay | authInfo = " +authInfo);

        HashMap params = new HashMap();
        params.put(PARAMS_FACE_AUTHTYPE, "FACEPAY");
        params.put(PARAMS_APPID, "wx2b029c08a6232582");
        params.put(PARAMS_MCH_ID, "1900007081");
        params.put(PARAMS_STORE_ID, "12345");
        params.put(PARAMS_OUT_TRADE_NO, "" + (System.currentTimeMillis() / 100000));
        params.put(PARAMS_TOTAL_FEE, "22222");
        params.put(PARAMS_TELEPHONE, "");
        params.put(PARAMS_AUTHINFO, authInfo);
        params.put(PARAMS_FACTORY, true);

        WxPayFace.getInstance().getWxpayfaceCode(params, new IWxPayfaceCallback() {
            @Override
            public void response(Map info) throws RemoteException {
            if (!FacePayService.isSuccessInfo(info)) {
                wxFacePayBtn.setEnabled(true);
                return;
            }
            Log.d(TAG, "response | getWxpayfaceCode");
            final String code = (String) info.get(RETURN_CODE);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (TextUtils.equals(code, WxfacePayCommonCode.VAL_RSP_PARAMS_SUCCESS)) {
                        Log.d(TAG, "response | intent");
                        wxFacePayBtn.setEnabled(true);
                        intent = new Intent(PayActivity.this, SuccessActivity.class);
                        intent.putExtra("money", moneyVal);

                    }else{
                        intent = new Intent(PayActivity.this, FailedActivity.class);
                    }
                    startActivity(intent);
                    finishTask();

                }
            });
            }
        });
        return true;
    }

//    private void doFaceRecognize(boolean once) {
//        HashMap params2 = new HashMap();
//        if (once) {
//            params2.put(PARAMS_FACE_AUTHTYPE, "FACEID-ONCE");
//        } else {
//            params2.put(PARAMS_FACE_AUTHTYPE, "FACEPAY");
//        }
//        params2.put(PARAMS_APPID, "wx2b029c08a6232582");
//        params2.put(PARAMS_MCH_ID, "1900007081");
//        params2.put(PARAMS_MCH_NAME, "科脉自助收银");
////                params2.put(PARAMS_MCH_ID,"12306");
////                params2.put(PARAMS_STORE_ID,"12345");
////                params2.put(PARAMS_SUB_APPID,"33333");
////                params2.put(PARAMS_SUB_MCH_ID,"44444");
//        params2.put(PARAMS_OUT_TRADE_NO, "" + (System.currentTimeMillis() / 100000));
//        params2.put(PARAMS_TOTAL_FEE, "22222");
//        String phone2 = "";
//        params2.put(PARAMS_TELEPHONE, phone2);
//        params2.put(PARAMS_AUTHINFO, mAuthInfo);
//        params2.put(PARAMS_FACTORY, true);
//        WxPayFace.getInstance().getWxpayfaceUserInfo(params2, new IWxPayfaceCallback() {
//            @Override
//            public void response(Map info) throws RemoteException {
//                isSuccessInfo(info);
//                Log.d(TAG, "response | getWxpayfaceUserInfo " + info.toString());
//                String openid = (String) info.get("openid");
//                if (!TextUtils.isEmpty(openid)) {
//                    showToast(PayActivity.this, "[1:N]测试成功，可重新测试，或点击停止[1:N]测试");
//                }
//            }
//        });
//    }
//
//    private void stopFaceRecognize() {
//        HashMap map = new HashMap();
//        WxPayFace.getInstance().stopWxpayface(map, new IWxPayfaceCallback() {
//            @Override
//            public void response(Map info) throws RemoteException {
//                if (!isSuccessInfo(info)) {
//                    return;
//                }
//                Log.d(TAG, "response | stopWxpayface");
//            }
//        });
//    }

//    private void getAuthInfo(String rawdata) throws IOException {
//        Log.d(TAG, "enter | getAuthInfo ");
//        try {
//            // Create a trust manager that does not validate certificate chains
//            final TrustManager[] trustAllCerts = new TrustManager[]{
//                new X509TrustManager() {
//                    @Override
//                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
//                    }
//
//                    @Override
//                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
//                    }
//
//                    @Override
//                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//                        return new java.security.cert.X509Certificate[]{};
//                    }
//                }
//            };
//
//            // Install the all-trusting trust manager
//            final SSLContext sslContext = SSLContext.getInstance("SSL");
//            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
//            // Create an ssl socket factory with our all-trusting manager
//            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
//
//            OkHttpClient client = new OkHttpClient.Builder()
//                    .sslSocketFactory(sslSocketFactory)
//                    .hostnameVerifier(new HostnameVerifier() {
//                        @Override
//                        public boolean verify(String hostname, SSLSession session) {
//                            return true;
//                        }
//                    })
//                    .build();
//
//            RequestBody body = RequestBody.create(null, rawdata);
//
//            Request request = new Request.Builder()
//                    .url("https://wxpay.wxutil.com/wxfacepay/api/getWxpayFaceAuthInfo.php")
//                    .post(body)
//                    .build();
//
//            client.newCall(request)
//                    .enqueue(new Callback() {
//                        @Override
//                        public void onFailure(Call call, IOException e) {
//                            Log.d(TAG, "onFailure | getAuthInfo " + e.toString());
//                        }
//
//                        @Override
//                        public void onResponse(Call call, Response response) throws IOException {
//                            try {
//                                Log.d(TAG, "onResponse | getAuthInfo");
//                                mAuthInfo = ReturnXMLParser.parseGetAuthInfoXML(response.body().byteStream());
//                                if (mAuthInfo == null) {
//                                    showToast(PayActivity.this, "authinfo为空");
//                                    return;
//                                }
//                                switch (mTestType) {
//                                    case TEST_TYPE_OBO:
//                                        doPay();
//                                        break;
//                                    case TEST_TYPE_OBN:
//                                        doFaceRecognize(false);
//                                        break;
//                                    case TEST_TYPE_OBN_ONCE:
//                                        doFaceRecognize(true);
//                                        break;
//                                    default:
//                                        break;
//                                }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                            Log.d(TAG, "onResponse | getAuthInfo " + mAuthInfo);
//                        }
//                    });
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException(e);
//        }
//    }

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
        Log.d(TAG, "lifecycle onDestroy");
    }

    protected void finishTask(){
        timer.cancel();
        FacePayService.releasePayFace(getApplicationContext());
    }

}
