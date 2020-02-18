package com.du.elasticsearch.service.impl;

import com.xinchao.data.constant.MessageConstant;
import com.xinchao.data.dao.FaceDataDeviceTimeAgeMapper;
import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.FaceDataDeviceTimeAge;
import com.xinchao.data.model.dto.SingleDeviceTimeAgeDTO;
import com.xinchao.data.service.FaceDataDeviceTimeAgeService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author dxy
 * @date 2019/4/2 21:40
 */
@Service(value = "faceDataDeviceTimeAgeService")
public class FaceDataDeviceTimeAgeServiceImpl implements FaceDataDeviceTimeAgeService {
	@Autowired
	private FaceDataDeviceTimeAgeMapper faceDataDeviceTimeAgeMapper;

	/**
	 * 插入数据
	 *
	 * @param faceDataDeviceTimeAge FaceDataDeviceTimeAge
	 * @throws ServiceException
	 */
	@Override
	public void insertFaceDataDeviceTimeAge(FaceDataDeviceTimeAge faceDataDeviceTimeAge) throws ServiceException {
		if (faceDataDeviceTimeAge == null) {
			throw new ServiceException(MessageConstant.PARAMETER_IS_NULL);
		}
		faceDataDeviceTimeAgeMapper.insertFaceDataDeviceTimeAge(faceDataDeviceTimeAge);
	}

	/**
	 * 批量插入数据
	 *
	 * @param faceDataDeviceTimeAgeList List<FaceDataDeviceTimeAge>
	 * @throws ServiceException
	 */
	@Override
	public void insertFaceDataDeviceTimeAgeBatch(List<FaceDataDeviceTimeAge> faceDataDeviceTimeAgeList) throws ServiceException {
		if (CollectionUtils.isEmpty(faceDataDeviceTimeAgeList)) {
			throw new ServiceException(MessageConstant.PARAMETER_IS_NULL);
		}
		faceDataDeviceTimeAgeMapper.insertFaceDataDeviceTimeAgeBatch(faceDataDeviceTimeAgeList);
	}

	/**
	 * 获取时间下的年龄段列表
	 *
	 * dateList 日期列表
	 * @param residenceTypeList 住宅类型列表
	 * @return List<SingleDeviceTimeAgeDTO>
	 * @throws ServiceException
	 */
	@Override
	public List<SingleDeviceTimeAgeDTO> listDeviceTimeAge(List<String> dateList, List<String> residenceTypeList) throws ServiceException {
		if (CollectionUtils.isEmpty(dateList)) {
			throw new ServiceException(MessageConstant.DATE_LIST_IS_NULL);
		}
		if (CollectionUtils.isEmpty(residenceTypeList)) {
			throw new ServiceException(MessageConstant.RESIDENCE_TYPE_LIST_IS_NULLL);
		}
		return faceDataDeviceTimeAgeMapper.listDeviceTimeAge(dateList, residenceTypeList);
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
		return faceDataDeviceTimeAgeMapper.listActualDate(dateList, residenceTypeList);
	}
}
