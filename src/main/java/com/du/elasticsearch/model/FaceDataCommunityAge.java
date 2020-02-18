package com.du.elasticsearch.model;

/**
 * @author dxy
 * @date 2019/4/2 16:14
 */
public class FaceDataCommunityAge {
	/**
	 * 主键ID
	 */
	private Long faceDataCommunityAgeId;
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
	 * 年龄
	 */
	private String age;
	/**
	 * 人数
	 */
	private Integer peopleNum;
	/**
	 * 人次
	 */
	private Integer peopleTime;
	/**
	 * 添加时间
	 */
	private Long addTime;

	public Long getFaceDataCommunityAgeId() {
		return faceDataCommunityAgeId;
	}

	public void setFaceDataCommunityAgeId(Long faceDataCommunityAgeId) {
		this.faceDataCommunityAgeId = faceDataCommunityAgeId;
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

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public Integer getPeopleNum() {
		return peopleNum;
	}

	public void setPeopleNum(Integer peopleNum) {
		this.peopleNum = peopleNum;
	}

	public Integer getPeopleTime() {
		return peopleTime;
	}

	public void setPeopleTime(Integer peopleTime) {
		this.peopleTime = peopleTime;
	}

	public Long getAddTime() {
		return addTime;
	}

	public void setAddTime(Long addTime) {
		this.addTime = addTime;
	}
}
