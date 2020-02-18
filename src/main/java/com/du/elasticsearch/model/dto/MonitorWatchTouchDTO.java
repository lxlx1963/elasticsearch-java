package com.du.elasticsearch.model.dto;

/**
 * @author dxy
 * @date 2019/3/7 14:12
 */
public class MonitorWatchTouchDTO {
	/**
	 * 时间
	 */
	private String time;
	/**
	 * 观看人次总数
	 */
	private Integer watchSum;
	/**
	 * 触达人次总数
	 */
	private Integer touchSum;

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Integer getWatchSum() {
		return watchSum;
	}

	public void setWatchSum(Integer watchSum) {
		this.watchSum = watchSum;
	}

	public Integer getTouchSum() {
		return touchSum;
	}

	public void setTouchSum(Integer touchSum) {
		this.touchSum = touchSum;
	}
}
