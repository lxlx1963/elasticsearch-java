package com.du.elasticsearch.service.impl;

import com.xinchao.data.constant.MessageConstant;
import com.xinchao.data.dao.FaceDataMapper;
import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.FaceData;
import com.xinchao.data.model.dto.*;
import com.xinchao.data.service.FaceDataService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author dxy
 * @date 2019/3/1 15:17
 */
@Service(value = "faceDataService")
public class FaceDataServiceImpl implements FaceDataService {
	@Autowired
	private FaceDataMapper faceDataMapper;

	/**
	 * 插入人脸数据
	 *
	 * @param faceData FaceData
	 * @throws ServiceException
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void insertFaceData(FaceData faceData) throws ServiceException {
		if (faceData == null) {
			throw new ServiceException(MessageConstant.PARAMETER_IS_NULL);
		}
		//添加时间
		long currentTimeMillis = System.currentTimeMillis();
		faceData.setAddTime(currentTimeMillis);
		faceDataMapper.insertFaceData(faceData);
	}

	/**
	 * 批量插入人脸数据
	 *
	 * @param faceDataList List<FaceData>
	 * @throws ServiceException
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void insertFaceDataBatch(List<FaceData> faceDataList) throws ServiceException {
		if (CollectionUtils.isEmpty(faceDataList)) {
			throw new ServiceException(MessageConstant.PARAMETER_IS_NULL);
		}
		faceDataMapper.insertFaceDataBatch(faceDataList);
	}


	/**
	 * 获取人脸数据列表（按省、性别分组）
	 *
	 * @param date 日期
	 * @return List<FaceDataProvinceSexDTO>
	 * @throws ServiceException
	 */
	@Override
	public List<FaceDataProvinceSexDTO> listFaceDataProvinceSexDTO(String date) throws ServiceException {
		if (StringUtils.isBlank(date)) {
			throw new ServiceException(MessageConstant.DATE_IS_NULLL);
		}
		return faceDataMapper.listFaceDataProvinceSexDTO(date);
	}

	/**
	 * 获取人脸数据列表（按省、年龄分组）
	 *
	 * @param date 日期
	 * @return List<FaceDataProvinceAgeDTO>
	 * @throws ServiceException
	 */
	@Override
	public List<FaceDataProvinceAgeDTO> listFaceDataProvinceAgeDTO(String date) throws ServiceException {
		if (StringUtils.isBlank(date)) {
			throw new ServiceException(MessageConstant.DATE_IS_NULLL);
		}
		return faceDataMapper.listFaceDataProvinceAgeDTO(date);
	}

	/**
	 * 获取汇总监播DTO
	 *
	 * @param date 日期
	 * @return SummarizePeopleNumDTO
	 * @throws ServiceException
	 */
	@Override
	public SummarizePeopleNumDTO getSummarizePeopleNumDTO(String date) throws ServiceException {
		if (StringUtils.isBlank(date)) {
			throw new ServiceException(MessageConstant.DATE_IS_NULLL);
		}
		return faceDataMapper.getSummarizePeopleNumDTO(date);
	}

	/**
	 * 获取终端编码列表
	 *
	 * @param date 日期
	 * @return List<String>
	 * @throws ServiceException
	 */
	@Override
	public List<String> listDeviceModel(String date) throws ServiceException {
		if (StringUtils.isBlank(date)) {
			throw new ServiceException(MessageConstant.DATE_IS_NULLL);
		}
		return faceDataMapper.listDeviceModel(date);
	}

	/**
	 * 获取城市列表
	 *
	 * @param date 日期
	 * @return List<String>
	 * @throws ServiceException
	 */
	@Override
	public List<String> listCity(String date) throws ServiceException {
		if (StringUtils.isBlank(date)) {
			throw new ServiceException(MessageConstant.DATE_IS_NULLL);
		}
		return faceDataMapper.listCity(date);
	}

	/**
	 * 获取省城市数量列表
	 *
	 * @param date 日期
	 * @return List<ProvinceCityNumDTO>
	 * @throws ServiceException
	 */
	@Override
	public List<ProvinceCityNumDTO> listProvinceCityNumDTO(String date) throws ServiceException {
		if (StringUtils.isBlank(date)) {
			throw new ServiceException(MessageConstant.DATE_IS_NULLL);
		}
		return faceDataMapper.listProvinceCityNumDTO(date);
	}

	/**
	 * 获取省终端数量列表
	 *
	 * @param date 日期
	 * @return List<ProvinceDeviceNumDTO>
	 * @throws ServiceException
	 */
	@Override
	public List<ProvinceDeviceNumDTO> listProvinceDeviceNumDTO(String date) throws ServiceException {
		if (StringUtils.isBlank(date)) {
			throw new ServiceException(MessageConstant.DATE_IS_NULLL);
		}
		return faceDataMapper.listProvinceDeviceNumDTO(date);
	}

	/**
	 * 获取省小区数量列表
	 *
	 * @param date 日期
	 * @return List<ProvinceCommunityNumDTO>
	 * @throws ServiceException
	 */
	@Override
	public List<ProvinceCommunityNumDTO> listProvinceCommunityNumDTO(String date) throws ServiceException {
		if (StringUtils.isBlank(date)) {
			throw new ServiceException(MessageConstant.DATE_IS_NULLL);
		}
		return faceDataMapper.listProvinceCommunityNumDTO(date);
	}
}
