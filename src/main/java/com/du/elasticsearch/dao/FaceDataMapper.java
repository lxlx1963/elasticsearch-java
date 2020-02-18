package com.du.elasticsearch.dao;

import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.FaceData;
import com.xinchao.data.model.dto.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 人脸数据Mapper
 *
 * @author dxy
 * @date 2019/3/1 10:55
 */
public interface FaceDataMapper {
	/**
	 * 插入人脸数据
	 *
	 * @param faceData FaceData
	 */
	void insertFaceData(@Param("faceData") FaceData faceData);

	/**
	 * 批量插入人脸数据
	 *
	 * @param faceDataList List<FaceData>
	 */
	void insertFaceDataBatch(@Param("faceDataList") List<FaceData> faceDataList);

	/**
	 * 获取人脸数据列表（按省、性别分组）
	 *
	 * @param date 日期
	 * @return List<FaceDataProvinceSexDTO>
	 * @throws ServiceException
	 */
	List<FaceDataProvinceSexDTO> listFaceDataProvinceSexDTO(String date);

	/**
	 * 获取人脸数据列表（按省、年龄分组）
	 *
	 * @param date 日期
	 * @return List<FaceDataProvinceAgeDTO>
	 * @throws ServiceException
	 */
	List<FaceDataProvinceAgeDTO> listFaceDataProvinceAgeDTO(String date);

	/**
	 * 获取汇总人数DTO
	 *
	 * @param date 日期
	 * @return SummarizePeopleNumDTO
	 */
	SummarizePeopleNumDTO getSummarizePeopleNumDTO(String date);

	/**
	 * 获取终端编码列表
	 *
	 * @param date 日期
	 * @return List<String>
	 */
	List<String> listDeviceModel(String date);

	/**
	 * 获取城市列表
	 *
	 * @param date 日期
	 * @return List<String>
	 */
	List<String> listCity(String date);

	/**
	 * 获取省城市数量列表
	 *
	 * @param date 日期
	 * @return List<ProvinceCityNumDTO>
	 */
	List<ProvinceCityNumDTO> listProvinceCityNumDTO(String date);

	/**
	 * 获取省终端数量列表
	 *
	 * @param date 日期
	 * @return List<ProvinceDeviceNumDTO>
	 */
	List<ProvinceDeviceNumDTO> listProvinceDeviceNumDTO(String date);

	/**
	 * 获取省小区数量列表
	 *
	 * @param date 日期
	 * @return List<ProvinceCommunityNumDTO>
	 */
	List<ProvinceCommunityNumDTO> listProvinceCommunityNumDTO(String date);

}
