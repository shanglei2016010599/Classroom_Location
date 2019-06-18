package com.example.classroom_location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity{

    private DrawerLayout mDrawerLayout;
    private Dialog dialog;

    private String account = "";
    private String type = "";

    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    private CircleImageView icon_image;
    private Uri imageUri;

    private SharedPreferences preferences;
    private String responseData;
    private static final int INIT = 0;
    private static final int CHECK_IN = 3;
    private static final int CHECK_IN_OK = 4;
    private static final int CHANGE_ICON_OK = 5;
    private static final int END_CLASS_OK = 6;
    private static final String TAG = "MainActivity";
    private Handler handler; // 定义一个android.os.Handler对象
    private static User user;

    private String location = "";

    /* 碎片切换 */
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_classroom:
                    replaceFragment(new ClassroomFragment());
                    return true;
                case R.id.navigation_forum:
                    replaceFragment(new ForumFragment());
                    return true;
            }
            return false;
        }
    };

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.inflateMenu(R.menu.nav_menu);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        account = preferences.getString("account", "");

        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        Log.d(TAG, "onCreate types is " + type);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case INIT:
                        try {
                            parseJSON(responseData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case CHANGE_ICON_OK:
                        InitDrawer();
                        Toast.makeText(MainActivity.this, "更换头像成功",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case END_CLASS_OK:
                        Toast.makeText(MainActivity.this, "下课成功",
                                Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }

            }
        };

        InitDrawer();

        ActionBar actionBar = getSupportActionBar();
        if ( actionBar != null ){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        /* 左侧菜单栏按钮点击事件 */
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch ( menuItem.getItemId() ) {
                    case R.id.nav_call:
                        Toast.makeText(MainActivity.this, "You clicked call",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_friends:
                        Toast.makeText(MainActivity.this, "You clicked friends",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_location:
                        Toast.makeText(MainActivity.this, "You clicked location",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_mail:
                        Toast.makeText(MainActivity.this, "You clicked mail",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_task:
                        Toast.makeText(MainActivity.this, "You clicked task",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_quit:
                        SharedPreferences.Editor editor =
                                getSharedPreferences("com.example.classroom_location_preferences",
                                        MODE_PRIVATE).edit();
                        editor.putBoolean("isAuto", false);
                        editor.apply();
                        Intent intent = new Intent(MainActivity.this, Login.class);
                        startActivity(intent);
                        break;
                    default:
                        mDrawerLayout.closeDrawers();
                        break;
                }
                return true;
            }
        });

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        replaceFragment(new ClassroomFragment());

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                /* 动态修改用户信息 */
                TextView Username = findViewById(R.id.username);
                TextView Account = findViewById(R.id.account);
                icon_image = findViewById(R.id.icon_image);
                icon_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ChangeHeadIcon();
                    }
                });
                Username.setText(user.getName());
                Account.setText(user.getAccount());
                /* 利用Glide加载图片 */
                if (user.getUrl() != null){
//                    Glide.with(MainActivity.this).clear(icon_image);

                    Glide.with(MainActivity.this)
                            .load(user.getUrl())
                            .signature(new ObjectKey(System.currentTimeMillis()))
                            .into(icon_image);
                    Log.d(TAG, "onDrawerOpened: " + user.getUrl());
//                    Glide.with(MainActivity.this).load(imageurl).into(icon_image);
                } else {
                    Glide.with(MainActivity.this).load(R.drawable.nav_head).into(icon_image);
                    Log.d(TAG, "onDrawerOpened: error");
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerLayout.addDrawerListener(drawerToggle);
    }

    /* 动态添加碎片 */
    /*
        创建待添加的碎片实例
        获取FragmentManager，在获得中可以直接通过调用getSupportFragmentManager()方法得到
        开启一个事务，通过调用beginTransaction()方法开启
        向容器内添加或替换碎片，一般使用replace()方法实现，需要传入容器的id和待添加的碎片实例
        提交事务，调用commit()方法来完成。
     */
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment, fragment);
        transaction.commit();
    }

    public boolean onCreateOptionsMenu(Menu menu){
        if (type.equals("student"))
            getMenuInflater().inflate(R.menu.toolbar_student, menu);
        else if (type.equals("teacher")){
            getMenuInflater().inflate(R.menu.toolbar_teacher, menu);
        }
        return true;
    }

    /* toolbar按钮的点击事件 */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            /* 打开左侧菜单栏 */
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            /* 打开相机，扫描二维码签到，学生端 */
            case R.id.QRCode:
                Intent intent = new Intent(MainActivity.this,
                        QRScannerActivity.class);
                intent.putExtra("name", user.getName());
                intent.putExtra("account", user.getAccount());
                intent.putExtra("status", user.getStatus());
                startActivityForResult(intent, CHECK_IN);
                break;
            /* 将所有学生的签到状态置为0,即下课，教师端 */
            case R.id.END:
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("提示");
                dialog.setMessage("确定要下课吗");
                dialog.setCancelable(false);
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        HttpUtil.sendOkHttpRequest(URL.url + "EndServlet",
                                new okhttp3.Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        Looper.prepare();
                                        Toast.makeText(MainActivity.this, "访问网络失败，请重试",
                                                Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                        Looper.loop();
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        assert response.body() != null;
                                        responseData = response.body().string();
                                        Log.d(TAG, "onResponse: " + responseData);
                                        Message message = new Message();
                                        message.what = END_CLASS_OK;
                                        handler.sendMessage(message);      // 发送消息
                                    }
                                });
                    }
                });
                dialog.show();
                break;
            default:
                break;
        }
        return true;
    }

    private void ChangeHeadIcon() {
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate
                (R.layout.photo_choose_dialog, null);
        Button takePhoto = view.findViewById(R.id.TakePhoto);
        Button choose = view.findViewById(R.id.Choose);
        Button cancel = view.findViewById(R.id.Cancel);
        dialog = new Dialog(this, R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        // 设置显示动画
        Objects.requireNonNull(window).setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.x = 0;
        layoutParams.y = getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        // 设置显示位置=
        dialog.onWindowAttributesChanged(layoutParams);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        /* 按钮点击事件 */
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                take();
            }
        });
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            1);
                }
                else {
                    openAlbum();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        /* 显示对话框 */
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK){
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().
                                openInputStream(imageUri));
                        icon_image.setImageBitmap(bitmap);
                        dialog.cancel();
                        up();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK){
                    if (data != null) {
                        handleImageOnKitKat(data);
                        up();
                    }
                }
                break;
            case CHECK_IN:
                if (resultCode == CHECK_IN_OK){
                    if (data != null){
                        location = Objects.requireNonNull(data.getExtras()).getString("location");
                        Log.d(TAG, "onActivityResult: " + location);
                    }
                }
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openAlbum();
            } else {
                Toast.makeText(this, "您拒绝了请求",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void take(){
        /* 创建File对象，用于存储拍照后的图片 */
        File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
        try {
            if (outputImage.exists()){
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e){
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 24){
            imageUri = FileProvider.getUriForFile(MainActivity.this,
                    "com.example.classroom_location.fileprovider", outputImage);
        }
        else {
            imageUri = Uri.fromFile(outputImage);
        }
        /* 启动相机程序 */
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    private void openAlbum(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);   //  打开相册
    }

    private void handleImageOnKitKat(Intent data){
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)){
            //  如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if (uri != null && "com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];    //  解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        selection);
            }
        }
        else if (uri != null) {
            if ("content".equalsIgnoreCase(uri.getScheme())){
                //  如果是content类型的Uri，则使用普通方式处理
                imagePath = getImagePath(uri, null);
            }
            else if ("file".equalsIgnoreCase(uri.getScheme())){
                //  如果是file类型的Uri，直接获取图片路径即可
                imagePath = uri.getPath();
            }
        }
        displayImage(imagePath);    //根据图片路径显示图片
    }

    private String getImagePath(Uri uri, String selection){
        String path = null;
        //  通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection,
                null, null);
        if (cursor != null){
            if (cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath){
        if (imagePath != null){
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            icon_image.setImageBitmap(bitmap);
            dialog.cancel();
        }
        else {
            Toast.makeText(this, "获取图片失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void InitDrawer(){
        String url = URL.url + "DrawerServlet?" +
                "account=" + account;
        HttpUtil.sendOkHttpRequest(url, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Looper.prepare();
                Toast.makeText(MainActivity.this, "访问网络失败，请重试",
                        Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                assert response.body() != null;
                responseData = response.body().string();
                Log.d(TAG, "onResponse: " + responseData);
                Message message = new Message();
                message.what = INIT;
                handler.sendMessage(message);      // 发送消息
            }
        });
    }

    /* 解析获取到的JSON数据 */
    private void parseJSON(String jsonData) throws JSONException {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "parseJSONWithGSON: " + jsonData);
        user = new User(jsonObject.getString("id"), jsonObject.getString("account"),
                jsonObject.getString("password"), jsonObject.getString("name"),
                jsonObject.getString("url"), jsonObject.getString("message"),
                jsonObject.getString("status"), jsonObject.getString("count"),
                jsonObject.getString("location"));
        Log.d(TAG, "parseJSONWithGSON: id is " + user.getId() + "\taccount is " +
                user.getAccount() + "\tpassword is " + user.getPassword() + "\tname is " +
                user.getName() + "\nurl is " + user.getUrl() + "\nmessage is " + user.getMessage() +
                "\nstatus is " + user.getStatus() + "\tcount is " + user.getCount() +
                "\tlocation is " + user.getLocation());
    }

    // 上传监听事件
    public void up() {
        Bitmap bitmap = ((BitmapDrawable) icon_image.getDrawable()).getBitmap();
        if (bitmap==null) {
            Toast.makeText(MainActivity.this, "未选择头像", Toast.LENGTH_SHORT).show();
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        assert bitmap != null;
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte [] bytes = stream.toByteArray();
        String image = Base64.encodeToString(bytes, Base64.DEFAULT);

//        Log.d(TAG, "up: length : " + new String(bytes, StandardCharsets.ISO_8859_1).length());
        Log.d(TAG, "up: " + bytes.length);
        Log.d(TAG, "up: " + image.length());
        String url = URL.url + "ImageServlet";
        HttpUtil.sendOkHttpRequestByPost(url,"image", image, "account", user.getAccount(), new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: error: " + e.toString());
                Looper.prepare();
                Toast.makeText(MainActivity.this, "访问网络失败，请重试",
                        Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "onResponse: " + response);
                assert response.body() != null;
                responseData = response.body().string();
                Message message = new Message();
                message.what = CHANGE_ICON_OK;
                handler.sendMessage(message);      // 发送消息
                Log.d(TAG, "onResponse: " + responseData);
            }
        });
    }

    public static String getUserName(){
        return user.getName();
    }

}
