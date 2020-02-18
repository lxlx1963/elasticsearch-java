package com.du.elasticsearch.service.impl;

import com.xinchao.data.constant.MessageConstant;
import com.xinchao.data.dao.FaceDataDeviceTimeSexMapper;
import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.FaceDataDeviceTimeSex;
import com.xinchao.data.model.dto.SingleDeviceTimeSexDTO;
import com.xinchao.data.service.FaceDataDeviceTimeSexService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author dxy
 * @date 2019/4/2 21:47
 */
@Service(value = "faceDataDeviceTimeSexService")
public class FaceDataDeviceTimeSexServiceImpl implements FaceDataDeviceTimeSexService {
	@Autowired
	private FaceDataDeviceTimeSexMapper faceDataDeviceTimeSexMapper;

	/**
	 * 插入数据
	 *
	 * @param faceDataDeviceTimeSex FaceDataDeviceTimeSex
	 * @throws ServiceException
	 */
	@Override
	public void insertFaceDataDeviceTimeSex(FaceDataDeviceTimeSex faceDataDeviceTimeSex) throws ServiceException {
		if (faceDataDeviceTimeSex == null) {
			throw new ServiceException(MessageConstant.PARAMETER_IS_NULL);
		}
		faceDataDeviceTimeSexMapper.insertFaceDataDeviceTimeSex(faceDataDeviceTimeSex);
	}

	/**
	 * 批量插入数据
	 *
	 * @param faceDataDeviceTimeSexList List<FaceDataDeviceTimeSex>
	 * @throws ServiceException
	 */
	@Override
	public void insertFaceDataDeviceTimeSexBatch(List<FaceDataDeviceTimeSex> faceDataDeviceTimeSexList) throws ServiceException {
		if (CollectionUtils.isEmpty(faceDataDeviceTimeSexList)) {
			throw new ServiceException(MessageConstant.PARAMETER_IS_NULL);
		}
		faceDataDeviceTimeSexMapper.insertFaceDataDeviceTimeSexBatch(faceDataDeviceTimeSexList);
	}

	/**
	 * 获取客流人数、人次按时间、性别分组
	 *
	 * @param dateList 日期列表
	 * @param residenceTypeList 住宅类型列表
	 * @return List<SingleDeviceTimeSexDTO>
	 * @throws ServiceException
	 */
	@Override
	public List<SingleDeviceTimeSexDTO> listDeviceTimeSex(List<String> dateList, List<String> residenceTypeList) throws ServiceException {
		if (CollectionUtils.isEmpty(dateList)) {
			throw new ServiceException(MessageConstant.DATE_LIST_IS_NULL);
		}
		if (CollectionUtils.isEmpty(residenceTypeList)) {
			throw new ServiceException(MessageConstant.RESIDENCE_TYPE_LIST_IS_NULLL);
		}
		return faceDataDeviceTimeSexMapper.listDeviceTimeSex(dateList, residenceTypeList);
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
		return faceDataDeviceTimeSexMapper.listActualDate(dateList, residenceTypeList);
	}
}
