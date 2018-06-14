package com.wangbo.www.drawingboard;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wangbo.www.drawinglibs.view.CustomDrawView;

public class MainActivity extends AppCompatActivity {
    private CustomDrawView customDrawView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        customDrawView.setFlags("6");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        customDrawView.cleanAll();
    }
}
