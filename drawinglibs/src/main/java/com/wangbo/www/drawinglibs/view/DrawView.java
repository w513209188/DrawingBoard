package com.wangbo.www.drawinglibs.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.wangbo.www.drawinglibs.command.Command;
import com.wangbo.www.drawinglibs.config.NoteApplication;
import com.wangbo.www.drawinglibs.data.BaseDrawData;
import com.wangbo.www.drawinglibs.ipm.DrawViewInterface;
import com.wangbo.www.drawinglibs.ipm.TouchableChild;
import com.wangbo.www.drawinglibs.state.BaseState;
import com.wangbo.www.drawinglibs.state.PathState;
import com.wangbo.www.drawinglibs.utils.CommandUtils;
import com.wangbo.www.drawinglibs.utils.DrawDataUtils;
import com.wangbo.www.drawinglibs.utils.FileUtils;
import com.wangbo.www.drawinglibs.utils.SVGUtils;
import com.wangbo.www.drawinglibs.utils.ScreenInfo;
import com.wangbo.www.drawinglibs.utils.XMLUtils;

import java.util.Iterator;
import java.util.List;

public class DrawView extends View implements DrawViewInterface,TouchableChild {

    private static final String TAG = "DrawView";
    private Paint mPaint;//画笔
    private Canvas mCanvas;//画布
    private Bitmap mBitmap;
    private int mCanvasCode = NoteApplication.CANVAS_NORMAL;
    private BaseDrawData mBaseDrawData = null;
    private BaseState mCurrentState = PathState.getInstance();//当前绘制的状态
    int w;
    int h;
    private Context mContext;
    public DrawView(Context context) {
        this(context,null);
    }

    public DrawView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DrawView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext=context;
    }

    public void setCanvasCode(int mCanvasCode) {
        this.mCanvasCode = mCanvasCode;
    }

    public void initParameter(int wt,int ht) {
        setInfo(new ScreenInfo((Activity) mContext), wt, ht);
        initPaint();
        initCanvas();
    }
    private void setInfo(ScreenInfo info, float wTimes, float hTimes) {
        w = (int) (info.getWidthPixels() * wTimes);
        h = (int) (info.getHeightPixels() * hTimes);
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
    }
    private void initPaint() {
        mPaint = new Paint();
        mPaint.setColor(Color.parseColor("#FF4081"));
        mPaint.setStrokeWidth(10);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    private void initCanvas() {
        mCanvas = new Canvas(mBitmap);
        mCanvas.drawColor(Color.TRANSPARENT);
    }

    public void changePaintColor(int color) {
        mPaint.setColor(color);
    }

    public void changePaintSize(float width) {
        mPaint.setStrokeWidth(width);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBitmap, 0, 0, mPaint);
        Log.e("mCanvasCode",mCanvasCode+"");


        switch (mCanvasCode) {
            case NoteApplication.CANVAS_NORMAL:
                mCurrentState.onDraw(mBaseDrawData, canvas);
                break;
            case NoteApplication.CANVAS_UNDO:
                undo();
                break;
            case NoteApplication.CANVAS_REDO:
                redo();
                break;
            case NoteApplication.CANVAS_RESET:
                reset();
                break;
            default:
                Log.e(TAG, "onDraw" + Integer.toString(mCanvasCode));
                break;
        }
        super.onDraw(canvas);
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        mCanvasCode = NoteApplication.CANVAS_NORMAL;
//
//        switch (event.getAction() & MotionEvent.ACTION_MASK) {
//            case MotionEvent.ACTION_DOWN:
//                actionDown(event);
//                if (mBaseDrawData == null && !mCurrentState.isShear()) {
//                    reset();
//                    drawFromData();
//                    return super.onTouchEvent(event);
//                }
//                break;
//            case MotionEvent.ACTION_POINTER_DOWN:
//                mCurrentState.pointerDownDrawData(event);
//                break;
//            case MotionEvent.ACTION_MOVE:
//                actionMove(event);
//                break;
//            case MotionEvent.ACTION_UP:
//                actionUp(event);
//                break;
//            case MotionEvent.ACTION_POINTER_UP:
//                mCurrentState.pointerUpDrawData(event);
//                break;
//            default:
//                break;
//        }
//        invalidate();
//        return true;
//    }


    private void actionDown(MotionEvent event) {
        mBaseDrawData = mCurrentState.downDrawData(event, mPaint);
    }

    private void actionMove(MotionEvent event) {
        mCurrentState.moveDrawData(event, mPaint, mCanvas);
    }

    private void actionUp(MotionEvent event) {
        mCurrentState.upDrawData(event, mPaint);
        mCurrentState.onDraw(mBaseDrawData, mCanvas);
    }

    @Override
    public void redo() {
        redoFromCommand(CommandUtils.getInstance().redo());
    }

    @Override
    public void undo() {
        reset();
        undoFromCommand(CommandUtils.getInstance().undo());
    }

    @Override
    public void reset() {
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mCanvas.drawPaint(mPaint);
        mPaint.setXfermode(null);
    }

    @Override
    public String save(String path, String rootPath) {
        // TODO: 2016/8/8 从数据源里面获取相应的参数，保存为XML,PNG,SVG
        String pngFileName = null;
        pngFileName = FileUtils.saveAsPng(mBitmap, path, rootPath);
        XMLUtils xmlUtils = new XMLUtils();
        String[] xmlData = DrawDataUtils.getInstance().getXMLData();
        xmlUtils.encodeXML(rootPath + "/" + pngFileName + ".xml", xmlData[0], xmlData[1], xmlData[2]);
        SVGUtils svgUtils = new SVGUtils();
        svgUtils.encodeSVG(DrawDataUtils.getInstance().getSaveDrawDataList(), rootPath + "/" + pngFileName + ".svg");
        return pngFileName;
    }

    public void setCurrentState(BaseState currentState) {
        mCurrentState = currentState;
    }

    private void undoFromCommand(Command command) {

        if (command != null) {
            switch (command.getCommand()) {
                case NoteApplication.COMMAND_ADD:
                    Iterator<BaseDrawData> iterator = DrawDataUtils.getInstance().getSaveDrawDataList().iterator();
                    while (iterator.hasNext()) {
                        BaseDrawData baseDrawData = iterator.next();
                        if (baseDrawData.equals(command.getCommandDrawList().get(0))) {
                            iterator.remove();
                            break;
                        }
                    }
                    drawFromData();
                    break;
                case NoteApplication.COMMAND_TRANSLATE:
                    Iterator<BaseDrawData> commandIterator = command.getCommandDrawList().iterator();
                    DrawDataUtils.getInstance().offSetDrawData(commandIterator, -command.getOffSetX(), -command.getOffSetY());
                    drawFromData();
                    break;
                case NoteApplication.COMMAND_SCALE:
                    break;
                default:
                    Log.e(TAG, "undoFromCommand" + Integer.toString(command.getCommand()));
                    break;
            }
        }
    }


    private void redoFromCommand(Command command) {
        if (command != null) {
            switch (command.getCommand()) {
                case NoteApplication.COMMAND_ADD:
                    drawFromRedoData(command.getCommandDrawList());
                    break;
                case NoteApplication.COMMAND_TRANSLATE:
                    Iterator<BaseDrawData> commandIterator = command.getCommandDrawList().iterator();
                    DrawDataUtils.getInstance().offSetDrawData(commandIterator, command.getOffSetX(), command.getOffSetY());
                    reset();
                    drawFromData();
                    break;
                case NoteApplication.COMMAND_SCALE:
                    break;
                default:
                    Log.e(TAG, "redoFromCommand" + Integer.toString(command.getCommand()));
                    break;
            }
        }
    }

    /**
     * 从redo里面恢复数据
     */
    private void drawFromRedoData(List<BaseDrawData> redoList) {
        int size = redoList.size();
        for (int i = 0; i < size; ++i) {
            BaseDrawData baseDrawData = redoList.get(i);
            baseDrawData.onDraw(mCanvas);
            DrawDataUtils.getInstance().getSaveDrawDataList().add(baseDrawData);
        }
    }

    public void drawFromData() {
        int size = DrawDataUtils.getInstance().getSaveDrawDataList().size();
        for (int i = 0; i < size; ++i) {
            BaseDrawData baseDrawData = DrawDataUtils.getInstance().getSaveDrawDataList().get(i);
            baseDrawData.onDraw(mCanvas);
        }
    }

    public void addCommand() {
        Iterator<BaseDrawData> iterator = DrawDataUtils.getInstance().getSaveDrawDataList().iterator();
        Command command;
        while (iterator.hasNext()) {
            BaseDrawData baseDrawData = iterator.next();
            command = new Command();
            command.setCommand(NoteApplication.COMMAND_ADD);
            command.getCommandDrawList().add(baseDrawData);
            CommandUtils.getInstance().getUndoCommandList().add(command);
        }
    }

    @Override
    public void fingerUp(MotionEvent event) {
        actionUp(event);
        invalidate();
    }

    @Override
    public void fingerDown(MotionEvent event) {
        mCanvasCode = NoteApplication.CANVAS_NORMAL;
        actionDown(event);
        if (mBaseDrawData == null && !mCurrentState.isShear()) {
            Log.e("需要重置","-------");
            reset();
            drawFromData();
        }else {
            Log.e("不需要重置","-------");
        }
        invalidate();
    }

    @Override
    public void fingerMove(MotionEvent event) {
        mCanvasCode = NoteApplication.CANVAS_NORMAL;
        actionMove(event);
        invalidate();
    }

    @Override
    public void fingerCancel(MotionEvent event) {
        mCanvasCode = NoteApplication.CANVAS_NORMAL;
    }
}
