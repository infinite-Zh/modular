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

    private final Matrix matrix =new Matrix();

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

    private InputStream is;

    public void setImageStream(InputStream is) {
        this.is = is;

        options.inJustDecodeBounds = true;
        options.inMutable = true;
        options.inPreferredConfig= Bitmap.Config.RGB_565;
        BitmapFactory.decodeStream(is, null, options);


        mImageWidth = options.outWidth;
        mImageHeight = options.outHeight;

        ratio = (float) mImageWidth / mImageHeight;

        try {
            decoder = BitmapRegionDecoder.newInstance(is, false);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRect.left = 0;
        mRect.top = 0;
        mRect.bottom = h;
        mRect.right = w;

        if (ratio > 1) {
            mScale = h / (float) mImageHeight;
//            mRect.right = (int) (mRect.bottom * mScale);
        } else {
            mScale = w / (float) mImageWidth;
//            mRect.bottom = (int) (mRect.right * mScale);
        }

        invalidate();

    }

    private void getBitmap() {
        options.inBitmap = mBitmap;
        mBitmap = decoder.decodeRegion(mRect, options);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        getBitmap();
        if (mBitmap != null) {
            matrix.setScale(mScale,mScale);
            canvas.drawBitmap(mBitmap, matrix,null);
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
        mRect.left += dx;
        if (mRect.left < 0) {
            mRect.left = 0;
        }
        if (mRect.left > mImageWidth - getMeasuredWidth()) {
            mRect.left = mImageWidth - getMeasuredWidth();
        }
        mRect.right = mRect.left + getMeasuredWidth();

        mRect.top += dy;
        if (mRect.top<0){
            mRect.top=0;
        }
        if (mRect.top>mImageHeight-getMeasuredHeight()){
            mRect.top=mImageHeight-getMeasuredHeight();
        }
        mRect.bottom = mRect.top + getMeasuredHeight();
        invalidate();
        return true;
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
