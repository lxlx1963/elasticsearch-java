package com.du.elasticsearch.controller;

import com.xinchao.data.constant.LoggerConstant;
import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.service.SummarizeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 汇总
 *
 * @author dxy
 * @date 2019/3/5 16:57
 */
@Controller
@RequestMapping(value = "/summarize")
public class SummarizeController {
	private static Logger logger = LoggerFactory.getLogger(SummarizeController.class);
	@Autowired
	private SummarizeService summarizeService;

	/**
	 * 男女比列表
	 */
	@ResponseBody
	@GetMapping(value = "/listMaleFemaleRatio")
	public Object listMaleFemaleRatio() {
		try {
			return summarizeService.listMaleFemaleRatio();
		} catch (ServiceException e) {
			logger.error(LoggerConstant.LIST_MALE_FEMALE_RATIO, e);
			return e;
		}
	}

	/**
	 * 年龄人数列表
	 */
	@ResponseBody
	@GetMapping(value = "/listAgePeopleNum")
	public Object listAgePeopleNum() {
		try {
			return summarizeService.listAgePeopleNum();
		} catch (ServiceException e) {
			logger.error(LoggerConstant.LIST_AGE_PEOPLE_NUM, e);
			return e;
		}
	}

	/**
	 * 近7天客流人数列表
	 */
	@ResponseBody
	@GetMapping(value = "/listSummarizeRecentSevenDTO")
	public Object listSummarizeRecentSevenDTO() {
		try {
			return summarizeService.listSummarizeRecentSevenDTO();
		} catch (ServiceException e) {
			logger.error(LoggerConstant.LIST_SUMMARIZE_RECENT_SEVEN_DTO, e);
			return e;
		}
	}

	/**
	 * 人数top10小区
	 */
	@ResponseBody
	@GetMapping(value = "/listPeopleNumTopTen")
	public Object listPeopleNumTopTen() {
		try {
			return summarizeService.listPeopleNumTopTen();
		} catch (ServiceException e) {
			logger.error(LoggerConstant.LIST_PEOPLE_NUM_TOP_TEN, e);
			return e;
		}
	}

	/**
	 * 客流人数
	 */
	@ResponseBody
	@GetMapping(value = "/getSummarizePeopleNumDTO")
	public Object getSummarizePeopleNumDTO() {
		try {
			return summarizeService.getSummarizePeopleNumDTO();
		} catch (ServiceException e) {
			logger.error(LoggerConstant.GET_SUMMARIZE_PEOPLE_NUM_DTO, e);
			return e;
		}
	}

	/**
	 * 监播数据
	 */
	@ResponseBody
	@GetMapping(value = "/getSummarizeMonitorDTO")
	public Object getSummarizeMonitorDTO() {
		try {
			return summarizeService.getSummarizeMonitor();
		} catch (ServiceException e) {
			logger.error(LoggerConstant.GET_SUMMARIZE_MONITOR_DTO, e);
			return e;
		}
	}

	/**
	 * 获取汇总省人脸数据
	 */
	@ResponseBody
	@GetMapping(value = "/listSummarizeFaceDataProvinceDTO")
	public Object listSummarizeFaceDataProvinceDTO() {
		try {
			return summarizeService.listSummarizeFaceDataProvinceDTO();
		} catch (ServiceException e) {
			logger.error(LoggerConstant.LIST_SUMMARIZE_FACE_DATA_PROVINCE_DTO, e);
			return e;
		}
	}
}
