package com.du.elasticsearch.model.dto;

/**
 * @author dxy
 * @date 2019/4/3 14:26
 */
public class DeviceDatePeopleSumDTO {
	/**
	 * 日期
	 */
	private String date;
	/**
	 * 人数总数
	 */
	private Integer peopleSum;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Integer getPeopleSum() {
		return peopleSum;
	}

	public void setPeopleSum(Integer peopleSum) {
		this.peopleSum = peopleSum;
	}
}
