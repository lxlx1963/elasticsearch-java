package com.du.elasticsearch.service.impl;

import com.xinchao.data.constant.MessageConstant;
import com.xinchao.data.dao.FaceDataProvinceMapper;
import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.FaceDataProvince;
import com.xinchao.data.model.dto.DateDeviceNumDTO;
import com.xinchao.data.model.dto.SummarizeFaceDataProvinceDTO;
import com.xinchao.data.service.FaceDataProvinceService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dxy
 * @date 2019/3/5 10:23
 */
@Service(value = "faceDataProvinceService")
public class FaceDataProvinceServiceImpl implements FaceDataProvinceService {
	@Autowired
	private FaceDataProvinceMapper faceDataProvinceMapper;

	/**
	 * 插入人数数据(省)
	 *
	 * @param faceDataProvince FaceDataProvince
	 * @throws ServiceException
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void insertFaceDataProvince(FaceDataProvince faceDataProvince) throws ServiceException {
		if (faceDataProvince == null) {
			throw new ServiceException(MessageConstant.PARAMETER_IS_NULL);
		}
		faceDataProvinceMapper.insertFaceDataProvince(faceDataProvince);
	}

	/**
	 * 批量插入人数数据(省)
	 *
	 * @param faceDataProvinceList List<FaceDataProvince>
	 * @throws ServiceException
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void insertFaceDataProvinceBatch(List<FaceDataProvince> faceDataProvinceList) throws ServiceException {
		if (CollectionUtils.isEmpty(faceDataProvinceList)) {
			throw new ServiceException(MessageConstant.PARAMETER_IS_NULL);
		}
		faceDataProvinceMapper.insertFaceDataProvinceBatch(faceDataProvinceList);
	}

	/**
	 * 获取汇总省人脸数据列表
	 *
	 * @param date 日期
	 * @return List<SummarizeFaceDataProvinceDTO>
	 * @throws ServiceException
	 */
	@Override
	public List<SummarizeFaceDataProvinceDTO> listSummarizeFaceDataProvinceDTO(String date) throws ServiceException {
		if (StringUtils.isBlank(date)) {
			throw new ServiceException(MessageConstant.DATE_IS_NULLL);
		}
		return faceDataProvinceMapper.listSummarizeFaceDataProvinceDTO(date);
	}

	/**
	 * 获取日期对应机器数量列表
	 *
	 * @param dateList 日期列表
	 * @return List<DateDeviceNumDTO>
	 * @throws ServiceException
	 */
	@Override
	public List<DateDeviceNumDTO> listDateDeviceNum(List<String> dateList) throws ServiceException {
		if (CollectionUtils.isEmpty(dateList)) {
			throw new ServiceException(MessageConstant.DATE_LIST_IS_NULL);
		}
		return faceDataProvinceMapper.listDateDeviceNum(dateList);
	}

	/**
	 * 获取日期机器数量Map
	 *
	 * @param dateList 日期列表
	 * @return Map<String, Integer>
	 * @throws ServiceException
	 */
	@Override
	public Map<String, Integer> getDateDeviceNumMap(List<String> dateList) throws ServiceException {
		if (CollectionUtils.isEmpty(dateList)) {
			throw new ServiceException(MessageConstant.DATE_LIST_IS_NULL);
		}
		// 日期机器数量Map
		Map<String, Integer> dateDeviceNumMap = new HashMap<>();
		// 日期机器数量列表
		List<DateDeviceNumDTO> dateDeviceNumList = listDateDeviceNum(dateList);
		if (CollectionUtils.isNotEmpty(dateDeviceNumList)) {
			for (DateDeviceNumDTO dateDeviceNumDTO : dateDeviceNumList) {
				String date = dateDeviceNumDTO.getDate();
				Integer deviceNum = dateDeviceNumDTO.getDeviceNum();
				if (StringUtils.isBlank(date) || deviceNum == null || deviceNum == 0) {
					continue;
				}
				dateDeviceNumMap.put(date, deviceNum);
			}
		}
		return dateDeviceNumMap;
	}
}
