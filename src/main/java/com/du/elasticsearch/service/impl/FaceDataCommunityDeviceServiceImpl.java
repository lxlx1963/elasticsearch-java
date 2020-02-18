package com.du.elasticsearch.service.impl;

import com.xinchao.data.constant.MessageConstant;
import com.xinchao.data.dao.FaceDataCommunityDeviceMapper;
import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.FaceDataCommunityDevice;
import com.xinchao.data.model.dto.CommunityDeviceNumDTO;
import com.xinchao.data.model.dto.ProvinceDeviceNumDTO;
import com.xinchao.data.service.FaceDataCommunityDeviceService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dxy
 * @date 2019/4/3 15:11
 */
@Service(value = "faceDataCommunityDeviceService")
public class FaceDataCommunityDeviceServiceImpl implements FaceDataCommunityDeviceService {
	@Autowired
	private FaceDataCommunityDeviceMapper faceDataCommunityDeviceMapper;

	/**
	 * 插入数据
	 *
	 * @param faceDataCommunityDevice FaceDataCommunityDevice
	 * @throws ServiceException
	 */
	@Override
	public void insertFaceDataCommunityDevice(FaceDataCommunityDevice faceDataCommunityDevice) throws ServiceException {
		if (faceDataCommunityDevice == null) {
			throw new ServiceException(MessageConstant.PARAMETER_IS_NULL);
		}
		faceDataCommunityDeviceMapper.insertFaceDataCommunityDevice(faceDataCommunityDevice);
	}

	/**
	 * 批量插入数据
	 *
	 * @param faceDataCommunityDeviceList List<FaceDataCommunityDevice>
	 * @throws ServiceException
	 */
	@Override
	public void insertFaceDataCommunityDeviceBatch(List<FaceDataCommunityDevice> faceDataCommunityDeviceList) throws ServiceException {
		if (CollectionUtils.isEmpty(faceDataCommunityDeviceList)) {
			throw new ServiceException(MessageConstant.PARAMETER_IS_NULL);
		}
		faceDataCommunityDeviceMapper.insertFaceDataCommunityDeviceBatch(faceDataCommunityDeviceList);
	}

	/**
	 * 获取小区点位数
	 * @param dateList  日期列表
	 * @param community 小区名称
	 * @return Integer
	 * @throws ServiceException
	 */
	@Override
	public Integer countCommnuity(List<String> dateList, String community) throws ServiceException {
		if (CollectionUtils.isEmpty(dateList) || StringUtils.isBlank(community)) {
			throw new ServiceException(MessageConstant.DATE_LIST_OR_COMMUNITY_NAME_IS_NULLL);
		}
		return faceDataCommunityDeviceMapper.countCommnuity(dateList, community);
	}

	/**
	 * 获取省终端数量列表
	 * @param date 日期
	 * @return List<ProvinceDeviceNumDTO>
	 * @throws ServiceException
	 */
	@Override
	public List<ProvinceDeviceNumDTO> listProvinceDeviceNumDTO(String date) throws ServiceException {
		if (StringUtils.isBlank(date)) {
			throw new ServiceException(MessageConstant.DATE_IS_NULLL);
		}
		return faceDataCommunityDeviceMapper.listProvinceDeviceNumDTO(date);
	}

	/**
	 * 获取终端编码列表
	 *
	 * @param date 日期
	 * @return List<String>
	 * @throws ServiceException
	 */
	@Override
	public List<String> listDeviceCode(String date) throws ServiceException {
		if (StringUtils.isBlank(date)) {
			throw new ServiceException(MessageConstant.DATE_IS_NULLL);
		}
		return faceDataCommunityDeviceMapper.listDeviceCode(date);
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
		return faceDataCommunityDeviceMapper.listCity(date);
	}

	/**
	 * 获取小区对应的终端数
	 *
	 * @param dateList 日期列表
	 * @param communityList 小区列表
	 * @return List<CommunityDeviceNumDTO>
	 */
	@Override
	public List<CommunityDeviceNumDTO> listCommunityDeviceNum(List<String> dateList, List<String> communityList) {
		return faceDataCommunityDeviceMapper.listCommunityDeviceNum(dateList, communityList);
	}

	/**
	 * 获取小区对应的终端数量Map(以小区为Key,终端数量为Value)
	 *
	 * @param dateList 日期列表
	 * @param communityList 小区列表
	 * @return Map<String, Integer>
	 */
	@Override
	public Map<String, Integer> getCommunityDeviceNumMap(List<String> dateList, List<String> communityList) {
		Map<String, Integer> communityDeviceNumMap = new HashMap<>();
		List<CommunityDeviceNumDTO> communityDeviceNumList = listCommunityDeviceNum(dateList, communityList);
		if (CollectionUtils.isNotEmpty(communityDeviceNumList)) {
			for (CommunityDeviceNumDTO dto : communityDeviceNumList) {
				String community = dto.getCommunity();
				Integer deviceNum = dto.getDeviceNum();
				if (StringUtils.isBlank(community) || deviceNum == null) {
					continue;
				}
				communityDeviceNumMap.put(community, deviceNum);
			}
		}
		return communityDeviceNumMap;
	}
}
