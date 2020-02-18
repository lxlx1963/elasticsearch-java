package com.du.elasticsearch.model.sys;

/**
 * 返回Json数据类
 */
public class JsonResponse {
	/** 0：成功；1：失败 */
	private Integer code;
	/** 返回数据 */
	private Object data;
	/** 提示信息 */
	private String msg;

	public JsonResponse() {

	}

	public JsonResponse(Integer code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public JsonResponse(Integer code, Object data, String msg) {
		this.code = code;
		this.data = data;
		this.msg = msg;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
