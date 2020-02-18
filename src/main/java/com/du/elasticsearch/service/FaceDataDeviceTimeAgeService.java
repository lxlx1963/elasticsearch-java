package com.du.elasticsearch.service;

import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.FaceDataDeviceTimeAge;
import com.xinchao.data.model.dto.SingleDeviceTimeAgeDTO;

import java.util.List;

/**
 * @author dxy
 * @date 2019/4/2 17:33
 */
public interface FaceDataDeviceTimeAgeService {
	/**
	 * 插入数据
	 *
	 * @param faceDataDeviceTimeAge FaceDataDeviceTimeAge
	 * @throws ServiceException
	 */
	void insertFaceDataDeviceTimeAge(FaceDataDeviceTimeAge faceDataDeviceTimeAge) throws ServiceException;

	/**
	 * 批量插入数据
	 *
	 * @param faceDataDeviceTimeAgeList List<FaceDataDeviceTimeAge>
	 * @throws ServiceException
	 */
	void insertFaceDataDeviceTimeAgeBatch(List<FaceDataDeviceTimeAge> faceDataDeviceTimeAgeList) throws ServiceException;

	/**
	 * 获取时间下的年龄段列表
	 *
	 * @param dateList 日期列表
	 * @param residenceTypeList 住宅类型列表
	 * @return List<SingleDeviceTimeAgeDTO>
	 * @throws ServiceException
	 */
	List<SingleDeviceTimeAgeDTO> listDeviceTimeAge(List<String> dateList, List<String> residenceTypeList) throws ServiceException;

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
