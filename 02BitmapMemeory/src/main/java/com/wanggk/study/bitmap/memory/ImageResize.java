package com.wanggk.study.bitmap.memory;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageResize {
    public static Bitmap resizeImage(Context context, int imageId, int requestWidth, int requestHeight,boolean isNeedAlpha) {
        Resources resources = context.getResources();
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 系统处理的信息，需要拿出来更改
        options.inJustDecodeBounds = true;

        // 获取bitmap的信息，并填装到options
        BitmapFactory.decodeResource(resources, imageId, options);

        //设置缩放系数
        options.inSampleSize = getSampleSize(requestWidth, requestHeight, options.outWidth, options.outHeight);

        // 去掉alpha通道
        if (!isNeedAlpha) {
            options.inPreferredConfig = Bitmap.Config.RGB_565;
        }

        // 关闭options可更改的标志
        options.inJustDecodeBounds=false;
       return BitmapFactory.decodeResource(resources, imageId, options);
    }

    private static int getSampleSize(int requestWidth, int requestHeight, int outWidth, int outHeight) {
        int inSampleSize = 1;
        while(outHeight / inSampleSize > requestHeight && outWidth / inSampleSize > requestWidth) {
            inSampleSize *= 2;
        }

        inSampleSize /= 2;
        return inSampleSize;
    }
}
