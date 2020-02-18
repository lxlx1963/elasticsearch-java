package com.du.elasticsearch.model;

/**
 * @author dxy
 * @date 2019/4/3 15:05
 */
public class FaceDataCommunityDevice {
	/**
	 * 主键ID
	 */
	private Long faceDataCommunityDeviceId;
	/**
	 * 日期
	 */
	private String date;
	/**
	 * 小区
	 */
	private String community;
	/**
	 * 城市
	 */
	private String city;
	/**
	 * 省
	 */
	private String province;
	/**
	 * 终端编码
	 */
	private String deviceCode;
	/**
	 * 添加时间
	 */
	private Long addTime;

	public Long getFaceDataCommunityDeviceId() {
		return faceDataCommunityDeviceId;
	}

	public void setFaceDataCommunityDeviceId(Long faceDataCommunityDeviceId) {
		this.faceDataCommunityDeviceId = faceDataCommunityDeviceId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

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

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getDeviceCode() {
		return deviceCode;
	}

	public void setDeviceCode(String deviceCode) {
		this.deviceCode = deviceCode;
	}

	public Long getAddTime() {
		return addTime;
	}

	public void setAddTime(Long addTime) {
		this.addTime = addTime;
	}
}
