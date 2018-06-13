package com.wangbo.www.drawinglibs.ipm;

import android.view.MotionEvent;

/**
 * 处理单点触控的Visitor
 */

public interface TouchableChild {

    public void fingerUp(MotionEvent event);

    public void fingerDown(MotionEvent event);

    public void fingerMove(MotionEvent event);

    public void fingerCancel(MotionEvent event);

}
