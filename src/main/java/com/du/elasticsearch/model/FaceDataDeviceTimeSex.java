package com.du.elasticsearch.model;

/**
 * @author dxy
 * @date 2019/4/2 17:04
 */
public class FaceDataDeviceTimeSex {
	/**
	 * 主键ID
	 */
	private Long faceDataDeviceTimeSexId;
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

	public Long getFaceDataDeviceTimeSexId() {
		return faceDataDeviceTimeSexId;
	}

	public void setFaceDataDeviceTimeSexId(Long faceDataDeviceTimeSexId) {
		this.faceDataDeviceTimeSexId = faceDataDeviceTimeSexId;
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
