package com.ysy.mindmap.uis.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ysy.mindmap.R;
import com.ysy.mindmap.bases.BaseMVPActivity;
import com.ysy.mindmap.bases.IUI;
import com.ysy.mindmap.interfaces.IMain;
import com.ysy.mindmap.models.datas.DataUser;
import com.ysy.mindmap.presenters.MainPresenter;

public class MainActivity extends BaseMVPActivity<MainPresenter> implements IMain {

    private static final String MAIN_USER = "main_user";
    private static final String MAIN_UID = "main_uid";

    private DataUser user = null;
    private long uid = -1;

    public static void launch(Context context, long uid) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(MAIN_UID, uid);
        context.startActivity(intent);
    }

    public static void launch(Context context, DataUser user) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(MAIN_USER, user);
        context.startActivity(intent);
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
