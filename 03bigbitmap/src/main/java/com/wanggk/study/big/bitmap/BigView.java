package com.wanggk.study.big.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;

/**
 * 自定义加载大图片的视图
 *
 * @author dengyu.wang
 * @since 2023-05-12
 */
public class BigView extends View {
    // 滑动帮助
    private Scroller mScroller;

    // 指定要加载的区域,其实就是一块内存区域
    private Rect mRect;

    // bitmap 信息改变配置，需要复用
    private BitmapFactory.Options mOptions;

    // 手势识别类
    private GestureDetector mGestureDetector;

    // 图片的原始宽和高值
    private int mImageWidth;
    private int mImageHeight;

    // Bitmap图片局部解码器
    private BitmapRegionDecoder mBitmapRegionDecoder;

    // 获取本视图测量之后的宽和高的值
    private int mViewWidth;
    private int mViewHeight;

    // 截取部分图片到内存的系数
    private int mScale;

    // 可复用的bitmap的内存块
    private Bitmap mReusableBitmap;

    public BigView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        mScroller = new Scroller(context);
        mRect = new Rect();
        mOptions = new BitmapFactory.Options();

        mGestureDetector = new GestureDetector(context, new GestureDetector.OnGestureListener() {
            /**
             * 当手指按下的时候触发
             * @param e 触摸事件
             */
            @Override
            public boolean onDown(MotionEvent e) {
                // 手指按下的时候如果滑动还没有停止，那么先强行停止滑动
                if (!mScroller.isFinished()) {
                    mScroller.forceFinished(true);
                }

                // 返回true是继续接收后续事件
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            /**
             * 手指滑动的事件处理，其实就是刷新要加载进入mRect区域的图片的部分内容
             *
             * @param e1 手指按下的时候的触摸事件
             * @param e2 手指按下之后滑动手指的触摸事件
             * @param distanceX 在X轴方向上移动距离
             * @param distanceY 在Y轴方向上的移动距离
             */
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                // 要刷新进区域的图片大小，在X轴上不需要偏移，因为mRect区域的宽度存储的是图片的宽度，高度是按照缩放系数加载了一部分图片的高度
                // 所以在滑动的时候，只要将滑动的偏移距离刷新进内存就可以了，相当于是淘汰一部分不显示的内存，重装一部分显示的内存。
                mRect.offset(0, (int) distanceY);

                // 如果说上下滑动到底部了，那么就不让再往下滑动了
                if (mRect.bottom > mImageHeight) {
                    mRect.bottom = mImageHeight;

                    // 底部到底之后，顶部的坐标就是图片的高度减去视图的高度，但是因为mRect存储的是未缩放之前的数据，
                    // 所以需要将视图的高度按照缩放系数放到或者缩小高度。
                    mRect.top = mImageHeight - mViewHeight / mScale;
                }

                // 如果说上滑到顶部
                if (mRect.top < 0) {
                    mRect.top = 0;

                    // 上滑到顶部之后，内存区域的底部坐标就是视图的高度，由于mRect存储的是未缩放的值，所以需要等比例放大高度
                    mRect.bottom = mViewHeight / mScale;
                }

                // 当区域的内容调整之后，刷新界面显示
                invalidate();
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            /**
             * 处理滑动惯性问题
             * @param e1
             * @param e2
             * @param velocityX
             * @param velocityY
             * @return
             */
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                // 此处仅仅是通过滑动工具mScroller做计算，计算应该再惯性滑动多少。将结果交给接口
                mScroller.fling(0, mRect.top, 0, (int) -velocityY, 0, 0, 0, mImageHeight - mViewHeight / mScale);
                return false;
            }
        });
    }

    /**
     * mScroller通过fling计算的结果，交由此接口进行处理
     */
    @Override
    public void computeScroll() {
        if (mScroller.isFinished()) {
            return;
        }

        if (mScroller.computeScrollOffset()) {
            mRect.top = mScroller.getCurrY();
            mRect.bottom = mRect.top + mViewHeight / mScale;

            // 重绘
            invalidate();
        }
    }

    /**
     * 使用者设置大图片
     *
     * @param is 图片输入流
     */
    public void setImage(InputStream is) {
        // 先获取图片的信息，宽和高等保存到BitmapFactory.Options中
        mOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, mOptions);

        // 从信息中读取图片的宽和高
        mImageWidth = mOptions.outWidth;
        mImageHeight = mOptions.outHeight;

        // 设置复用功能
        mOptions.inMutable = true;

        // 设置Config的格式,设置压缩
        mOptions.inPreferredConfig = Bitmap.Config.RGB_565;

        mOptions.inJustDecodeBounds = false;

        // 局部解码器初始化
        try {
            mBitmapRegionDecoder = BitmapRegionDecoder.newInstance(is, false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        requestLayout();
    }

    /**
     * 在测量的时候，将图片的一部分内容加载到mRect中，然后对这一部分内容进行缩放显示
     *
     * @param widthMeasureSpec  父类Group给出的宽度限制
     * @param heightMeasureSpec 父类Group给出的高度限制
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 获取的量的本View在父类的宽和高的值
        mViewWidth = getMeasuredWidth();
        mViewHeight = getMeasuredHeight();

        // 将要显示的部分图片内存信息填装到mRect中
        mRect.left = 0;
        mRect.top = 0;
        mRect.right = mImageWidth;
        // mRect底部坐标高度是图片高度根据系数截取了一部分，需要根据图片的宽度和测量出
        // 的本View的宽度比例（也可以自定义系数，只要宽度和高度取自相同的系数即可）来截取部分高度
        mScale = mViewWidth / mImageWidth;

        mRect.bottom = mImageHeight / mScale;
    }

    /**
     * 将加载到内存的Bitmap的部分区域绘制出来
     *
     * @param canvas 画布
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 如果此局部图片解码器为空，表示没有设置过图片，也就不需要处理
        if (mBitmapRegionDecoder == null) {
            return;
        }

        // 复用上一张bitmap，避免内存重复开辟出现内存抖动的问题
        mOptions.inBitmap = mReusableBitmap;

        // 使用解码器将指定区域的内存加载到公用内存块中
        mReusableBitmap = mBitmapRegionDecoder.decodeRegion(mRect, mOptions);

        // 将得到的区域的宽和高按照相同的比例系数进行等比缩放(按照对角线缩放)
        Matrix matrix = new Matrix();
        matrix.setScale(mScale, mScale);

        // 将缩放完成的区域绘制到本视图上
        canvas.drawBitmap(mReusableBitmap, matrix, null);
    }

    /**
     * 为了能够上下的移动，需要处理触摸事件, 将触摸事件传递给手势识别器
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }
}
