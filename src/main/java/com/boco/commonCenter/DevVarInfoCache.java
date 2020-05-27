package com.boco.commonCenter;

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
    private String   devvartypeid;
    /**
     * 设备变量类型描述
     */
    private String   devvartypedesc;
    /**
     * 设备变量值（设备变量最后一次采集值），用于比较设备变量采集变化，变化时进行websocket数据上传
     */
    private String   devvarlastvalue;
    /**
     * 设备变量分组编码
     */
    private String   devvargroupid;

    public String getDevvartypeid() {
        return devvartypeid;
    }

    public void setDevvartypeid(String devvartypeid) {
        this.devvartypeid = devvartypeid;
    }


    public String getDevvartypedesc() {
        return devvartypedesc;
    }

    public void setDevvartypedesc(String devvartypedesc) {
        this.devvartypedesc = devvartypedesc;
    }

    public String getDevvargroupid() {
        return devvargroupid;
    }

    public void setDevvargroupid(String devvargroupid) {
        this.devvargroupid = devvargroupid;
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

    public String getCollCtrTime() {
        return collCtrTime;
    }

    public void setCollCtrTime(String collCtrTime) {
        this.collCtrTime = collCtrTime;
    }

    public String getDevvarlastvalue() {
        return devvarlastvalue;
    }

    public void setDevvarlastvalue(String devvarlastvalue) {
        this.devvarlastvalue = devvarlastvalue;
    }
}
