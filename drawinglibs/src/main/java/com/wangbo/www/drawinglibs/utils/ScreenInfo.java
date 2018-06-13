package com.wangbo.www.drawinglibs.utils;

import android.app.Activity;
import android.util.DisplayMetrics;


/**
 * 屏幕信息
 */

public class ScreenInfo {

    private int mWidthPixels;
    private int mHeightPixels;

    public ScreenInfo(Activity activity) {
        getDisplayMetrics(activity);
    }

    private void getDisplayMetrics(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();

        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        this.mWidthPixels = dm.widthPixels;
        this.mHeightPixels = dm.heightPixels;
    }

    public int getWidthPixels() {
        return mWidthPixels;
    }

    public int getHeightPixels() {
        return mHeightPixels;
    }
}