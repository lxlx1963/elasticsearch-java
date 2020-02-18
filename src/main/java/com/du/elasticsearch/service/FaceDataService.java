package com.du.elasticsearch.service;

import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.FaceData;
import com.xinchao.data.model.dto.*;

import java.util.List;

/**
 * 人脸数据Service
 *
 * @author dxy
 * @date 2019/3/1 15:15
 */
public interface FaceDataService {

	/**
	 * 插入人脸数据
	 *
	 * @param faceData FaceData
	 * @throws ServiceException
	 */
	void insertFaceData(FaceData faceData) throws ServiceException;

	/**
	 * 批量插入人脸数据
	 *
	 * @param faceDataList List<FaceData>
	 * @throws ServiceException
	 */
	void insertFaceDataBatch(List<FaceData> faceDataList) throws ServiceException;

	/**
	 * 获取人脸数据列表（按省、性别分组）
	 *
	 * @param date 日期
	 * @return List<FaceDataProvinceSexDTO>
	 */
	List<FaceDataProvinceSexDTO> listFaceDataProvinceSexDTO(String date) throws ServiceException;

	/**
	 * 获取人脸数据列表（按省、年龄分组）
	 *
	 * @param date 日期
	 * @return List<FaceDataProvinceAgeDTO>
	 */
	List<FaceDataProvinceAgeDTO> listFaceDataProvinceAgeDTO(String date) throws ServiceException;

	/**
	 * 获取汇总监播DTO
	 *
	 * @param date 日期
	 * @return SummarizePeopleNumDTO
	 * @throws ServiceException
	 */
	SummarizePeopleNumDTO getSummarizePeopleNumDTO(String date) throws ServiceException;


	/**
	 * 获取终端编码列表
	 *
	 * @param date 日期
	 * @return List<String>
	 */
	List<String> listDeviceModel(String date) throws ServiceException;

	/**
	 * 获取城市列表
	 *
	 * @param date 日期
	 * @return List<String>
	 */
	List<String> listCity(String date) throws ServiceException;


	/**
	 * 获取省城市数量列表
	 *
	 * @param date 日期
	 * @return List<ProvinceCityNumDTO>
	 */
	List<ProvinceCityNumDTO> listProvinceCityNumDTO(String date) throws ServiceException;

	/**
	 * 获取省终端数量列表
	 *
	 * @param date 日期
	 * @return List<ProvinceDeviceNumDTO>
	 * @throws ServiceException
	 */
	List<ProvinceDeviceNumDTO> listProvinceDeviceNumDTO(String date) throws ServiceException;

	/**
	 * 获取省小区数量列表
	 *
	 * @param date 日期
	 * @return List<ProvinceCommunityNumDTO>
	 * @throws ServiceException
	 */
	List<ProvinceCommunityNumDTO> listProvinceCommunityNumDTO(String date) throws ServiceException;

}
