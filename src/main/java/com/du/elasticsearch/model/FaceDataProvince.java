package com.du.elasticsearch.model;

/**
 * 人脸识别数据（省）
 *
 * @author dxy
 * @date 2019/3/5 10:12
 */
public class FaceDataProvince {
	/**
	 * 主键ID
	 */
	private Long faceDataProvinceId;
	/**
	 * 日期
	 */
	private String date;
	/**
	 * 省
	 */
	private String province;
	/**
	 * 省中心坐标
	 */
	private String provinceCenter;
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
	/**
	 * 人数
	 */
	private Integer peopleNum;
	/**
	 * 男性
	 */
	private Integer maleNum;
	/**
	 * 女性
	 */
	private Integer femaleNum;
	/**
	 * 主力年龄
	 */
	private String mainAge;
	/**
	 * 添加时间
	 */
	private Long addTime;

	public Long getFaceDataProvinceId() {
		return faceDataProvinceId;
	}

	public void setFaceDataProvinceId(Long faceDataProvinceId) {
		this.faceDataProvinceId = faceDataProvinceId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

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

	public Integer getPeopleNum() {
		return peopleNum;
	}

	public void setPeopleNum(Integer peopleNum) {
		this.peopleNum = peopleNum;
	}

	public Integer getMaleNum() {
		return maleNum;
	}

	public void setMaleNum(Integer maleNum) {
		this.maleNum = maleNum;
	}

	public Integer getFemaleNum() {
		return femaleNum;
	}

	public void setFemaleNum(Integer femaleNum) {
		this.femaleNum = femaleNum;
	}

	public String getMainAge() {
		return mainAge;
	}

	public void setMainAge(String mainAge) {
		this.mainAge = mainAge;
	}

	public Long getAddTime() {
		return addTime;
	}

	public void setAddTime(Long addTime) {
		this.addTime = addTime;
	}
}
