package com.ysy.mindmap.uis.main;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ysy.mindmap.R;
import com.ysy.mindmap.bases.BaseMVPActivity;
import com.ysy.mindmap.bases.GlobalConstant;
import com.ysy.mindmap.bases.IUI;
import com.ysy.mindmap.interfaces.ILoginUI;
import com.ysy.mindmap.models.datas.DataUser;
import com.ysy.mindmap.models.listeners.NoDoubleViewClickListener;
import com.ysy.mindmap.presenters.LoginPresenter;
import com.ysy.mindmap.uis.editor.RegisterActivity;
import com.ysy.mindmap.utils.AppDataUtil;
import com.ysy.mindmap.utils.DialogUtil;
import com.ysy.mindmap.utils.ToastUtil;

/**
 * Created by Sylvester on 17/5/4.
 */

public class LoginActivity extends BaseMVPActivity<LoginPresenter> implements ILoginUI {

    private Button mLoginBtn;
    private View mRegisterTv;
    private EditText mUsernameEdt;
    private EditText mPwEdt;
    private Dialog mWaitingDialog;

    public static void launch(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    protected void onCreateExecute(Bundle savedInstanceState) {
        setContentView(R.layout.activity_login);
        initViews();
        setListeners();
    }

    @Override
    protected LoginPresenter createPresenter() {
        return new LoginPresenter();
    }

    @Override
    protected IUI getUI() {
        return this;
    }

    private void initViews() {
        mLoginBtn = (Button) findViewById(R.id.login_btn);
        mRegisterTv = findViewById(R.id.login_register_tip_tv);
        mUsernameEdt = (EditText) findViewById(R.id.login_user_edt);
        mPwEdt = (EditText) findViewById(R.id.login_pw_edt);
    }

    private void setListeners() {
        mLoginBtn.setOnClickListener(new NoDoubleViewClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                toLogin(mUsernameEdt.getText().toString(), mPwEdt.getText().toString());
            }
        });

        mRegisterTv.setOnClickListener(new NoDoubleViewClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                RegisterActivity.launch(LoginActivity.this);
            }
        });
    }

    private void toLogin(String username, String pw) {
        mWaitingDialog = new DialogUtil(this).showWaitDialog(getString(R.string.login_loading));
        getPresenter().toLogin(username, pw);

        // test
//        MainActivity.launch(this, 1);
//        finish();
    }

    @Override
    public void onLoginSuccess(DataUser loginUser) {
        if (mWaitingDialog != null)
            mWaitingDialog.dismiss();
        AppDataUtil aDU = new AppDataUtil(this);
        aDU.saveData(GlobalConstant.SP_UID, loginUser.getUid());
        MainActivity.launch(this, loginUser);
        finish();
    }

    @Override
    public void onLoginFail(String errorMsg) {
        if (mWaitingDialog != null)
            mWaitingDialog.dismiss();
        new ToastUtil(this).showToastShort(errorMsg);
    }
}
