package com.du.elasticsearch.model.vo;

/**
 * @author dxy
 * @date 2019/3/6 10:28
 */
public class SummarizeMonitorVO {

	/**
	 * 曝光次数
	 */
	private Integer exposuresSum;
	/**
	 * 触达人次
	 */
	private Integer touchNumSum;
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
	/**
	 * 日均出现次数
	 */
	private Float dayAvgPeopleTime;

	public Integer getExposuresSum() {
		return exposuresSum;
	}

	public void setExposuresSum(Integer exposuresSum) {
		this.exposuresSum = exposuresSum;
	}

	public Integer getTouchNumSum() {
		return touchNumSum;
	}

	public void setTouchNumSum(Integer touchNumSum) {
		this.touchNumSum = touchNumSum;
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

	public Float getDayAvgPeopleTime() {
		return dayAvgPeopleTime;
	}

	public void setDayAvgPeopleTime(Float dayAvgPeopleTime) {
		this.dayAvgPeopleTime = dayAvgPeopleTime;
	}
}
