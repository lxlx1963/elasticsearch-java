package com.du.elasticsearch.model.dto;

/**
 * 人脸识别数据（省）
 *
 * @author dxy
 * @date 2019/3/5 15:12
 */
public class FaceDataProvinceDTO {
	/**
	 * 省
	 */
	private String province;
	/**
	 * 城市数
	 */
	private Integer cityNum;
	/**
	 * 小区数
	 */
	private Integer communityNum;
	/**
	 * 联网设备
	 */
	private Integer deviceNum;

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

	public Integer getCommunityNum() {
		return communityNum;
	}

	public void setCommunityNum(Integer communityNum) {
		this.communityNum = communityNum;
	}

	public Integer getDeviceNum() {
		return deviceNum;
	}

	public void setDeviceNum(Integer deviceNum) {
		this.deviceNum = deviceNum;
	}
}
