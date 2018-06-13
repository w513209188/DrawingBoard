package com.wangbo.www.drawinglibs.ipm;

public interface DraggableParent {

    /**
     * @param enable true: 拦截TouchEvent(并将其用于拖动)
     *               false: 不拦截TouchEvent
     */

    public void interceptTouchEventToDrag(boolean enable);

}
