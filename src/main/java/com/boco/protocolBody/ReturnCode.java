package com.boco.protocolBody;

/**
 * 返回值
 *
 */
public interface ReturnCode {

    /**
     * 发送成功
     */
    String ReturnCode_success = "000000";
    /**
     * 数据格式非法
     */
    String ReturnCode_formaterror = "100000";
    /**
     * 无效请求类型
     */
    String ReturnCode_Invalid = "200000";
    /**
     * 未知错误
     */
    String ReturnCode_unknown = "111111";
}
