package com.ysy.mindmap.presenters;

import com.ysy.mindmap.bases.BasePresenter;
import com.ysy.mindmap.interfaces.IRegister;
import com.ysy.mindmap.models.datas.DataUser;
import com.ysy.mindmap.uis.editor.RegisterErrorConstant;
import com.ysy.mindmap.utils.StringUtil;

/**
 * Created by Sylvester on 17/5/3.
 */

public class RegisterPresenter extends BasePresenter<IRegister> {

    public void toRegister(DataUser newUser, String rePw) {

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
        if (!newUser.getBirthday().contains("-"))
            return RegisterErrorConstant.ERROR_BIRTHDAY_EMPTY;
        if (newUser.getUsername().length() > 32)
            return RegisterErrorConstant.ERROR_USERNAME_MORE_32;
        if (newUser.getPw().length() > 32 || rePw.length() > 32)
            return RegisterErrorConstant.ERROR_PW_MORE_24;
        if (newUser.getNickname().length() > 32)
            return RegisterErrorConstant.ERROR_NICKNAME_MORE_32;
        if (newUser.getNickname().length() > 32 && StringUtil.getChineseCount(newUser.getNickname()) > 16)
            return RegisterErrorConstant.ERROR_NICKNAME_MORE_CHN_16;
        return -1;
    }
}
