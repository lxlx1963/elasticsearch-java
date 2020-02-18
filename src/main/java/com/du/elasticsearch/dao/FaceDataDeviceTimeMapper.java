package com.du.elasticsearch.dao;

import com.xinchao.data.model.FaceDataDeviceTime;
import com.xinchao.data.model.dto.SingleDeviceTimeDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author dxy
 * @date 2019/4/2 16:59
 */
public interface FaceDataDeviceTimeMapper {
	/**
	 * 插入数据
	 *
	 * @param faceDataDeviceTime FaceDataDeviceTime
	 */
	void insertFaceDataDeviceTime(@Param("faceDataDeviceTime") FaceDataDeviceTime faceDataDeviceTime);

	/**
	 * 批量插入数据
	 *
	 * @param faceDataDeviceTimeList List<FaceDataDeviceTime>
	 */
	void insertFaceDataDeviceTimeBatch(@Param("faceDataDeviceTimeList") List<FaceDataDeviceTime> faceDataDeviceTimeList);

	/**
	 * 获取客流人数、人次按时间分组
	 *
	 * @param dateList 日期列表
	 * @param residenceTypeList 住宅类型列表
	 * @return List<SingleDeviceTimeDTO>
	 */
	List<SingleDeviceTimeDTO> listDeviceTime(@Param("dateList") List<String> dateList, @Param("residenceTypeList") List<String> residenceTypeList);

	/**
	 * 获取实际日期列表
	 *
	 * @param dateList 日期列表
	 * @param residenceTypeList 住宅类型列表
	 * @return List<String>
	 */
	List<String> listActualDate(@Param("dateList") List<String> dateList, @Param("residenceTypeList") List<String> residenceTypeList);
}
