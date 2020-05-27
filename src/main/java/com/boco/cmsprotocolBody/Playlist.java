package com.boco.cmsprotocolBody;

import java.io.Serializable;
import java.util.List;

/**
 * Cms通讯协议包
 */
public class Playlist implements Serializable {

    private static final long serialVersionUID = -811180490562398435L;
    /**
     * 可变情报板屏幕宽度
     */
    private Integer dpw;
    /**
     * 可变情报板屏幕高度度
     */
    private Integer dph;
    /**
     * 显示屏类型  0-双基色或全彩；1－琥珀色；2-不需要设置颜色
     */
    private Integer dpt;
    /**
     * 可变情报板每屏的图标参数列表
     */
    private List<Itemlist> itemlist;

    public Integer getDpw() {
        return dpw;
    }

    public void setDpw(Integer dpw) {
        this.dpw = dpw;
    }

    public Integer getDph() {
        return dph;
    }

    public void setDph(Integer dph) {
        this.dph = dph;
    }

    public Integer getDpt() {
        return dpt;
    }

    public void setDpt(Integer dpt) {
        this.dpt = dpt;
    }

    public List<Itemlist> getItemlist() {
        return itemlist;
    }

    public void setItemlist(List<Itemlist> itemlist) {
        this.itemlist = itemlist;
    }
}
