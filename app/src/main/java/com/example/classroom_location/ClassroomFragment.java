package com.example.classroom_location;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class ClassroomFragment extends Fragment {

    private Student[] students = {
            new Student("第一张图", R.drawable.image1),
            new Student("第二张图", R.drawable.image2),
            new Student("第三张图", R.drawable.image3),
            new Student("第四张图", R.drawable.image4),
            new Student("第五张图", R.drawable.image5),
            new Student("第六张图", R.drawable.image6),
    };

    private List<Student> studentList = new ArrayList<>();

    private StudentAdapter adapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    @SuppressLint("ResourceAsColor")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.classroom_fragment, container, false);

        initStudents();
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
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
            int index = random.nextInt(students.length);
            studentList.add(students[index]);
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

}
