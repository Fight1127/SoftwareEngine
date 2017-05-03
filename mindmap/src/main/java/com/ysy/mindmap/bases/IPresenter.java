package com.ysy.mindmap.bases;

import android.content.Context;
import android.os.Bundle;

/**
 * MVP的Presenter层协议.
 */
public interface IPresenter {

    <U extends IUI> void init(Context context, U ui);

    /**
     * UI被创建的时候应该invoke这个method
     * 比如Activity的onCreate()，Fragment的onCreateView()应该调用Presenter的这个方法
     *
     * @param savedInstanceState 保存了的状态
     */
    void onUICreate(Bundle savedInstanceState);

    /**
     * 在UI被创建和被显示到屏幕之间应该回调这个方法
     * 比如Activity的onStart()方法应该调用Presenter的这个方法
     */
    void onUIStart();

    /**
     * 在UI被显示到屏幕的时候应该回调这个方法
     * 比如Activity的onResume()方法应该调用Presenter的这个方法
     */
    void onUIResume();

    /**
     * 在UI从屏幕上消失的时候应该回调这个方法
     * 比如Activity的onPause()方法应该调用Presenter的这个方法
     */
    void onUIPause();

    /**
     * 在UI从屏幕完全隐藏应该回调这个方法
     * 比如Activity的onStop()方法应该调用Presenter的这个方法
     */
    void onUIStop();

    /**
     * 当UI被Destroy的时候应该回调这个方法
     */
    void onUIDestroy();

    /**
     * 保存数据
     * 一般是因为内存不足UI的状态被回收的时候调用
     *
     * @param outState 待保存的状态
     */
    void onSaveInstanceState(Bundle outState);

    /**
     * 当UI被恢复的时候被调用
     *
     * @param savedInstanceState 保存了的状态
     */
    void onRestoreInstanceState(Bundle savedInstanceState);
}
