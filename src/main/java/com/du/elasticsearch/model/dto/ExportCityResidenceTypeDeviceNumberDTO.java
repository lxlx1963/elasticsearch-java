package com.du.elasticsearch.model.dto;

/**
 * @author dxy
 * @date 2019/7/18 10:12
 */
public class ExportCityResidenceTypeDeviceNumberDTO {
	/**
	 * 日均<50
	 */
	private Integer lessThanFiftyNum;
	/**
	 * 50-200
	 */
	private Integer fiftyAndTwoHundredNum;
	/**
	 * 200-400
	 */
	private Integer twoAndFourHundredNum;
	/**
	 * 400+
	 */
	private Integer greaterFourHoudredNum;

	public Integer getLessThanFiftyNum() {
		return lessThanFiftyNum;
	}

	public void setLessThanFiftyNum(Integer lessThanFiftyNum) {
		this.lessThanFiftyNum = lessThanFiftyNum;
	}

	public Integer getFiftyAndTwoHundredNum() {
		return fiftyAndTwoHundredNum;
	}

	public void setFiftyAndTwoHundredNum(Integer fiftyAndTwoHundredNum) {
		this.fiftyAndTwoHundredNum = fiftyAndTwoHundredNum;
	}

	public Integer getTwoAndFourHundredNum() {
		return twoAndFourHundredNum;
	}

	public void setTwoAndFourHundredNum(Integer twoAndFourHundredNum) {
		this.twoAndFourHundredNum = twoAndFourHundredNum;
	}

	public Integer getGreaterFourHoudredNum() {
		return greaterFourHoudredNum;
	}

	public void setGreaterFourHoudredNum(Integer greaterFourHoudredNum) {
		this.greaterFourHoudredNum = greaterFourHoudredNum;
	}
}
