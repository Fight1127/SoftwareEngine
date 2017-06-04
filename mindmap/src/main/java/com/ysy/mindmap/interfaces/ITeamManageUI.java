package com.ysy.mindmap.interfaces;

import com.ysy.mindmap.bases.IUI;
import com.ysy.mindmap.models.datas.DataTeam;

import java.util.List;

/**
 * Created by Administrator on 2017/6/4.
 */

public interface ITeamManageUI extends IUI {

    void onQueryTeamsSuccess(List<DataTeam> dataTeams);

    void onQueryTeamsFail(String errorMsg);
}
