package com.ysy.mindmap.uis.manage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.ysy.mindmap.R;
import com.ysy.mindmap.bases.BaseMVPActivity;
import com.ysy.mindmap.bases.IUI;
import com.ysy.mindmap.interfaces.ITeamManageUI;
import com.ysy.mindmap.models.datas.DataUser;
import com.ysy.mindmap.presenters.TeamManagePresenter;

/**
 * Created by Administrator on 2017/6/4.
 */

public class TeamManageActivity extends BaseMVPActivity<TeamManagePresenter> implements ITeamManageUI {

    public static final String TEAM_MANAGE_USER = "team_manage_user";

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    protected TeamManagePresenter createPresenter() {
        return new TeamManagePresenter();
    }

    @Override
    protected IUI getUI() {
        return this;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void launch(Context context, DataUser user) {
        Intent intent =new Intent(context, TeamManageActivity.class);
        intent.putExtra(TEAM_MANAGE_USER, user);
        context.startActivity(intent);
    }

    @Override
    protected void onCreateExecute(Bundle savedInstanceState) {
        setContentView(R.layout.activity_team_manage);
        setupActionBar();
    }
}
