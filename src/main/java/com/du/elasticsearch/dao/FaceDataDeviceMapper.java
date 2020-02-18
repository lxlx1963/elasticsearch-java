package com.du.elasticsearch.dao;

import com.xinchao.data.model.FaceDataDevice;
import com.xinchao.data.model.dto.DeviceDatePeopleSumDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author dxy
 * @date 2019/4/2 16:52
 */
public interface FaceDataDeviceMapper {
	/**
	 * 插入数据
	 *
	 * @param faceDataDevice FaceDataDevice
	 */
	void insertFaceDataDevice(@Param("faceDataDevice") FaceDataDevice faceDataDevice);

	/**
	 * 批量插入数据
	 *
	 * @param faceDataDeviceList List<FaceDataDevice>
	 */
	void insertFaceDataDeviceBatch(@Param("faceDataDeviceList") List<FaceDataDevice> faceDataDeviceList);

	/**
	 * 获取日期人数总数列表
	 *
	 * @param dateList 日期列表
	 * @return List<DeviceDatePeopleSumDTO>
	 */
	List<DeviceDatePeopleSumDTO> listDeviceDatePeopleSum(@Param("dateList") List<String> dateList);

	/**
	 * 获取住宅类型当天的机器编码列表
	 *
	 * @param dateList 日期列表
	 * @param residenceTypeList 住宅类型列表
	 * @return List<String>
	 */
	List<String> listResidenceTypeDeviceModel(@Param("dateList") List<String> dateList, @Param("residenceTypeList") List<String> residenceTypeList);
}
