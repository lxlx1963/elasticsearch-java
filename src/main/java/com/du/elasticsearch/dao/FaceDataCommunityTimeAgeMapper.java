package com.du.elasticsearch.dao;

import com.xinchao.data.model.FaceDataCommunityTimeAge;
import com.xinchao.data.model.dto.PeopleNumTimeAgeDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author dxy
 * @date 2019/4/2 16:32
 */
public interface FaceDataCommunityTimeAgeMapper {
	/**
	 * 插入数据
	 *
	 * @param faceDataCommunityTimeAge FaceDataCommunityTimeAge
	 */
	void insertFaceDataCommunityTimeAge(@Param("faceDataCommunityTimeAge") FaceDataCommunityTimeAge faceDataCommunityTimeAge);

	/**
	 * 批量插入数据
	 *
	 * @param faceDataCommunityTimeAgeList List<FaceDataCommunityTimeAge>
	 */
	void insertFaceDataCommunityTimeAgeBatch(@Param("faceDataCommunityTimeAgeList") List<FaceDataCommunityTimeAge> faceDataCommunityTimeAgeList);

	/**
	 * 获取人数人次年龄列表
	 *
	 * @param dateList  日期列表
	 * @param community 小区
	 * @return List<PeopleNumTimeAgeDTO>
	 */
	List<PeopleNumTimeAgeDTO> listPeopleNumTimeAge(@Param("dateList") List<String> dateList, @Param("community") String community);
}
