package com.du.elasticsearch.service.impl;

import com.google.common.collect.Maps;
import com.xinchao.data.dao.DeviceInfoMapper;
import com.xinchao.data.model.dto.DeviceCommunityCityDTO;
import com.xinchao.data.model.dto.DeviceInfoDTO;
import com.xinchao.data.model.dto.DeviceNumberCityDTO;
import com.xinchao.data.model.dto.DeviceNumberResidenceTypeDTO;
import com.xinchao.data.service.DeviceInfoService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 终端信息Service实现类
 *
 * @author dxy
 * @date 2019/3/4 16:57
 */
@Service(value = "deviceInfoService")
public class DeviceInfoServiceImpl implements DeviceInfoService {
	@Autowired
	private DeviceInfoMapper deviceInfoMapper;

	/**
	 * 获取所有终端信息DTO
	 *
	 * @return List<DeviceInfoDTO>
	 */
	@Override
	public List<DeviceInfoDTO> listAllDeviceInfoDTO() {
		return deviceInfoMapper.listAllDeviceInfoDTO();
	}

	/**
	 * 获取终端信息Map(已终端编码为Key)
	 *
	 * @return Map<String   ,       DeviceInfoDTO>
	 */
	@Override
	public Map<String, DeviceInfoDTO> getDeviceInfoDTOMap() {
		List<DeviceInfoDTO> deviceInfoDTOList = listAllDeviceInfoDTO();
		if (CollectionUtils.isEmpty(deviceInfoDTOList)) {
			return null;
		}
		//终端信息DTOMap
		Map<String, DeviceInfoDTO> deviceInfoDTOMap = new HashMap<>();
		int listSize = deviceInfoDTOList.size();

		for (int i = 0; i < listSize; i++) {
			DeviceInfoDTO deviceInfoDTO = deviceInfoDTOList.get(i);
			String deviceNumber = deviceInfoDTO.getDeviceNumber();
			if (StringUtils.isBlank(deviceNumber)) {
				continue;
			}
			deviceInfoDTOMap.put(deviceNumber, deviceInfoDTO);
		}
		return deviceInfoDTOMap;
	}

	/**
	 * 获取小区对应城市列表
	 *
	 * @return List<DeviceCommunityCityDTO>
	 */
	@Override
	public List<DeviceCommunityCityDTO> listDeviceCommunityCity() {
		return deviceInfoMapper.listDeviceCommunityCity();
	}

	/**
	 * 获取小区对应城市Map(小区为Key,城市为Value)
	 *
	 * @return Map<Object, Object>
	 */
	@Override
	public Map<Object, Object> getDeviceCommunityCityMap() {
		Map<Object, Object> communityCityMap = Maps.newHashMap();
		//获取小区对应城市列表
		List<DeviceCommunityCityDTO> communityCityList = listDeviceCommunityCity();
		for (DeviceCommunityCityDTO deviceCommunityCity: communityCityList) {
			String community = deviceCommunityCity.getCommunity();
			String city = deviceCommunityCity.getCity();
			if (StringUtils.isBlank(community) || StringUtils.isBlank(city)) {
				continue;
			}
			communityCityMap.put(community, city);
		}
		return communityCityMap;
	}

	/**
	 * 获取终端编码对应的住宅类型列表
	 *
	 * @return List<DeviceNumberResidenceTypeDTO>
	 */
	@Override
	public List<DeviceNumberResidenceTypeDTO> listDeviceNumberResidenceType() {
		return deviceInfoMapper.listDeviceNumberResidenceType();
	}

	/**
	 * 获取终端编码对应住宅类型Map(终端编码为Key,住宅类型为Value)
	 *
	 * @return Map<String, String>
	 */
	@Override
	public Map<String, String> getDeviceNumberResidenceTypeMap() {
		List<DeviceNumberResidenceTypeDTO> deviceNumberResidenceTypeList = listDeviceNumberResidenceType();
		//终端编码为Key,住宅类型为Value
		Map<String, String> deviceNumberResidenceTypeMap = Maps.newHashMap();
		if (CollectionUtils.isNotEmpty(deviceNumberResidenceTypeList)) {
			for (int i = 0; i < deviceNumberResidenceTypeList.size(); i++) {
				DeviceNumberResidenceTypeDTO dto = deviceNumberResidenceTypeList.get(i);
				String deviceNumber = dto.getDeviceNumber();
				String residenceType = dto.getResidenceType();
				if (StringUtils.isBlank(deviceNumber) || StringUtils.isBlank(residenceType)) {
					continue;
				}
				deviceNumberResidenceTypeMap.put(deviceNumber, residenceType);
			}
		}
		return deviceNumberResidenceTypeMap;
	}

	/**
	 * 获取终端编码对应城市Map(终端编码为Key,城市为Value)
	 *
	 * @return Map<String, String>
	 */
	@Override
	public Map<String, String> getDeviceNumberCityMap() {
		// 终端编码对应城市Map
		Map<String, String> deviceNumberCityMap = Maps.newHashMap();

		// 终端编码对应城市List
		List<DeviceNumberCityDTO> deviceNumberCityList = deviceInfoMapper.listDeviceNumberCity();

		for (int i = 0; i < deviceNumberCityList.size(); i++) {
			DeviceNumberCityDTO deviceNumberCityDTO = deviceNumberCityList.get(i);
			// 终端编码
			String deviceNumber = deviceNumberCityDTO.getDeviceNumber();
			// 城市
			String city = deviceNumberCityDTO.getCity();
			if (StringUtils.isBlank(deviceNumber) || StringUtils.isBlank(city)) {
				continue;
			}
			deviceNumberCityMap.put(deviceNumber, city);
		}
		return deviceNumberCityMap;
	}

}
