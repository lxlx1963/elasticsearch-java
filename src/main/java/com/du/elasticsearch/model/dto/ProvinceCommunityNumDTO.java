package com.du.elasticsearch.model.dto;

/**
 * @author dxy
 * @date 2019/3/6 18:31
 */
public class ProvinceCommunityNumDTO {
	/***
	 * 省
	 */
	private String province;
	/**
	 * 小区数
	 */
	private Integer communityNum;

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public Integer getCommunityNum() {
		return communityNum;
	}

	public void setCommunityNum(Integer communityNum) {
		this.communityNum = communityNum;
	}
}
