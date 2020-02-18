package com.du.elasticsearch.model.dto;

/**
 * @author dxy
 * @date 2019/3/6 11:00
 */
public class SummarizeMaleFemaleDTO {
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
