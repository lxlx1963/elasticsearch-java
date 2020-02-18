package com.du.elasticsearch.controller;

import com.xinchao.data.constant.LoggerConstant;
import com.xinchao.data.constant.MessageConstant;
import com.xinchao.data.constant.ResponseConstant;
import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.sys.JsonResponse;
import com.xinchao.data.service.SingleCommunityService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author dxy
 * @date 2019/3/11 17:25
 */
@Controller
@RequestMapping(value = "/singleCommunity")
public class SingleCommunityController {
	private static Logger logger = LoggerFactory.getLogger(SingleCommunityController.class);
	@Autowired
	private SingleCommunityService singleCommunityService;

	/**
	 * 昨日小区列表
	 */
	@ResponseBody
	@GetMapping(value = "/listCommunity")
	public Object listCommunity() {
		try {
			return singleCommunityService.listCommunity();
		} catch (ServiceException e) {
			logger.error(LoggerConstant.CONTROLLER_LIST_COMMUNITY, e);
			return e;
		}
	}

	/**
	 * 昨日客流人数男女占比列表
	 */
	@ResponseBody
	@GetMapping(value = "/listPeopleNumSex")
	public Object listPeopleNumSex(String community) {
		if (StringUtils.isBlank(community)) {
			return new JsonResponse(ResponseConstant.CODE_FAIL, MessageConstant.COMMUNITY_NAME_IS_NULLL);
		}
		try {
			return singleCommunityService.listPeopleNumSex(community);
		} catch (ServiceException e) {
			logger.error(LoggerConstant.LIST_PEOPLE_NUM_SEX, e);
			return e;
		}
	}

	/**
	 * 昨日客流
	 */
	@ResponseBody
	@GetMapping(value = "/getPeopleNumTime")
	public Object getPeopleNumTime(String community) {
		if (StringUtils.isBlank(community)) {
			return new JsonResponse(ResponseConstant.CODE_FAIL, MessageConstant.COMMUNITY_NAME_IS_NULLL);
		}
		try {
			return singleCommunityService.getPeopleNumTime(community);
		} catch (ServiceException e) {
			logger.error(LoggerConstant.GET_PEOPLE_NUM_TIME, e);
			return e;
		}
	}

	/**
	 * 昨日客流人数年龄分布
	 */
	@ResponseBody
	@GetMapping(value = "/listPeopleNumAge")
	public Object listPeopleNumAge(String community) {
		if (StringUtils.isBlank(community)) {
			return new JsonResponse(ResponseConstant.CODE_FAIL, MessageConstant.COMMUNITY_NAME_IS_NULLL);
		}
		try {
			return singleCommunityService.listPeopleNumAge(community);
		} catch (ServiceException e) {
			logger.error(LoggerConstant.LIST_PEOPLE_NUM_AGE, e);
			return e;
		}
	}

	/**
	 * 近7天24小时平均客流人次&客流人数
	 */
	@ResponseBody
	@GetMapping(value = "/listPeopleNumTimeTime")
	public Object listPeopleNumTimeTime(String community) {
		if (StringUtils.isBlank(community)) {
			return new JsonResponse(ResponseConstant.CODE_FAIL, MessageConstant.COMMUNITY_NAME_IS_NULLL);
		}
		try {
			return singleCommunityService.listPeopleNumTimeTime(community);
		} catch (ServiceException e) {
			logger.error(LoggerConstant.LIST_PEOPLE_NUM_TIME_TIME, e);
			return e;
		}
	}

	/**
	 * 近7天各年龄段出入时段分析
	 */
	@ResponseBody
	@GetMapping(value = "/listPeopleNumTimeAge")
	public Object listPeopleNumTimeAge(String community) {
		if (StringUtils.isBlank(community)) {
			return new JsonResponse(ResponseConstant.CODE_FAIL, MessageConstant.COMMUNITY_NAME_IS_NULLL);
		}
		try {
			return singleCommunityService.listPeopleNumTimeAge(community);
		} catch (ServiceException e) {
			logger.error(LoggerConstant.LIST_PEOPLE_NUM_TIME_AGE, e);
			return e;
		}
	}


}
