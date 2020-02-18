package com.du.elasticsearch.model;

/**
 * 人脸数据(性别)
 *
 * @author dxy
 * @date 2019/3/1 10:22
 */
public class FaceDataSex {
	/**
	 * 主键ID
	 */
	private Long faceDataSexId;
	/**
	 * 日期
	 */
	private String date;
	/**
	 * 性别
	 */
	private String sex;
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

	public Long getFaceDataSexId() {
		return faceDataSexId;
	}

	public void setFaceDataSexId(Long faceDataSexId) {
		this.faceDataSexId = faceDataSexId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
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
