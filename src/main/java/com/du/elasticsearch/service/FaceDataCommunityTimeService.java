package com.du.elasticsearch.service;

import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.FaceDataCommunityTime;
import com.xinchao.data.model.dto.PeopleNumTimeTimeDTO;

import java.util.List;

/**
 * @author dxy
 * @date 2019/4/2 16:24
 */
public interface FaceDataCommunityTimeService {
	/**
	 * 插入数据
	 *
	 * @param faceDataCommunityTime FaceDataCommunityTime
	 * @throws ServiceException
	 */
	void insertFaceDataCommunityTime(FaceDataCommunityTime faceDataCommunityTime) throws ServiceException;

	/**
	 * 批量插入数据
	 *
	 * @param faceDataCommunityTimeList List<FaceDataCommunityTime>
	 * @throws ServiceException
	 */
	void insertFaceDataCommunityTimeBatch(List<FaceDataCommunityTime> faceDataCommunityTimeList) throws ServiceException;

	/**
	 * 获取人数人次时间列表
	 * @param dateList  日期列表
	 * @param community 小区
	 * @return List<PeopleNumTimeTimeDTO>
	 * @throws ServiceException
	 */
	List<PeopleNumTimeTimeDTO> listPeopleNumTimeTime(List<String> dateList, String community) throws ServiceException;

	/**
	 * 获取实际日期列表
	 *
	 * @param dateList 日期列表
	 * @return List<String>
	 */
	List<String> listActualDate(List<String> dateList) throws ServiceException;
}
