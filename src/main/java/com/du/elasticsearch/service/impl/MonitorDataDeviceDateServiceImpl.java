package com.du.elasticsearch.service.impl;

import com.xinchao.data.constant.MessageConstant;
import com.xinchao.data.dao.MonitorDataDeviceDateMapper;
import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.MonitorDataDeviceDate;
import com.xinchao.data.service.MonitorDataDeviceDateService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author dxy
 * @date 2019/3/5 14:35
 */
@Service(value = "monitorDataDeviceDateService")
public class MonitorDataDeviceDateServiceImpl implements MonitorDataDeviceDateService {
	@Autowired
	private MonitorDataDeviceDateMapper monitorDataDeviceDateMapper;

	/**
	 * 插入监播数据（设备日期）
	 *
	 * @param monitorDataDeviceDate MonitorDataDeviceDate
	 * @throws ServiceException
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void insertMonitorDataDeviceDate(MonitorDataDeviceDate monitorDataDeviceDate) throws ServiceException {
		if (monitorDataDeviceDate == null) {
			throw new ServiceException(MessageConstant.PARAMETER_IS_NULL);
		}
		monitorDataDeviceDateMapper.insertMonitorDataDeviceDate(monitorDataDeviceDate);
	}

	/**
	 * 批量插入监播数据（设备日期）
	 *
	 * @param monitorDataDeviceDateList List<MonitorDataDeviceDate>
	 * @throws ServiceException
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void insertMonitorDataDeviceDateBatch(List<MonitorDataDeviceDate> monitorDataDeviceDateList) throws ServiceException {
		if (CollectionUtils.isEmpty(monitorDataDeviceDateList)) {
			throw new ServiceException(MessageConstant.PARAMETER_IS_NULL);
		}
		monitorDataDeviceDateMapper.insertMonitorDataDeviceDateBatch(monitorDataDeviceDateList);
	}
}
