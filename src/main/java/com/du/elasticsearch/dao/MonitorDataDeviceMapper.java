package com.du.elasticsearch.dao;

import com.xinchao.data.model.MonitorDataDevice;
import com.xinchao.data.model.MonitorDataDeviceDate;
import com.xinchao.data.model.dto.MonitorAgeTypeDTO;
import com.xinchao.data.model.dto.MonitorCommunityWatchDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 监播数（设备）据Mapper
 *
 * @author dxy
 * @date 2019/3/1 10:55
 */
public interface MonitorDataDeviceMapper {
	/**
	 * 插入监播数据（设备）
	 *
	 * @param monitorDataDevice MonitorDataDevice
	 */
	void insertMonitorDataDevice(@Param("monitorDataDevice") MonitorDataDevice monitorDataDevice);

	/**
	 * 批量插入监播数据（设备）
	 *
	 * @param monitorDataDeviceList List<MonitorDataDevice>
	 */
	void insertMonitorDataDeviceBatch(@Param("monitorDataDeviceList") List<MonitorDataDevice> monitorDataDeviceList);

	/**
	 * 获取监播数据设备日期数据列表
	 *
	 * @param date 日期
	 * @return List<MonitorDataDeviceDate>
	 */
	List<MonitorDataDeviceDate> listMonitorDataDeviceDate(String date);

	/**
	 * 获取小区观看人次列表
	 *
	 * @param dateList 日期列表
	 * @return List<MonitorCommunityWatchDTO>
	 */
	List<MonitorCommunityWatchDTO> listMonitorCommunityWatch(@Param("dateList") List<String> dateList);

	/**
	 * 获取监播年龄住宅类型列表
	 *
	 * @param dateList          日期列表
	 * @param residenceTypeList 住宅类型列表
	 * @return List<MonitorAgeTypeDTO>
	 */
	List<MonitorAgeTypeDTO> listMonitorAgeType(@Param("dateList") List<String> dateList, @Param("residenceTypeList") List<String> residenceTypeList);

	/**
	 * 获取实际日期列表
	 *
	 * @param dateList 日期列表
	 * @return List<String>
	 */
	List<String> listActualDate(@Param("dateList") List<String> dateList);

}
