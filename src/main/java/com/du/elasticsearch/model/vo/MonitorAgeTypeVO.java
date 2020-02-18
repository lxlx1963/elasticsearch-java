package com.du.elasticsearch.model.vo;

/**
 * @author dxy
 * @date 2019/3/7 15:12
 */
public class MonitorAgeTypeVO {
	/**
	 * 住宅类型
	 */
	private String residenceType;
	/**
	 * 年龄
	 */
	private String age;
	/**
	 * 观看率
	 */
	private Float watchRatio;

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

	public Float getWatchRatio() {
		return watchRatio;
	}

	public void setWatchRatio(Float watchRatio) {
		this.watchRatio = watchRatio;
	}
}
