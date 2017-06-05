package com.ysy.mindmap.uis.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ysy.mindmap.R;
import com.ysy.mindmap.bases.BaseMVPActivity;
import com.ysy.mindmap.bases.GlobalConstant;
import com.ysy.mindmap.bases.IUI;
import com.ysy.mindmap.interfaces.IMainUI;
import com.ysy.mindmap.models.MindMapItem;
import com.ysy.mindmap.models.datas.DataTeamMindMap;
import com.ysy.mindmap.models.datas.DataUser;
import com.ysy.mindmap.presenters.MainPresenter;
import com.ysy.mindmap.uis.manage.TeamManageActivity;
import com.ysy.mindmap.uis.manage.UserMapManageActivity;
import com.ysy.mindmap.uis.mindmap.MindMapEditMode;
import com.ysy.mindmap.uis.mindmap.MindMapItemActionRequestListener;
import com.ysy.mindmap.uis.mindmap.MindMapView;
import com.ysy.mindmap.uis.mindmap.OnSelectedBulletChangeListener;
import com.ysy.mindmap.uis.mindmap.components.Bullet;
import com.ysy.mindmap.utils.AppDataUtil;
import com.ysy.mindmap.utils.JsonUtil;
import com.ysy.mindmap.utils.ToastUtil;

public class MainActivity extends BaseMVPActivity<MainPresenter> implements IMainUI, NavigationView.OnNavigationItemSelectedListener {

    private static final String MAIN_USER = "main_user";
    private static final String MAIN_UID = "main_uid";

    private MindMapView mindMapView;
    private View mActionLayout;
    private EditText mContentEdt;
    private MindMapEditMode currentEditMode;
    private View mHistoryCard;
    private EditText mHistoryEdt;

    private TextView mNicknameTv;
    private TextView mIntroTv;
    private ImageView mAvatarImg;

    private DataUser user = null;
    private long uid = -1;

    private long teamMapId = -1;
    private String mapJson;

    private String lastHistory = "";
    private String currentHistory = "";

    private MindMapItem rootItem;
    private Handler mHandler;
    private Runnable mInitMapRunnable = new Runnable() {
        @Override
        public void run() {
            if (mindMapView != null) {
                mindMapView.updateMindMap(rootItem);
            }
        }
    };

    private Runnable mSaveSuccessRunnable = new Runnable() {
        @Override
        public void run() {
            new ToastUtil(MainActivity.this).showToastShort("成功保存到本地");
        }
    };

    private Runnable mCommitRunnable = new Runnable() {
        @Override
        public void run() {
            commitMindMapToTeam();
        }
    };

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    protected MainPresenter createPresenter() {
        return new MainPresenter();
    }

    @Override
    protected IUI getUI() {
        return this;
    }

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
    protected void onNewIntent(Intent intent) {
        DataTeamMindMap teamMindMap = (DataTeamMindMap) intent.getSerializableExtra("mind_map");
        if (teamMindMap != null) {
            mapJson = teamMindMap.getData();
            if (!TextUtils.isEmpty(mapJson)) {
                teamMapId = teamMindMap.getTmid();
                AppDataUtil aDU = new AppDataUtil(this);
                aDU.saveData(GlobalConstant.SP_SAVE_MAP_ID_PREF + (uid + ""), teamMapId);
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        rootItem = JsonUtil.jsonToMindMap(mapJson);
                        mHandler.post(mInitMapRunnable);
                    }
                }).start();
            }
        }
        super.onNewIntent(intent);
    }

    @Override
    protected void onCreateExecute(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        mHandler = new Handler();
        initViews();
        initDatas();
    }

    private void initMapFromSP(long uid) {
        AppDataUtil aDU = new AppDataUtil(this);
        mapJson = aDU.readStringData(GlobalConstant.SP_SAVE_MAP_PREF + (uid + ""), getString(R.string.map_json));
        teamMapId = aDU.readLongData(GlobalConstant.SP_SAVE_MAP_ID_PREF + (uid) + "");
        new Thread(new Runnable() {
            @Override
            public void run() {
                rootItem = JsonUtil.jsonToMindMap(mapJson);
                mHandler.post(mInitMapRunnable);
            }
        }).start();
    }

    private void initDatas() {
        user = (DataUser) getIntent().getSerializableExtra(MAIN_USER);
        uid = getIntent().getLongExtra(MAIN_UID, -1);
        if (uid == -1 && user != null) {
            uid = user.getUid();
            initMapFromSP(uid);
            if (mNicknameTv != null)
                mNicknameTv.setText(user.getNickname());
            if (mIntroTv != null)
                mIntroTv.setText(user.getIntro());
        }
        if (user == null)
            getPresenter().queryUser(uid);
    }

    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        mNicknameTv = (TextView) headerView.findViewById(R.id.main_drawer_header_nickname_tv);
        mIntroTv = (TextView) headerView.findViewById(R.id.main_drawer_header_intro_tv);
        mAvatarImg = (ImageView) headerView.findViewById(R.id.main_drawer_header_avatar_iv);

        mindMapView = (MindMapView) findViewById(R.id.mindMap);
        mindMapView.setOnSelectedBulletChangeListener(selectedBulletChangeListener);
        mindMapView.setMindMapActionListener(mindMapItemActionRequestListener);

        mActionLayout = findViewById(R.id.action_layout);
        mHistoryCard = findViewById(R.id.history_edt_card);
        mHistoryEdt = (EditText) findViewById(R.id.history_edt);
        initContentEdt();
    }

    private void initContentEdt() {
        mContentEdt = (EditText) findViewById(R.id.content_edt);
        mContentEdt.addTextChangedListener(mContentEdtWatcher);
        mContentEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (!event.isShiftPressed()) {
                        // the user is done typing.
                        hideKeyboard();
                        MindMapItem selectedItem = mindMapView.getSelectedMindMapItem();
                        if (currentEditMode == MindMapEditMode.EDIT) {
                            currentHistory = mContentEdt.getText().toString();
                            if (!TextUtils.isEmpty(selectedItem.getHistory()))
                                selectedItem.setHistory(selectedItem.getHistory() + "\n" + lastHistory + " -> " + currentHistory + "\n");
                            else
                                selectedItem.setHistory(selectedItem.getHistory() + lastHistory + " -> " + currentHistory + "\n");
                            currentEditMode = MindMapEditMode.UNDEFINED;
                            mContentEdt.setVisibility(View.INVISIBLE);
                            return true; // consume.
                        } else if (currentEditMode == MindMapEditMode.ADD_CHILD && selectedItem != null) {
                            selectedItem.addChild(mContentEdt.getText().toString());
                            currentEditMode = MindMapEditMode.UNDEFINED;
                            mContentEdt.setVisibility(View.INVISIBLE);
                            mindMapView.invalidate();
                        } else if (currentEditMode == MindMapEditMode.ADD_SIBLING && selectedItem != null) {
                            selectedItem.addSibling(mContentEdt.getText().toString());
                            currentEditMode = MindMapEditMode.UNDEFINED;
                            mContentEdt.setVisibility(View.INVISIBLE);
                            mindMapView.invalidate();
                        }
                    }
                }
                return false; // pass on to other listeners.
            }
        });
    }

    public void onEditClicked(View v) {
        if (mindMapView.getSelectedMindMapItem() != null) {
            lastHistory = mindMapView.getSelectedMindMapItem().getText();
            currentEditMode = MindMapEditMode.EDIT;
            mContentEdt.setText(lastHistory);
            mContentEdt.setVisibility(View.VISIBLE);
            mContentEdt.requestFocus();
        }
    }

    public void onShowHistoryClicked(View v) {
        if (mindMapView.getSelectedMindMapItem() != null) {
            if (mHistoryCard.getVisibility() == View.GONE) {
                mHistoryCard.setVisibility(View.VISIBLE);
                mHistoryEdt.setText(mindMapView.getSelectedMindMapItem().getHistory());
            } else {
                mHistoryEdt.setText("");
                mHistoryCard.setVisibility(View.GONE);
            }
        }
    }

    public void onLockClicked(View v) {
        MindMapItem mapItem = mindMapView.getSelectedMindMapItem();
        if (mapItem != null) {
            if (mapItem.getIsLocked() == 1) { // 如果锁定，点击后就解锁
                mapItem.setIsLocked((byte) 0);
                mapItem.setLockedBy(-1);
                new ToastUtil(this).showToastShort("已解锁");
            } else {
                mapItem.setIsLocked((byte) 1);
                mapItem.setLockedBy(uid);
                new ToastUtil(this).showToastShort("已加锁");
            }
        }
    }

    public void onAddSiblingClicked(View v) {
        if (mindMapView.getSelectedMindMapItem() != null) {
            if (mindMapView.getSelectedMindMapItem().getParent() == null) {
                new ToastUtil(MainActivity.this).showToastShort("根节点不能添加同系");
                return;
            }
            currentEditMode = MindMapEditMode.ADD_SIBLING;
            mContentEdt.setText("");
            mContentEdt.setVisibility(View.VISIBLE);
            mContentEdt.requestFocus();
        }
    }

    public void onAddChildClicked(View v) {
        if (mindMapView.getSelectedMindMapItem() != null) {
            currentEditMode = MindMapEditMode.ADD_CHILD;
            mContentEdt.setText("");
            mContentEdt.setVisibility(View.VISIBLE);
            mContentEdt.requestFocus();
        }
    }

    public void onDeleteSelectedClicked(View v) {
        MindMapItem item = mindMapView.getSelectedMindMapItem();
        if (item != null) {
            if (item.getChildren() != null && item.getChildren().size() > 0) // 当选中的item有子item时，不能执行deleteOne操作
                return;
            mindMapView.getSelectedMindMapItem().deleteOne();
            mindMapView.setSelectedBullet(null);
        }
    }

    public void onDeleteBranchClicked(View v) {
        if (mindMapView.getSelectedMindMapItem() != null) {
            mindMapView.getSelectedMindMapItem().deleteBranch();
            mindMapView.setSelectedBullet(null);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save) {
            if (teamMapId == -1) {
                new ToastUtil(this).showToastLong("当前导图为体验预览图，请先拉取或从“我的导图”选取后再保存");
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (mindMapView != null) {
                            AppDataUtil aDU = new AppDataUtil(MainActivity.this);
                            aDU.saveData(GlobalConstant.SP_SAVE_MAP_PREF + (uid + ""),
                                    JsonUtil.mindMapToJson(mindMapView.getMindMapRoot()));
                            aDU.saveData(GlobalConstant.SP_SAVE_MAP_ID_PREF + (uid + ""), teamMapId);
                            mHandler.post(mSaveSuccessRunnable);
                        }
                    }
                }).start();
            }
            return true;
        } else if (id == R.id.action_commit) {
            if (teamMapId != -1 && rootItem != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mapJson = JsonUtil.mindMapToJson(rootItem);
                        mHandler.post(mCommitRunnable);
                    }
                }).start();
            } else {
                new ToastUtil(this).showToastLong("请先拉取或从“我的导图”选取后再提交");
            }
        } else if (id == R.id.action_pull) {
            if (teamMapId != -1)
                getPresenter().pullMinMapFromTeam(teamMapId);
            else
                new ToastUtil(this).showToastLong("请先从“我的导图”选取后再拉取");
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_draw_board) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            if (mindMapView != null)
                mindMapView.invalidate();
        } else if (id == R.id.nav_user_map) {
            if (user != null)
                UserMapManageActivity.launch(this, user);
        } else if (id == R.id.nav_team_manage) {
            if (user != null)
                TeamManageActivity.launch(this, user);
        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_exit) {
            AppDataUtil aDU = new AppDataUtil(this);
            aDU.saveData(GlobalConstant.SP_UID, -1L);
            LoginActivity.launch(this);
            finish();
        }

        return true;
    }

    private TextWatcher mContentEdtWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (currentEditMode == MindMapEditMode.EDIT) {
                String newText = mContentEdt.getText().toString();
                if (TextUtils.isEmpty(newText)) {
                    new ToastUtil(MainActivity.this).showToastShort("不能为空哦");
                    return;
                }
                mindMapView.getSelectedMindMapItem().setText(newText);
                mindMapView.invalidate();
            }
        }
    };

    private OnSelectedBulletChangeListener selectedBulletChangeListener = new OnSelectedBulletChangeListener() {
        public void onSelectedBulletChanged(View v, Bullet selectedBullet) {
            if (selectedBullet != null) {
                MindMapItem mapItem = mindMapView.getSelectedMindMapItem();
                if (mapItem.getLockedBy() > 0 && mapItem.getLockedBy() != uid) {
                    mActionLayout.setVisibility(View.INVISIBLE);
                    mContentEdt.setVisibility(View.INVISIBLE);
                    new ToastUtil(MainActivity.this).showToastShort("已被他人上锁，无法修改");
                    return;
                }
                mActionLayout.setVisibility(View.VISIBLE);
                mContentEdt.setVisibility(View.INVISIBLE);
            } else {
                mHistoryCard.setVisibility(View.GONE);
                mActionLayout.setVisibility(View.INVISIBLE);
                mContentEdt.setVisibility(View.INVISIBLE);
            }
        }
    };

    private MindMapItemActionRequestListener mindMapItemActionRequestListener = new MindMapItemActionRequestListener() {
        public boolean requestBecomeChild(MindMapItem requestedParent, MindMapItem requestChild, int index) {
            requestedParent.addChild(requestChild);
            return true;
        }

        public boolean requestSwap(MindMapItem firstItem, MindMapItem secondItem) {
            if (firstItem == null || secondItem == null || firstItem == secondItem) {
                return false;
            }
            MindMapItem.SwapItems(firstItem, secondItem);
            return true;
        }
    };

    private void hideKeyboard() {
        InputMethodManager iMM = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null)
            iMM.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onQueryUserSuccess(DataUser user) {
        this.user = user;
        initMapFromSP(user.getUid());
        if (mNicknameTv != null && !TextUtils.isEmpty(user.getNickname()))
            mNicknameTv.setText(user.getNickname());
        if (mIntroTv != null && !TextUtils.isEmpty(user.getIntro()))
            mIntroTv.setText(user.getIntro());
    }

    @Override
    public void onQueryUserFail(String errorMsg) {
        new ToastUtil(this).showToastShort(errorMsg);
    }

    @Override
    public void onCommitMindMapFinished(String msg) {
        new ToastUtil(this).showToastShort(msg);
    }

    @Override
    public void onPullMindMapSuccess(final String json) {
        if (!TextUtils.isEmpty(json)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    rootItem = JsonUtil.jsonToMindMap(json);
                    mHandler.post(mInitMapRunnable);
                }
            }).start();
        }
        new ToastUtil(this).showToastLong("拉取成功");
    }

    @Override
    public void onPullMindMapFail(String errorMsg) {
        new ToastUtil(this).showToastShort(errorMsg);
    }

    private void commitMindMapToTeam() {
        getPresenter().commitMinMapToTeam(teamMapId, rootItem.getText(), mapJson);
    }
}
