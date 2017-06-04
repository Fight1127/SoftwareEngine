package com.ysy.mindmap.interfaces;

import com.ysy.mindmap.bases.IUI;
import com.ysy.mindmap.models.datas.DataTeamMindMap;

import java.util.List;

/**
 * Created by Administrator on 2017/6/4.
 */

public interface IUserMapManageUI extends IUI {

    void onQueryUserMapsSuccess(List<DataTeamMindMap> mapList);

    void onQueryUserMapsFail(String errorMsg);
}
