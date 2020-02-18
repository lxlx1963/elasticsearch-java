package com.du.elasticsearch.dao;

import com.xinchao.data.model.MonitorDataDeviceDate;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 监播数据(设备日期)Mapper
 *
 * @author dxy
 * @date 2019/3/5 14:30
 */
public interface MonitorDataDeviceDateMapper {
	/**
	 * 插入监播数据（设备日期）
	 *
	 * @param monitorDataDeviceDate MonitorDataDeviceDate
	 */
	void insertMonitorDataDeviceDate(@Param("monitorDataDeviceDate") MonitorDataDeviceDate monitorDataDeviceDate);

	/**
	 * 批量插入监播数据（设备日期）
	 *
	 * @param monitorDataDeviceDateList List<MonitorDataDeviceDate>
	 */
	void insertMonitorDataDeviceDateBatch(@Param("monitorDataDeviceDateList") List<MonitorDataDeviceDate> monitorDataDeviceDateList);

}
