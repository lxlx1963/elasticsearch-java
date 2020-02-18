package com.du.elasticsearch.model;

/**
 * 监播数据(广告日期)
 *
 * @author dxy
 * @date 2019/3/1 20:27
 */
public class MonitorDataAdvertisementDate {
	/**
	 * 主键ID
	 */
	private Long monitorDataAdvertisementDateId;
	/**
	 * 广告名称
	 */
	private String advertisementName;
	/**
	 * 日期
	 */
	private String date;
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

	public Long getMonitorDataAdvertisementDateId() {
		return monitorDataAdvertisementDateId;
	}

	public void setMonitorDataAdvertisementDateId(Long monitorDataAdvertisementDateId) {
		this.monitorDataAdvertisementDateId = monitorDataAdvertisementDateId;
	}

	public String getAdvertisementName() {
		return advertisementName;
	}

	public void setAdvertisementName(String advertisementName) {
		this.advertisementName = advertisementName;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
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
