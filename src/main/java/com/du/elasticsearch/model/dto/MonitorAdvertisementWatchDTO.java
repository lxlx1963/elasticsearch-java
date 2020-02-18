package com.du.elasticsearch.model.dto;

/**
 * @author dxy
 * @date 2019/3/7 14:07
 */
public class MonitorAdvertisementWatchDTO {
	/**
	 * 广告名称
	 */
	private String advertisementName;
	/**
	 * 观看次数总数
	 */
	private Integer watchSum;

	public String getAdvertisementName() {
		return advertisementName;
	}

	public void setAdvertisementName(String advertisementName) {
		this.advertisementName = advertisementName;
	}

	public Integer getWatchSum() {
		return watchSum;
	}

	public void setWatchSum(Integer watchSum) {
		this.watchSum = watchSum;
	}
}
