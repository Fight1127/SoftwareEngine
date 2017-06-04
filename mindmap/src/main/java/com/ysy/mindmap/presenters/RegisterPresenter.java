package com.ysy.mindmap.presenters;

import com.ysy.mindmap.bases.BasePresenter;
import com.ysy.mindmap.interfaces.IRegisterUI;
import com.ysy.mindmap.models.datas.DataUser;
import com.ysy.mindmap.uis.editor.RegisterErrorConstant;
import com.ysy.mindmap.utils.ConnectionDetector;
import com.ysy.mindmap.utils.StringUtil;
import com.ysy.mindmap.utils.ToastUtil;
import com.ysy.mindmap.utils.dbconnector.SQLCallback;
import com.ysy.mindmap.utils.dbconnector.SQLClient;
import com.ysy.mindmap.utils.dbconnector.SQLEntity;
import com.ysy.mindmap.utils.dbconnector.SQLErrorConstant;
import com.ysy.mindmap.utils.dbconnector.SQLRequest;

import java.sql.ResultSet;

/**
 * Created by Sylvester on 17/5/3.
 */

public class RegisterPresenter extends BasePresenter<IRegisterUI> {

    public void toRegister(DataUser newUser, String rePw) {
        ConnectionDetector cd = new ConnectionDetector(getContext());
        if (!cd.isConnectingToInternet()) {
            getUI().onRegisterFail("请检查网络连接哦");
            return;
        }

        int resCode = checkTextLegality(newUser, rePw);
        if (resCode == -1) {
            SQLEntity<String> entity = new SQLEntity<>();
            entity.setSQL(
                    "insert into user(username, pw, nickname, sex, intro, teams, avatar, create_time, update_time) value('" +
                            newUser.getUsername() + "', '" +
                            newUser.getPw() + "', '" +
                            newUser.getNickname() + "', " +
                            newUser.getSex() + ", '', '', '', now(), now())"
            );
            entity.setType(SQLRequest.RequestType.INSERT);
            SQLClient.invokeStringRequest(getUI().getActivity(), entity, new SQLCallback<ResultSet>() {
                @Override
                public void onSuccess(ResultSet res) {
                    getUI().onRegisterSuccess();
                }

                @Override
                public void onFail(int errorCode) {
                    getUI().onRegisterFail(SQLErrorConstant.getErrorMsg(errorCode));
                }
            });
        } else {
            getUI().onRegisterFail(RegisterErrorConstant.getErrorMsg(resCode));
        }
    }

    private int checkTextLegality(DataUser newUser, String rePw) {
        if (StringUtil.replaceBlank(newUser.getUsername()).equals(""))
            return RegisterErrorConstant.ERROR_USERNAME_EMPTY;
        if (StringUtil.isHavingBlank(newUser.getUsername()))
            return RegisterErrorConstant.ERROR_USERNAME_SPACE;
        if (StringUtil.replaceBlank(newUser.getPw()).equals("") || StringUtil.replaceBlank(rePw).equals(""))
            return RegisterErrorConstant.ERROR_PW_EMPTY;
        if (StringUtil.isHavingBlank(newUser.getPw()) || StringUtil.isHavingBlank(rePw))
            return RegisterErrorConstant.ERROR_PW_SPACE;
        if (!newUser.getPw().equals(rePw))
            return RegisterErrorConstant.ERROR_REPW_NOT_SAME;
        if (newUser.getNickname().equals(""))
            return RegisterErrorConstant.ERROR_NICKNAME_EMPTY;
//        if (!newUser.getBirthday().contains("-"))
//            return RegisterErrorConstant.ERROR_BIRTHDAY_EMPTY;
        if (newUser.getUsername().length() > 24)
            return RegisterErrorConstant.ERROR_USERNAME_MORE_24;
        if (newUser.getPw().length() > 32 || rePw.length() > 32)
            return RegisterErrorConstant.ERROR_PW_MORE_32;
        if (newUser.getNickname().length() > 24)
            return RegisterErrorConstant.ERROR_NICKNAME_MORE_24;
        if (newUser.getNickname().length() > 24 && StringUtil.getChineseCount(newUser.getNickname()) > 12)
            return RegisterErrorConstant.ERROR_NICKNAME_MORE_CHN_12;
        return -1;
    }
}
