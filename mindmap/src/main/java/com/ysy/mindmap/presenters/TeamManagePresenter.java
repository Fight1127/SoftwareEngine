package com.ysy.mindmap.presenters;

import com.ysy.mindmap.bases.BasePresenter;
import com.ysy.mindmap.interfaces.ITeamManageUI;
import com.ysy.mindmap.models.datas.DataTeam;
import com.ysy.mindmap.models.datas.DataUser;
import com.ysy.mindmap.utils.ConnectionDetector;
import com.ysy.mindmap.utils.dbconnector.SQLCallback;
import com.ysy.mindmap.utils.dbconnector.SQLClient;
import com.ysy.mindmap.utils.dbconnector.SQLEntity;
import com.ysy.mindmap.utils.dbconnector.SQLErrorConstant;
import com.ysy.mindmap.utils.dbconnector.SQLRequest;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/4.
 */

public class TeamManagePresenter extends BasePresenter<ITeamManageUI> {

    public void queryTeamList(String teams) {
        ConnectionDetector cd = new ConnectionDetector(getContext());
        if (!cd.isConnectingToInternet()) {
            getUI().onQueryTeamsFail("请检查网络连接哦");
            return;
        }

        SQLEntity<String> entity = new SQLEntity<>();
        entity.setSQL("select tid, teamname, intro, avatar, members, create_time, update_time from team where " +
                "tid in (" + teams + ")"
        );
        entity.setType(SQLRequest.RequestType.SELECT);
        SQLClient.invokeStringRequest(getUI().getActivity(), entity, new SQLCallback<ResultSet>() {
            @Override
            public void onSuccess(ResultSet res) {
                try {
                    List<DataTeam> dataTeams = new ArrayList<>();
                    while (res.next()) {
                        DataTeam team = new DataTeam();
                        team.setTid(res.getLong(1));
                        team.setTeamname(res.getString(2));
                        team.setIntro(res.getString(3));
                        team.setAvatar(res.getString(4));
                        team.setMembers(res.getString(5));
                        team.setCreateTime(res.getString(6));
                        team.setUpdateTime(res.getString(7));
                        dataTeams.add(team);
                    }
                    getUI().onQueryTeamsSuccess(dataTeams);
                } catch (SQLException e) {
                    e.printStackTrace();
                    getUI().onQueryTeamsFail(SQLErrorConstant.getErrorMsg(SQLErrorConstant.ERROR_SYSTEM));
                }
            }

            @Override
            public void onFail(int errorCode) {
                getUI().onQueryTeamsFail(SQLErrorConstant.getErrorMsg(errorCode));
            }
        });
    }
}
