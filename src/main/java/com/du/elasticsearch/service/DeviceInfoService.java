package com.du.elasticsearch.service;

import com.xinchao.data.model.dto.DeviceCommunityCityDTO;
import com.xinchao.data.model.dto.DeviceInfoDTO;
import com.xinchao.data.model.dto.DeviceNumberResidenceTypeDTO;

import java.util.List;
import java.util.Map;

/**
 * 终端信息Service
 *
 * @author dxy
 * @date 2019/3/4 16:50
 */
public interface DeviceInfoService {
	/**
	 * 获取所有终端信息DTO
	 *
	 * @return List<DeviceInfoDTO>
	 */
	List<DeviceInfoDTO> listAllDeviceInfoDTO();

	/**
	 * 获取终端信息Map(已终端编码为Key)
	 *
	 * @return Map<String ,DeviceInfoDTO>
	 */
	Map<String, DeviceInfoDTO> getDeviceInfoDTOMap();

	/**
	 * 获取小区对应城市列表
	 *
	 * @return List<DeviceCommunityCityDTO>
	 */
	List<DeviceCommunityCityDTO> listDeviceCommunityCity();

	/**
	 * 获取小区对应城市Map(小区为Key,城市为Value)
	 *
	 * @return Map<Object, Object>
	 */
	Map<Object, Object> getDeviceCommunityCityMap();

	/**
	 * 获取终端编码对应的住宅类型列表
	 *
	 * @return List<DeviceNumberResidenceTypeDTO>
	 */
	List<DeviceNumberResidenceTypeDTO> listDeviceNumberResidenceType();

	/**
	 * 获取终端编码对应住宅类型Map(终端编码为Key,住宅类型为Value)
	 *
	 * @return Map<String, String>
	 */
	Map<String, String> getDeviceNumberResidenceTypeMap();

	/**
	 * 获取终端编码对应城市Map(终端编码为Key,城市为Value)
	 *
	 * @return Map<String, String>
	 */
	Map<String, String> getDeviceNumberCityMap();


}
