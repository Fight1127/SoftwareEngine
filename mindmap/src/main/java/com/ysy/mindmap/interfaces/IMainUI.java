package com.ysy.mindmap.interfaces;

import com.ysy.mindmap.bases.IUI;
import com.ysy.mindmap.models.datas.DataUser;

/**
 * Created by Sylvester on 17/5/3.
 */

public interface IMainUI extends IUI {

    void onQueryUserSuccess(DataUser user);

    void onQueryUserFail(String errorMsg);

    void onCommitMindMapFinished(String msg);

    void onPullMindMapSuccess(String json);

    void onPullMindMapFail(String errorMsg);
}
