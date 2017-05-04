package com.ysy.mindmap.interfaces;

import com.ysy.mindmap.bases.IUI;
import com.ysy.mindmap.models.datas.DataUser;

/**
 * Created by Sylvester on 17/5/3.
 */

public interface ILogin extends IUI {
    void onLoginSuccess(DataUser user);

    void onLoginFail(String errorMsg);
}
