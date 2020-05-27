package com.boco.cmsprotocolBody;

import java.io.Serializable;

public class Iconlist implements Serializable {
    private static final long serialVersionUID = -8355626034348940215L;

    /**
     * 可变情报板固定图片起点X坐标
     */
    private Integer gx;
    /**
     * 可变情报板固定图片起点Y坐标
     */
    private Integer gy;
    /**
     * 可变情报板固定图片id
     */
    private String gid;
    /**
     * 可变情报板固定图片名称
     */
    private String gn;

    public Integer getGx() {
        return gx;
    }

    public void setGx(Integer gx) {
        this.gx = gx;
    }

    public Integer getGy() {
        return gy;
    }

    public void setGy(Integer gy) {
        this.gy = gy;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getGn() {
        return gn;
    }

    public void setGn(String gn) {
        this.gn = gn;
    }
}
