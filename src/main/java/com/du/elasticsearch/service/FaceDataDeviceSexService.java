package com.du.elasticsearch.service;

import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.FaceDataDeviceSex;
import com.xinchao.data.model.dto.FaceDataSexSumDTO;

import java.util.List;

/**
 * @author dxy
 * @date 2019/4/2 17:29
 */
public interface FaceDataDeviceSexService {
	/**
	 * 插入数据
	 *
	 * @param faceDataDeviceSex FaceDataDeviceSex
	 * @throws ServiceException
	 */
	void insertFaceDataDeviceSex(FaceDataDeviceSex faceDataDeviceSex) throws ServiceException;

	/**
	 * 批量插入数据
	 *
	 * @param faceDataDeviceSexList List<FaceDataDeviceSex>
	 * @throws ServiceException
	 */
	void insertFaceDataDeviceSexBatch(List<FaceDataDeviceSex> faceDataDeviceSexList) throws ServiceException;

	/**
	 * 获取人数人次总数性别列表
	 *
	 * @param dateList          日期列表
	 * @param residenceTypeList 住宅类型列表
	 * @return List<FaceDataSexDTO>
	 */
	List<FaceDataSexSumDTO> listFaceDataSexSum(List<String> dateList, List<String> residenceTypeList) throws ServiceException;


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
