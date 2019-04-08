package com.example.classroom_location;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {

    public Context mContext;

    private List<Student> mStudentList;

    public class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        ImageView studentImage;
        TextView studentName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            studentImage = itemView.findViewById(R.id.student_image);
            studentName = itemView.findViewById(R.id.student_name);
        }
    }

    public StudentAdapter(List<Student> studentList){
        mStudentList = studentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if ( mContext == null ){
            mContext = viewGroup.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.student_item, viewGroup,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentAdapter.ViewHolder viewHolder, int i) {
        Student student = mStudentList.get(i);
        viewHolder.studentName.setText(student.getName());
        Glide.with(mContext).load(student.getImageId()).into(viewHolder.studentImage);
    }

    @Override
    public int getItemCount() {
        return mStudentList.size();
    }
}
