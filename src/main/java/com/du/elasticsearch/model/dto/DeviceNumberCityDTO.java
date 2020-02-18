package com.du.elasticsearch.model.dto;

/**
 * @author dxy
 * @date 2019/4/3 10:27
 */
public class DeviceNumberCityDTO {
	/**
	 * 终端编码
	 */
	private String deviceNumber;
	/**
	 * 城市
	 */
	private String city;

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
}
