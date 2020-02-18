package com.du.elasticsearch.model.dto;

/**
 * 人数TOP10
 *
 * @author dxy
 * @date 2019/3/6 10:11
 */
public class SummarizeTopTenDTO {
	/**
	 * 小区名称
	 */
	private String community;
	/**
	 * 人数总数
	 */
	private Integer peopleNumSum;

	public String getCommunity() {
		return community;
	}

	public void setCommunity(String community) {
		this.community = community;
	}

	public Integer getPeopleNumSum() {
		return peopleNumSum;
	}

	public void setPeopleNumSum(Integer peopleNumSum) {
		this.peopleNumSum = peopleNumSum;
	}
}
