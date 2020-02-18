package com.du.elasticsearch.model.dto;

/**
 * @author dxy
 * @date 2019/3/25 17:02
 */
public class DeviceCommunityCityDTO {
	/**
	 * 小区
	 */
	private String community;
	/**
	 * 城市
	 */
	private String city;

	public String getCommunity() {
		return community;
	}

	public void setCommunity(String community) {
		this.community = community;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
}

