package com.du.elasticsearch.dao;

import com.xinchao.data.model.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 监播数（广告）据Mapper
 *
 * @author dxy
 * @date 2019/3/1 10:55
 */
public interface MonitorDataAdvertisementMapper {
	/**
	 * 插入监播数据
	 *
	 * @param monitorDataAdvertisement MonitorDataAdvertisement
	 */
	void insertMonitorDataAdvertisement(@Param("monitorDataAdvertisement") MonitorDataAdvertisement monitorDataAdvertisement);

	/**
	 * 批量插入监播数据
	 *
	 * @param monitorDataAdvertisementList List<MonitorDataAdvertisement>
	 */
	void insertMonitorDataAdvertisementBatch(@Param("monitorDataAdvertisementList") List<MonitorDataAdvertisement> monitorDataAdvertisementList);

	/**
	 * 获取监播数据广告列表（按日期统计）
	 *
	 * @param date 日期
	 * @return List<MonitorDataAdvertisementDate>
	 */
	List<MonitorDataAdvertisementDate> listMonitorDataAdvertisementDate(String date);

	/**
	 * 获取监播数据广告列表（按时间统计）
	 *
	 * @param date 日期
	 * @return List<MonitorDataAdvertisementDate>
	 */
	List<MonitorDataAdvertisementTime> listMonitorDataAdvertisementTime(String date);

	/**
	 * 获取监播数据列表（按日期统计）
	 *
	 * @param date 日期
	 * @return List<MonitorData>
	 */
	List<MonitorData> listMonitorData(String date);

	/**
	 * 获取监播数据列表（按年龄统计）
	 *
	 * @param date 日期
	 * @return List<MonitorDataAge>
	 */
	List<MonitorDataAge> listMonitorDataAge(String date);
}
