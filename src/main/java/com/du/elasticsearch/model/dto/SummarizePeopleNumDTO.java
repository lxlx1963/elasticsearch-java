package com.du.elasticsearch.model.dto;

/**
 * 汇总监播数据
 * @author dxy
 * @date 2019/3/6 10:17
 */
public class SummarizePeopleNumDTO {
	/**
	 * 人数
	 */
	private Integer peopleNumSum;
	/**
	 * 人次
	 */
	private Integer peopleTimeSum;
	/**
	 * 城市数
	 */
	private Integer cityNum;
	/**
	 * 联网设备
	 */
	private Integer deviceNum;

	public Integer getPeopleNumSum() {
		return peopleNumSum;
	}

	public void setPeopleNumSum(Integer peopleNumSum) {
		this.peopleNumSum = peopleNumSum;
	}

	public Integer getPeopleTimeSum() {
		return peopleTimeSum;
	}

	public void setPeopleTimeSum(Integer peopleTimeSum) {
		this.peopleTimeSum = peopleTimeSum;
	}

	public Integer getCityNum() {
		return cityNum;
	}

	public void setCityNum(Integer cityNum) {
		this.cityNum = cityNum;
	}

	public Integer getDeviceNum() {
		return deviceNum;
	}

	public void setDeviceNum(Integer deviceNum) {
		this.deviceNum = deviceNum;
	}
}
