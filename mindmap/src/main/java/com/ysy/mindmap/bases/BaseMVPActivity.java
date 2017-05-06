package com.ysy.mindmap.bases;

import android.os.Bundle;

/**
 * MVP的Activity基类
 * Created by Sylvester on 17/4/19.
 */
public abstract class BaseMVPActivity<P extends BasePresenter> extends BaseActivity {

    public static final String KEY_DATA = "keyDataOfActivity";

    protected P mPresenter;
    protected Bundle mBundle;

    protected void beforeSetContentView() {

    }

    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        beforeSetContentView();
        if (savedInstanceState != null) {
            if (savedInstanceState.getBundle(KEY_DATA) != null) {
                mBundle = savedInstanceState.getBundle(KEY_DATA);
            }
        }

        if (getIntent() != null && getIntent().getExtras() != null) {
            mBundle = getIntent().getExtras();
        }

        this.mPresenter = createPresenter();
        getPresenter().init(BaseMVPActivity.this, getUI());
        onCreateExecute(savedInstanceState);
        getPresenter().onUICreate(savedInstanceState);
    }

    /**
     * 所有BaseMVPActivity的子类不能再实现onCreate()方法，而是实现onCreateExecute()方法
     */
    protected abstract void onCreateExecute(Bundle savedInstanceState);

    /**
     * 创建一个Presenter，子类来实现，可以通过new的方式直接new出来一个
     */
    protected abstract P createPresenter();

    /**
     * 得到UI层组件，一般都是Activity或者Fragment本身
     */
    protected abstract IUI getUI();

    /**
     * 子类应该通过这个方法拿到Presenter的实例，而不是通过变量拿到
     */
    protected final P getPresenter() {
        return mPresenter;
    }

    @Override
    protected void onStart() {
        super.onStart();
        getPresenter().onUIStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPresenter().onUIResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPresenter().onUIPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        getPresenter().onUIStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getPresenter().onUIDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mBundle != null) {
            outState.putBundle(KEY_DATA, mBundle);
        }
        if (getPresenter() != null) {
            getPresenter().onSaveInstanceState(outState);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.getBundle(KEY_DATA) != null) {
                mBundle = savedInstanceState.getBundle(KEY_DATA);
            }
        }
        getPresenter().onRestoreInstanceState(savedInstanceState);
        super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * 得到界面必需的Bundle数据
     */
    public Bundle getData() {
        return mBundle;
    }
}
