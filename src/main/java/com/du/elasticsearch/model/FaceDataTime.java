package com.du.elasticsearch.model;

/**
 * 人脸数据(时间)
 *
 * @author dxy
 * @date 2019/3/1 20:32
 */
public class FaceDataTime {
	/**
	 * 人脸数据ID
	 */
	private Long faceDataTimeId;
	/**
	 * 日期
	 */
	private String date;
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

	public Long getFaceDataTimeId() {
		return faceDataTimeId;
	}

	public void setFaceDataTimeId(Long faceDataTimeId) {
		this.faceDataTimeId = faceDataTimeId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
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
