package com.infinite.common.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;

public class BigView extends View implements GestureDetector.OnGestureListener {

    //图片显示区域
    private Rect mRect = new Rect();
    //缩放比例
    private float mScale;

    //图片尺寸
    private int mImageWidth, mImageHeight;

    private GestureDetector mGestureDetector;

    private Bitmap mBitmap;

    private Paint mPaint = new Paint();

    //图片宽高比
    private float ratio;


    private BitmapFactory.Options options;

    private BitmapRegionDecoder decoder;

    private final Matrix matrix = new Matrix();

    public BigView(Context context) {
        super(context);
    }

    public BigView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BigView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        mGestureDetector = new GestureDetector(getContext(), this);
        options = new BitmapFactory.Options();
    }

    public void setImageStream(InputStream is) {

        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, options);

        options.inMutable = true;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inJustDecodeBounds = false;
        mImageWidth = options.outWidth;
        mImageHeight = options.outHeight;

        ratio = (float) mImageWidth / mImageHeight;

        try {
            decoder = BitmapRegionDecoder.newInstance(is, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        requestLayout();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mRect.left = 0;
        mRect.top = 0;
//        mRect.bottom = getMeasuredHeight();
//        mRect.right = getMeasuredWidth();

        if (ratio > 1) {
            mScale = getMeasuredHeight() / (float) mImageHeight;

            mRect.bottom = mImageHeight;
            mRect.right = (int) (mImageWidth / mScale);
        } else {
            mScale = getMeasuredWidth() / (float) mImageWidth;
            mRect.right = mImageWidth;
            mRect.bottom = (int) (mRect.right * mScale);
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        options.inBitmap = mBitmap;
        mBitmap = decoder.decodeRegion(mRect, options);
        if (mBitmap != null) {
            matrix.setScale(mScale, mScale);
            canvas.drawBitmap(mBitmap, matrix, null);
        }
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float dx, float dy) {

        mRect.offset((int) dx, (int) dy);

        checkRect(mRect);
//        mRect.left += dx;
//        if (mRect.left < 0) {
//            mRect.left = 0;
//        }
//        if (mRect.left > mImageWidth - getMeasuredWidth()) {
//            mRect.left = mImageWidth - getMeasuredWidth();
//        }
//        mRect.right = (int) ((mRect.left + getMeasuredWidth()) / mScale);
//
//        mRect.top += dy;
//        if (mRect.top < 0) {
//            mRect.top = 0;
//        }
//        if (mRect.top > mImageHeight - getMeasuredHeight()) {
//            mRect.top = mImageHeight - getMeasuredHeight();
//        }
//        mRect.bottom = (int) ((mRect.top + getMeasuredHeight()) / mScale);
        invalidate();
        return true;
    }

    private void checkRect(Rect mRect) {
        //图片宽高比>1
        if (ratio > 1) {
            mRect.top = 0;
            mRect.bottom = mImageHeight;
            if (mRect.left < 0) {
                mRect.left = 0;
                mRect.right = (int) (getMeasuredWidth() / mScale);
            }
            if (mRect.right > mImageWidth) {
                mRect.right = mImageWidth;
                mRect.left = (int) (mImageWidth - getMeasuredWidth() / mScale);
            }
        } else {
            mRect.left = 0;
            mRect.right = mImageWidth;

            if (mRect.top < 0) {
                mRect.top = 0;
                mRect.bottom = (int) (getMeasuredHeight() / mScale);
            }

            if (mRect.bottom > mImageHeight) {
                mRect.bottom = mImageHeight;
                mRect.top = (int) (mImageHeight - getMeasuredHeight() / mScale);
            }
        }
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }
}
