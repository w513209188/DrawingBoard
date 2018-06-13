package com.wangbo.www.drawinglibs.state;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;

import com.wangbo.www.drawinglibs.command.Command;
import com.wangbo.www.drawinglibs.config.NoteApplication;
import com.wangbo.www.drawinglibs.data.BaseDrawData;
import com.wangbo.www.drawinglibs.data.RectangleDrawData;
import com.wangbo.www.drawinglibs.utils.CommandUtils;
import com.wangbo.www.drawinglibs.utils.DrawDataUtils;


public class RectangleState extends BaseState {
    private static RectangleState mRectangleState = null;
    private RectangleDrawData mRectangleDrawData;

    private RectangleState() {
    }

    public static RectangleState getInstance() {
        if (mRectangleState == null) {
            mRectangleState = new RectangleState();
        }
        return mRectangleState;
    }

    @Override
    public void onDraw(BaseDrawData baseDrawData, Canvas canvas) {
        RectangleDrawData rectangleDrawData = (RectangleDrawData) baseDrawData;
        canvas.drawRect(rectangleDrawData.getLeft(), rectangleDrawData.getTop(), rectangleDrawData.getRight(), rectangleDrawData.getBottom(), rectangleDrawData.getPaint());
    }

    @Override
    public BaseDrawData downDrawData(MotionEvent event, Paint paint) {
        Paint rectPaint = new Paint(paint);
        Command command = new Command();
        mRectangleDrawData = new RectangleDrawData();
        mRectangleDrawData.setPaint(rectPaint);
        mRectangleDrawData.setLeft(event.getX());
        mRectangleDrawData.setTop(event.getY());
        mRectangleDrawData.setRight(event.getX());
        mRectangleDrawData.setBottom(event.getY());
        command.setCommand(NoteApplication.COMMAND_ADD);
        command.getCommandDrawList().add(mRectangleDrawData);
        CommandUtils.getInstance().getUndoCommandList().add(command);
        DrawDataUtils.getInstance().getSaveDrawDataList().add(mRectangleDrawData);
        return mRectangleDrawData;
    }

    @Override
    public void moveDrawData(MotionEvent event, Paint paint, Canvas canvas) {
        mRectangleDrawData.setRight(event.getX());
        mRectangleDrawData.setBottom(event.getY());
    }

    @Override
    public void upDrawData(MotionEvent event, Paint paint) {
        mRectangleDrawData.setRight(event.getX());
        mRectangleDrawData.setBottom(event.getY());
    }

    @Override
    public void pointerDownDrawData(MotionEvent event) {

    }

    @Override
    public void pointerUpDrawData(MotionEvent event) {

    }

    @Override
    public void destroy() {
        mRectangleState = null;
    }
}
