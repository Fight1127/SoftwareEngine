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
import com.ysy.mindmap.interfaces.IUserMapManageUI;
import com.ysy.mindmap.models.datas.DataTeamMindMap;
import com.ysy.mindmap.models.datas.DataUser;
import com.ysy.mindmap.models.listeners.OnRecyclerViewItemClickListener;
import com.ysy.mindmap.presenters.UserMapManagePresenter;
import com.ysy.mindmap.uis.main.MainActivity;
import com.ysy.mindmap.utils.ToastUtil;

import java.util.List;

/**
 * Created by Administrator on 2017/6/4.
 */

public class UserMapManageActivity extends BaseMVPActivity<UserMapManagePresenter> implements IUserMapManageUI {

    private static final String USER_MAP_MANAGE_USER = "user_map_manage_user";
    private DataUser user;

    private SwipeRefreshLayout mRefreshLayout;
    private UserMapListRecyclerViewAdapter mUserMapListAdapter;
    private RecyclerView mUserMapListRecyclerView;

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    protected UserMapManagePresenter createPresenter() {
        return new UserMapManagePresenter();
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
        Intent intent = new Intent(context, UserMapManageActivity.class);
        intent.putExtra(USER_MAP_MANAGE_USER, user);
        context.startActivity(intent);
    }

    @Override
    protected void onCreateExecute(Bundle savedInstanceState) {
        setContentView(R.layout.activity_user_map_manage);
        setupActionBar();
        initViews();
        initDatas();
    }

    private void initViews() {
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.user_map_refresh_layout);
        mRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccentDark));
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (user != null)
                    getPresenter().queryUserMapList(TextUtils.isEmpty(user.getTeams()) ? "-1" : user.getTeams());
            }
        });

        mUserMapListRecyclerView = (RecyclerView) findViewById(R.id.user_map_listView);
        mUserMapListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mUserMapListAdapter = new UserMapListRecyclerViewAdapter(this);
        mUserMapListRecyclerView.setAdapter(mUserMapListAdapter);
        mUserMapListAdapter.setOnItemClickListener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                DataTeamMindMap map = mUserMapListAdapter.getItem(position);
                if (map != null) {
                    String mapJson = map.getData();
                    if (TextUtils.isEmpty(mapJson)) {
                        mapJson = getString(R.string.map_json);
                        map.setData(mapJson);
                    }
                    Intent intent = new Intent(UserMapManageActivity.this, MainActivity.class);
                    intent.putExtra("mind_map", map);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    private void initDatas() {
        user = (DataUser) getIntent().getSerializableExtra(USER_MAP_MANAGE_USER);
        mRefreshLayout.setRefreshing(true);
        if (user != null)
            getPresenter().queryUserMapList(TextUtils.isEmpty(user.getTeams()) ? "-1" : user.getTeams());
    }

    @Override
    public void onQueryUserMapsSuccess(List<DataTeamMindMap> mapList) {
        if (mapList != null && mUserMapListAdapter != null)
            mUserMapListAdapter.addAll(mapList);
        mRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onQueryUserMapsFail(String errorMsg) {
        new ToastUtil(this).showToastShort(errorMsg);
        mRefreshLayout.setRefreshing(false);
    }
}
