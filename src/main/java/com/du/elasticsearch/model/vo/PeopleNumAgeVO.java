package com.du.elasticsearch.model.vo;

/**
 * @author dxy
 * @date 2019/3/11 16:16
 */
public class PeopleNumAgeVO {
	/**
	 * 年龄
	 */
	private String age;
	/**
	 * 比率
	 */
	private Float ratio;

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public Float getRatio() {
		return ratio;
	}

	public void setRatio(Float ratio) {
		this.ratio = ratio;
	}
}
