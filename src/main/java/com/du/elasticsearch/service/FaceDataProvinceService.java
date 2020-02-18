package com.du.elasticsearch.service;

import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.FaceDataProvince;
import com.xinchao.data.model.dto.DateDeviceNumDTO;
import com.xinchao.data.model.dto.SummarizeFaceDataProvinceDTO;

import java.util.List;
import java.util.Map;

/**
 * 人数数据(省)Service
 *
 * @author dxy
 * @date 2019/3/5 10:16
 */
public interface FaceDataProvinceService {

	/**
	 * 插入人数数据(省)
	 *
	 * @param faceDataProvince FaceDataProvince
	 * @throws ServiceException
	 */
	void insertFaceDataProvince(FaceDataProvince faceDataProvince) throws ServiceException;

	/**
	 * 批量插入人数数据(省)
	 *
	 * @param faceDataProvinceList List<FaceDataProvince>
	 * @throws ServiceException
	 */
	void insertFaceDataProvinceBatch(List<FaceDataProvince> faceDataProvinceList) throws ServiceException;

	/**
	 * 获取汇总省人脸数据列表
	 *
	 * @param date 日期
	 * @return List<SummarizeFaceDataProvinceDTO>
	 */
	List<SummarizeFaceDataProvinceDTO> listSummarizeFaceDataProvinceDTO(String date) throws ServiceException;

	/**
	 * 获取日期对应机器数量列表
	 *
	 * @param dateList 日期列表
	 * @return List<DateDeviceNumDTO>
	 * @throws ServiceException
	 */
	List<DateDeviceNumDTO> listDateDeviceNum(List<String> dateList) throws ServiceException;

	/**
	 * 获取日期机器数量Map
	 *
	 * @param dateList 日期列表
	 * @return Map<String, Integer>
	 * @throws ServiceException
	 */
	Map<String, Integer> getDateDeviceNumMap(List<String> dateList) throws ServiceException;
}
