package com.du.elasticsearch.model;

/**
 * @author dxy
 * @date 2019/4/2 16:58
 */
public class FaceDataDeviceTime {
	/**
	 * 主键ID
	 */
	private Long faceDataDeviceTimeId;
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
	 * 人数
	 */
	private Integer peopleNum;
	/**
	 * 人次
	 */
	private Integer peopleTime;

	public Long getFaceDataDeviceTimeId() {
		return faceDataDeviceTimeId;
	}

	public void setFaceDataDeviceTimeId(Long faceDataDeviceTimeId) {
		this.faceDataDeviceTimeId = faceDataDeviceTimeId;
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

	/**
	 * 添加时间
	 */

	private Long addTime;

}
