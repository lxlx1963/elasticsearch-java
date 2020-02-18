package com.du.elasticsearch.model;

/**
 * 监播数据(设备维度)
 *
 * @author dxy
 * @date 2019/3/1 10:23
 */
public class MonitorDataDevice {
	/**
	 * 监播数据设备ID
	 */
	private Long monitorDataDeviceId;
	/**
	 * 设备编码
	 */
	private String deviceModel;
	/**
	 * 日期
	 */
	private String date;
	/**
	 * 时间
	 */
	private String time;
	/**
	 * 年龄段
	 */
	private String age;
	/**
	 * 性别
	 */
	private String sex;
	/**
	 * 城市
	 */
	private String city;
	/**
	 * 小区
	 */
	private String community;
	/**
	 * 住宅类型
	 */
	private String residenceType;
	/**
	 * 曝光次数
	 */
	private Double exposuresSum;
	/**
	 * 触达人次
	 */
	private Integer touchSum;
	/**
	 * 观看人次
	 */
	private Integer watchSum;
	/**
	 * 播放时长
	 */
	private Long playDurationSum;
	/**
	 * 添加时间
	 */
	private Long addTime;

	public Long getMonitorDataDeviceId() {
		return monitorDataDeviceId;
	}

	public void setMonitorDataDeviceId(Long monitorDataDeviceId) {
		this.monitorDataDeviceId = monitorDataDeviceId;
	}

	public String getDeviceModel() {
		return deviceModel;
	}

	public void setDeviceModel(String deviceModel) {
		this.deviceModel = deviceModel;
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

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCommunity() {
		return community;
	}

	public void setCommunity(String community) {
		this.community = community;
	}

	public String getResidenceType() {
		return residenceType;
	}

	public void setResidenceType(String residenceType) {
		this.residenceType = residenceType;
	}

	public Double getExposuresSum() {
		return exposuresSum;
	}

	public void setExposuresSum(Double exposuresSum) {
		this.exposuresSum = exposuresSum;
	}

	public Integer getTouchSum() {
		return touchSum;
	}

	public void setTouchSum(Integer touchSum) {
		this.touchSum = touchSum;
	}

	public Integer getWatchSum() {
		return watchSum;
	}

	public void setWatchSum(Integer watchSum) {
		this.watchSum = watchSum;
	}

	public Long getPlayDurationSum() {
		return playDurationSum;
	}

	public void setPlayDurationSum(Long playDurationSum) {
		this.playDurationSum = playDurationSum;
	}

	public Long getAddTime() {
		return addTime;
	}

	public void setAddTime(Long addTime) {
		this.addTime = addTime;
	}
}
