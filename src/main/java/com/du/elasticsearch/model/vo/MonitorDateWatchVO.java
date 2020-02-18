package com.du.elasticsearch.model.vo;

/**
 * @author dxy
 * @date 2019/3/7 11:29
 */
public class MonitorDateWatchVO {
	/**
	 * 日期
	 */
	private String date;
	/**
	 * 平均观看时长
	 */
	private Double avgWatch;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Double getAvgWatch() {
		return avgWatch;
	}

	public void setAvgWatch(Double avgWatch) {
		this.avgWatch = avgWatch;
	}
}
