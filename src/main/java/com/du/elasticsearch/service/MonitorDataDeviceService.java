package com.du.elasticsearch.service;

import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.MonitorDataDevice;
import com.xinchao.data.model.MonitorDataDeviceDate;
import com.xinchao.data.model.dto.MonitorAgeTypeDTO;
import com.xinchao.data.model.dto.MonitorCommunityWatchDTO;

import java.util.List;

/**
 * 监播数（设备）据Service
 *
 * @author dxy
 * @date 2019/3/1 15:35
 */
public interface MonitorDataDeviceService {
	/**
	 * 插入监播数据（设备）
	 *
	 * @param monitorDataDevice MonitorDataDevice
	 */
	void insertMonitorDataDevice(MonitorDataDevice monitorDataDevice) throws ServiceException;

	/**
	 * 批量插入监播数据（设备）
	 *
	 * @param monitorDataDeviceList List<MonitorDataDevice>
	 */
	void insertMonitorDataDeviceBatch(List<MonitorDataDevice> monitorDataDeviceList) throws ServiceException;

	/**
	 * 获取监播数据设备日期数据列表
	 *
	 * @param date 日期
	 * @return List<MonitorDataDeviceDate>
	 * @throws ServiceException
	 */
	List<MonitorDataDeviceDate> listMonitorDataDeviceDate(String date) throws ServiceException;

	/**
	 * 获取小区观看人次列表
	 *
	 * @param dateList 日期列表
	 * @return List<MonitorCommunityWatchDTO>
	 * @throws ServiceException
	 */
	List<MonitorCommunityWatchDTO> listMonitorCommunityWatch(List<String> dateList) throws ServiceException;

	/**
	 * 获取监播年龄住宅类型列表
	 *
	 * @param dateList 日期列表
	 * @param residenceTypeList 住宅类型列表
	 * @return List<MonitorAgeTypeDTO>
	 */
	List<MonitorAgeTypeDTO> listMonitorAgeType(List<String> dateList, List<String> residenceTypeList);

	/**
	 * 获取实际日期列表
	 *
	 * @param dateList 日期列表
	 * @return List<String>
	 */
	List<String> listActualDate(List<String> dateList);
}
