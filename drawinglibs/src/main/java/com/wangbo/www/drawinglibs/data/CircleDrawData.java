package com.wangbo.www.drawinglibs.data;

import android.graphics.Canvas;

import com.wangbo.www.drawinglibs.config.NoteApplication;


public class CircleDrawData extends BaseDrawData{
    private float mCircleX;
    private float mCircleY;
    private float mRadius;

    public float getCircleY() {
        return mCircleY;
    }

    public void setCircleY(float circleY) {
        mCircleY = circleY;
    }

    public float getRadius() {
        return mRadius;
    }

    public void setRadius(float radius) {
        mRadius = radius;
    }

    public float getCircleX() {
        return mCircleX;
    }

    public void setCircleX(float circleX) {
        mCircleX = circleX;
    }

    public void offSet(float dx, float dy) {
        this.mCircleX += dx;
        this.mCircleY += dy;
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawCircle(mCircleX, mCircleY, mRadius, mPaint);
    }

    @Override
    public int getMode() {
        return NoteApplication.MODE_CIRCLE;
    }
}
