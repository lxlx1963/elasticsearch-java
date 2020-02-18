package com.du.elasticsearch.service.impl;

import com.xinchao.data.constant.MessageConstant;
import com.xinchao.data.dao.MonitorDataAgeMapper;
import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.MonitorDataAge;
import com.xinchao.data.model.dto.MonitorAgeWatchDTO;
import com.xinchao.data.service.MonitorDataAgeService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 监播数据（年龄）Service实现类
 *
 * @author dxy
 * @date 2019/3/2 9:35
 */
@Service(value = "monitorDataAgeService")
public class MonitorDataAgeServiceImpl implements MonitorDataAgeService {
	@Autowired
	private MonitorDataAgeMapper monitorDataAgeMapper;

	/**
	 * 插入数据
	 *
	 * @param monitorDataAge MonitorDataAge
	 * @throws ServiceException
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void insertMonitorDataAge(MonitorDataAge monitorDataAge) throws ServiceException {
		if (monitorDataAge == null) {
			throw new ServiceException(MessageConstant.PARAMETER_IS_NULL);
		}
		monitorDataAgeMapper.insertMonitorDataAge(monitorDataAge);
	}

	/**
	 * 批量插入数据
	 *
	 * @param monitorDataAgeList List<MonitorDataAge>
	 * @throws ServiceException
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void insertMonitorDataAgeBatch(List<MonitorDataAge> monitorDataAgeList) throws ServiceException {
		if (CollectionUtils.isEmpty(monitorDataAgeList)) {
			throw new ServiceException(MessageConstant.PARAMETER_IS_NULL);
		}
		monitorDataAgeMapper.insertMonitorDataAgeBatch(monitorDataAgeList);
	}

	/**
	 * 获取监播年龄观看人次列表
	 *
	 * @param dateList 日期列表
	 * @return List<MonitorAgeWatchDTO>
	 * @throws ServiceException
	 */
	@Override
	public List<MonitorAgeWatchDTO> listMonitorAgeWatch(List<String> dateList) throws ServiceException {
		if (CollectionUtils.isEmpty(dateList)) {
			throw new ServiceException(MessageConstant.DATE_LIST_IS_NULL);
		}
		return monitorDataAgeMapper.listMonitorAgeWatch(dateList);
	}

	/**
	 * 获取实际日期列表
	 * @param dateList 日期列表
	 * @return List<String>
	 * @throws ServiceException
	 */
	@Override
	public List<String> listActualDate(List<String> dateList) throws ServiceException {
		if (CollectionUtils.isEmpty(dateList)) {
			throw new ServiceException(MessageConstant.DATE_LIST_IS_NULL);
		}
		return monitorDataAgeMapper.listActualDate(dateList);
	}
}
