package com.wangbo.www.drawinglibs.command;

import com.wangbo.www.drawinglibs.config.NoteApplication;
import com.wangbo.www.drawinglibs.data.BaseDrawData;

import java.util.ArrayList;
import java.util.List;

public class Command {
    private int mCommand = NoteApplication.COMMAND_ADD;
    private List<BaseDrawData> mCommandDrawList;
    private float mOffSetX;
    private float mOffSetY;

    public Command() {
        mCommandDrawList = new ArrayList<>();
    }

    public List<BaseDrawData> getCommandDrawList() {
        return mCommandDrawList;
    }

    public int getCommand() {
        return mCommand;
    }

    public void setCommand(int command) {
        mCommand = command;
    }

    public float getOffSetY() {
        return mOffSetY;
    }

    public void setOffSetY(float offSetY) {
        mOffSetY = offSetY;
    }

    public float getOffSetX() {
        return mOffSetX;
    }

    public void setOffSetX(float offSetX) {
        mOffSetX = offSetX;
    }
}
