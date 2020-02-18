package com.du.elasticsearch.model.dto;

/**
 * @author dxy
 * @date 2019/3/6 18:22
 */
public class ProvinceCityNumDTO {
	/***
	 * 省
	 */
	private String province;
	/**
	 * 城市数
	 */
	private Integer cityNum;

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public Integer getCityNum() {
		return cityNum;
	}

	public void setCityNum(Integer cityNum) {
		this.cityNum = cityNum;
	}
}
