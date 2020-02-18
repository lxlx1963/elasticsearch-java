package com.du.elasticsearch.model.dto;

/**
 * @author dxy
 * @date 2019/4/4 13:54
 */
public class ProvinceCityDTO {
	/**
	 * 省
	 */
	private String province;
	/**
	 * 城市
	 */
	private String city;

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
}
