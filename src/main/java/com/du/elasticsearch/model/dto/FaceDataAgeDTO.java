package com.du.elasticsearch.model.dto;

/**
 * @author dxy
 * @date 2019/3/6 12:37
 */
public class FaceDataAgeDTO {
	/**
	 * 年龄
	 */
	private String age;
	/**
	 * 人数总数
	 */
	private Integer peopleNumSum;

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public Integer getPeopleNumSum() {
		return peopleNumSum;
	}

	public void setPeopleNumSum(Integer peopleNumSum) {
		this.peopleNumSum = peopleNumSum;
	}
}
