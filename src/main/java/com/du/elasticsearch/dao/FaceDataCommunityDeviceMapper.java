package com.du.elasticsearch.dao;

import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.FaceDataCommunityDevice;
import com.xinchao.data.model.dto.CommunityDeviceNumDTO;
import com.xinchao.data.model.dto.ProvinceDeviceNumDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author dxy
 * @date 2019/4/3 15:08
 */
public interface FaceDataCommunityDeviceMapper {
	/**
	 * 插入数据
	 *
	 * @param faceDataCommunityDevice FaceDataCommunityDevice
	 */
	void insertFaceDataCommunityDevice(@Param("faceDataCommunityDevice") FaceDataCommunityDevice faceDataCommunityDevice);

	/**
	 * 批量插入数据
	 *
	 * @param faceDataCommunityDeviceList List<FaceDataCommunityDevice>
	 */
	void insertFaceDataCommunityDeviceBatch(@Param("faceDataCommunityDeviceList") List<FaceDataCommunityDevice> faceDataCommunityDeviceList);

	/**
	 * 获取小区点位数
	 *
	 * @param dateList 日期列表
	 * @param community 小区名称
	 * @return Integer
	 */
	Integer countCommnuity(@Param("dateList") List<String> dateList, @Param("community") String community);

	/**
	 * 获取省终端数量列表
	 *
	 * @param date 日期
	 * @return List<ProvinceDeviceNumDTO>
	 */
	List<ProvinceDeviceNumDTO> listProvinceDeviceNumDTO(String date);

	/**
	 * 获取终端编码列表
	 *
	 * @param date 日期
	 * @return List<String>
	 * @throws ServiceException
	 */
	List<String> listDeviceCode(String date);

	/**
	 * 获取城市列表
	 *
	 * @param date 日期
	 * @return List<String>
	 */
	List<String> listCity(String date);

	/**
	 * 获取小区对应的终端数
	 *
	 * @param dateList 日期列表
	 * @param communityList 小区列表
	 * @return List<CommunityDeviceNumDTO>
	 */
	List<CommunityDeviceNumDTO> listCommunityDeviceNum(@Param("dateList") List<String> dateList, @Param("communityList") List<String> communityList);

}
