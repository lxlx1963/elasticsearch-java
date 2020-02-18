package com.du.elasticsearch.model;

/**
 * 监播数据(年龄)
 *
 * @author dxy
 * @date 2019/3/1 21:23
 */
public class MonitorDataAge {
	/**
	 * 监播数据设备ID
	 */
	private Long monitorDataAgeId;
	/**
	 * 日期
	 */
	private String date;
	/**
	 * 年龄段
	 */
	private String age;
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

	public Long getMonitorDataAgeId() {
		return monitorDataAgeId;
	}

	public void setMonitorDataAgeId(Long monitorDataAgeId) {
		this.monitorDataAgeId = monitorDataAgeId;
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
