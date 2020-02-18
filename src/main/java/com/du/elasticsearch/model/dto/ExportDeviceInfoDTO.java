package com.du.elasticsearch.model.dto;

/**
 * 终端信息DTO
 *
 * @author dxy
 * @date 2019/3/4 16:52
 */
public class ExportDeviceInfoDTO {
	/**
	 * 终端编码
	 */
	private String deviceNumber;
	/**
	 * 城市
	 */
	private String city;
	/**
	 * 住宅类型
	 */
	private String residenceType;
	/**
	 * 人数
	 */
	private int peopleNumber;

	public String getDeviceNumber() {
		return deviceNumber;
	}

	public void setDeviceNumber(String deviceNumber) {
		this.deviceNumber = deviceNumber;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getResidenceType() {
		return residenceType;
	}

	public void setResidenceType(String residenceType) {
		this.residenceType = residenceType;
	}

	public int getPeopleNumber() {
		return peopleNumber;
	}

	public void setPeopleNumber(int peopleNumber) {
		this.peopleNumber = peopleNumber;
	}
}
