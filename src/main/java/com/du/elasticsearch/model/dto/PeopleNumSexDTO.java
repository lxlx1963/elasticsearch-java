package com.du.elasticsearch.model.dto;

/**
 * 客流人数性别DTO
 *
 * @author dxy
 * @date 2019/3/11 10:11
 */
public class PeopleNumSexDTO {
	/**
	 * 性别
	 */
	private String sex;
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

	public Integer getPeopleNum() {
		return peopleNum;
	}

	public void setPeopleNum(Integer peopleNum) {
		this.peopleNum = peopleNum;
	}
}
