package com.du.elasticsearch.model.dto;

/**
 * @author dxy
 * @date 2019/3/7 15:08
 */
public class MonitorAgeTypeDTO {
	/**
	 * 住宅类型
	 */
	private String residenceType;
	/**
	 * 年龄
	 */
	private String age;
	/**
	 * 触达人次
	 */
	private Double touchSum;
	/**
	 * 观看次数
	 */
	private Integer watchSum;

	public String getResidenceType() {
		return residenceType;
	}

	public void setResidenceType(String residenceType) {
		this.residenceType = residenceType;
	}

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
}
