package com.ysy.mindmap.uis.other;

import android.app.Activity;
import android.os.Bundle;

import com.ysy.mindmap.R;
import com.ysy.mindmap.bases.BaseMVPActivity;
import com.ysy.mindmap.bases.IUI;
import com.ysy.mindmap.interfaces.ISettingsUI;
import com.ysy.mindmap.presenters.SettingsPresenter;

/**
 * Created by Sylvester on 17/5/4.
 */

public class SettingsActivity extends BaseMVPActivity<SettingsPresenter> implements ISettingsUI {

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    protected void onCreateExecute(Bundle savedInstanceState) {
        setContentView(R.layout.activity_settings);

    }

    @Override
    protected SettingsPresenter createPresenter() {
        return new SettingsPresenter();
    }

    @Override
    protected IUI getUI() {
        return this;
    }
}
