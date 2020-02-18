package com.du.elasticsearch.service;

import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.FaceDataDevice;
import com.xinchao.data.model.dto.DeviceDatePeopleSumDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author dxy
 * @date 2019/4/2 21:29
 */
public interface FaceDataDeviceService {
	/**
	 * 插入数据
	 *
	 * @param faceDataDevice FaceDataDevice
	 * @throws ServiceException
	 */
	void insertFaceDataDevice(FaceDataDevice faceDataDevice) throws ServiceException;

	/**
	 * 批量插入数据
	 *
	 * @param faceDataDeviceList List<FaceDataDevice>
	 * @throws ServiceException
	 */
	void insertFaceDataDeviceBatch(List<FaceDataDevice> faceDataDeviceList) throws ServiceException;

	/**
	 * 获取日期人数总数列表
	 *
	 * @param dateList 日期列表
	 * @return List<DeviceDatePeopleSumDTO>
	 */
	List<DeviceDatePeopleSumDTO> listDeviceDatePeopleSum(@Param("dateList") List<String> dateList) throws ServiceException;

	/**
	 * 获取住宅类型当天的机器编码列表
	 *
	 * @param dateList 日期列表
	 * @param residenceTypeList 住宅类型列表
	 * @return List<String>
	 */
	List<String> listResidenceTypeDeviceModel(List<String> dateList, List<String> residenceTypeList) throws ServiceException;
}
