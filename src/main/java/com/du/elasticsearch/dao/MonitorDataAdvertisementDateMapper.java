package com.du.elasticsearch.dao;

import com.xinchao.data.model.MonitorDataAdvertisementDate;
import com.xinchao.data.model.dto.MonitorAdvertisementWatchDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 监播数据（广告日期）Mapper
 *
 * @author dxy
 * @date 2019/3/1 21:50
 */
public interface MonitorDataAdvertisementDateMapper {
	/**
	 * 插入数据
	 *
	 * @param monitorDataAdvertisementDate MonitorDataAdvertisementDate
	 */
	void insertMonitorDataAdvertisementDate(@Param("monitorDataAdvertisementDate") MonitorDataAdvertisementDate monitorDataAdvertisementDate);

	/**
	 * 批量插入数据
	 *
	 * @param monitorDataAdvertisementDateList List<MonitorDataAdvertisementDate>
	 */
	void insertMonitorDataAdvertisementDateBatch(@Param("monitorDataAdvertisementDateList") List<MonitorDataAdvertisementDate> monitorDataAdvertisementDateList);

	/**
	 * 获取广告观看次数列表
	 *
	 * @param dateList 日期列表
	 * @param topSize  钱多少条
	 * @return List<MonitorAdvertisementWatchDTO>
	 */
	List<MonitorAdvertisementWatchDTO> listMonitorAdvertisementWatch(@Param("dateList") List<String> dateList, @Param("topSize") int topSize);

	/**
	 * 获取实际日期列表
	 *
	 * @param dateList 日期列表
	 * @return List<String>
	 */
	List<String> listActualDate(@Param("dateList") List<String> dateList);
}
