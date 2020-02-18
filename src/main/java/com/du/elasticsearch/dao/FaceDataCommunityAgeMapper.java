package com.du.elasticsearch.dao;

import com.xinchao.data.model.FaceDataCommunityAge;
import com.xinchao.data.model.dto.FaceDataProvinceAgeDTO;
import com.xinchao.data.model.dto.PeopleNumAgeDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author dxy
 * @date 2019/4/2 16:16
 */
public interface FaceDataCommunityAgeMapper {
	/**
	 * 插入数据
	 *
	 * @param faceDataCommunityAge FaceDataCommunityAge
	 */
	void insertFaceDataCommunityAge(@Param("faceDataCommunityAge") FaceDataCommunityAge faceDataCommunityAge);

	/**
	 * 批量插入数据
	 *
	 * @param faceDataCommunityAgeList List<FaceDataCommunityAge>
	 */
	void insertFaceDataCommunityAgeBatch(@Param("faceDataCommunityAgeList") List<FaceDataCommunityAge> faceDataCommunityAgeList);

	/**
	 * 获取人数年龄列表
	 *
	 * @param dateList 日期列表
	 * @param community 小区
	 * @return List<PeopleNumAgeDTO>
	 */
	List<PeopleNumAgeDTO> listPeopleNumAge(@Param("dateList") List<String> dateList, @Param("community") String community);

	/**
	 * 获取人脸数据列表（按省、年龄分组）
	 *
	 * @param date 日期
	 * @return List<FaceDataProvinceAgeDTO>
	 */
	List<FaceDataProvinceAgeDTO> listFaceDataProvinceAgeDTO(String date);
}
