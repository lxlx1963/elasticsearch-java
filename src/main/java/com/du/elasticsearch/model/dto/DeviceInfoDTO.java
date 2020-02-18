package com.du.elasticsearch.model.dto;

/**
 * 终端信息DTO
 *
 * @author dxy
 * @date 2019/3/4 16:52
 */
public class DeviceInfoDTO {
	/**
	 * 终端编码
	 */
	private String deviceNumber;
	/**
	 * 省
	 */
	private String province;
	/**
	 * 城市
	 */
	private String city;
	/**
	 * 小区
	 */
	private String community;
	/**
	 * 住宅类型
	 */
	private String residenceType;

	public String getDeviceNumber() {
		return deviceNumber;
	}

	public void setDeviceNumber(String deviceNumber) {
		this.deviceNumber = deviceNumber;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCommunity() {
		return community;
	}

	public void setCommunity(String community) {
		this.community = community;
	}

	public String getResidenceType() {
		return residenceType;
	}

	public void setResidenceType(String residenceType) {
		this.residenceType = residenceType;
	}
}
