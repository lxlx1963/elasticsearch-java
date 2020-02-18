package com.du.elasticsearch.model;

/**
 * 人脸数据（年龄）
 *
 * @author dxy
 * @date 2019/3/1 20:22
 */
public class FaceDataAge {
	/**
	 * 主键ID
	 */
	private Long faceDataAgeId;
	/**
	 * 日期
	 */
	private String date;
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

	public Long getFaceDataAgeId() {
		return faceDataAgeId;
	}

	public void setFaceDataAgeId(Long faceDataAgeId) {
		this.faceDataAgeId = faceDataAgeId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
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
