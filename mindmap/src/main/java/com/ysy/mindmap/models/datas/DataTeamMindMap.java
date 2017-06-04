package com.ysy.mindmap.models.datas;

/**
 * Created by Administrator on 2017/6/4.
 */

public class DataTeamMindMap extends DataMinMap {

    private long tmid;
    private long tid;
    private String partners;
    private String valids;

    private String ownTeam;

    public void setOwnTeam(String ownTeam) {
        this.ownTeam = ownTeam;
    }

    public void setTid(long tid) {
        this.tid = tid;
    }

    public void setTmid(long tmid) {
        this.tmid = tmid;
    }

    public void setPartners(String partners) {
        this.partners = partners;
    }

    public void setValids(String valids) {
        this.valids = valids;
    }

    public long getTid() {
        return tid;
    }

    public long getTmid() {
        return tmid < 1 ? -1 : tmid;
    }

    public String getPartners() {
        return partners;
    }

    public String getValids() {
        return valids;
    }

    public String getOwnTeam() {
        return ownTeam;
    }
}
