package com.du.elasticsearch.service;

import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.FaceDataCommunityTimeAge;
import com.xinchao.data.model.dto.PeopleNumTimeAgeDTO;

import java.util.List;

/**
 * @author dxy
 * @date 2019/4/2 16:32
 */
public interface FaceDataCommunityTimeAgeService {
	/**
	 * 插入数据
	 *
	 * @param faceDataCommunityTimeAge FaceDataCommunityTimeAge
	 */
	void insertFaceDataCommunityTimeAge(FaceDataCommunityTimeAge faceDataCommunityTimeAge) throws ServiceException;

	/**
	 * 批量插入数据
	 *
	 * @param faceDataCommunityTimeAgeList List<FaceDataCommunityTimeAge>
	 */
	void insertFaceDataCommunityTimeAgeBatch(List<FaceDataCommunityTimeAge> faceDataCommunityTimeAgeList) throws ServiceException;

	/**
	 * 获取人数人次年龄列表
	 *
	 * @param dateList  日期列表
	 * @param community 小区
	 * @return List<PeopleNumTimeAgeDTO>
	 */
	List<PeopleNumTimeAgeDTO> listPeopleNumTimeAge(List<String> dateList, String community) throws ServiceException;
}
