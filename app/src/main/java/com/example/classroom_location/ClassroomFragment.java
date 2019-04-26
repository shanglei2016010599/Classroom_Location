package com.example.classroom_location;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

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

    @SuppressLint("ResourceAsColor")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.classroom_fragment, container, false);

        initStudents();
        getAndroiodScreenProperty();
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
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
                        initStudents();
                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    /* 获取当前屏幕的宽度、高度 */
    public void getAndroiodScreenProperty(){
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

}
