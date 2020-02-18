package com.du.elasticsearch.service;

import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.*;

import java.util.List;

/**
 * 监播数（广告）据Service
 *
 * @author dxy
 * @date 2019/3/1 15:25
 */
public interface MonitorDataAdvertisementService {
	/**
	 * 插入监播数据
	 *
	 * @param monitorDataAdvertisement MonitorDataAdvertisement
	 */
	void insertMonitorDataAdvertisement(MonitorDataAdvertisement monitorDataAdvertisement) throws ServiceException;

	/**
	 * 批量插入监播数据
	 *
	 * @param monitorDataAdvertisementList List<MonitorDataAdvertisement>
	 */
	void insertMonitorDataAdvertisementBatch(List<MonitorDataAdvertisement> monitorDataAdvertisementList) throws ServiceException;


	/**
	 * 获取监播数据广告列表（按日期统计）
	 *
	 * @param date 日期
	 * @return List<MonitorDataAdvertisementDate>
	 * @throws ServiceException
	 */
	List<MonitorDataAdvertisementDate> listMonitorDataAdvertisementDate(String date) throws ServiceException;

	/**
	 * 获取监播数据广告列表（按时间统计）
	 *
	 * @param date 日期
	 * @return List<MonitorDataAdvertisementTime>
	 * @throws ServiceException
	 */
	List<MonitorDataAdvertisementTime> listMonitorDataAdvertisementTime(String date) throws ServiceException;

	/**
	 * 获取监播数据列表（按日期统计）
	 *
	 * @param date 日期
	 * @return List<MonitorData>
	 * @throws ServiceException
	 */
	List<MonitorData> listMonitorData(String date) throws ServiceException;


	/**
	 * 获取监播数据列表（按年龄统计）
	 *
	 * @param date 日期
	 * @return List<MonitorDataAge>
	 * @throws ServiceException
	 */
	List<MonitorDataAge> listMonitorDataAge(String date) throws ServiceException;
}
