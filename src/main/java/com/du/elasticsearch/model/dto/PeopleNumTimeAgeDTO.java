package com.du.elasticsearch.model.dto;

/**
 * @author dxy
 * @date 2019/3/11 10:16
 */
public class PeopleNumTimeAgeDTO {
	/**
	 * 时间
	 */
	private String time;
	/**
	 * 年龄
	 */
	private String age;
	/**
	 * 人数
	 */
	private Integer peopleNum;

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

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
