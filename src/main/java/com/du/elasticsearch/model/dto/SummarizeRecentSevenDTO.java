package com.du.elasticsearch.model.dto;

/**
 * @author dxy
 * @date 2019/3/6 14:22
 */
public class SummarizeRecentSevenDTO {
	/**
	 * 日期
	 */
	private String date;
	/**
	 * 人数总数
	 */
	private Integer peopleNumSum;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Integer getPeopleNumSum() {
		return peopleNumSum;
	}

	public void setPeopleNumSum(Integer peopleNumSum) {
		this.peopleNumSum = peopleNumSum;
	}
}
