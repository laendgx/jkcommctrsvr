package com.boco.protocolBody;

import java.io.Serializable;

/**
 * 消息标识实体
 *
 */
public class Identity implements Serializable {
    private static final long serialVersionUID = 3229225226823944124L;
    /**
     * 消息发送方的标识
     */
    private String sourceId;
    /**
     * 消息的目标标识
     */
    private String targetId;
    /**
     * 设备id
     */
    private String devId;
    /**
     * 返消息发送时间
     */
    private String time;

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
