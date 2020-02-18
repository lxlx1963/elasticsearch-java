package com.du.elasticsearch.model.vo;

/**
 * 人数TOP10
 *
 * @author dxy
 * @date 2019/3/6 10:11
 */
public class SummarizeTopTenVO {
	/**
	 * 小区名称
	 */
	private String community;
	/**
	 * 人数平均数
	 */
	private Float peopleNumAvg;
	/**
	 * 设备数
	 */
	private Integer deviceSum;

	public String getCommunity() {
		return community;
	}

	public void setCommunity(String community) {
		this.community = community;
	}

	public Float getPeopleNumAvg() {
		return peopleNumAvg;
	}

	public void setPeopleNumAvg(Float peopleNumAvg) {
		this.peopleNumAvg = peopleNumAvg;
	}

	public Integer getDeviceSum() {
		return deviceSum;
	}

	public void setDeviceSum(Integer deviceSum) {
		this.deviceSum = deviceSum;
	}
}
