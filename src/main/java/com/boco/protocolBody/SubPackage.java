package com.boco.protocolBody;

import java.io.Serializable;
import java.util.List;

/**
 * 通讯协议消息内容实体
 */
public class SubPackage implements Serializable {
    private static final long serialVersionUID = 7703407291031281459L;
    /**
     * 用户名编码useid
     */
    private String  useid;
    /**
     * 设备机构id
     */
    private String  orgId;
    /**
     * 设备id
     */
    private String devId;
    /**
     * 设备采集控制时间
     */
    private String collCtrTime;
    /**
     * 消息数据实体
     */
    private List<DevVarInfo> devVarInfoList;

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getCollCtrTime() {
        return collCtrTime;
    }

    public void setCollCtrTime(String collCtrTime) {
        this.collCtrTime = collCtrTime;
    }

    public List<DevVarInfo> getDevVarInfoList() {
        return devVarInfoList;
    }

    public void setDevVarInfoList(List<DevVarInfo> devVarInfoList) {
        this.devVarInfoList = devVarInfoList;
    }

    public String getUseid() {
        return useid;
    }

    public void setUseid(String useid) {
        this.useid = useid;
    }
}
