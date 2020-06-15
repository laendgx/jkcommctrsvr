package com.boco.commwebsocket;

import com.boco.commdevinfocache.DevVarInfoCache;
import com.boco.protocolBody.ReturnState;

import java.io.Serializable;
import java.util.List;

/**
 * websocket发送消息包类型打包
 */
public class WebSocketCommPackage  implements Serializable {

    private static final long serialVersionUID = -747911143355079754L;
    /**
     * 消息传输类型
     */
    private String webInfoType;
    /**
     * 消息数据实体
     */
    private List<DevVarInfoCache> devVarInfoList;

    /**
     * 只在响应类消息中存在，非响应数据时为空，存放处理的结果。
     */
    private ReturnState returnState;

    public String getWebInfoType() {
        return webInfoType;
    }

    public void setWebInfoType(String webInfoType) {
        this.webInfoType = webInfoType;
    }

    public List<DevVarInfoCache> getDevVarInfoList() {
        return devVarInfoList;
    }

    public void setDevVarInfoList(List<DevVarInfoCache> devVarInfoList) {
        this.devVarInfoList = devVarInfoList;
    }

    public ReturnState getReturnState() {
        return returnState;
    }

    public void setReturnState(ReturnState returnState) {
        this.returnState = returnState;
    }
}
