package com.example.classroom_location;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

@SuppressLint("Registered")
public class Login extends AppCompatActivity implements View.OnClickListener{

    private CheckBox RememberPass;
    private EditText account_edit;
    private EditText password_edit;
    private SharedPreferences preferences;
//    private final static String mAccount = "2016010599";
//    private final static String mPassword = "123456";
    private final static String TAG = "LoginActivity";
    String account;
    String password;

    //    private final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        RememberPass = findViewById(R.id.remember_password);
        account_edit = findViewById(R.id.account_edit);
        password_edit = findViewById(R.id.password_edit);
        Button logIn = findViewById(R.id.Login_btn);

        boolean isRemember = preferences.getBoolean("remember_password", false);
        boolean autoLogin = preferences.getBoolean("isAuto", false);
        if (isRemember){
            if (autoLogin){
                AutoLogin();
            }
            init();
        }
        logIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.Login_btn) {
            account = account_edit.getText().toString();
            password = password_edit.getText().toString();
            HttpUtil.sendOkHttpRequest("http://192.168.0.101:8080/test1_war_exploded/LoginServlet?" +
                    "account=" + account +
                    "&password=" + password, new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Looper.prepare();
                    Toast.makeText(Login.this, "访问网络失败，请重试",
                            Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    Looper.loop();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    assert response.body() != null;
                    if (response.body().string().equals("success")){
                        Looper.prepare();
                        Toast.makeText(Login.this, "登录成功",
                                Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = preferences.edit();
                        if (RememberPass.isChecked()) {    //检查复选框是否被选中
                            editor.putBoolean("remember_password", true);
                            editor.putString("account", account);
                            editor.putString("password", password);
                            editor.putBoolean("isAuto", true);
                        } else {
                            editor.clear();
                        }
                        editor.apply();
                        Intent intent = new Intent(Login.this, MainActivity.class);
                        startActivity(intent);
                        Looper.loop();
                    } else if (response.body().string().equals("fail")){
                        Toast.makeText(Login.this, "账号或密码错误",
                                Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }

                }
            });
//            if (account.equals(mAccount) && password.equals(mPassword)) {
//                SharedPreferences.Editor editor = preferences.edit();
//                if (RememberPass.isChecked()) {    //检查复选框是否被选中
//                    editor.putBoolean("remember_password", true);
//                    editor.putString("account", account);
//                    editor.putString("password", password);
//                    editor.putBoolean("isAuto", true);
//                } else {
//                    editor.clear();
//                }
//                editor.apply();
//                Intent intent = new Intent(Login.this, MainActivity.class);
//                startActivity(intent);
//            } else {
//                Toast.makeText(Login.this, "账号或密码错误",
//                        Toast.LENGTH_SHORT).show();
//            }
        }
    }

    private void AutoLogin(){
        String account = preferences.getString("account", "");
        String password = preferences.getString("password", "");
        HttpUtil.sendOkHttpRequest("http://192.168.0.101:8080/test1_war_exploded/LoginServlet?" +
                "account=" + account +
                "&password=" + password, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Looper.prepare();
                Toast.makeText(Login.this, "访问网络失败，请重试",
                        Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                assert response.body() != null;
                if (response.body().string().equals("success")){
                    Log.d(TAG, "onResponse: 自动登录成功");
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    startActivity(intent);
                } else if (response.body().string().equals("fail")){
                    Looper.prepare();
                    Toast.makeText(Login.this, "账号或密码错误",
                            Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        });
    }

    private void init(){
        //  将账号和密码都设置到文本框中
        String account = preferences.getString("account", "");
        String password = preferences.getString("password", "");
        account_edit.setText(account);
        password_edit.setText(password);
        RememberPass.setChecked(true);
    }

}
