package com.wangbo.www.drawinglibs.state;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;

import com.wangbo.www.drawinglibs.command.Command;
import com.wangbo.www.drawinglibs.config.NoteApplication;
import com.wangbo.www.drawinglibs.data.BaseDrawData;
import com.wangbo.www.drawinglibs.data.LineDrawData;
import com.wangbo.www.drawinglibs.utils.CommandUtils;
import com.wangbo.www.drawinglibs.utils.DrawDataUtils;

public class LineState extends BaseState {
    private static LineState mLineState = null;
    private LineDrawData mLineDrawData;

    private LineState() {
    }

    public static LineState getInstance() {
        if (mLineState == null) {
            mLineState = new LineState();
        }
        return mLineState;
    }

    @Override
    public void onDraw(BaseDrawData baseDrawData, Canvas canvas) {
        LineDrawData lineDrawData = (LineDrawData) baseDrawData;
        canvas.drawLine(lineDrawData.getStartX(), lineDrawData.getStartY(), lineDrawData.getEndX(), lineDrawData.getEndY(), lineDrawData.getPaint());
    }

    @Override
    public BaseDrawData downDrawData(MotionEvent event, Paint paint) {
        Paint linePaint = new Paint(paint);
        Command command = new Command();
        mLineDrawData = new LineDrawData();
        mLineDrawData.setPaint(linePaint);
        mLineDrawData.setStartX(event.getX());
        mLineDrawData.setStartY(event.getY());
        mLineDrawData.setEndX(event.getX());
        mLineDrawData.setEndY(event.getY());
        command.setCommand(NoteApplication.COMMAND_ADD);
        command.getCommandDrawList().add(mLineDrawData);
        CommandUtils.getInstance().getUndoCommandList().add(command);
        DrawDataUtils.getInstance().getSaveDrawDataList().add(mLineDrawData);
        return mLineDrawData;
    }

    @Override
    public void moveDrawData(MotionEvent event, Paint paint, Canvas canvas) {
        mLineDrawData.setEndX(event.getX());
        mLineDrawData.setEndY(event.getY());
    }

    @Override
    public void upDrawData(MotionEvent event, Paint paint) {
        mLineDrawData.setEndX(event.getX());
        mLineDrawData.setEndY(event.getY());
    }

    @Override
    public void pointerDownDrawData(MotionEvent event) {

    }

    @Override
    public void pointerUpDrawData(MotionEvent event) {

    }

    @Override
    public void destroy() {
        mLineState = null;
    }
}
