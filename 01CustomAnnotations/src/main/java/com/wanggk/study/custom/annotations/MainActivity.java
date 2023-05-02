package com.wanggk.study.custom.annotations;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Season().setSeason(Season.AUTUMN);
        Log.d(TAG, new Season().getSeason());

        StudentPool studentPool = new StudentPool(2, 4);
        Student student = studentPool.getObject();
        student.setName("老刘");
        student.setAge(33);

        Student student1 = studentPool.getObject();
        student1.setName("老张");
        student1.setAge(35);

        Student student2 = studentPool.getObject();
        student2.setName("老李");
        student2.setAge(40);

        Student student3 = studentPool.getObject();
        student3.setName("老王");
        student3.setAge(50);

        studentPool.releaseObject(student);
    }
}