package com.du.elasticsearch.model.dto;

/**
 * @author dxy
 * @date 2019/4/4 14:06
 */
public class ProvinceCenterDTO {
	/**
	 * 省
	 */
	private String province;
	/**
	 * 省会中心坐标
	 */
	private String provinceCenter;

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getProvinceCenter() {
		return provinceCenter;
	}

	public void setProvinceCenter(String provinceCenter) {
		this.provinceCenter = provinceCenter;
	}
}
