package com.ysy.mindmap.utils.dbconnector;

/**
 * Created by Sylvester on 17/4/18.
 */

public class SQLErrorConstant {

    public static int ERROR_TIMEOUT = 0;
    public static int ERROR_NETWORK = 1;
    public static int ERROR_SYSTEM = 2;
    public static int ERROR_DUPLICATE = 3;

    public static String getErrorMsg(int errorCode) {
        String msg = null;
        switch (errorCode) {
            case 0:
                msg = "连接超时啦ㄒoㄒ，请稍后重试";
                break;
            case 1:
                msg = "网络异常(⊙﹏⊙)b，请稍后重试";
                break;
            case 2:
                msg = "系统错误T_T，请稍后重试";
                break;
            case 3:
                msg = "用户已存在啦，想个别的吧";
                break;
            default:
                break;
        }
        return msg;
    }
}
