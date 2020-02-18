package com.du.elasticsearch.model.vo;

/**
 * @author dxy
 * @date 2019/3/6 14:22
 */
public class SummarizeRecentSevenVO {
	/**
	 * 日期
	 */
	private String date;
	/**
	 * 人数总数
	 */
	private Float peopleNumSum;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Float getPeopleNumSum() {
		return peopleNumSum;
	}

	public void setPeopleNumSum(Float peopleNumSum) {
		this.peopleNumSum = peopleNumSum;
	}
}
