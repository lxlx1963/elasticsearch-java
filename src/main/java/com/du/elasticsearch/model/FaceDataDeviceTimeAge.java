package com.du.elasticsearch.model;

/**
 * @author dxy
 * @date 2019/4/2 17:33
 */
public class FaceDataDeviceTimeAge {
	/**
	 * 主键ID
	 */
	private Long faceDataDeviceTimeAgeId;
	/**
	 * 日期
	 */
	private String date;
	/**
	 * 终端编码
	 */
	private String deviceCode;
	/**
	 * 住宅类型
	 */
	private String residenceType;
	/**
	 * 时间
	 */
	private String time;
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

	public Long getFaceDataDeviceTimeAgeId() {
		return faceDataDeviceTimeAgeId;
	}

	public void setFaceDataDeviceTimeAgeId(Long faceDataDeviceTimeAgeId) {
		this.faceDataDeviceTimeAgeId = faceDataDeviceTimeAgeId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDeviceCode() {
		return deviceCode;
	}

	public void setDeviceCode(String deviceCode) {
		this.deviceCode = deviceCode;
	}

	public String getResidenceType() {
		return residenceType;
	}

	public void setResidenceType(String residenceType) {
		this.residenceType = residenceType;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
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
