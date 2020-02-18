package com.du.elasticsearch.service;

import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.FaceDataDeviceTimeSex;
import com.xinchao.data.model.dto.SingleDeviceTimeSexDTO;

import java.util.List;

/**
 * @author dxy
 * @date 2019/4/2 17:07
 */
public interface FaceDataDeviceTimeSexService {
	/**
	 * 插入数据
	 *
	 * @param faceDataDeviceTimeSex FaceDataDeviceTimeSex
	 * @throws ServiceException
	 */
	void insertFaceDataDeviceTimeSex(FaceDataDeviceTimeSex faceDataDeviceTimeSex) throws ServiceException;

	/**
	 * 批量插入数据
	 *
	 * @param faceDataDeviceTimeSexList List<FaceDataDeviceTimeSex>
	 * @throws ServiceException
	 */
	void insertFaceDataDeviceTimeSexBatch(List<FaceDataDeviceTimeSex> faceDataDeviceTimeSexList) throws ServiceException;

	/**
	 * 获取客流人数、人次按时间、性别分组
	 *
	 * @param dateList 日期列表
	 * @param residenceTypeList 住宅类型列表
	 * @return List<SingleDeviceTimeSexDTO>
	 * @throws ServiceException
	 */
	List<SingleDeviceTimeSexDTO> listDeviceTimeSex(List<String> dateList, List<String> residenceTypeList) throws ServiceException;

	/**
	 * 获取实际日期列表
	 *
	 * @param dateList 日期列表
	 * @param residenceTypeList 住宅类型列表
	 * @return List<String>
	 */
	List<String> listActualDate(List<String> dateList, List<String> residenceTypeList) throws ServiceException;
}
