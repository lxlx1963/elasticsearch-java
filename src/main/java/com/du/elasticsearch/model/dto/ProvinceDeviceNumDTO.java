package com.du.elasticsearch.model.dto;

/**
 * @author dxy
 * @date 2019/3/6 18:24
 */
public class ProvinceDeviceNumDTO {
	/***
	 * 省
	 */
	private String province;
	/**
	 * 设备数
	 */
	private Integer deviceNum;

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public Integer getDeviceNum() {
		return deviceNum;
	}

	public void setDeviceNum(Integer deviceNum) {
		this.deviceNum = deviceNum;
	}
}
