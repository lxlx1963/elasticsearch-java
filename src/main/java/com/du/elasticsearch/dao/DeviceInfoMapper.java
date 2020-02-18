package com.du.elasticsearch.dao;

import com.xinchao.data.model.dto.DeviceCommunityCityDTO;
import com.xinchao.data.model.dto.DeviceInfoDTO;
import com.xinchao.data.model.dto.DeviceNumberCityDTO;
import com.xinchao.data.model.dto.DeviceNumberResidenceTypeDTO;

import java.util.List;

/**
 * @author dxy
 * @date 2019/3/4 16:58
 */
public interface DeviceInfoMapper {
	/**
	 * 获取所有终端信息DTO
	 *
	 * @return List<DeviceInfoDTO>
	 */
	List<DeviceInfoDTO> listAllDeviceInfoDTO();

	/**
	 * 获取小区对应城市列表
	 *
	 * @return List<DeviceCommunityCityDTO>
	 */
	List<DeviceCommunityCityDTO> listDeviceCommunityCity();

	/**
	 * 获取终端编码对应的住宅类型列表
	 *
	 * @return List<DeviceNumberResidenceTypeDTO>
	 */
	List<DeviceNumberResidenceTypeDTO> listDeviceNumberResidenceType();

	/**
	 * 获取终端编码对应城市列表
	 *
	 * @return List<DeviceNumberCityDTO>
	 */
	List<DeviceNumberCityDTO> listDeviceNumberCity();
}
