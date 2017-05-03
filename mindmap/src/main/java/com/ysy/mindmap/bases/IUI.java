package com.ysy.mindmap.bases;

import android.app.Activity;

/**
 * MVP的View层协议
 * Created by Sylvester on 17/5/3.
 */
public interface IUI extends IUIState {
    /**
     * 如果加载对话框正在显示，则dismiss掉它
     */
    void dismissWaitingDialogIfShowing();

    /**
     * 显示正在加载对话框
     */
    void showWaitingDialog();

    /**
     * 得到页面名称，为了统计
     */
    String getPageName();

    void showToast(String msg);

    void showToast(int stringId);

    Activity getActivity();
}
