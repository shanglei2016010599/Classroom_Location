package com.example.classroom_location;

import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;


public class StudentActivity extends AppCompatActivity {

    public static final String STUDENT_NAME = "student_name";

    public static final String STUDENT_IMAGE_ID = "student_image_id";

    public static final String STUDENT_IMAGE_URL = "student_image_url";

    public static final String STUDENT_MESSAGE = "student_message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        Intent intent = getIntent();
        String studentName = intent.getStringExtra(STUDENT_NAME);
        int studentImageId = intent.getIntExtra(STUDENT_IMAGE_ID, 0);
        String studentImageUrl = intent.getStringExtra(STUDENT_IMAGE_URL);
        String studentMessage = intent.getStringExtra(STUDENT_MESSAGE);
        Toolbar toolbar = findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        ImageView studentImageView = findViewById(R.id.student_image_view);
        TextView studentContentText = findViewById(R.id.student_content_text);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if ( actionBar != null ){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        collapsingToolbarLayout.setTitle(studentName);
        if (studentImageUrl != null){
            Glide.with(this).load(studentImageUrl).into(studentImageView);
        } else {
            Glide.with(this).load(studentImageId).into(studentImageView);
        }
        if (studentMessage != null){
            studentContentText.setText(studentMessage);
        } else {
            String studentContent = generateStudentContent(studentName);
            studentContentText.setText(studentContent);
        }
    }

    private String generateStudentContent(String studentName){
        StringBuilder studentContent = new StringBuilder();
        for ( int i = 0; i < 5; i++ ){
            studentContent.append(studentName);
        }
        return studentContent.toString();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
