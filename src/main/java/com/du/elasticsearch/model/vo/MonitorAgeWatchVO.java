package com.du.elasticsearch.model.vo;

/**
 * @author dxy
 * @date 2019/3/7 11:05
 */
public class MonitorAgeWatchVO {
	/**
	 * 年龄
	 */
	private String age;
	/**
	 * 观看率
	 */
	private float watchRatio;
	/**
	 * 观看时长
	 */
	private double watchDuration;

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public float getWatchRatio() {
		return watchRatio;
	}

	public void setWatchRatio(float watchRatio) {
		this.watchRatio = watchRatio;
	}

	public double getWatchDuration() {
		return watchDuration;
	}

	public void setWatchDuration(double watchDuration) {
		this.watchDuration = watchDuration;
	}
}
