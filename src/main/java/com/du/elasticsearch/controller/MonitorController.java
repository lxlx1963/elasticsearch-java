package com.du.elasticsearch.controller;

import com.xinchao.data.constant.LoggerConstant;
import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.service.MonitorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author dxy
 * @date 2019/3/7 20:49
 */
@Controller
@RequestMapping(value = "/monitor")
public class MonitorController{
	private static Logger logger = LoggerFactory.getLogger(MonitorController.class);
	@Autowired
	private MonitorService monitorService;

	/**
	 * 近7天各年龄段的观看率&观看时长
	 */
	@ResponseBody
	@GetMapping(value = "/listMonitorAgeWatch")
	public Object listMaleFemaleRatio() {
		try {
			return monitorService.listMonitorAgeWatch();
		} catch (ServiceException e) {
			logger.error(LoggerConstant.LIST_MONITOR_AGE_WATCH, e);
			return e;
		}
	}

	/**
	 * 昨日监播数据
	 */
	@ResponseBody
	@GetMapping(value = "/getSummarizeMonitorDTO")
	public Object getSummarizeMonitorDTO() {
		try {
			return monitorService.getSummarizeMonitor();
		} catch (ServiceException e) {
			logger.error(LoggerConstant.GET_SUMMARIZE_MONITOR_DTO, e);
			return e;
		}
	}

	/**
	 * 近7天日均广告平均观看时长
	 */
	@ResponseBody
	@GetMapping(value = "/listMonitorSevenWatch")
	public Object listMonitorSevenWatch() {
		try {
			return monitorService.listMonitorSevenWatch();
		} catch (ServiceException e) {
			logger.error(LoggerConstant.LIST_MONITOR_SEVEN_WATCH, e);
			return e;
		}
	}

	/**
	 * 各年龄段的人员观看率
	 */
	@ResponseBody
	@GetMapping(value = "/getMonitorAgeTypeMap")
	public Object getMonitorAgeTypeMap() {
		return monitorService.getMonitorAgeTypeMap();
	}

	/**
	 * 近7天日均观看时长top10点位
	 */
	@ResponseBody
	@GetMapping(value = "/listMonitorCommunityWatch")
	public Object listMonitorCommunityWatch() {
		try {
			return monitorService.listMonitorCommunityWatch();
		} catch (ServiceException e) {
			logger.error(LoggerConstant.LIST_MONITOR_COMMUNITY_WATCH, e);
			return e;
		}
	}

	/**
	 *  近7天观看次数TOP10广告
	 */
	@ResponseBody
	@GetMapping(value = "/listMonitorAdvertisementWatch")
	public Object listMonitorAdvertisementWatch() {
		try {
			return monitorService.listMonitorAdvertisementWatch();
		} catch (ServiceException e) {
			logger.error(LoggerConstant.LIST_MONITOR_ADVERTISEMENT_WATCH, e);
			return e;
		}
	}

	/**
	 *  单广告近7天每小时平均：观看人次&触达人次
	 */
	@ResponseBody
	@GetMapping(value = "/listMonitorWatchTouch")
	public Object listMonitorWatchTouch() {
		try {
			return monitorService.listMonitorWatchTouch();
		} catch (ServiceException e) {
			logger.error(LoggerConstant.LIST_MONITOR_WATCH_TOUCH, e);
			return e;
		}
	}

}
