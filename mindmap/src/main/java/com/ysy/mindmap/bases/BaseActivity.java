package com.ysy.mindmap.bases;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ysy.mindmap.utils.DialogUtil;
import com.ysy.mindmap.utils.ToastUtil;

/**
 * Created by Sylvester on 17/5/3.
 */

public abstract class BaseActivity extends AppCompatActivity implements IUI {

    protected boolean isActivityDestroyed = false;
    private boolean isPaused;
    private boolean isStopped;
    private Dialog mWaitingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isActivityDestroyed = false;
    }

    @Override
    protected void onResume() {
        isPaused = false;
        super.onResume();
    }

    @Override
    protected void onPause() {
        isPaused = true;
        super.onPause();
    }

    @Override
    protected void onStart() {
        isStopped = false;
        super.onStart();
    }

    @Override
    protected void onRestart() {
        isStopped = false;
        super.onRestart();
    }

    @Override
    protected void onStop() {
        isStopped = true;
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        isActivityDestroyed = true;
        dismissWaitingDialogIfShowing();
        super.onDestroy();
    }

    @Override
    public String getPageName() {
        return getClass().getSimpleName();
    }

    public boolean isActivityDestroyed() {
        return isActivityDestroyed;
    }

    @Override
    public boolean isPaused() {
        return isPaused;
    }

    public boolean isStopped() {
        return isStopped;
    }

    @Override
    public boolean isDestroyed() {
        return isActivityDestroyed();
    }

    @Override
    public boolean isDetached() {
        return isDestroyed();
    }

    @Override
    public boolean isFragmentHidden() {
        return isDestroyed();
    }

    @Override
    public boolean isVisibleToUser() {
        return !isPaused();
    }

    @Override
    public void showWaitingDialog() {
        dismissWaitingDialogIfShowing();
        if (!isFinishing() && !isActivityDestroyed()) {
            mWaitingDialog = new DialogUtil(this).showWaitDialog();
        }
    }

    @Override
    public void dismissWaitingDialogIfShowing() {
        if (!isActivityDestroyed() && mWaitingDialog != null && mWaitingDialog.isShowing()) {
            mWaitingDialog.dismiss();
            mWaitingDialog = null;
        }
    }

    @Override
    public void showToast(String msg) {
        new ToastUtil(this).showToastShort(msg);
    }

    @Override
    public void showToast(int stringId) {
        showToast(getString(stringId));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 一定不要干掉这段代码
        super.onActivityResult(requestCode, resultCode, data);
    }
}
