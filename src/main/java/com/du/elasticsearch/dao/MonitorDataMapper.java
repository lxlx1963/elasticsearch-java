package com.du.elasticsearch.dao;

import com.xinchao.data.model.MonitorData;
import com.xinchao.data.model.dto.MonitorDataDTO;
import com.xinchao.data.model.dto.MonitorDateWatchDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 监播数据
 *
 * @author dxy
 * @date 2019/3/1 21:44
 */
public interface MonitorDataMapper {
	/**
	 * 插入数据
	 *
	 * @param monitorData MonitorData
	 */
	void insertMonitorData(@Param("monitorData") MonitorData monitorData);

	/**
	 * 批量插入数据
	 *
	 * @param monitorDataList List<MonitorData>
	 */
	void insertMonitorDataBatch(@Param("monitorDataList") List<MonitorData> monitorDataList);

	/**
	 * 获取监播数据DTO
	 *
	 * @param dateList 日期列表
	 * @return MonitorDataDTO
	 */
	MonitorDataDTO getMonitorDataDTO(@Param("dateList") List<String> dateList);

	/**
	 * 获取监日期观看人次列表
	 *
	 * @param dateList 日期列表
	 * @return List<MonitorDateWatchDTO>
	 */
	List<MonitorDateWatchDTO> listMonitorDateWatch(@Param("dateList") List<String> dateList);

	/**
	 * 获取实际日期列表
	 *
	 * @param dateList 日期列表
	 * @return List<String>
	 */
	List<String> listActualDate(@Param("dateList") List<String> dateList);
}
