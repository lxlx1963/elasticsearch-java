package com.du.elasticsearch.service;

import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.FaceDataSex;
import com.xinchao.data.model.dto.FaceDataSexDTO;
import com.xinchao.data.model.dto.SingleDeviceTimeSexDTO;

import java.util.List;

/**
 * 人脸数据（性别）Service
 *
 * @author dxy
 * @date 2019/3/1 21:40
 */
public interface FaceDataSexService {
	/**
	 * 插入数据
	 *
	 * @param faceDataSex FaceDataSex
	 * @throws ServiceException
	 */
	void insertFaceDataSex(FaceDataSex faceDataSex) throws ServiceException;

	/**
	 * 批量插入数据
	 *
	 * @param faceDataSexList List<FaceDataSex>
	 * @throws ServiceException
	 */
	void insertFaceDataSexBatch(List<FaceDataSex> faceDataSexList) throws ServiceException;

	/**
	 * 获取人脸数据性别DTO列表
	 *
	 * @param dateList 日期列表
	 * @return List<FaceDataSexDTO>
	 */
	List<FaceDataSexDTO> listFaceDataSexDTO(List<String> dateList) throws ServiceException;

	/**
	 * 获取客流人数、人次按时间、性别分组
	 *
	 * @param date          日期
	 * @param residenceTypeList 住宅类型列表
	 * @return List<DeviceTimeSexDTO>
	 * @throws ServiceException
	 */
	List<SingleDeviceTimeSexDTO> listDeviceTimeSex(String date, List<String> residenceTypeList) throws ServiceException;

	/**
	 * 获取实际日期列表
	 *
	 * @param dateList 日期列表
	 * @return List<String>
	 * @throws ServiceException
	 */
	List<String> listActualDate(List<String> dateList) throws ServiceException;

}
