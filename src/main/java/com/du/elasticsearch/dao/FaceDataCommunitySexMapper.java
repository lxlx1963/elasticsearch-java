package com.du.elasticsearch.dao;

import com.xinchao.data.model.FaceDataCommunitySex;
import com.xinchao.data.model.dto.FaceDataProvinceSexDTO;
import com.xinchao.data.model.dto.PeopleNumSexDTO;
import com.xinchao.data.model.dto.PeopleNumTimeDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author dxy
 * @date 2019/3/11 14:40
 */
public interface FaceDataCommunitySexMapper {
	/**
	 * 批量插入人脸数据小区性别数据
	 *
	 * @param faceDataCommunitySexList List<FaceDataCommunitySex>
	 */
	void insertFaceDataCommunitySexBatch(@Param("faceDataCommunitySexList") List<FaceDataCommunitySex> faceDataCommunitySexList);

	/**
	 * 获取人数人次
	 *
	 * @param date      日期
	 * @param community 小区
	 * @return PeopleNumTimeDTO
	 */
	PeopleNumTimeDTO getPeopleNumTime(@Param("date") String date, @Param("community") String community);

	/**
	 * 获取人数按性别分组列表
	 *
	 * @param dateList  日期列表
	 * @param community 小区
	 * @return List<PeopleNumSexDTO>
	 */
	List<PeopleNumSexDTO> listPeopleNumSex(@Param("dateList") List<String> dateList, @Param("community") String community);

	/**
	 * 获取小区列表
	 *
	 * @param date 日期
	 * @return List<String>
	 */
	List<String> listCommunity(@Param("date") String date);

	/**
	 * 获取人脸数据列表（按省、性别分组）
	 *
	 * @param date 日期
	 * @return List<FaceDataProvinceSexDTO>
	 */
	List<FaceDataProvinceSexDTO> listFaceDataProvinceSexDTO(String date);

	/**
	 * 获取实际日期列表
	 *
	 * @param dateList 日期列表
	 * @param community 小区
	 * @return List<String>
	 */
	List<String> listActualDate(@Param("dateList") List<String> dateList, @Param("community") String community);

}
