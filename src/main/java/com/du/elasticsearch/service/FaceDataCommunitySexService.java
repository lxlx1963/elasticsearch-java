package com.du.elasticsearch.service;

import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.FaceDataCommunitySex;
import com.xinchao.data.model.dto.FaceDataProvinceSexDTO;
import com.xinchao.data.model.dto.PeopleNumSexDTO;
import com.xinchao.data.model.dto.PeopleNumTimeDTO;

import java.util.List;

/**
 * @author dxy
 * @date 2019/3/11 14:44
 */
public interface FaceDataCommunitySexService {
	/**
	 * 批量插入人脸数据小区性别数据
	 *
	 * @param faceDataCommunitySexList List<FaceDataCommunitySex>
	 * @throws ServiceException
	 */
	void insertFaceDataCommunitySexBatch(List<FaceDataCommunitySex> faceDataCommunitySexList) throws ServiceException;

	/**
	 * 获取人数按性别分组列表
	 *
	 * @param dateList  日期列表
	 * @param community 小区
	 * @return List<PeopleNumSexDTO>
	 */
	List<PeopleNumSexDTO> listPeopleNumSex(List<String> dateList, String community) throws ServiceException;

	/**
	 * 获取人数人次
	 *
	 * @param date      日期
	 * @param community 小区
	 * @return PeopleNumTimeDTO
	 */
	PeopleNumTimeDTO getPeopleNumTime(String date, String community) throws ServiceException;

	/**
	 * 获取小区列表
	 *
	 * @param date 日期
	 * @return List<String>
	 * @throws ServiceException
	 */
	List<String> listCommunity(String date) throws ServiceException;

	/**
	 * 获取人脸数据列表（按省、性别分组）
	 *
	 * @param date 日期
	 * @return List<FaceDataProvinceSexDTO>
	 */
	List<FaceDataProvinceSexDTO> listFaceDataProvinceSexDTO(String date) throws ServiceException;

	/**
	 * 获取实际日期列表
	 *
	 * @param dateList  日期列表
	 * @param community 小区
	 * @return List<String>
	 */
	List<String> listActualDate(List<String> dateList, String community) throws ServiceException;

}
