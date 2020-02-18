package com.du.elasticsearch.service;

import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.FaceDataCommunityAge;
import com.xinchao.data.model.dto.FaceDataProvinceAgeDTO;
import com.xinchao.data.model.dto.PeopleNumAgeDTO;

import java.util.List;

/**
 * @author dxy
 * @date 2019/4/2 20:36
 */
public interface FaceDataCommunityAgeService {

	/**
	 * 插入数据
	 *
	 * @param faceDataCommunityAge FaceDataCommunityAge
	 * @throws ServiceException
	 */
	void insertFaceDataCommunityAge(FaceDataCommunityAge faceDataCommunityAge) throws ServiceException;

	/**
	 * 批量插入数据
	 *
	 * @param faceDataCommunityAgeList List<FaceDataCommunityAge>
	 * @throws ServiceException
	 */
	void insertFaceDataCommunityAgeBatch(List<FaceDataCommunityAge> faceDataCommunityAgeList) throws ServiceException;

	/**
	 * 获取人数年龄列表
	 *
	 * @param dateList 日期列表
	 * @param community 小区
	 * @return List<PeopleNumAgeDTO>
	 * @throws ServiceException
	 */
	List<PeopleNumAgeDTO> listPeopleNumAge(List<String> dateList, String community) throws ServiceException;

	/**
	 * 获取人脸数据列表（按省、年龄分组）
	 *
	 * @param date 日期
	 * @return List<FaceDataProvinceAgeDTO>
	 */
	List<FaceDataProvinceAgeDTO> listFaceDataProvinceAgeDTO(String date) throws ServiceException;
}
