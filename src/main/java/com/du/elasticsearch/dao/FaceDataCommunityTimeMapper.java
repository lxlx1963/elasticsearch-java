package com.du.elasticsearch.dao;

import com.xinchao.data.model.FaceDataCommunityTime;
import com.xinchao.data.model.dto.PeopleNumTimeTimeDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author dxy
 * @date 2019/4/2 16:24
 */
public interface FaceDataCommunityTimeMapper {
	/**
	 * 插入数据
	 *
	 * @param faceDataCommunityTime FaceDataCommunityTime
	 */
	void insertFaceDataCommunityTime(@Param("faceDataCommunityTime") FaceDataCommunityTime faceDataCommunityTime);

	/**
	 * 批量插入数据
	 *
	 * @param faceDataCommunityTimeList List<FaceDataCommunityTime>
	 */
	void insertFaceDataCommunityTimeBatch(@Param("faceDataCommunityTimeList") List<FaceDataCommunityTime> faceDataCommunityTimeList);

	/**
	 * 获取人数人次时间列表
	 *
	 * @param dateList  日期列表
	 * @param community 小区
	 * @return List<PeopleNumTimeTimeDTO>
	 */
	List<PeopleNumTimeTimeDTO> listPeopleNumTimeTime(@Param("dateList") List<String> dateList, @Param("community") String community);

	/**
	 * 获取实际日期列表
	 *
	 * @param dateList 日期列表
	 * @return List<String>
	 */
	List<String> listActualDate(@Param("dateList") List<String> dateList);
}
