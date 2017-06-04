package com.ysy.mindmap.presenters;

import com.ysy.mindmap.bases.BasePresenter;
import com.ysy.mindmap.interfaces.IUserMapManageUI;
import com.ysy.mindmap.models.datas.DataTeam;
import com.ysy.mindmap.models.datas.DataTeamMindMap;
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

public class UserMapManagePresenter extends BasePresenter<IUserMapManageUI> {

    public void queryUserMapList(String teams) {
        ConnectionDetector cd = new ConnectionDetector(getContext());
        if (!cd.isConnectingToInternet()) {
            getUI().onQueryUserMapsFail("请检查网络连接哦");
            return;
        }

        SQLEntity<String> entity = new SQLEntity<>();
        entity.setSQL("select data, teamname, tmid, mapname, m.intro, m.tid, partners, is_cloneable, valids, m.create_time, m.update_time " +
                "from team_map m, team t where " +
                "m.tid in (" + teams + ") and m.tid = t.tid"
        );
        entity.setType(SQLRequest.RequestType.SELECT);
        SQLClient.invokeStringRequest(getUI().getActivity(), entity, new SQLCallback<ResultSet>() {
            @Override
            public void onSuccess(ResultSet res) {
                try {
                    List<DataTeamMindMap> dataTeamMindMaps = new ArrayList<>();
                    while (res.next()) {
                        DataTeamMindMap map = new DataTeamMindMap();
                        map.setData(res.getString(1));
                        map.setOwnTeam(res.getString(2));
                        map.setTmid(res.getLong(3));
                        map.setMapname(res.getString(4));
                        map.setIntro(res.getString(5));
                        map.setTid(res.getLong(6));
                        map.setPartners(res.getString(7));
                        map.setCloneable(res.getBoolean(8));
                        map.setValids(res.getString(9));
                        map.setCreateTime(res.getString(10));
                        map.setUpdateTime(res.getString(11));
                        dataTeamMindMaps.add(map);
                    }
                    getUI().onQueryUserMapsSuccess(dataTeamMindMaps);
                } catch (SQLException e) {
                    e.printStackTrace();
                    getUI().onQueryUserMapsFail(SQLErrorConstant.getErrorMsg(SQLErrorConstant.ERROR_SYSTEM));
                }
            }

            @Override
            public void onFail(int errorCode) {
                getUI().onQueryUserMapsFail(SQLErrorConstant.getErrorMsg(errorCode));
            }
        });
    }
}
