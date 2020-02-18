package com.du.elasticsearch.service.impl;

import com.xinchao.data.constant.MessageConstant;
import com.xinchao.data.dao.FaceDataDeviceSexMapper;
import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.FaceDataDeviceSex;
import com.xinchao.data.model.dto.FaceDataSexSumDTO;
import com.xinchao.data.service.FaceDataDeviceSexService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author dxy
 * @date 2019/4/2 21:35
 */
@Service(value = "faceDataDeviceSexService")
public class FaceDataDeviceSexServiceImpl implements FaceDataDeviceSexService {
	@Autowired
	private FaceDataDeviceSexMapper faceDataDeviceSexMapper;

	/**
	 * 插入数据
	 *
	 * @param faceDataDeviceSex FaceDataDeviceSex
	 * @throws ServiceException
	 */
	@Override
	public void insertFaceDataDeviceSex(FaceDataDeviceSex faceDataDeviceSex) throws ServiceException {
		if (faceDataDeviceSex == null) {
			throw new ServiceException(MessageConstant.PARAMETER_IS_NULL);
		}
		faceDataDeviceSexMapper.insertFaceDataDeviceSex(faceDataDeviceSex);
	}

	/**
	 * 批量插入数据
	 *
	 * @param faceDataDeviceSexList List<FaceDataDeviceSex>
	 * @throws ServiceException
	 */
	@Override
	public void insertFaceDataDeviceSexBatch(List<FaceDataDeviceSex> faceDataDeviceSexList) throws ServiceException {
		if (CollectionUtils.isEmpty(faceDataDeviceSexList)) {
			throw new ServiceException(MessageConstant.PARAMETER_IS_NULL);
		}
		faceDataDeviceSexMapper.insertFaceDataDeviceSexBatch(faceDataDeviceSexList);
	}

	/**
	 * 获取人数人次总数性别列表
	 *
	 * @param dateList          日期列表
	 * @param residenceTypeList 住宅类型列表
	 * @return List<FaceDataSexSumDTO>
	 * @throws ServiceException
	 */
	@Override
	public List<FaceDataSexSumDTO> listFaceDataSexSum(List<String> dateList, List<String> residenceTypeList) throws ServiceException {
		if (CollectionUtils.isEmpty(dateList)) {
			throw new ServiceException(MessageConstant.DATE_LIST_IS_NULL);
		}
		if (CollectionUtils.isEmpty(residenceTypeList)) {
			throw new ServiceException(MessageConstant.RESIDENCE_TYPE_LIST_IS_NULLL);
		}
		return faceDataDeviceSexMapper.listFaceDataSexSum(dateList, residenceTypeList);
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
		return faceDataDeviceSexMapper.listActualDate(dateList, residenceTypeList);
	}
}
