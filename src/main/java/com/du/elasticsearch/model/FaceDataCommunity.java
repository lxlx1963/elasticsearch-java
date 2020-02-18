package com.du.elasticsearch.model;

/**
 * 人脸数据(小区)
 *
 * @author dxy
 * @date 2019/3/1 20:12
 */
public class FaceDataCommunity {
	/**
	 * 主键ID
	 */
	private Long faceDataCommunityId;
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

	public Long getFaceDataCommunityId() {
		return faceDataCommunityId;
	}

	public void setFaceDataCommunityId(Long faceDataCommunityId) {
		this.faceDataCommunityId = faceDataCommunityId;
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
