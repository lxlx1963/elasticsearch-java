package com.du.elasticsearch.model.dto;

/**
 * @author dxy
 * @date 2019/3/11 10:13
 */
public class PeopleNumAgeDTO {
	/**
	 * 年龄
	 */
	private String age;
	/**
	 * 人数
	 */
	private Integer peopleNum;

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public Integer getPeopleNum() {
		return peopleNum;
	}

	public void setPeopleNum(Integer peopleNum) {
		this.peopleNum = peopleNum;
	}
}
