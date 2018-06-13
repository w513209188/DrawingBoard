package com.wangbo.www.drawinglibs.state;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.MotionEvent;

import com.wangbo.www.drawinglibs.command.Command;
import com.wangbo.www.drawinglibs.config.NoteApplication;
import com.wangbo.www.drawinglibs.data.BaseDrawData;
import com.wangbo.www.drawinglibs.data.EraserDrawData;
import com.wangbo.www.drawinglibs.utils.CommandUtils;
import com.wangbo.www.drawinglibs.utils.DrawDataUtils;


public class EraserState extends BaseState {
    private static EraserState mEraserState = null;
    private EraserDrawData mEraserPathDrawData;

    private EraserState() {
    }
    public static EraserState getInstance() {
        if (mEraserState == null) {
            mEraserState = new EraserState();
        }
        return mEraserState;
    }
    @Override
    public void onDraw(BaseDrawData baseDrawData, Canvas canvas) {

    }

    @Override
    public BaseDrawData downDrawData(MotionEvent event, Paint paint) {
        Command command = new Command();
        mEraserPathDrawData = new EraserDrawData();
        Paint pathPaint = new Paint(paint);
        pathPaint.setAlpha(0);
        pathPaint.setColor(Color.TRANSPARENT);
        pathPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        Path path = new Path();
        path.reset();
        path.moveTo(event.getX(),event.getY());
        mEraserPathDrawData.setPaint(pathPaint);
        mEraserPathDrawData.setPath(path);
        mEraserPathDrawData.setPoint(event.getX() + "," + event.getY() + "|");
        command.setCommand(NoteApplication.COMMAND_ADD);
        command.getCommandDrawList().add(mEraserPathDrawData);
        CommandUtils.getInstance().getUndoCommandList().add(command);
        DrawDataUtils.getInstance().getSaveDrawDataList().add(mEraserPathDrawData);
        return mEraserPathDrawData;
    }

    @Override
    public void moveDrawData(MotionEvent event, Paint paint, Canvas canvas) {
        mEraserPathDrawData.setPoint(event.getX() + "," + event.getY() + "|");
        mEraserPathDrawData.getPath().lineTo(event.getX(), event.getY());
        canvas.drawPath(mEraserPathDrawData.getPath(), mEraserPathDrawData.getPaint());
    }

    @Override
    public void upDrawData(MotionEvent event, Paint paint) {
        mEraserPathDrawData.setPoint(event.getX() + "," + event.getY() + "]");
        mEraserPathDrawData.getPath().lineTo(event.getX(), event.getY());
    }

    @Override
    public void pointerDownDrawData(MotionEvent event) {

    }

    @Override
    public void pointerUpDrawData(MotionEvent event) {

    }

    @Override
    public void destroy() {
        mEraserState = null;
    }
}
