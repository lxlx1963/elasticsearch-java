package com.du.elasticsearch.model.dto;

/**
 * 人脸识别数据（省）
 *
 * @author dxy
 * @date 2019/3/5 15:12
 */
public class FaceDataProvinceSexDTO {
	/**
	 * 省
	 */
	private String province;
	/**
	 * 性别
	 */
	private String sex;
	/**
	 * 人数
	 */
	private Integer peopleNum;

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

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
