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
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Scroller;

import java.io.IOException;
import java.io.InputStream;

public class BigView extends View implements GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener,
        ScaleGestureDetector.OnScaleGestureListener {

    //图片显示区域
    private Rect mRect = new Rect();
    //缩放比例
    private float mScale;

    private float mOriginalScale;

    //图片尺寸
    private int mImageWidth, mImageHeight;

    private GestureDetector mGestureDetector;

    private ScaleGestureDetector mScaleGestureDetector;

    private Bitmap mBitmap;

    //图片宽高比
    private float ratio;


    private BitmapFactory.Options options;

    private BitmapRegionDecoder decoder;

    private final Matrix matrix = new Matrix();

    private Scroller mScroller;

    private boolean isInScaleMode;

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
        mScroller = new Scroller(getContext());
        mGestureDetector = new GestureDetector(getContext(), this);
        mScaleGestureDetector = new ScaleGestureDetector(getContext(), this);
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

        if (ratio > 1) {
            mScale = getMeasuredHeight() / (float) mImageHeight;
            mRect.bottom = mImageHeight;
            mRect.right = (int) (mImageWidth / mScale);
        } else {
            mScale = getMeasuredWidth() / (float) mImageWidth;
            mRect.right = mImageWidth;
            mRect.bottom = (int) (mRect.right * mScale);
        }
        mOriginalScale = mScale;
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        if (!mScroller.isFinished()) {
            mScroller.forceFinished(true);
        }
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

        if (isInScaleMode) {
            return false;
        }
        mRect.offset((int) (dx / mScale), (int) (dy / mScale));
        checkRect(mRect);
        invalidate();

        Log.e("scroll", "onScroll");
        return true;
    }


    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float vx, float vy) {
        mScroller.fling(mRect.left, mRect.top, -(int) vx, -(int) vy,
                0, (int) (mImageWidth - getMeasuredWidth() / mScale),
                0, (int) (mImageHeight - getMeasuredHeight() / mScale));
        return false;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.isFinished()) {
            return;
        }
        if (mScroller.computeScrollOffset()) {
            if (ratio > 1) {
                mRect.left = mScroller.getCurrX();
                mRect.right = (int) (mRect.left + getMeasuredWidth() / mScale);
            } else {
                mRect.top = mScroller.getCurrY();
                mRect.bottom = (int) (mRect.top + getMeasuredHeight() / mScale);
            }
            invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleGestureDetector.onTouchEvent(event);
        mGestureDetector.onTouchEvent(event);
        return true;
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

    private void checkRect(Rect mRect) {
        //图片宽高比>1
        if (ratio > 1) {
            // 没有缩放的情况
            if (mRect.top < 0) {
                mRect.top = 0;
            }
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
    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent motionEvent) {
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        return true;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        mScale *= detector.getScaleFactor();
        if (mScale < mOriginalScale) {
            mScale = mOriginalScale;
        }
        if (mScale > 3 * mOriginalScale) {
            mScale = 3 * mOriginalScale;
        }

        setRectWithScale(detector.getFocusX(), detector.getFocusY(), detector.getScaleFactor());

        Log.e("factor", String.valueOf(detector.getScaleFactor()));
        invalidate();
        return true;
    }

    // 以两指间的中心点为中心缩放
    private void setRectWithScale(float x, float y, float factor) {

        if ((mRect.bottom - mRect.top < 500) || mRect.right - mRect.left < 500) {
            return;
        }
        float absX = ((factor - 1F) * (x - mRect.left));
        float absY = ((factor - 1F) * (y - mRect.top));

        Log.e("abs", "absx=" + absX + "  absY=" + absY);
        Log.e("rectO", mRect.flattenToString());


        mRect.top += absY * (y - mRect.top) / mRect.height();
        mRect.left += absX * (x - mRect.left) / mRect.width();
        mRect.bottom -= absY * (mRect.bottom - y) / mRect.height();
        mRect.right -= absX * (mRect.right - x) / mRect.width();

        Log.e("rect", mRect.flattenToString());
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
        isInScaleMode = true;

        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
        isInScaleMode = false;
    }
}
