package com.example.classroom_location;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

@SuppressLint("Registered")
public class Login extends AppCompatActivity implements View.OnClickListener,
        View.OnFocusChangeListener {

    private CheckBox RememberPass;
    private EditText account_edit;
    private EditText password_edit;
    private SharedPreferences preferences;

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
        if (isRemember){
           init();
        }
        logIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String mAccount = "2016010599";
        String mPassword = "123456";
        switch (v.getId()){
            case R.id.Login_btn:
                String account = account_edit.getText().toString();
                String password = password_edit.getText().toString();
                if ( account.equals(mAccount) && password.equals(mPassword) ){
                    SharedPreferences.Editor editor = preferences.edit();
                    if ( RememberPass.isChecked() ){    //检查复选框是否被选中
                        editor.putBoolean("remember_password", true);
                        editor.putString("account", account);
                        editor.putString("password", password);
                    }
                    else {
                        editor.clear();
                    }
                    editor.apply();
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(Login.this, "账号或密码错误",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }

    }

    private void init(){
        //  将账号和密码都设置到文本框中
        String account = preferences.getString("account", "");
        String password = preferences.getString("password", "");
        account_edit.setText(account);
        password_edit.setText(password);
        RememberPass.setChecked(true);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()){
            case R.id.account_edit:
                if (hasFocus){
                    account_edit.setHintTextColor(Color.parseColor("#FFFFFF"));
                }
                else {
                    account_edit.setHintTextColor(Color.parseColor("#515151"));
                }
                break;
            case R.id.password_edit:

                break;
            default:
                break;
        }
    }
}