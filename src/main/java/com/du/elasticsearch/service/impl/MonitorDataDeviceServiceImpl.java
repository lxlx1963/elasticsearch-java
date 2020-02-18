package com.du.elasticsearch.service.impl;

import com.xinchao.data.constant.MessageConstant;
import com.xinchao.data.dao.MonitorDataDeviceMapper;
import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.MonitorDataDevice;
import com.xinchao.data.model.MonitorDataDeviceDate;
import com.xinchao.data.model.dto.MonitorAgeTypeDTO;
import com.xinchao.data.model.dto.MonitorCommunityWatchDTO;
import com.xinchao.data.service.MonitorDataDeviceService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 监播数（设备）据Service实现类
 *
 * @author dxy
 * @date 2019/3/1 15:18
 */
@Service(value = "monitorDataDeviceService")
public class MonitorDataDeviceServiceImpl implements MonitorDataDeviceService {
	@Autowired
	private MonitorDataDeviceMapper monitorDataDeviceMapper;

	/**
	 * 插入监播数据（设备）
	 *
	 * @param monitorDataDevice MonitorDataDevice
	 * @throws ServiceException
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void insertMonitorDataDevice(MonitorDataDevice monitorDataDevice) throws ServiceException {
		if (monitorDataDevice == null) {
			throw new ServiceException(MessageConstant.PARAMETER_IS_NULL);
		}
		monitorDataDeviceMapper.insertMonitorDataDevice(monitorDataDevice);
	}

	/**
	 * 批量插入监播数据（设备）
	 *
	 * @param monitorDataDeviceList List<MonitorDataDevice>
	 * @throws ServiceException
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void insertMonitorDataDeviceBatch(List<MonitorDataDevice> monitorDataDeviceList) throws ServiceException {
		if (CollectionUtils.isEmpty(monitorDataDeviceList)) {
			throw new ServiceException(MessageConstant.PARAMETER_IS_NULL);
		}
		monitorDataDeviceMapper.insertMonitorDataDeviceBatch(monitorDataDeviceList);
	}

	/**
	 * 获取监播数据设备日期数据列表
	 *
	 * @param date 日期
	 * @return List<MonitorDataDeviceDate>
	 * @throws ServiceException
	 */
	@Override
	public List<MonitorDataDeviceDate> listMonitorDataDeviceDate(String date) throws ServiceException {
		if (StringUtils.isBlank(date)) {
			throw new ServiceException(MessageConstant.DATE_IS_NULLL);
		}
		return monitorDataDeviceMapper.listMonitorDataDeviceDate(date);
	}

	/**
	 * 获取小区观看人次列表
	 *
	 * @param dateList 日期列表
	 * @return List<MonitorCommunityWatchDTO>
	 * @throws ServiceException
	 */
	@Override
	public List<MonitorCommunityWatchDTO> listMonitorCommunityWatch(List<String> dateList) throws ServiceException {
		if (CollectionUtils.isEmpty(dateList)) {
			throw new ServiceException(MessageConstant.DATE_LIST_IS_NULL);
		}
		return monitorDataDeviceMapper.listMonitorCommunityWatch(dateList);
	}

	/**
	 * 获取监播年龄住宅类型列表
	 *
	 * @param dateList          日期列表
	 * @param residenceTypeList 住宅类型列表
	 * @return List<MonitorAgeTypeDTO>
	 */
	@Override
	public List<MonitorAgeTypeDTO> listMonitorAgeType(List<String> dateList, List<String> residenceTypeList) {
		return monitorDataDeviceMapper.listMonitorAgeType(dateList, residenceTypeList);
	}

	/**
	 * 获取实际日期列表
	 *
	 * @param dateList 日期列表
	 * @return List<String>
	 */
	@Override
	public List<String> listActualDate(List<String> dateList){
		return monitorDataDeviceMapper.listActualDate(dateList);
	}
}
