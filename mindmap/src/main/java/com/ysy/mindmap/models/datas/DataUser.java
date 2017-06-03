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
    private String avatar;
    private String nickname;
    private String intro;
    private String teams;
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

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public void setTeams(String teams) {
        this.teams = teams;
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

    public String getNickname() {
        return nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getIntro() {
        return intro;
    }

    public String getTeams() {
        return teams;
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }
}
