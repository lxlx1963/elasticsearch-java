package com.du.elasticsearch.dao;

import com.xinchao.data.model.MonitorDataAge;
import com.xinchao.data.model.dto.MonitorAgeWatchDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 监播数据（年龄）Mapper
 *
 * @author dxy
 * @date 2019/3/1 21:59
 */
public interface MonitorDataAgeMapper {
	/**
	 * 插入数据
	 *
	 * @param monitorDataAge MonitorDataAge
	 */
	void insertMonitorDataAge(@Param("monitorDataAge") MonitorDataAge monitorDataAge);

	/**
	 * 批量插入数据
	 *
	 * @param monitorDataAgeList List<MonitorDataAge>
	 */
	void insertMonitorDataAgeBatch(@Param("monitorDataAgeList") List<MonitorDataAge> monitorDataAgeList);

	/**
	 * 获取监播年龄观看人次列表
	 *
	 * @param dateList 日期列表
	 * @return List<MonitorAgeWatchDTO>
	 */
	List<MonitorAgeWatchDTO> listMonitorAgeWatch(@Param("dateList") List<String> dateList);

	/**
	 * 获取实际日期列表
	 *
	 * @param dateList 日期列表
	 * @return List<String>
	 */
	List<String> listActualDate(@Param("dateList") List<String> dateList);
}
