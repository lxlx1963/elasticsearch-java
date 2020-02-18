package com.du.elasticsearch.model.dto;

/**
 * @author dxy
 * @date 2019/7/16 15:26
 */
public class ExportAdvertisementResidenceTypeDTO {
	/**
	 * 设备总数
	 */
	private Integer deviveSum;
	/**
	 * 播放总数
	 */
	private Integer playSum;
	/**
	 * 触达总数
	 */
	private Integer touchSum;
	/**
	 * 观看总数
	 */
	private Integer watchSum;
	/**
	 * 平均观看时长
	 */
	private Double watchDurationAvg;

	public Integer getDeviveSum() {
		return deviveSum;
	}

	public void setDeviveSum(Integer deviveSum) {
		this.deviveSum = deviveSum;
	}

	public Integer getPlaySum() {
		return playSum;
	}

	public void setPlaySum(Integer playSum) {
		this.playSum = playSum;
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

	public Double getWatchDurationAvg() {
		return watchDurationAvg;
	}

	public void setWatchDurationAvg(Double watchDurationAvg) {
		this.watchDurationAvg = watchDurationAvg;
	}
}
