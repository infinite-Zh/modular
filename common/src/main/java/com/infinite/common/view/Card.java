package com.infinite.common.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

class Card {
    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    private Bitmap bitmap;
    private int x;
    private int width;
    private int height;
    private Paint paint=new Paint();

    public boolean isLast() {
        return isLast;
    }

    public void setLast(boolean last) {
        isLast = last;
    }

    private boolean isLast;
    public Card(Bitmap bitmap,int x){
        this.bitmap=bitmap;
        this.x=x;
        width=bitmap.getWidth();
        height=bitmap.getHeight();
    }

    public void draw(Canvas canvas,Card next){
        if (isLast){
            canvas.drawBitmap(bitmap,x,0, paint);
        }else {
            canvas.save();
            canvas.clipRect(x, 0, next.x, height);
            canvas.drawBitmap(bitmap,x,0, paint);
            canvas.restore();
        }

    }
}
