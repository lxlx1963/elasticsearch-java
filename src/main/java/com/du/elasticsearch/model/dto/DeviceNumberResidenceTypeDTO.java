package com.du.elasticsearch.model.dto;

/**
 * @author dxy
 * @date 2019/4/3 10:27
 */
public class DeviceNumberResidenceTypeDTO {
	/**
	 * 终端编码
	 */
	private String deviceNumber;
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

	public String getResidenceType() {
		return residenceType;
	}

	public void setResidenceType(String residenceType) {
		this.residenceType = residenceType;
	}
}
