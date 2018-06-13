package com.wangbo.www.drawinglibs.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.wangbo.www.drawinglibs.utils.ScreenInfo;


public class PaperContainer extends ViewGroup {

    /**
     * Paper长宽的倍数（相对于当前屏幕@ScreenInfo）
     */


    int w;
    int h;

    public PaperContainer(Context context) {
        super(context);
    }

    public PaperContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void initSize(Activity activity, float wTimes, float hTimes) {
        setInfo(new ScreenInfo(activity), wTimes, hTimes);
    }
    public void initSize(Activity activity,int DefaultWidth,int DefaultHeight) {
        setInfo(new ScreenInfo(activity), DefaultWidth, DefaultHeight);
    }

    private void setInfo(ScreenInfo info, float wTimes, float hTimes) {
        w = (int) (info.getWidthPixels() * wTimes);
        h = (int) (info.getHeightPixels() * hTimes);
        Log.e("w","w"+w+"h"+h);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        View child = getChildAt(0);

        int mW = MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY);
        int mH = MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY);

        child.measure(mW, mH);

        setMeasuredDimension(mW, mH);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        View child = getChildAt(0);
        child.layout(0, 0, w, h);
    }
}
