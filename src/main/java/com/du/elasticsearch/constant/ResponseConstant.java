package com.du.elasticsearch.constant;

/**
 * Response常量类
 */
public final class ResponseConstant {
	/**
	 * 私有化构造器
	 */
	private ResponseConstant() {

	}

	/**
	 * 提示码-成功
	 */
	public static final int CODE_SUCCESS = 0;
	/**
	 * 提示码-错误
	 */
	public static final int CODE_FAIL = 1;
	/**
	 * 提示信息-成功
	 */
	public static final String MSG_SUCCESS = "操作成功";
	/**
	 * 提示信息-操作失败
	 */
	public static final String MSG_ERROR = "操作失败";
	/**
	 * 提示信息-操作失败,参数错误
	 */
	public static final String MSG_PARAM_ERROR = "操作失败,参数错误";
	/**
	 * 返回数据JSON-code
	 */
	public static final String RESPONSE_JSON_CODE = "code";
	/**
	 * 返回数据JSON-data
	 */
	public static final String RESPONSE_JSON_DATA = "data";
	/**
	 * 返回数据JSON-msg
	 */
	public static final String RESPONSE_JSON_MSG = "msg";
}
