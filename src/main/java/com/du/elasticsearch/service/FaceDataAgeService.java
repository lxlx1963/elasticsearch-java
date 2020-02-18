package com.du.elasticsearch.service;

import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.FaceDataAge;
import com.xinchao.data.model.dto.FaceDataAgeDTO;
import com.xinchao.data.model.dto.SingleDeviceTimeAgeDTO;

import java.util.List;

/**
 * 人脸数据（年龄）Service
 *
 * @author dxy
 * @date 2019/3/1 21:19
 */
public interface FaceDataAgeService {
	/**
	 * 插入数据
	 *
	 * @param faceDataAge FaceDataAge
	 * @throws ServiceException
	 */
	void insertFaceDataAge(FaceDataAge faceDataAge) throws ServiceException;

	/**
	 * 批量插入数据
	 *
	 * @param faceDataAgeList List<FaceDataAge>
	 * @throws ServiceException
	 */
	void insertFaceDataAgeBatch(List<FaceDataAge> faceDataAgeList) throws ServiceException;

	/**
	 * 获取人脸数据年龄DTO列表
	 *
	 * @param dateList 日期列表
	 * @return List<FaceDataAgeDTO>
	 */
	List<FaceDataAgeDTO> listFaceDataAgeDTO(List<String> dateList) throws ServiceException;

	/**
	 * 获取时间下的年龄段列表
	 *
	 * @param date              日期
	 * @param residenceTypeList 住宅类型列表
	 * @return List<DeviceTimeAgeDTO>
	 * @throws ServiceException
	 */
	List<SingleDeviceTimeAgeDTO> listDeviceTimeAge(String date, List<String> residenceTypeList) throws ServiceException;

	/**
	 * 获取实际日期列表
	 *
	 * @param dateList 日期列表
	 * @return List<String>
	 * @throws ServiceException
	 */
	List<String> listActualDate(List<String> dateList) throws ServiceException;
}
