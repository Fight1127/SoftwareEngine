package com.ysy.mindmap.uis.editor;

/**
 * Created by Sylvester on 17/4/24.
 */

public class RegisterErrorConstant {

    public static int ERROR_USERNAME_SPACE = 0;
    public static int ERROR_PW_SPACE = 1;
    public static int ERROR_USERNAME_EMPTY = 2;
    public static int ERROR_PW_EMPTY = 3;
    public static int ERROR_REPW_NOT_SAME = 4;
    public static int ERROR_NICKNAME_EMPTY = 5;
    public static int ERROR_BIRTHDAY_EMPTY = 6;
    public static int ERROR_USERNAME_MORE_32 = 7;
    public static int ERROR_PW_MORE_24 = 8;
    public static int ERROR_NICKNAME_MORE_32 = 9;
    public static int ERROR_NICKNAME_MORE_CHN_16 = 10;

    public static String getErrorMsg(int errorCode) {
        String msg = null;
        switch (errorCode) {
            case 0:
                msg = "用户名不能包含空格哦";
                break;
            case 1:
                msg = "密码不能包含空格哦";
                break;
            case 2:
                msg = "要有用户名哦";
                break;
            case 3:
                msg = "要有密码哦";
                break;
            case 4:
                msg = "两次输入密码不一样呢";
                break;
            case 5:
                msg = "要有昵称哦";
                break;
            case 6:
                msg = "设置一下生日吧";
                break;
            case 7:
                msg = "用户名不能超过24个字符哦";
                break;
            case 8:
                msg = "密码不能超过24个字符哦";
                break;
            case 9:
                msg = "昵称不能超过24个字符哦";
                break;
            case 10:
                msg = "昵称不能包含超过12个汉字哦";
                break;
            default:
                break;
        }
        return msg;
    }
}
