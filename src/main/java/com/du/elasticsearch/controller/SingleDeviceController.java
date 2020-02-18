package com.du.elasticsearch.controller;

import com.xinchao.data.constant.LoggerConstant;
import com.xinchao.data.constant.MessageConstant;
import com.xinchao.data.constant.ResponseConstant;
import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.sys.JsonResponse;
import com.xinchao.data.service.SingleDeviceService;
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
 * @date 2019/3/9 16:34
 */
@Controller
@RequestMapping(value = "/singleDevice")
public class SingleDeviceController {
    private static Logger logger = LoggerFactory.getLogger(SingleDeviceController.class);
    @Autowired
    private SingleDeviceService singleDeviceService;

    /**
     * 获取昨天的客流人数、客流人次及男女分别的客流人数、客流人次
     */
    @ResponseBody
    @GetMapping(value = "/getSingleDeviceTimeSex")
    public Object getSingleDeviceTimeSex(String residenceType) {
        if (StringUtils.isBlank(residenceType)) {
            return new JsonResponse(ResponseConstant.CODE_FAIL, MessageConstant.RESIDENCE_TYPE_IS_NULLL);
        }
        try {
            return singleDeviceService.getSingleDeviceTimeSex(residenceType);
        } catch (ServiceException e) {
            logger.error(LoggerConstant.GET_SINGLE_DEVICE_TIME_SEX, e);
            return e;
        }
    }

    /**
     * 昨日每小时的人员构成 (年龄分布)
     */
    @ResponseBody
    @GetMapping(value = "/listDeviceTimeAge")
    public Object listDeviceTimeAge(String residenceType) {
        if (StringUtils.isBlank(residenceType)) {
            return new JsonResponse(ResponseConstant.CODE_FAIL, MessageConstant.RESIDENCE_TYPE_IS_NULLL);
        }
        try {
            return singleDeviceService.listDeviceTimeAge(residenceType);
        } catch (ServiceException e) {
            logger.error(LoggerConstant.LIST_DEVICE_TIME_AGE, e);
            return e;
        }
    }

    /**
     * 昨日每小时的人员构成 (性别分布)
     */
    @ResponseBody
    @GetMapping(value = "/listDeviceTimeSex")
    public Object listDeviceTimeSex(String residenceType) {
        if (StringUtils.isBlank(residenceType)) {
            return new JsonResponse(ResponseConstant.CODE_FAIL, MessageConstant.RESIDENCE_TYPE_IS_NULLL);
        }
        try {
            return singleDeviceService.listDeviceTimeSex(residenceType);
        } catch (ServiceException e) {
            logger.error(LoggerConstant.LIST_DEVICE_TIME_SEX, e);
            return e;
        }
    }

    /**
     * 昨日每小时平均客流人次和客流人数
     */
    @ResponseBody
    @GetMapping(value = "/listDeviceTime")
    public Object listDeviceTime(String residenceType) {
        if (StringUtils.isBlank(residenceType)) {
            return new JsonResponse(ResponseConstant.CODE_FAIL, MessageConstant.RESIDENCE_TYPE_IS_NULLL);
        }
        try {
            return singleDeviceService.listDeviceTime(residenceType);
        } catch (ServiceException e) {
            logger.error(LoggerConstant.LIST_DEVICE_TIME, e);
            return e;
        }
    }
}
