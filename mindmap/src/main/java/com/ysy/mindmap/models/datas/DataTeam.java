package com.ysy.mindmap.models.datas;

import com.ysy.mindmap.bases.BaseData;

/**
 * Created by Administrator on 2017/6/4.
 */

public class DataTeam extends BaseData {

    private long tid;
    private String teamname;
    private String intro;
    private String avatar;
    private String members;
    private String createTime;
    private String updateTime;

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public void setMembers(String members) {
        this.members = members;
    }

    public void setTeamname(String teamname) {
        this.teamname = teamname;
    }

    public void setTid(long tid) {
        this.tid = tid;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public long getTid() {
        return tid;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getIntro() {
        return intro;
    }

    public String getMembers() {
        return members;
    }

    public String getTeamname() {
        return teamname;
    }

    public String getUpdateTime() {
        return updateTime;
    }
}
