package com.du.elasticsearch.model.dto;

/**
 * @author dxy
 * @date 2019/4/8 17:49
 */
public class CommunityDeviceNumDTO {
	/**
	 * 小区
	 */
	private String community;
	/**
	 * 设备数
	 */
	private Integer deviceNum;

	public String getCommunity() {
		return community;
	}

	public void setCommunity(String community) {
		this.community = community;
	}

	public Integer getDeviceNum() {
		return deviceNum;
	}

	public void setDeviceNum(Integer deviceNum) {
		this.deviceNum = deviceNum;
	}
}
