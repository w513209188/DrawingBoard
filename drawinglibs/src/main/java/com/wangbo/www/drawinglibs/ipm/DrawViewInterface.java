package com.wangbo.www.drawinglibs.ipm;

public interface DrawViewInterface {

    void redo();

    void undo();

    void reset();

    String save(String path, String rootPath);
}
