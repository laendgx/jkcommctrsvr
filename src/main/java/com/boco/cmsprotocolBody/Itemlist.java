package com.boco.cmsprotocolBody;

import java.io.Serializable;
import java.util.List;

public class Itemlist implements Serializable {

    private static final long serialVersionUID = -69864080257728808L;
    /**
     * 停留时间，单位：百分之一秒
     */
    private Integer delay;
    /**
     * 出字方式
     */
    private Integer mode;
    /**
     * 字体颜色，红r、绿g、黄y、琥珀色a
     */
    private String fc;
    /**
     * 字体大小③　通常分为16,24,32,48
     */
    private Integer fs;
    /**
     * 字体
     * s－宋体  h－黑体  k－楷体
     */
    private String fn;
    /**
     * 可变情报板每屏的图标参数列表
     */
    private List<Iconlist> graphList;
    /**
     * 可变情报板每屏的文字参数列表
     */
    private List<Wordlsit> wordList;

    public Integer getDelay() {
        return delay;
    }

    public void setDelay(Integer delay) {
        this.delay = delay;
    }

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    public String getFc() {
        return fc;
    }

    public void setFc(String fc) {
        this.fc = fc;
    }

    public Integer getFs() {
        return fs;
    }

    public void setFs(Integer fs) {
        this.fs = fs;
    }

    public String getFn() {
        return fn;
    }

    public void setFn(String fn) {
        this.fn = fn;
    }

    public List<Iconlist> getGraphList() {
        return graphList;
    }

    public void setGraphList(List<Iconlist> graphList) {
        this.graphList = graphList;
    }

    public List<Wordlsit> getWordList() {
        return wordList;
    }

    public void setWordList(List<Wordlsit> wordList) {
        this.wordList = wordList;
    }
}

