package com.du.elasticsearch.service.impl;

import com.xinchao.data.constant.MessageConstant;
import com.xinchao.data.dao.FaceDataDeviceTimeMapper;
import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.FaceDataDeviceTime;
import com.xinchao.data.model.dto.SingleDeviceTimeDTO;
import com.xinchao.data.service.FaceDataDeviceTimeService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author dxy
 * @date 2019/4/2 21:44
 */
@Service(value = "faceDataDeviceTimeService")
public class FaceDataDeviceTimeServiceImpl implements FaceDataDeviceTimeService {
	@Autowired
	private FaceDataDeviceTimeMapper faceDataDeviceTimeMapper;

	/**
	 * 插入数据
	 *
	 * @param faceDataDeviceTime FaceDataDeviceTime
	 * @throws ServiceException
	 */
	@Override
	public void insertFaceDataDeviceTime(FaceDataDeviceTime faceDataDeviceTime) throws ServiceException {
		if (faceDataDeviceTime == null) {
			throw new ServiceException(MessageConstant.PARAMETER_IS_NULL);
		}
		faceDataDeviceTimeMapper.insertFaceDataDeviceTime(faceDataDeviceTime);
	}

	/**
	 * 批量插入数据
	 *
	 * @param faceDataDeviceTimeList List<FaceDataDeviceTime>
	 * @throws ServiceException
	 */
	@Override
	public void insertFaceDataDeviceTimeBatch(List<FaceDataDeviceTime> faceDataDeviceTimeList) throws ServiceException {
		if (CollectionUtils.isEmpty(faceDataDeviceTimeList)) {
			throw new ServiceException(MessageConstant.PARAMETER_IS_NULL);
		}
		faceDataDeviceTimeMapper.insertFaceDataDeviceTimeBatch(faceDataDeviceTimeList);
	}

	/**
	 * 获取客流人数、人次按时间分组
	 *
	 * @param dateList 日期列表
	 * @param residenceTypeList 住宅类型列表
	 * @return List<SingleDeviceTimeDTO>
	 * @throws ServiceException
	 */
	@Override
	public List<SingleDeviceTimeDTO> listDeviceTime(List<String> dateList, List<String> residenceTypeList) throws ServiceException {
		if (CollectionUtils.isEmpty(dateList)) {
			throw new ServiceException(MessageConstant.DATE_LIST_IS_NULL);
		}
		if (CollectionUtils.isEmpty(residenceTypeList)) {
			throw new ServiceException(MessageConstant.RESIDENCE_TYPE_LIST_IS_NULLL);
		}
		return faceDataDeviceTimeMapper.listDeviceTime(dateList, residenceTypeList);
	}

	/**
	 * 获取实际日期列表
	 *
	 * @param dateList 日期列表
	 * @param residenceTypeList 住宅类型列表
	 * @return List<String>
	 * @throws ServiceException
	 */
	@Override
	public List<String> listActualDate(List<String> dateList, List<String> residenceTypeList) throws ServiceException {
		if (CollectionUtils.isEmpty(dateList)) {
			throw new ServiceException(MessageConstant.DATE_LIST_IS_NULL);
		}
		if (CollectionUtils.isEmpty(residenceTypeList)) {
			throw new ServiceException(MessageConstant.RESIDENCE_TYPE_LIST_IS_NULLL);
		}
		return faceDataDeviceTimeMapper.listActualDate(dateList, residenceTypeList);
	}
}
