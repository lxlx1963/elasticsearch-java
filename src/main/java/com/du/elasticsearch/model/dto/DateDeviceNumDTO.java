package com.du.elasticsearch.model.dto;

/**
 * @author dxy
 * @date 2019/3/22 15:35
 */
public class DateDeviceNumDTO {
	/**
	 * 日期
	 */
	private String date;
	/**
	 * 机器数量
	 */
	private Integer deviceNum;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Integer getDeviceNum() {
		return deviceNum;
	}

	public void setDeviceNum(Integer deviceNum) {
		this.deviceNum = deviceNum;
	}
}
