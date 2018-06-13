package com.wangbo.www.drawinglibs.data;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import com.wangbo.www.drawinglibs.config.NoteApplication;

public class EraserDrawData extends BaseDrawData {

    private Path mPath;
    private String mPoint = "";

    public EraserDrawData() {
    }

    public Path getPath() {
        return mPath;
    }

    public void setPath(Path path) {
        mPath = path;
    }

    public String getPoint() {
        return mPoint;
    }

    public void setPoint(String point) {
        mPoint += point;
    }

    @Override
    public void onDraw(Canvas canvas) {
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawPath(mPath, mPaint);
        mPaint.setXfermode(null);
    }

    @Override
    public int getMode() {
        return NoteApplication.MODE_ERASER;
    }
}
