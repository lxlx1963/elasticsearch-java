package com.du.elasticsearch.dao;

import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.FaceDataCommunity;
import com.xinchao.data.model.dto.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 人脸数据（小区）
 */
public interface FaceDataCommunityMapper {
	/**
	 * 插入数据
	 *
	 * @param faceDataCommunity FaceDataCommunity
	 */
	void insertFaceDataCommunity(@Param("faceDataCommunity") FaceDataCommunity faceDataCommunity);

	/**
	 * 批量插入数据
	 *
	 * @param faceDataCommunityList List<FaceDataCommunity>
	 */
	void insertFaceDataCommunityBatch(@Param("faceDataCommunityList") List<FaceDataCommunity> faceDataCommunityList);

	/**
	 * 获取汇总近7天top10小区的人数列表
	 *
	 * @param dateList List<String>
	 * @return List<SummarizeTopTenDTO>
	 */
	List<SummarizeTopTenDTO> listSummarizeTopTenDTO(@Param("dateList") List<String> dateList);

	/**
	 * 获取人数年龄列表
	 *
	 * @param date      日期
	 * @param community 小区
	 * @return List<PeopleNumAgeDTO>
	 */
	List<PeopleNumAgeDTO> listPeopleNumAge(@Param("date") String date, @Param("community") String community);

	/**
	 * 获取人数人次时间列表
	 *
	 * @param dateList  日期列表
	 * @param community 小区
	 * @return List<PeopleNumTimeTimeDTO>
	 */
	List<PeopleNumTimeTimeDTO> listPeopleNumTimeTime(@Param("dateList") List<String> dateList, @Param("community") String community);

	/**
	 * 获取人数人次年龄列表
	 *
	 * @param dateList  日期列表
	 * @param community 小区
	 * @return List<PeopleNumTimeAgeDTO>
	 */
	List<PeopleNumTimeAgeDTO> listPeopleNumTimeAge(@Param("dateList") List<String> dateList, @Param("community") String community);

	/**
	 * 获取实际日期列表
	 *
	 * @param dateList 日期列表
	 * @return List<String>
	 */
	List<String> listActualDate(@Param("dateList") List<String> dateList);


	/**
	 * 获取小区列表
	 *
	 * @param date 日期
	 * @return List<String>
	 * @throws ServiceException
	 */
	List<String> listCommunity(@Param("date") String date) throws ServiceException;


	/**
	 * 获取人数人次
	 *
	 * @param dateList 日期列表
	 * @param community 小区
	 * @return PeopleNumTimeDTO
	 */
	PeopleNumTimeDTO getPeopleNumTime(@Param("dateList") List<String> dateList, @Param("community") String community);


	/**
	 * 获取省城市数量列表
	 *
	 * @param date 日期
	 * @return List<ProvinceCityNumDTO>
	 */
	List<ProvinceCityNumDTO> listProvinceCityNumDTO(String date);

	/**
	 * 获取省小区数量列表
	 *
	 * @param date 日期
	 * @return List<ProvinceCommunityNumDTO>
	 */
	List<ProvinceCommunityNumDTO> listProvinceCommunityNumDTO(String date);

}
