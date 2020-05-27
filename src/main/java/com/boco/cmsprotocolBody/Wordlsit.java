package com.boco.cmsprotocolBody;

import java.io.Serializable;

public class Wordlsit implements Serializable {

    private static final long serialVersionUID = 5015026804980254177L;

    /**
     * 文字起点x坐标
     */
    private Integer wx;
    /**
     * 文字起点y坐标
     */
    private Integer wy;
    /**
     * 文字内容
     */
    private String wc;

    public Integer getWx() {
        return wx;
    }

    public void setWx(Integer wx) {
        this.wx = wx;
    }

    public Integer getWy() {
        return wy;
    }

    public void setWy(Integer wy) {
        this.wy = wy;
    }

    public String getWc() {
        return wc;
    }

    public void setWc(String wc) {
        this.wc = wc;
    }
}
