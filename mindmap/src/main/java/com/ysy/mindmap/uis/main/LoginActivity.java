package com.ysy.mindmap.uis.main;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.ysy.mindmap.bases.BaseMVPActivity;
import com.ysy.mindmap.bases.IUI;
import com.ysy.mindmap.interfaces.ILogin;
import com.ysy.mindmap.presenters.LoginPresenter;

/**
 * Created by Sylvester on 17/5/4.
 */

public class LoginActivity extends BaseMVPActivity<LoginPresenter> implements ILogin {

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
    protected LoginPresenter createPresenter() {
        return new LoginPresenter();
    }

    @Override
    protected IUI getUI() {
        return this;
    }
}
