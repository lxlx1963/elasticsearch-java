package com.du.elasticsearch.model.dto;

/**
 * 人脸识别数据（省）
 *
 * @author dxy
 * @date 2019/3/5 15:12
 */
public class FaceDataProvinceAgeDTO {
	/**
	 * 省
	 */
	private String province;
	/**
	 * 年龄
	 */
	private String age;

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}
}
