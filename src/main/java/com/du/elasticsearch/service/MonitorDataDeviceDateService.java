package com.du.elasticsearch.service;

import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.MonitorDataDeviceDate;

import java.util.List;

/**
 * 监播数据(设备日期)Mapper
 *
 * @author dxy
 * @date 2019/3/5 14:30
 */
public interface MonitorDataDeviceDateService {
	/**
	 * 插入监播数据（设备日期）
	 *
	 * @param monitorDataDeviceDate MonitorDataDeviceDate
	 * @throws ServiceException
	 */
	void insertMonitorDataDeviceDate(MonitorDataDeviceDate monitorDataDeviceDate) throws ServiceException;

	/**
	 * 批量插入监播数据（设备日期）
	 *
	 * @param monitorDataDeviceDateList List<MonitorDataDeviceDate>
	 * @throws ServiceException
	 */
	void insertMonitorDataDeviceDateBatch(List<MonitorDataDeviceDate> monitorDataDeviceDateList) throws ServiceException;
}
