package com.du.elasticsearch.model.dto;

/**
 * @author dxy
 * @date 2019/3/6 10:28
 */
public class SummarizeMonitorDTO {

	/**
	 * 曝光次数
	 */
	private Double exposuresSum;
	/**
	 * 触达率
	 */
	private Float touchRatio;
	/**
	 * 观看率
	 */
	private Float watchRatio;
	/**
	 * 平均观看时长
	 */
	private Float watchAvg;

	public Double getExposuresSum() {
		return exposuresSum;
	}

	public void setExposuresSum(Double exposuresSum) {
		this.exposuresSum = exposuresSum;
	}

	public Float getTouchRatio() {
		return touchRatio;
	}

	public void setTouchRatio(Float touchRatio) {
		this.touchRatio = touchRatio;
	}

	public Float getWatchRatio() {
		return watchRatio;
	}

	public void setWatchRatio(Float watchRatio) {
		this.watchRatio = watchRatio;
	}

	public Float getWatchAvg() {
		return watchAvg;
	}

	public void setWatchAvg(Float watchAvg) {
		this.watchAvg = watchAvg;
	}
}
