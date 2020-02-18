package com.du.elasticsearch.model.dto;

/**
 * @author dxy
 * @date 2019/3/7 11:05
 */
public class MonitorAgeWatchDTO {
	/**
	 * 年龄
	 */
	private String age;

	/**
	 * 触达人次
	 */
	private Double touchSum;
	/**
	 * 观看人次
	 */
	private Integer watchSum;
	/**
	 * 播放时长
	 */
	private Long playDurationSum;

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public Double getTouchSum() {
		return touchSum;
	}

	public void setTouchSum(Double touchSum) {
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
}
