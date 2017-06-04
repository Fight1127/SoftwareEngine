package com.ysy.mindmap.presenters;

import com.ysy.mindmap.bases.BasePresenter;
import com.ysy.mindmap.interfaces.ILoginUI;
import com.ysy.mindmap.models.datas.DataUser;
import com.ysy.mindmap.utils.ConnectionDetector;
import com.ysy.mindmap.utils.dbconnector.SQLCallback;
import com.ysy.mindmap.utils.dbconnector.SQLClient;
import com.ysy.mindmap.utils.dbconnector.SQLEntity;
import com.ysy.mindmap.utils.dbconnector.SQLErrorConstant;
import com.ysy.mindmap.utils.dbconnector.SQLRequest;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Sylvester on 17/5/3.
 */

public class LoginPresenter extends BasePresenter<ILoginUI> {

    public void toLogin(String username, final String pw) {
        ConnectionDetector cd = new ConnectionDetector(getContext());
        if (!cd.isConnectingToInternet()) {
            getUI().onLoginFail("请检查网络连接哦");
            return;
        }

        if (username.equals(""))
            getUI().onLoginFail("要有用户名哦");
        else if (pw.equals(""))
            getUI().onLoginFail("要有密码哦");
        else {
            SQLEntity<String> entity = new SQLEntity<>();
            entity.setSQL("select pw, uid, sex, avatar, nickname, intro, teams, create_time, update_time" +
                    " from user where username = '" + username + "'");
            entity.setType(SQLRequest.RequestType.SELECT);
            SQLClient.invokeStringRequest(getUI().getActivity(), entity, new SQLCallback<ResultSet>() {
                @Override
                public void onSuccess(ResultSet res) {
                    DataUser user = new DataUser();
                    try {
                        int loop = 0;
                        while (res.next()) {
                            if (res.getString(1).equals(pw)) {
                                user.setUid(res.getLong(2));
                                user.setSex(res.getByte(3));
                                user.setAvatar(res.getString(4));
                                user.setNickname(res.getString(5));
                                user.setIntro(res.getString(6));
                                user.setTeams(res.getString(7));
                                user.setCreateTime(res.getString(8));
                                user.setUpdateTime(res.getString(9));
                                getUI().onLoginSuccess(user);
                            } else
                                getUI().onLoginFail("密码错啦，再想想哦");
                            ++loop;
                        }
                        if (loop == 0)
                            getUI().onLoginFail(SQLErrorConstant.getErrorMsg(SQLErrorConstant.ERROR_USER_NOT_EXIST));
                    } catch (SQLException e) {
                        e.printStackTrace();
                        getUI().onLoginFail(SQLErrorConstant.getErrorMsg(SQLErrorConstant.ERROR_SYSTEM));
                    }
                }

                @Override
                public void onFail(int errorCode) {
                    getUI().onLoginFail(SQLErrorConstant.getErrorMsg(errorCode));
                }
            });
        }
    }
}
