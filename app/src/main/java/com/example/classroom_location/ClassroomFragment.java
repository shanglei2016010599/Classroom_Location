package com.example.classroom_location;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Response;

public class ClassroomFragment extends Fragment {

//    private Student[] students = {
//            new Student("第一张图", R.drawable.image1),
//            new Student("第二张图", R.drawable.image2),
//            new Student("第三张图", R.drawable.image3),
//            new Student("第四张图", R.drawable.image4),
//            new Student("第五张图", R.drawable.image5),
//            new Student("第六张图", R.drawable.image6),
//    };

    private int[] ImageId = {
            R.drawable.image1, R.drawable.image2, R.drawable.image3,
            R.drawable.image4, R.drawable.image5, R.drawable.image6,
    };

    private List<Student> studentList = new ArrayList<>();

    private StudentAdapter adapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    private final static String TAG = "ClassroomFragment";

    private View view;

    private int screenWidth;

    private int screenHeight;

    private List<User> users = new ArrayList<>();
    private String responseData;
    private Handler handler; // 定义一个android.os.Handler对象

    public static final int INIT = 1;

    @SuppressLint({"ResourceAsColor", "HandlerLeak"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.classroom_fragment, container, false);

        initStudents();
        getAndroidScreenProperty();
        final RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setMinimumWidth(screenWidth / 8);
        recyclerView.setMinimumHeight(screenHeight / 6);
//        ImageView imageView = view.findViewById(R.id.student_image);
//        imageView.setMaxWidth(screenWidth / 8);
//        imageView.setMaxHeight(screenHeight / 6);

        GridLayoutManager layoutManager = new GridLayoutManager(view.getContext(), 8);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new StudentAdapter(studentList);
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeColors(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshStudents();
            }
        });

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == INIT) {
                    /* 解析得到的数据，并将已签到的学生数据存储 */
                    parseJSONWithGSON(responseData);
                    /* 在这里进行UI修改，将已经签到的学生显示 */
                    updateStudentList();
                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);
                }
            }
        };

        Init();

        return view;
    }

    /* 初始化方法 */
    public void initStudents(){
        studentList.clear();
        for ( int i = 0; i < 48; i++ ){
            Random random = new Random();
            int index = random.nextInt(6);
            int row, col;
            if ( (i + 1) % 8 != 0){
                row = (i + 1) / 8 + 1;
                col = (i + 1) % 8;
            } else {
                row = (i + 1) / 8;
                col = 8;
            }
            Student student = new Student("第" + (i + 1) + "个同学", ImageId[index], row, col);
            studentList.add(student);
        }
    }

    /* 下拉刷新 */
    private void refreshStudents(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                /* 需要通过获取活动，才能成功使用runOnUiThread()方法 */
                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        Init();
                        updateStudentList();
                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    /* 获取当前屏幕的宽度、高度 */
    public void getAndroidScreenProperty(){
        WindowManager wm = (WindowManager) view.getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;// 屏幕宽度（像素）
        int height= dm.heightPixels; // 屏幕高度（像素）
        float density = dm.density;//屏幕密度（0.75 / 1.0 / 1.5）
//        int densityDpi = dm.densityDpi;//屏幕密度dpi（120 / 160 / 240）
        //屏幕宽度算法:屏幕宽度（像素）/屏幕密度
        screenWidth = (int) (width/density);//屏幕宽度(dp)
        screenHeight = (int)(height/density);//屏幕高度(dp)
        Log.e(TAG, screenWidth + "======" + screenHeight);
    }

    /* 获取所有学生的信息，以JSON数据格式存储 */
    private void Init(){
        String url = "http://192.168.0.102:8080/test1_war_exploded/InitServlet";
        HttpUtil.sendOkHttpRequest(url, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Looper.prepare();
                Toast.makeText(view.getContext(), "访问网络失败，请重试",
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

    /* 使用GSON解析获取到的JSON数据 */
    private void parseJSONWithGSON(String jsonData){
        Gson gson = new Gson();
        List<User> userList = gson.fromJson(jsonData, new TypeToken<List<User>>(){}.getType());
        Log.d(TAG, "parseJSONWithGSON: " + userList);
        for (User user : userList){
            Log.d(TAG, "parseJSONWithGSON: " + user.getName());
            if (user.getStatus().equals("1")){
                users.add(user);
            }
        }
        Log.d(TAG, "parseJSONWithGSON: " + users);
    }

    /* 将已签到的学生列表存储到studentList中 */
    private void updateStudentList(){
        for (User user : users){
            String[] location = user.getLocation().split("-");
            int row = Integer.parseInt(location[0]);
            int col = Integer.parseInt(location[1]);
            Log.d(TAG, "updateStudentList: row: " + row + " col: " + col);
            int index = (row - 1) * 8 + col;
            Log.d(TAG, "updateStudentList: index: " + index);
            Student student = new Student(user.getName(), row, col, user.getUrl(), user.getMessage(),
                    user.getStatus());
            studentList.set(index - 1, student);
            Log.d(TAG, "updateStudentList: url: " + studentList.get(index - 1).getUrl());
        }
    }

}
