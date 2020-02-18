package com.du.elasticsearch.model.dto;

/**
 * @author dxy
 * @date 2019/3/7 14:27
 */
public class MonitorCommunityWatchDTO {
	/**
	 * 小区名称
	 */
	private String community;
	/**
	 * 观看时长
	 */
	private Float watchSum;

	public String getCommunity() {
		return community;
	}

	public void setCommunity(String community) {
		this.community = community;
	}

	public Float getWatchSum() {
		return watchSum;
	}

	public void setWatchSum(Float watchSum) {
		this.watchSum = watchSum;
	}
}
