package com.du.elasticsearch.model.dto;

/**
 * @author dxy
 * @date 2019/3/7 17:24
 */
public class MonitorDateWatchDTO {
	/**
	 * 日期
	 */
	private String date;
	/**
	 * 观看人次
	 */
	private Integer watchSum;
	/**
	 * 播放时长
	 */
	private Long playDurationSum;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
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
}
