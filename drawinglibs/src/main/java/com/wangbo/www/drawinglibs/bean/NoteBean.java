package com.wangbo.www.drawinglibs.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class NoteBean implements Serializable {
    private String path;
    private String flags;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFlags() {
        return flags;
    }

    public void setFlags(String flags) {
        this.flags = flags;
    }
}
