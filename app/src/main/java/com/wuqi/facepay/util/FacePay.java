package com.wuqi.facepay.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.RemoteException;
import android.util.Log;

import com.tencent.wxpayface.IWxPayfaceCallback;
import com.tencent.wxpayface.WxPayFace;
import com.tencent.wxpayface.WxfacePayCommonCode;
import com.wuqi.facepay.R;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @ClassName DateUtils
 * @Description 刷脸支付工具类
 * @Author Luo Yi
 * @Date 2019/10/7 20:03
 */
public class FacePay {

    private static String TAG = "FacePay";

    private static String mRawData;
    public static String mAuthInfo;
    public static String expiresIn;
    public static boolean mHasInited;
    private static Context context;
    //
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
    private int mTestType = TEST_TYPE_NONE;


    /**
     * 初始化微信刷脸支付程序
     * @param ctx
     */
    public static void initPayFace(Context ctx) {
        // 初始化微信刷脸支付程序接口
        context = ctx;
        WxPayFace.getInstance().initWxpayface(ctx, new IWxPayfaceCallback() {
            @Override
            public void response(Map info) throws RemoteException {
                if (!isSuccessInfo(info)) {
                    return;
                }
                mHasInited = true;
                // 一定要在初始化成功后调用否则失败
                getRawData(context);
            }
        });
    }

    /**
     * 判断响应结果是否成功
     * @param info
     * @return boolean
     */
    public static boolean isSuccessInfo(Map info){
        if (info == null) {
            new RuntimeException("调用返回为空").printStackTrace();
            return false;
        }
        Log.d(TAG, String.valueOf(info));
        String code = (String) info.get(RETURN_CODE);
        String msg = (String) info.get(RETURN_MSG);
        Log.d(TAG, "response info :: " + code + " | " + msg);
        if (code == null || !code.equals(WxfacePayCommonCode.VAL_RSP_PARAMS_SUCCESS)) {
            new RuntimeException("调用返回非成功信息: " + msg).printStackTrace();
            return false;
        }
        Log.d(TAG, "调用返回成功");
        return true;
    }

    /**
     * 获取数据(RawData)
     * @param ctx
     */
    public static void getRawData(Context ctx) {
        context = ctx;
        Log.d(TAG, "enter | getRawData ");
        WxPayFace.getInstance().getWxpayfaceRawdata(new IWxPayfaceCallback() {
            @Override
            public void response(Map info) throws RemoteException {
                Log.d(TAG, "response | getWxpayfaceRawdata");
                if (!isSuccessInfo(info)) {
                    return;
                }
                mRawData = info.get("rawdata").toString();
                if (mRawData == null) {
                    Log.d(TAG, "rawdata为空");
                }
                Log.d(TAG, "mRawData="+mRawData);
                try {
                    getAuthInfo(mRawData, context);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "authinfo获取出错");
                }
            }
        });
    }


    /**
     * 获取SDK调用凭证
     * @param rawdata
     * @throws IOException
     */
    private static void getAuthInfo(String rawdata, Context ctx) throws IOException {
        Log.d(TAG, "enter | getAuthInfo ");
        context = ctx;
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }
                }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient client = new OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory)
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                        return true;
                        }
                    })
                    .build();

            RequestBody body = RequestBody.create(null, rawdata);

            Request request = new Request.Builder()
                    .url("https://wxpay.wxutil.com/wxfacepay/api/getWxpayFaceAuthInfo.php")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d(TAG, "onFailure | getAuthInfo " + e.toString());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        Log.d(TAG, "onResponse | getAuthInfo");
                        InputStream inputStream = response.body().byteStream();
                        mAuthInfo = ReturnXMLParser.parseGetAuthInfoXML(inputStream);
                        expiresIn = ReturnXMLParser.parseExpireInXML(inputStream);

                        if (mAuthInfo == null) {
                            Log.d(TAG, " authinfo为空 " );
                            return;
                        }

                        // 将 authInfo expiresIn savedTime 存储到 SharedPreferences
                        SharedPreferences sharedPref = context.getSharedPreferences(
                                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(context.getString(R.string.saved_auth_info), mAuthInfo);

                        if (expiresIn == null) {
                            editor.commit();
                            return;
                        }

                        Log.d(TAG, "onResponse | mAuthInfo " + mAuthInfo);
                        Log.d(TAG, "onResponse | expiresIn " + expiresIn);


                        editor.putString(context.getString(R.string.expires_in), expiresIn);
                        editor.putString(context.getString(R.string.saved_time), String.valueOf(System.currentTimeMillis()));
                        editor.commit();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    /**
     * 释放资源
     * @param ctx
     */
    public static void releasePayFace(Context ctx) {
        WxPayFace.getInstance().releaseWxpayface(ctx);
    }

}
