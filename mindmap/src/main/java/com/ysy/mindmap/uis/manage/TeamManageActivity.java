package com.ysy.mindmap.uis.manage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import com.ysy.mindmap.R;
import com.ysy.mindmap.bases.BaseMVPActivity;
import com.ysy.mindmap.bases.IUI;
import com.ysy.mindmap.interfaces.ITeamManageUI;
import com.ysy.mindmap.models.datas.DataTeam;
import com.ysy.mindmap.models.datas.DataUser;
import com.ysy.mindmap.models.listeners.NoDoubleViewClickListener;
import com.ysy.mindmap.presenters.TeamManagePresenter;
import com.ysy.mindmap.utils.ToastUtil;

import java.util.List;

/**
 * Created by Administrator on 2017/6/4.
 */

public class TeamManageActivity extends BaseMVPActivity<TeamManagePresenter> implements ITeamManageUI {

    public static final String TEAM_MANAGE_USER = "team_manage_user";
    private SwipeRefreshLayout mRefreshLayout;
    private FloatingActionButton mCreateTeamFab;
    private TeamListRecyclerViewAdapter mTeamListAdapter;
    private RecyclerView mTeamListRecyclerView;

    private DataUser user;

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
        Intent intent = new Intent(context, TeamManageActivity.class);
        intent.putExtra(TEAM_MANAGE_USER, user);
        context.startActivity(intent);
    }

    @Override
    protected void onCreateExecute(Bundle savedInstanceState) {
        setContentView(R.layout.activity_team_manage);
        setupActionBar();
        initViews();
        initDatas();
    }

    private void initViews() {
        mCreateTeamFab = (FloatingActionButton) findViewById(R.id.create_team_fab);
        mCreateTeamFab.setOnClickListener(new NoDoubleViewClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                createTeam();
            }
        });

        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.team_refresh_layout);
        mRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (user != null)
                    getPresenter().queryTeamList(TextUtils.isEmpty(user.getTeams()) ? "-1" : user.getTeams());
            }
        });

        mTeamListRecyclerView = (RecyclerView) findViewById(R.id.team_listView);
        mTeamListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mTeamListAdapter = new TeamListRecyclerViewAdapter(this);
        mTeamListRecyclerView.setAdapter(mTeamListAdapter);
    }

    private void initDatas() {
        user = (DataUser) getIntent().getSerializableExtra(TEAM_MANAGE_USER);
        mRefreshLayout.setRefreshing(true);
        if (user != null)
            getPresenter().queryTeamList(TextUtils.isEmpty(user.getTeams()) ? "-1" : user.getTeams());
    }

    private void createTeam() {

    }

    @Override
    public void onQueryTeamsSuccess(List<DataTeam> dataTeams) {
        if (dataTeams != null && mTeamListAdapter != null)
            mTeamListAdapter.addAll(dataTeams);
        mRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onQueryTeamsFail(String errorMsg) {
        new ToastUtil(this).showToastShort(errorMsg);
        mRefreshLayout.setRefreshing(false);
    }
}
