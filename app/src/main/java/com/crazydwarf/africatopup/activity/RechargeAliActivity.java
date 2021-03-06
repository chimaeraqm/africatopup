package com.crazydwarf.africatopup.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.alipay.sdk.app.EnvUtils;
import com.alipay.sdk.app.PayTask;
import com.crazydwarf.africatopup.AlipayUtil.OrderInfoUtil2_0;
import com.crazydwarf.africatopup.AlipayUtil.PayResult;
import com.crazydwarf.africatopup.Objects.User;
import com.crazydwarf.africatopup.R;
import com.crazydwarf.africatopup.view.SimpleToolBar;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RechargeAliActivity extends BaseActivity
{
    private static final String url = "sandboxOrderInfo.php";

    private static final String ORDER_REQUEST_URL = "https://wx.dwarfworkshop.com/congo/alipay_sign.php";

    /**
     * 根据flag选择相关业务，SDK_PAY_FLAG支付业务，SDK_AUTH_FLAG授权业务
     */
    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;

    /**
     * APPID 填写支付宝应用平台中的当前应用appid，使用沙箱时需切换为沙箱的appid
     */
//    public static final String APPID = "2018090561232507";
//    public static final String APPID = "2016091600526477";
    public static final String APPID = "2016091600526466";

    /**
     * RSA2_PRIVATE 填写生成的应用私钥
     * 应用公钥和支付宝公钥对于不同的app是相同的，对沙箱环境也是如此
     * 使用沙箱测试时，应用私钥可以不用重新生成，只需公钥和私钥对应即可
     */
//    public static final String RSA2_PRIVATE = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCWZ3WtQfRZLAgSsaKNflIoshcwkE7EyaYlF1s9WGxNY59GGosb25h76dxt6qnmK7YptomxswWGc5mlFmczcfAlcT8fBZNL762DbJdKvzYnP8JO2R+3e/eA+tFmCkYGXeS2HHR62LbpejX6phaFGBpGo5/P4bNNTKft6S6tXWIcFuC9wRTsh9UhPKpJSJcDz/TYXL2j5EdJQ0G2uk5LZI283QYm7g9NZJA+b4J7qD7k5ZLfDlwry27PlMFS2dqQR2aroUeflKQENc076qV7USi5Fug7nMcOlmvdaRmvhpRhG2LxWumDo5UJiTlsTOE3tw5HQ80+/Fv1ks1Drl9lyTr9AgMBAAECggEAN/bzYK7D/1JVBq+2brPsWlw6KMXpqWvnOLICL0dxtTI2l91Umd8SVDlm3jeNVKo7NDZJ8idNDQSCzV0StZ/V3fjPpflrH7xlchu8CAIyYlRlNvWvyZSFOicaN7/m4oOZkPHxnax6E1J4N3YJtyiKznIgCzhOgZreebZkufmfghelQoR7Q/6M8DDDCgFxJ2oqvmV5YinSJ+TYZH3WUzSUqK+vmnqXhb805W4K/PtFjYxWQNRqfWUntvdvXBrbo9b0ghaCurLzXDAwjYHDzwp+lehTXXBWqUxpgwqFrD5azepwJaYCrSeuAZlDTmCRIHH5hBdstRhnD01dWoDNaNkZAQKBgQDmsahoyI+gL5KrqzKYY4vVeJ2Y1tG0yau9KelkYgD+BM6tSvMt1Uzq9q+a8bsqES38ZUb+X+IAlCivMTr5pudSU1iH05Sp0Q7ESp89/5VV3EgmDdsRcbZoyKZgh8bHCiwsS+tWv1gEL2oiW2wcOr95ro8O2s44Na7AgfpP2VkinQKBgQCm5xrQ9j4yT8/LfCxCQ9mxpRJvkgUnFm9kXuVq+2TH07mFiUwMx5MCY6n7CHF0DgCSHgqvg3sGF6eMT1NsQKCC8GMRtY8VRbJFNAlRLXAN5ekCvir7BzpOSxN3DYvebV2hAmHGl8J5F7A5OslismZlWwgsC9GhlGIkkS5HAHRb4QKBgQCZo9JUTtaQyX42RKNCqHGVr2nOQ6uQawusxQACcd7VTmBTO2pvqPI8PiS/3aRYJO7qfIzmlvcOiZ3655+uawD+bDG27CvDWU8rXcNmaBSOBVIrveibWo8whAmCmorBPr4ilkFbGb5Fs0pNLXP37QxevunZ69GRz/bdkPMjQM7DZQKBgH7kQa0RKeRSbadFNtkCgOXgnI2atLQtCG+E/mNB0jNisy/lXJ5ytUAhycADgU48vw5YLMOX8NaG8WlpfgFVeTNT095I7Qm9PXYDw/ml1AWdAHSHZGIJ8rhHt/rRn79x4rdg+jlkdARgkChSm32gKN1yQlpKegygpaUrg8WTgOPBAoGAXbEi9YREYTeZ8Ff1vhJRmZZsWgAgCk/au+sUsAjGVu/SpZH/mXY6Uggec7e4028CorxLvzndlxf2haNB//rAJreJlq0OcEC0j7PBK1sWL31S/yKy7Nn1FpcYlo592qI6eRjDJoYZnPGt6AY3A7PhMZ6Y4zLOqjJZbZTPK8P9sqI=";
    public static final String RSA2_PRIVATE = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDcNtCG7H6WzpcbsoiErWYT1fInCU3ZjJyScBFOcRBubmzITWVh1fCPiABlTLXWpbz5uzusgkS3HXub3tuZkShNVuddFZChAzARle8OJ+L3yiMCxYvk+RnISK+82q/A51R+vJH+4gEDWC2St7Yv0GK+YPmq3P3PTG2Ftqq1V2kxvN9ke51HMvErpxw1UyykJ3WuVALKfTlmxPgkF4PeOVH2d14iUlUT9xunOJJDDjBvN4hkLqxbSArRSFiwgrotMDWAJAe3MfHTeys5+aS15w6OZDP+RpHiLROoEfcEtepr86iLEqW0rHIpwu8tvRoCwgWvQXS1vP/eKiLuUk+Jj7TBAgMBAAECggEBAL8t11tLdZr3P7iMBnkpELWnx9KcATodGNEI+f1fqyQ0ObyrdJi7Y104dR98uWDgAm/MXMInVgCjV+y6TWvE2XhLOhFV53nhipm50RA5EJKOqlZ9qjXfDMMUV31dXbfbHT/p9dWR389HfkkhtMlN4GvnRNlRgge2El9DdnB4spq6Ad6YBd2MDN666pMN7GuwFTXoNCLkHZbOwXOgjmsoxjVPaSK2R++TfjJzwW1Lt3m6svtuQ6UXKK3cwtyID0MgARmsNOht9g6iimCmfTrNu0p7r24Hkpul5kGwQyt8b2c3lH1VIzIJnKrN3WTbJTassZhnUUGha9oU95G4tQeY2QECgYEA8jllSR7epFWdccKwGw8MCnG1YC83LCcSjsPKnxOxzlceENP4ayTUcHfJxrQhQxCNLwu2O0Un8eOQ08gfpi3LsePbaGZEYsM1npNeiBl99U0szlmdpBFUJ2mqzLbkVEvGtCvMP1W8tdc/sZmmzynlYJJi6Uj70u5M8sIO5ojwa58CgYEA6Lz37bB0pjNNlV0SzRNlIjYCFODaECa7hG8fGSXag3yyJVdsuj/uDaRozWzi+JNsz75CmL0GHmCXDEfxIqIq5vyMOm+VTf+/PuLbxOuNI4MzZJd0Oosxm2+LI45TDJs/sQnZsOvWVCGnxmvOQY1VslYP4xHRYoS821ZgdtLGA58CgYEAnVWstZ8ojOPTndYQ37KZZptPbceddb93bWY90bBQOTAbJmEZJjdJCji0xq2wQUiZ/CoGV19nP9ZJ91YxQJbUjZrspFZxKxmTVYpxx8OPLmvPo8VQAQcEe4lGWSyfCSYjTlp4k9cT+aV0CN8+G6giBuVu/ArdD7icIL7uQM5IJA8CgYBfBGcj/xpgkClohWsbloN/iBUpePMODQIZe1Ry6+VeMRZ58EtPbrI9l5XB6GZxnZ6a4c5GlUVeOYjxVp23ygVB8HE/mjaWdYcaoXuf1Zr1mKbutnaZQCKslslzZrO5Pdiu6pqZhG8oQeVBONooIQ+Pk1tBFREq644+7fDMEhkNeQKBgQDw53zRR9gsMXW3/IRsnYcweTPOBDpulC+FzAktAoGJsw/iN5OCcjl3KT42jdisBULgcjcYYUa4KBEn4SWluoGBDeHhLK49yZ5eTAdXINxO5MSYuURH/UnaHqH9phwwfr/Srv+bYZQuriVUbuyBWslh8uzmMbDIclXlpstTCRguIg==";
    public static final String RSA_PRIVATE = "";

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler()
    {
        @Override
        @SuppressWarnings("unused")
        public void handleMessage(Message msg)
        {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    // 同步返回需要验证的信息
                    String resultInfo = payResult.getResult();
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        Toast.makeText(RechargeAliActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(RechargeAliActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        //用于alipay沙箱支付测试
        EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_ali);

        //动态获取权限
        requestPermission();

        SimpleToolBar toolBar = findViewById(R.id.top_menu);
        setSupportActionBar(toolBar);
        toolBar.setBackIconClickListener(new SimpleToolBar.BackIconClickListener() {
            @Override
            public void OnClick() {
                finish();
            }
        });

        Button bnHistory = findViewById(R.id.bn_history);
        bnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payV2(view);
//                Intent intent = new Intent(RechargeAliActivity.this,HistoryActivity.class);
//                startActivity(intent);
            }
        });


        //测试服务器数据库INSERT功能
        String func_name = "INSERT";
        String insert_name = "test3";
        String insert_pw = "vfbgnhmj";
        String insert_info = "this is a insert test message";
        //sendInsertRequest(func_name,insert_name,insert_pw,insert_info);

        String request_name = "test2";
        String request_func = "SELECT_ALL";
//        sendRequest(request_func,request_name);

    }

    /**
     * Alipay task
     */
    public void payV2(View v)
    {
        /**
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以此处加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * orderInfo 的获取必须来自服务端；
         */

        sendOrderRequest("supersmashbros","99.99");
    }

    private void payProcess(final String ori_OrderInfo)
    {
//本地生成orderinfo
/*
        boolean rsa2 = (RSA2_PRIVATE.length() > 0);
        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(APPID, rsa2);
        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);
        String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
        String sign = OrderInfoUtil2_0.getSign(params, privateKey, rsa2);
        final String orderInfo1 = orderParam + "&" + sign;
        Log.i("orderInfo", ori_OrderInfo);//打印一下看看对不对
*/

        Runnable payRunnable = new Runnable()
        {
            @Override
            public void run() {
                try {
                    PayTask alipay = new PayTask(RechargeAliActivity.this);
                    Map<String, String> result = alipay.payV2(ori_OrderInfo, true);


                    Log.i("msp", result.toString());

                    Message msg = new Message();
                    msg.what = SDK_PAY_FLAG;
                    msg.obj = result;
                    mHandler.sendMessage(msg);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }
    /**
     * 获取权限使用的 RequestCode
     */
    private static final int PERMISSIONS_REQUEST_CODE = 1002;

    /**
     * 检查支付宝 SDK 所需的权限，并在必要的时候动态获取。
     * 在 targetSDK = 23 以上，READ_PHONE_STATE 和 WRITE_EXTERNAL_STORAGE 权限需要应用在运行时获取。
     * 如果接入支付宝 SDK 的应用 targetSdk 在 23 以下，可以省略这个步骤。
     */
    private void requestPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, PERMISSIONS_REQUEST_CODE);

        } else {
            showToast(this, "支付宝 SDK 已有所需的权限");
        }
    }

    /**
     * 权限获取回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {

                // 用户取消了权限弹窗
                if (grantResults.length == 0) {
                    showToast(this, "无法获取支付宝 SDK 所需的权限, 请到系统设置开启");
                    return;
                }

                // 用户拒绝了某些权限
                for (int x : grantResults) {
                    if (x == PackageManager.PERMISSION_DENIED) {
                        showToast(this, "无法获取支付宝 SDK 所需的权限, 请到系统设置开启");
                        return;
                    }
                }

                // 所需的权限均正常获取
                showToast(this, "支付宝 SDK 所需的权限已经正常获取");
            }
        }
    }

    private static void showToast(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
    }
    void sendInsertRequest(final String func_name, String insert_name, String insert_pw, String insert_info)
    {
        RequestBody requestBody = new FormBody.Builder()
                .add("func_name",func_name)
                .add("username",insert_name)
                .add("password",insert_pw)
                .add("info",insert_info)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(RechargeAliActivity.this, "fail to connect to sever", Toast.LENGTH_SHORT).show();
                    }
                });
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();
                double a = 0;
            }
        });
    }

    void sendRequest(final String request_func, String request_name)
    {
        RequestBody requestBody = new FormBody.Builder()
                .add("func_name",request_func)
                .add("username",request_name)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(RechargeAliActivity.this, "fail to connect to sever", Toast.LENGTH_SHORT).show();
                    }
                });
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();
                JsonObject jsonObject = new JsonParser().parse(str).getAsJsonObject();
                JsonArray jsonArray = jsonObject.getAsJsonArray("result");
                List<User> userList = new ArrayList<>();
                for (JsonElement user : jsonArray) {
                    User userBean = new Gson().fromJson(user, new TypeToken<User>(){}.getType());
                    userList.add(userBean);
                }

                for (User user : userList) {
                    Log.i("tag", "id : " + user.getId());
                    Log.i("tag", "username : " + user.getUsername());
                    Log.i("tag", "password : " + user.getPassword());
                    Log.i("tag", "info : " + user.getInfo());
                    Log.i("tag", "addtime : " + user.getAdd_time());
                    Log.i("tag", "————————————————————");
                }
            }
        });
    }

    void sendOrderRequest(final String request_uid, String request_price)
    {
        RequestBody requestBody = new FormBody.Builder()
                .add("uid",request_uid)
                .add("price",request_price)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(ORDER_REQUEST_URL)
                .post(requestBody)
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(RechargeAliActivity.this, "fail to connect to sever", Toast.LENGTH_SHORT).show();
                    }
                });
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();
                payProcess(str);
//                JsonObject jsonObject = new JsonParser().parse(str).getAsJsonObject();
//                JsonArray jsonArray = jsonObject.getAsJsonArray("result");
//                List<User> userList = new ArrayList<>();
//                for (JsonElement user : jsonArray) {
//                    User userBean = new Gson().fromJson(user, new TypeToken<User>(){}.getType());
//                    userList.add(userBean);
//                }
//
//                for (User user : userList) {
//                    Log.i("tag", "id : " + user.getId());
//                    Log.i("tag", "username : " + user.getUsername());
//                    Log.i("tag", "password : " + user.getPassword());
//                    Log.i("tag", "info : " + user.getInfo());
//                    Log.i("tag", "addtime : " + user.getAdd_time());
//                    Log.i("tag", "————————————————————");
//                }
            }
        });
    }
}
