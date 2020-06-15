package com.boco.comm;

import com.boco.cmsprotocolBody.PlayList;

import java.io.Serializable;

/**
 * web前端对情报板控制下发实体
 *
 */
public class CmsCmdProtocol  implements Serializable {
    private static final long serialVersionUID = -183404096187892460L;
    /**
     * 用户名编码useid
     */
    private String  useId;
    /**
     * 设备机构id
     */
    private String  orgId;
    /**
     * 设备id
     */
    private String devId;
    /**
     * 设备变量类型id
     */
    private String   devVarTypeId;
    /**
     * 设备变量值（设备命令下发值或设备变量采集值）,情报板播放表控制playlist
     */
    private PlayList playList;
    /**
     * 设备变量值（设备命令下发值或设备变量采集值）,非情报板设备控制字段
     */
    private String   devVarValue;

    public String getUseId() {
        return useId;
    }

    public void setUseId(String useId) {
        this.useId = useId;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }

    public String getDevVarTypeId() {
        return devVarTypeId;
    }

    public void setDevVarTypeId(String devVarTypeId) {
        this.devVarTypeId = devVarTypeId;
    }

    public PlayList getPlayList() {
        return playList;
    }

    public void setPlayList(PlayList playList) {
        this.playList = playList;
    }

    public String getDevVarValue() {
        return devVarValue;
    }

    public void setDevVarValue(String devVarValue) {
        this.devVarValue = devVarValue;
    }
}
