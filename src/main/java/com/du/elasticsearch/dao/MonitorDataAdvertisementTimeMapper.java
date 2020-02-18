package com.du.elasticsearch.dao;

import com.xinchao.data.model.MonitorDataAdvertisementTime;
import com.xinchao.data.model.dto.MonitorWatchTouchDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 监播数据（广告时间）Mapper
 *
 * @author dxy
 * @date 2019/3/1 21:55
 */
public interface MonitorDataAdvertisementTimeMapper {
	/**
	 * 插入数据
	 *
	 * @param monitorDataAdvertisementTime MonitorDataAdvertisementTime
	 */
	void insertMonitorDataAdvertisementTime(@Param("monitorDataAdvertisementTime") MonitorDataAdvertisementTime monitorDataAdvertisementTime);

	/**
	 * 批量插入数据
	 *
	 * @param monitorDataAdvertisementTimeList List<MonitorDataAdvertisementTime>
	 */
	void insertMonitorDataAdvertisementTimeBatch(@Param("monitorDataAdvertisementTimeList") List<MonitorDataAdvertisementTime> monitorDataAdvertisementTimeList);

	/**
	 * 获取监播观看触达人次列表
	 *
	 * @param dateList 日期列表
	 * @return List<MonitorWatchTouchDTO>
	 */
	List<MonitorWatchTouchDTO> listMonitorWatchTouch(@Param("dateList") List<String> dateList);

	/**
	 * 获取实际日期列表
	 *
	 * @param dateList 日期列表
	 * @return List<String>
	 */
	List<String> listActualDate(@Param("dateList") List<String> dateList);
}
