package com.wanggk.study.custom.annotations;

import android.util.Log;

public class StudentPool extends ObjectPool<Student> {
    private static final String TAG = StudentPool.class.getSimpleName();

    public StudentPool(int defaultObjectCount, int maxObjectCount) {
        super(defaultObjectCount, maxObjectCount);
    }

    @Override
    public Student create() {
        return new Student();
    }

    @Override
    public void restore(Student student) {
        super.restore(student);
        Log.d(TAG, "restore student name: " + student.getName() + ", age: " + student.getAge());
    }
}
