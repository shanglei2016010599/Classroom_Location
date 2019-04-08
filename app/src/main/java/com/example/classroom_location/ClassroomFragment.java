package com.example.classroom_location;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.classroom_fragment, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(view.getContext(), 4);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new StudentAdapter(studentList);
        recyclerView.setAdapter(adapter);
        return view;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStudents();

    }

    public void initStudents(){
        studentList.clear();
        for ( int i = 0; i < 50; i++ ){
            Random random = new Random();
            int index = random.nextInt(students.length);
            studentList.add(students[index]);
        }
    }

}
