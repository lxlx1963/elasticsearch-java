package com.du.elasticsearch.model;

/**
 * 人脸数据(日期)
 *
 * @author dxy
 * @date 2019/3/1 20:32
 */
public class FaceDataDate {
	/**
	 * 人脸数据ID
	 */
	private Long faceDataDataId;
	/**
	 * 日期
	 */
	private String date;
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

	public Long getFaceDataDataId() {
		return faceDataDataId;
	}

	public void setFaceDataDataId(Long faceDataDataId) {
		this.faceDataDataId = faceDataDataId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
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
