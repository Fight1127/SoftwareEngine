package com.ysy.mindmap.uis.editor;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.ysy.mindmap.bases.BaseMVPActivity;
import com.ysy.mindmap.bases.IUI;
import com.ysy.mindmap.interfaces.IRegister;
import com.ysy.mindmap.presenters.RegisterPresenter;

/**
 * Created by Sylvester on 17/5/4.
 */

public class RegisterActivity extends BaseMVPActivity<RegisterPresenter> implements IRegister {

    public static void launch(Context context) {

    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    protected void onCreateExecute(Bundle savedInstanceState) {

    }

    @Override
    protected RegisterPresenter createPresenter() {
        return new RegisterPresenter();
    }

    @Override
    protected IUI getUI() {
        return this;
    }
}
