package com.boco.comm;

import java.io.Serializable;

public class CommResult<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8899231789474640510L;
	/**
	 * 结果编码
	 */
	private String resultCode;
	/**
	 * 结果信息描述
	 */
	private String resultMsg;
	/**
	 * 结果数据
	 */
	private T resultData;

	/**
	 * 获取结果编码
	 * 
	 * @return the resultCode
	 */
	public String getResultCode() {
		return resultCode;
	}

	/**
	 * 设置结果编码
	 * 
	 * @param resultCode
	 *            the 结果编码 to set
	 */
	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	/**
	 * 获取结果信息描述
	 * 
	 * @return the resultMsg
	 */
	public String getResultMsg() {
		return resultMsg;
	}

	/**
	 * 设置结果信息描述
	 * 
	 * @param resultMsg
	 *            the 结果信息描述 to set
	 */
	public void setResultMsg(String resultMsg) {
		this.resultMsg = resultMsg;
	}

	/**
	 * @return the 结果数据
	 */
	public T getResultData() {
		return resultData;
	}

	/**
	 *            the resultData to set
	 */
	public void setResultData(T resultData) {
		this.resultData = resultData;
	}


	

}
