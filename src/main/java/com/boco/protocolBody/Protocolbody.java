package com.boco.protocolBody;

import cms.cmsconst.ReturnState;

import java.io.Serializable;

/**
 * 通讯协议包父类
 */
public class Protocolbody  implements Serializable {

    private static final long serialVersionUID = -629093441640661383L;
    /**
     * 通讯业务编码（自定义），为消息级通讯唯一标识。
     */
    private String businessno;
    /**
     * 通讯标识及设备标识
     */
    private Identity identity;
    /**
     * 消息传输类型
     */
    private InfoType infoType;

    /**
     * 只在响应类消息中存在，非响应数据时为空，存放处理的结果。
     */
    private ReturnState returnState;

    public Protocolbody() {
    }

    public String getBusinessno() {
        return businessno;
    }

    public void setBusinessno(String businessno) {
        this.businessno = businessno;
    }

    public InfoType getInfoType() {
        return infoType;
    }

    public void setInfoType(InfoType infoType) {
        this.infoType = infoType;
    }

    public ReturnState getReturnState() {
        return returnState;
    }

    public void setReturnState(ReturnState returnState) {
        this.returnState = returnState;
    }

    public Identity getIdentity() {
        return identity;
    }

    public void setIdentity(Identity identity) {
        this.identity = identity;
    }
}
