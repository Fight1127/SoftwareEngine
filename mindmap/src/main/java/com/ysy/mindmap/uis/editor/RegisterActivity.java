package com.ysy.mindmap.uis.editor;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.github.glomadrian.materialanimatedswitch.MaterialAnimatedSwitch;
import com.ysy.mindmap.R;
import com.ysy.mindmap.bases.BaseMVPActivity;
import com.ysy.mindmap.bases.IUI;
import com.ysy.mindmap.interfaces.IRegister;
import com.ysy.mindmap.models.datas.DataUser;
import com.ysy.mindmap.models.listeners.NoDoubleViewClickListener;
import com.ysy.mindmap.presenters.RegisterPresenter;
import com.ysy.mindmap.utils.DialogUtil;
import com.ysy.mindmap.utils.ToastUtil;

/**
 * Created by Sylvester on 17/5/4.
 */

public class RegisterActivity extends BaseMVPActivity<RegisterPresenter> implements IRegister {

    private MaterialAnimatedSwitch mSexSwitch;
    private View mBackImg;
    private View mDoneImg;
    private EditText mUsernameEdt;
    private EditText mPwEdt;
    private EditText mRePwEdt;
    private EditText mNicknameEdt;
    private TextView mSetBirthdayTv;
    private Dialog mWaitingDialog;

    private byte sex = 0;
    private int year = 1995;
    private int month = 8;
    private int day = 3;

    public static void launch(Context context) {
        Intent intent = new Intent(context, RegisterActivity.class);
        context.startActivity(intent);
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    protected void onCreateExecute(Bundle savedInstanceState) {
        setContentView(R.layout.activity_register);
        initViews();
        setListeners();
    }

    @Override
    protected RegisterPresenter createPresenter() {
        return new RegisterPresenter();
    }

    @Override
    protected IUI getUI() {
        return this;
    }

    private void initViews() {
        mSexSwitch = (MaterialAnimatedSwitch) findViewById(R.id.register_sex_switch);
        mBackImg = findViewById(R.id.register_back_img);
        mDoneImg = findViewById(R.id.register_done_img);
        mUsernameEdt = (EditText) findViewById(R.id.register_user_edt);
        mPwEdt = (EditText) findViewById(R.id.register_pw_edt);
        mRePwEdt = (EditText) findViewById(R.id.register_re_pw_edt);
        mNicknameEdt = (EditText) findViewById(R.id.register_nickname_edt);
        mSetBirthdayTv = (TextView) findViewById(R.id.register_birthday_tv);
    }

    private void setListeners() {
        mSexSwitch.setOnCheckedChangeListener(new MaterialAnimatedSwitch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(boolean isChecked) {
                sex = (byte) (isChecked ? 1 : 0);
            }
        });

        mBackImg.setOnClickListener(new NoDoubleViewClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                finish();
            }
        });

        mDoneImg.setOnClickListener(new NoDoubleViewClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                toRegister();
            }
        });

        mSetBirthdayTv.setOnClickListener(new NoDoubleViewClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                openCalendar();
            }
        });
    }

    private void toRegister() {
        mWaitingDialog = new DialogUtil(this).showWaitDialog(getString(R.string.register_loading));
        DataUser newUser = new DataUser();
        newUser.setUsername(mUsernameEdt.getText().toString());
        newUser.setPw(mPwEdt.getText().toString());
        newUser.setBirthday(mSetBirthdayTv.getText().toString());
        newUser.setSex(sex);
        newUser.setMsgRead(true);
        newUser.setNickname(mNicknameEdt.getText().toString());
        getPresenter().toRegister(newUser, mRePwEdt.getText().toString());
    }

    private void openCalendar() {
        new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mSetBirthdayTv.setText(formatDate(year + "-" + (month + 1) + "-" + dayOfMonth));
            }
        }, year, month - 1, day).show();
    }

    private String formatDate(String date) {
        String[] yMd = date.split("-");
        String year = yMd[0];
        String month = yMd[1];
        String day = yMd[2];
        this.year = Integer.parseInt(year);
        this.month = Integer.parseInt(month);
        this.day = Integer.parseInt(day);
        if (this.month < 10)
            month = "0" + month;
        if (this.day < 10)
            day = "0" + day;
        return yMd[0] + "-" + month + "-" + day;
    }

    @Override
    public void onRegisterSuccess() {
        if (mWaitingDialog != null && mWaitingDialog.isShowing()) {
            mWaitingDialog.dismiss();
            mWaitingDialog = null;
        }
        new ToastUtil(this).showToastShort(getString(R.string.register_success));
        finish();
    }

    @Override
    public void onRegisterFail(String errorMsg) {
        if (mWaitingDialog != null && mWaitingDialog.isShowing()) {
            mWaitingDialog.dismiss();
            mWaitingDialog = null;
        }
        new ToastUtil(this).showToastShort(errorMsg);
    }
}
