package com.boco.cmsprotocolBody;

import java.io.Serializable;

/**
 * 情报板控制下发实体
 *
 */
public class CmsCmdProtocol  implements Serializable {
    private static final long serialVersionUID = -183404096187892460L;
    /**
     * 用户名编码useid
     */
    private String  useid;
    /**
     * 设备机构id
     */
    private String  orgid;
    /**
     * 设备id
     */
    private String devId;
    /**
     * 设备变量类型id
     */
    private String   devvartypeid;
    /**
     * 设备变量值（设备命令下发值或设备变量采集值）,情报板播放表控制playlist
     */
    private Playlist   playlist;
    /**
     * 设备变量值（设备命令下发值或设备变量采集值）,非情报板设备控制字段
     */
    private String   devvarvalue;

    public String getOrgid() {
        return orgid;
    }

    public void setOrgid(String orgid) {
        this.orgid = orgid;
    }

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }

    public String getDevvartypeid() {
        return devvartypeid;
    }

    public void setDevvartypeid(String devvartypeid) {
        this.devvartypeid = devvartypeid;
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }

    public String getDevvarvalue() {
        return devvarvalue;
    }

    public void setDevvarvalue(String devvarvalue) {
        this.devvarvalue = devvarvalue;
    }

    public String getUseid() {
        return useid;
    }

    public void setUseid(String useid) {
        this.useid = useid;
    }
}
