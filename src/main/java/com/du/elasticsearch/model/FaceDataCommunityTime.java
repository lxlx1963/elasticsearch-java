package com.du.elasticsearch.model;

/**
 * @author dxy
 * @date 2019/4/2 16:23
 */
public class FaceDataCommunityTime {
	/**
	 * 主键ID
	 */
	private Long faceDataCommunityTimeId;
	/**
	 * 日期
	 */
	private String date;
	/**
	 * 小区
	 */
	private String community;
	/**
	 * 时间
	 */
	private String time;
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

	public Long getFaceDataCommunityTimeId() {
		return faceDataCommunityTimeId;
	}

	public void setFaceDataCommunityTimeId(Long faceDataCommunityTimeId) {
		this.faceDataCommunityTimeId = faceDataCommunityTimeId;
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

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
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
