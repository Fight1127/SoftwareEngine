package com.ysy.mindmap.models.datas;

import com.ysy.mindmap.bases.BaseData;

/**
 * Created by Sylvester on 17/5/4.
 */

public class DataUser extends BaseData {

    private long uid;
    private String pw;
    private String username;
    private byte sex;
    private String birthday;
    private String avatarUrl;
    private String nickname;
    private String intro;
    private boolean isMsgRead;
    private String createTime;
    private String updateTime;

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public String getPw() {
        return pw;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setSex(byte sex) {
        this.sex = sex;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public void setMsgRead(boolean msgRead) {
        isMsgRead = msgRead;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public byte getSex() {
        return sex;
    }

    public String getUsername() {
        return username;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getNickname() {
        return nickname;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getIntro() {
        return intro;
    }

    public boolean isMsgRead() {
        return isMsgRead;
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }
}
