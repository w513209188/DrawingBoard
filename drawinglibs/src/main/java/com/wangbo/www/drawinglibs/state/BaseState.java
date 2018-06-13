package com.wangbo.www.drawinglibs.state;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;

import com.wangbo.www.drawinglibs.data.BaseDrawData;


public abstract class BaseState {

    public abstract void onDraw(BaseDrawData baseDrawData, Canvas canvas);

    public abstract BaseDrawData downDrawData(MotionEvent event, Paint paint);

    public abstract void moveDrawData(MotionEvent event, Paint paint, Canvas canvas);

    public abstract void upDrawData(MotionEvent event, Paint paint);

    public abstract void pointerDownDrawData(MotionEvent event);

    public abstract void pointerUpDrawData(MotionEvent event);

    public abstract void destroy();

    public boolean isShear() {
        return false;
    }
}
