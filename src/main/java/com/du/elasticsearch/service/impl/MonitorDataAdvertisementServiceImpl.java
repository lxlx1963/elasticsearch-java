package com.du.elasticsearch.service.impl;

import com.xinchao.data.constant.MessageConstant;
import com.xinchao.data.dao.MonitorDataAdvertisementMapper;
import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.*;
import com.xinchao.data.service.MonitorDataAdvertisementService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author dxy
 * @date 2019/3/1 15:18
 */
@Service(value = "monitorDataAdvertisementService")
public class MonitorDataAdvertisementServiceImpl implements MonitorDataAdvertisementService {
	@Autowired
	private MonitorDataAdvertisementMapper monitorDataAdvertisementMapper;

	/**
	 * 插入监播数据
	 *
	 * @param monitorDataAdvertisement MonitorDataAdvertisement
	 * @throws ServiceException
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void insertMonitorDataAdvertisement(MonitorDataAdvertisement monitorDataAdvertisement) throws ServiceException {
		if (monitorDataAdvertisement == null) {
			throw new ServiceException(MessageConstant.PARAMETER_IS_NULL);
		}
		monitorDataAdvertisementMapper.insertMonitorDataAdvertisement(monitorDataAdvertisement);
	}

	/**
	 * 批量插入监播数据
	 *
	 * @param monitorDataAdvertisementList List<MonitorDataAdvertisement>
	 * @throws ServiceException
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void insertMonitorDataAdvertisementBatch(List<MonitorDataAdvertisement> monitorDataAdvertisementList) throws ServiceException {
		if (CollectionUtils.isEmpty(monitorDataAdvertisementList)) {
			throw new ServiceException(MessageConstant.PARAMETER_IS_NULL);
		}
		monitorDataAdvertisementMapper.insertMonitorDataAdvertisementBatch(monitorDataAdvertisementList);
	}

	/**
	 * 获取监播数据广告列表（按日期统计）
	 *
	 * @param date 日期
	 * @return List<MonitorDataAdvertisementDate>
	 * @throws ServiceException
	 */
	@Override
	public List<MonitorDataAdvertisementDate> listMonitorDataAdvertisementDate(String date) throws ServiceException {
		if (StringUtils.isBlank(date)) {
			throw new ServiceException(MessageConstant.DATE_IS_NULLL);
		}
		return monitorDataAdvertisementMapper.listMonitorDataAdvertisementDate(date);
	}

	/**
	 * 获取监播数据广告列表（按时间统计）
	 *
	 * @param date 日期
	 * @return List<MonitorDataAdvertisementTime>
	 * @throws ServiceException
	 */
	@Override
	public List<MonitorDataAdvertisementTime> listMonitorDataAdvertisementTime(String date) throws ServiceException {
		if (StringUtils.isBlank(date)) {
			throw new ServiceException(MessageConstant.DATE_IS_NULLL);
		}
		return monitorDataAdvertisementMapper.listMonitorDataAdvertisementTime(date);
	}

	/**
	 * 获取监播数据列表（按日期统计）
	 *
	 * @param date 日期
	 * @return List<MonitorData>
	 * @throws ServiceException
	 */
	@Override
	public List<MonitorData> listMonitorData(String date) throws ServiceException {
		if (StringUtils.isBlank(date)) {
			throw new ServiceException(MessageConstant.DATE_IS_NULLL);
		}
		return monitorDataAdvertisementMapper.listMonitorData(date);
	}

	/**
	 * 获取监播数据列表（按年龄统计）
	 *
	 * @param date 日期
	 * @return List<MonitorDataAge>
	 * @throws ServiceException
	 */
	@Override
	public List<MonitorDataAge> listMonitorDataAge(String date) throws ServiceException {
		if (StringUtils.isBlank(date)) {
			throw new ServiceException(MessageConstant.DATE_IS_NULLL);
		}
		return monitorDataAdvertisementMapper.listMonitorDataAge(date);
	}
}
