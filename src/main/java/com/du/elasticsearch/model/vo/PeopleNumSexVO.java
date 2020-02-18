package com.du.elasticsearch.model.vo;

/**
 * @author dxy
 * @date 2019/3/11 16:06
 */
public class PeopleNumSexVO {
	/**
	 * 性别
	 */
	private String sex;
	/**
	 * 比率
	 */
	private Float ratio;
	/**
	 * 人数
	 */
	private Integer peopleNum;

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public Float getRatio() {
		return ratio;
	}

	public void setRatio(Float ratio) {
		this.ratio = ratio;
	}

	public Integer getPeopleNum() {
		return peopleNum;
	}

	public void setPeopleNum(Integer peopleNum) {
		this.peopleNum = peopleNum;
	}
}
