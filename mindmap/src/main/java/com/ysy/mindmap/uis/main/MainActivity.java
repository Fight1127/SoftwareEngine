package com.ysy.mindmap.uis.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ysy.mindmap.R;
import com.ysy.mindmap.bases.BaseMVPActivity;
import com.ysy.mindmap.bases.IUI;
import com.ysy.mindmap.interfaces.IMainUI;
import com.ysy.mindmap.models.MindMapItem;
import com.ysy.mindmap.models.datas.DataUser;
import com.ysy.mindmap.presenters.MainPresenter;
import com.ysy.mindmap.uis.mindmap.MindMapEditMode;
import com.ysy.mindmap.uis.mindmap.MindMapItemActionRequestListener;
import com.ysy.mindmap.uis.mindmap.MindMapView;
import com.ysy.mindmap.uis.mindmap.OnSelectedBulletChangeListener;
import com.ysy.mindmap.uis.mindmap.components.Bullet;
import com.ysy.mindmap.utils.ToastUtil;

public class MainActivity extends BaseMVPActivity<MainPresenter>
        implements IMainUI, NavigationView.OnNavigationItemSelectedListener {

    private static final String MAIN_USER = "main_user";
    private static final String MAIN_UID = "main_uid";

    private MindMapView mindMapView;
    private LinearLayout mActionLayout;
    private EditText mContentEdt;
    private MindMapEditMode currentEditMode;

    private TextView mNicknameTv;
    private TextView mIntroTv;
    private ImageView mAvatarImg;

    private DataUser user = null;
    private long uid = -1;

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
    protected void onCreateExecute(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        initViews();
        initDatas();
    }

    private void initDatas() {
        user = (DataUser) getIntent().getSerializableExtra(MAIN_USER);
        uid = getIntent().getLongExtra(MAIN_UID, -1);
        if (uid == -1 && user != null)
            uid = user.getUid();
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

        mActionLayout = (LinearLayout) findViewById(R.id.action_layout);
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

    public void refreshView(View view) {
        mindMapView.invalidate();
    }

    public void onEditClicked(View v) {
        if (mindMapView.getSelectedMindMapItem() != null) {
            currentEditMode = MindMapEditMode.EDIT;
            mContentEdt.setText(mindMapView.getSelectedMindMapItem().getText());
            mContentEdt.setVisibility(View.VISIBLE);
            mContentEdt.requestFocus();
        }
    }

    public void onAddSiblingClicked(View v) {
        if (mindMapView.getSelectedMindMapItem() != null) {
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
            super.onBackPressed();
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
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
                mindMapView.getSelectedMindMapItem().setText(newText);
                mindMapView.invalidate();
            }
        }
    };

    private OnSelectedBulletChangeListener selectedBulletChangeListener = new OnSelectedBulletChangeListener() {
        public void onSelectedBulletChanged(View v, Bullet selectedBullet) {
            if (selectedBullet != null) {
                mActionLayout.setVisibility(View.VISIBLE);
            } else {
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
        if (mNicknameTv != null && !TextUtils.isEmpty(user.getNickname()))
            mNicknameTv.setText(user.getNickname());
        if (mIntroTv != null && !TextUtils.isEmpty(user.getIntro()))
            mIntroTv.setText(user.getIntro());
    }

    @Override
    public void onQueryUserFail(String errorMsg) {
        new ToastUtil(this).showToastShort(errorMsg);
    }
}
