package com.boco.protocolBody;

import java.io.Serializable;

public class DevVarInfo implements Serializable {

    private static final long serialVersionUID = 4345461957013373625L;

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

    public String getDevvartypeid() {
        return devvartypeid;
    }

    public void setDevvartypeid(String devvartypeid) {
        this.devvartypeid = devvartypeid;
    }

    public String getDevvarvalue() {
        return devvarvalue;
    }

    public void setDevvarvalue(String devvarvalue) {
        this.devvarvalue = devvarvalue;
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
}
