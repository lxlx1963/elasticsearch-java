package com.du.elasticsearch.service;

import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.FaceDataDeviceTime;
import com.xinchao.data.model.dto.SingleDeviceTimeDTO;

import java.util.List;

/**
 * @author dxy
 * @date 2019/4/2 16:59
 */
public interface FaceDataDeviceTimeService {
	/**
	 * 插入数据
	 *
	 * @param faceDataDeviceTime FaceDataDeviceTime
	 * @throws ServiceException
	 */
	void insertFaceDataDeviceTime(FaceDataDeviceTime faceDataDeviceTime) throws ServiceException;

	/**
	 * 批量插入数据
	 *
	 * @param faceDataDeviceTimeList List<FaceDataDeviceTime>
	 * @throws ServiceException
	 */
	void insertFaceDataDeviceTimeBatch(List<FaceDataDeviceTime> faceDataDeviceTimeList) throws ServiceException;

	/**
	 * 获取客流人数、人次按时间分组
	 *
	 * @param dateList 日期列表
	 * @param residenceTypeList 住宅类型列表
	 * @return List<SingleDeviceTimeDTO>
	 */
	List<SingleDeviceTimeDTO> listDeviceTime(List<String> dateList, List<String> residenceTypeList) throws ServiceException;

	/**
	 * 获取实际日期列表
	 *
	 * @param dateList 日期列表
	 * @param residenceTypeList 住宅类型列表
	 * @return List<String>
	 * @throws ServiceException
	 */
	List<String> listActualDate(List<String> dateList, List<String> residenceTypeList) throws ServiceException;
}
