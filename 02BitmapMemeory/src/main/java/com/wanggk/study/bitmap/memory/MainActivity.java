package com.wanggk.study.bitmap.memory;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.mipmap.wyz_p);
        printBitmapInfo(bitmap);

        Bitmap bitmap1 = ImageResize.resizeImage(this.getApplicationContext(), R.mipmap.wyz_p, 497, 611, false);
        printBitmapInfo(bitmap1);
    }

    private void printBitmapInfo(Bitmap bitmap) {
        StringBuilder sb = new StringBuilder("图片: ").append(bitmap.getWidth())
                .append(" X ").append(bitmap.getHeight())
                .append(", 内存大小: ").append(bitmap.getByteCount());
        Log.d(TAG, sb.toString());
    }
}