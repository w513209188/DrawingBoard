package com.wangbo.www.drawinglibs.view;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.wangbo.www.drawinglibs.ipm.DraggableParent;
import com.wangbo.www.drawinglibs.ipm.TouchableChild;

/**
 * 处理多点触控，控制将touch事件分发给父控件(@parent)或者子控件(@child)
 */

public class DraggablePaperTouchListener implements View.OnTouchListener {

    private TouchableChild child;

    private DraggableParent parent;

    /**
     * mode标识当前的画布的状态：
     * 0: 当前画布静止，可以画画，事务交给mVisitor处理
     * 1: 当前画布进去拖动状态，parent将touch事件拦截并处理
     * 2: 拖动结束的状态（可能还有一个手指在屏幕上）
     */

    private int mode;

    public DraggablePaperTouchListener(DraggableParent parent, TouchableChild child) {
        this.parent = parent;
        this.child = child;
    }

    public DraggablePaperTouchListener(DraggableParent parent) {
        this.parent = parent;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        parent.interceptTouchEventToDrag(false);
        int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_POINTER_UP:
                mode = 2;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                parent.interceptTouchEventToDrag(true);
                mode = 1;
                break;
            case MotionEvent.ACTION_CANCEL:
                mode = 0;
                if (child != null) {
                    child.fingerCancel(event);
                }
                break;
            case MotionEvent.ACTION_DOWN:
                mode = 0;
                if (child != null) {
                    child.fingerDown(event);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (child != null) {
                    child.fingerUp(event);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == 1) {
                    parent.interceptTouchEventToDrag(true);
                } else if (mode == 0) {
                    if (child != null) {
                        child.fingerMove(event);
                    }
                }

                break;
        }

        return true;
    }
}
