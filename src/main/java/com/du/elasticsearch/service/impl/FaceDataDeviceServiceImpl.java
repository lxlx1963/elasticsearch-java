package com.du.elasticsearch.service.impl;

import com.xinchao.data.constant.MessageConstant;
import com.xinchao.data.dao.FaceDataDeviceMapper;
import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.FaceDataDevice;
import com.xinchao.data.model.dto.DeviceDatePeopleSumDTO;
import com.xinchao.data.service.FaceDataDeviceService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author dxy
 * @date 2019/4/2 21:29
 */
@Service(value = "faceDataDeviceService")
public class FaceDataDeviceServiceImpl implements FaceDataDeviceService {
	@Autowired
	private FaceDataDeviceMapper faceDataDeviceMapper;

	/**
	 * 插入数据
	 *
	 * @param faceDataDevice FaceDataDevice
	 * @throws ServiceException
	 */
	@Override
	public void insertFaceDataDevice(FaceDataDevice faceDataDevice) throws ServiceException {
		if (faceDataDevice == null) {
			throw new ServiceException(MessageConstant.PARAMETER_IS_NULL);
		}
		faceDataDeviceMapper.insertFaceDataDevice(faceDataDevice);
	}

	/**
	 * 批量插入数据
	 *
	 * @param faceDataDeviceList List<FaceDataDevice>
	 * @throws ServiceException
	 */
	@Override
	public void insertFaceDataDeviceBatch(List<FaceDataDevice> faceDataDeviceList) throws ServiceException {
		if (CollectionUtils.isEmpty(faceDataDeviceList)) {
			throw new ServiceException(MessageConstant.PARAMETER_IS_NULL);
		}
		faceDataDeviceMapper.insertFaceDataDeviceBatch(faceDataDeviceList);
	}

	/**
	 * 获取日期人数总数列表
	 *
	 * @param dateList 日期列表
	 * @return List<DeviceDatePeopleSumDTO>
	 */
	@Override
	public List<DeviceDatePeopleSumDTO> listDeviceDatePeopleSum(List<String> dateList) throws ServiceException {
		if (CollectionUtils.isEmpty(dateList)) {
			throw new ServiceException(MessageConstant.DATE_LIST_IS_NULL);
		}
		return faceDataDeviceMapper.listDeviceDatePeopleSum(dateList);
	}

	/**
	 * 获取住宅类型当天的机器编码列表
	 *
	 * @param dateList 日期列表
	 * @param residenceTypeList 住宅类型列表
	 * @return List<String>
	 * @throws ServiceException
	 */
	@Override
	public List<String> listResidenceTypeDeviceModel(List<String> dateList, List<String> residenceTypeList) throws ServiceException {
		if (CollectionUtils.isEmpty(residenceTypeList)) {
			throw new ServiceException(MessageConstant.RESIDENCE_TYPE_LIST_IS_NULLL);
		}
		if (CollectionUtils.isEmpty(dateList)) {
			throw new ServiceException(MessageConstant.DATE_LIST_IS_NULL);
		}
		return faceDataDeviceMapper.listResidenceTypeDeviceModel(dateList, residenceTypeList);
	}
}
