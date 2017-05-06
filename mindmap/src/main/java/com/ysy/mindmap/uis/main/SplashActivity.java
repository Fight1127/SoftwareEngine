package com.ysy.mindmap.uis.main;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import com.ysy.mindmap.R;
import com.ysy.mindmap.bases.BaseActivity;
import com.ysy.mindmap.bases.GlobalConstant;
import com.ysy.mindmap.utils.AppDataUtil;

public class SplashActivity extends BaseActivity {

    private long uid = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        AppDataUtil aDU = new AppDataUtil(this);
        uid = aDU.readLongData(GlobalConstant.SP_UID);
        int WAIT_TIME;
        if (uid == -1)
            WAIT_TIME = 2048;
        else
            WAIT_TIME = 1024;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (uid == -1) {
                    LoginActivity.launch(getActivity());
                    finish();
                } else {
                    MainActivity.launch(getActivity(), uid);
                    finish();
                }
            }
        }, WAIT_TIME);
    }

    @Override
    public Activity getActivity() {
        return this;
    }
}
