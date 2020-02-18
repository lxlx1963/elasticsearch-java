package com.du.elasticsearch.model.dto;

/**
 * @author dxy
 * @date 2019/3/6 16:33
 */
public class SummarizeFaceDataProvinceDTO {
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
}
