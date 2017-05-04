package com.ysy.mindmap.uis.main;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ysy.mindmap.R;
import com.ysy.mindmap.bases.BaseMVPActivity;
import com.ysy.mindmap.bases.IUI;
import com.ysy.mindmap.interfaces.IMain;
import com.ysy.mindmap.presenters.MainPresenter;

public class MainActivity extends BaseMVPActivity<MainPresenter> implements IMain {

    public static void launch(Context context, long uid) {

    }

    @Override
    protected void onCreateExecute(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);

    }

    @Override
    protected MainPresenter createPresenter() {
        return new MainPresenter();
    }

    @Override
    protected IUI getUI() {
        return this;
    }

    @Override
    public Activity getActivity() {
        return this;
    }
}
