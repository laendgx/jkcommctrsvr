package com.boco.protocolBody;

import java.io.Serializable;
import java.util.List;

/**
 * 返回web前端通讯协议返回消息内容实体
 */
public class CtrlReturnPackage implements Serializable {
    private static final long serialVersionUID = 7703407291031281459L;
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
     * 设备变量值（设备命令下发值或设备变量采集值）,具体内容根据devvartypeid来决定
     */
    private String   devvarvalue;
    /**
     * 设备变量分组编码
     */
    private String   devvargroupid;

    /**
     * 只在响应类消息中存在，非响应数据时为空，存放处理的结果。
     */
    private ReturnState returnState;

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }

    public String getOrgId() { return orgId; }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getCollCtrTime() {
        return collCtrTime;
    }

    public void setCollCtrTime(String collCtrTime) {
        this.collCtrTime = collCtrTime;
    }

    public ReturnState getReturnState() {
        return returnState;
    }

    public void setReturnState(ReturnState returnState) {
        this.returnState = returnState;
    }

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

    public String getDevvarvalue() {
        return devvarvalue;
    }

    public void setDevvarvalue(String devvarvalue) {
        this.devvarvalue = devvarvalue;
    }

    public String getDevvargroupid() {
        return devvargroupid;
    }

    public void setDevvargroupid(String devvargroupid) {
        this.devvargroupid = devvargroupid;
    }
}
