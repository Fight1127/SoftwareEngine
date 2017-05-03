package com.ysy.mindmap.uis.main;

import android.app.Activity;
import android.os.Bundle;

import com.ysy.mindmap.R;
import com.ysy.mindmap.bases.BaseActivity;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    public Activity getActivity() {
        return this;
    }
}
