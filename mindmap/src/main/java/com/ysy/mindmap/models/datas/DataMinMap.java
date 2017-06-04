package com.ysy.mindmap.models.datas;

import com.ysy.mindmap.bases.BaseData;

/**
 * Created by Administrator on 2017/6/4.
 */

public class DataMinMap extends BaseData {

    private String createTime;
    private String updateTime;
    private String mapname;
    private String data;
    private boolean isCloneable;
    private String intro;

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public void setCloneable(boolean cloneable) {
        isCloneable = cloneable;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setMapname(String mapname) {
        this.mapname = mapname;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getData() {
        return data;
    }

    public String getMapname() {
        return mapname;
    }

    public String getIntro() {
        return intro;
    }
}
