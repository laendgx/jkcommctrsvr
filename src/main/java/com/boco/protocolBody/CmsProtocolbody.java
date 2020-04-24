package com.boco.protocolBody;

import cms.cmsconst.CmsProtocalEntity;

import java.io.Serializable;

/**
 * Cms通讯协议包
 */
public class CmsProtocolbody extends Protocolbody implements Serializable {

    private static final long serialVersionUID = -811180490562398435L;

    /**
     * 主要的数据
     */
    private CmsProtocalEntity subPackage;

    public CmsProtocalEntity getSubPackage() {
        return subPackage;
    }

    public void setSubPackage(CmsProtocalEntity subPackage) {
        this.subPackage = subPackage;
    }
}
