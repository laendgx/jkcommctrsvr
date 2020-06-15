package com.boco.commdevinfocache;

import java.io.Serializable;

public class DevVarInfoCache implements Serializable {

    private static final long serialVersionUID = 4345461957013373625L;
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
     * 设备变量类型id
     */
    private String   devVarTypeId;
    /**
     * 设备变量类型描述
     */
    private String   devVarTypeDesc;
    /**
     * 设备变量值（设备变量最后一次采集值），用于比较设备变量采集变化，变化时进行websocket数据上传
     */
    private String   devVarLastValue;
    /**
     * 设备变量分组编码
     */
    private String   devVarGroupId;

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

    public String getCollCtrTime() {
        return collCtrTime;
    }

    public void setCollCtrTime(String collCtrTime) {
        this.collCtrTime = collCtrTime;
    }

    public String getDevVarTypeDesc() {
        return devVarTypeDesc;
    }

    public void setDevVarTypeDesc(String devVarTypeDesc) {
        this.devVarTypeDesc = devVarTypeDesc;
    }

    public String getDevVarLastValue() {
        return devVarLastValue;
    }

    public void setDevVarLastValue(String devVarLastValue) {
        this.devVarLastValue = devVarLastValue;
    }

    public String getDevVarGroupId() {
        return devVarGroupId;
    }

    public void setDevVarGroupId(String devVarGroupId) {
        this.devVarGroupId = devVarGroupId;
    }

    public String getDevVarTypeId() {
        return devVarTypeId;
    }

    public void setDevVarTypeId(String devVarTypeId) {
        this.devVarTypeId = devVarTypeId;
    }
}
