package com.du.elasticsearch.service;

import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.FaceDataCommunityDevice;
import com.xinchao.data.model.dto.CommunityDeviceNumDTO;
import com.xinchao.data.model.dto.ProvinceDeviceNumDTO;

import java.util.List;
import java.util.Map;

/**
 * @author dxy
 * @date 2019/4/3 15:08
 */
public interface FaceDataCommunityDeviceService {
	/**
	 * 插入数据
	 *
	 * @param faceDataCommunityDevice FaceDataCommunityDevice
	 * @throws ServiceException
	 */
	void insertFaceDataCommunityDevice(FaceDataCommunityDevice faceDataCommunityDevice) throws ServiceException;

	/**
	 * 批量插入数据
	 *
	 * @param faceDataCommunityDeviceList List<FaceDataCommunityDevice>
	 * @throws ServiceException
	 */
	void insertFaceDataCommunityDeviceBatch(List<FaceDataCommunityDevice> faceDataCommunityDeviceList) throws ServiceException;

	/**
	 * 获取小区点位数
	 *
	 * @param dateList 日期列表
	 * @param community 小区名称
	 * @return Integer
	 */
	Integer countCommnuity(List<String> dateList, String community) throws ServiceException;

	/**
	 * 获取省终端数量列表
	 * @param date 日期
	 * @return List<ProvinceDeviceNumDTO>
	 * @throws ServiceException
	 */
	List<ProvinceDeviceNumDTO> listProvinceDeviceNumDTO(String date) throws ServiceException;

	/**
	 * 获取终端编码列表
	 *
	 * @param date 日期
	 * @return List<String>
	 */
	List<String> listDeviceCode(String date) throws ServiceException;

	/**
	 * 获取城市列表
	 *
	 * @param date 日期
	 * @return List<String>
	 */
	List<String> listCity(String date) throws ServiceException;

	/**
	 * 获取小区对应的终端数
	 *
	 * @param dateList 日期列表
	 * @param communityList 小区列表
	 * @return List<CommunityDeviceNumDTO>
	 */
	List<CommunityDeviceNumDTO> listCommunityDeviceNum(List<String> dateList, List<String> communityList);

	/**
	 * 获取小区对应的终端数量Map(以小区为Key,终端数量为Value)
	 *
	 * @param dateList 日期列表
	 * @param communityList 小区列表
	 * @return Map<String, Integer>
	 */
	Map<String, Integer> getCommunityDeviceNumMap(List<String> dateList, List<String> communityList);
}
