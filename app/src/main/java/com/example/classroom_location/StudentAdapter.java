package com.example.classroom_location;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {

    private Context mContext;

    private List<Student> mStudentList;

    class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        ImageView studentImage;
        TextView studentName;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            studentImage = itemView.findViewById(R.id.student_image);
            studentName = itemView.findViewById(R.id.student_name);
        }
    }

    StudentAdapter(List<Student> studentList){
        mStudentList = studentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        if ( mContext == null ){
            mContext = viewGroup.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.student_item, viewGroup,
                false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Student student = mStudentList.get(position);
                Intent intent = new Intent(mContext, StudentActivity.class);
                intent.putExtra(StudentActivity.STUDENT_NAME, student.getName());
                intent.putExtra(StudentActivity.STUDENT_IMAGE_ID, student.getImageId());
                intent.putExtra(StudentActivity.STUDENT_IMAGE_URL, student.getUrl());
                intent.putExtra(StudentActivity.STUDENT_MESSAGE, student.getMessage());
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull StudentAdapter.ViewHolder viewHolder, int i) {
        Student student = mStudentList.get(i);
        if (student.getStatus().equals("1")){
            viewHolder.studentName.setText(student.getName());
        } else {
            viewHolder.studentName.setText("  " + student.getRow() + "排" + student.getCol() + "号  ");
        }
        if (student.getImageId() == -1){
            Glide.with(mContext)
                    .load(student.getUrl())
                    .signature(new ObjectKey(System.currentTimeMillis()))
                    .into(viewHolder.studentImage);
        } else {
            Glide.with(mContext).load(student.getImageId()).into(viewHolder.studentImage);
        }
    }

    @Override
    public int getItemCount() {
        return mStudentList.size();
    }
}
