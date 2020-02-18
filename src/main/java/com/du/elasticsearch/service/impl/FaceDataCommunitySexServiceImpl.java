package com.du.elasticsearch.service.impl;

import com.xinchao.data.constant.MessageConstant;
import com.xinchao.data.dao.FaceDataCommunitySexMapper;
import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.FaceDataCommunitySex;
import com.xinchao.data.model.dto.FaceDataProvinceSexDTO;
import com.xinchao.data.model.dto.PeopleNumSexDTO;
import com.xinchao.data.model.dto.PeopleNumTimeDTO;
import com.xinchao.data.service.FaceDataCommunitySexService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author dxy
 * @date 2019/3/11 14:46
 */
@Service(value = "faceDataCommunitySexService")
public class FaceDataCommunitySexServiceImpl implements FaceDataCommunitySexService {
	@Autowired
	private FaceDataCommunitySexMapper faceDataCommunitySexMapper;

	/**
	 * 批量插入人脸数据小区性别数据
	 *
	 * @param faceDataCommunitySexList List<FaceDataCommunitySex>
	 * @throws ServiceException
	 */
	@Override
	public void insertFaceDataCommunitySexBatch(List<FaceDataCommunitySex> faceDataCommunitySexList) throws ServiceException {
		if (CollectionUtils.isEmpty(faceDataCommunitySexList)) {
			throw new ServiceException(MessageConstant.PARAMETER_IS_NULL);
		}
		faceDataCommunitySexMapper.insertFaceDataCommunitySexBatch(faceDataCommunitySexList);
	}

	/**
	 * 获取人数按性别分组列表
	 *
	 * @param dateList 日期列表
	 * @param community 小区
	 * @return List<PeopleNumSexDTO>
	 * @throws ServiceException
	 */
	@Override
	public List<PeopleNumSexDTO> listPeopleNumSex(List<String> dateList, String community) throws ServiceException {
		if (CollectionUtils.isEmpty(dateList)) {
			throw new ServiceException(MessageConstant.DATE_LIST_IS_NULL);
		}
		if (StringUtils.isBlank(community)) {
			throw new ServiceException(MessageConstant.COMMUNITY_NAME_IS_NULLL);
		}
		return faceDataCommunitySexMapper.listPeopleNumSex(dateList, community);
	}

	/**
	 * 获取人数人次
	 *
	 * @param date      日期
	 * @param community 小区
	 * @return PeopleNumTimeDTO
	 * @throws ServiceException
	 */
	@Override
	public PeopleNumTimeDTO getPeopleNumTime(String date, String community) throws ServiceException {
		if (StringUtils.isBlank(date)) {
			throw new ServiceException(MessageConstant.DATE_IS_NULLL);
		}
		if (StringUtils.isBlank(community)) {
			throw new ServiceException(MessageConstant.COMMUNITY_NAME_IS_NULLL);
		}
		return faceDataCommunitySexMapper.getPeopleNumTime(date, community);
	}

	/**
	 * 获取小区列表
	 *
	 * @param date 日期
	 * @return List<String>
	 * @throws ServiceException
	 */
	@Override
	public List<String> listCommunity(String date) throws ServiceException {
		if (StringUtils.isBlank(date)) {
			throw new ServiceException(MessageConstant.DATE_IS_NULLL);
		}
		return faceDataCommunitySexMapper.listCommunity(date);
	}

	/**
	 * 获取人脸数据列表（按省、性别分组）
	 *
	 * @param date 日期
	 * @return List<FaceDataProvinceSexDTO>
	 * @throws ServiceException
	 */
	@Override
	public List<FaceDataProvinceSexDTO> listFaceDataProvinceSexDTO(String date) throws ServiceException {
		if (StringUtils.isBlank(date)) {
			throw new ServiceException(MessageConstant.DATE_IS_NULLL);
		}
		return faceDataCommunitySexMapper.listFaceDataProvinceSexDTO(date);
	}

	/**
	 * 获取实际日期列表
	 *
	 * @param dateList  日期列表
	 * @param community 小区
	 * @return List<String>
	 * @throws ServiceException
	 */
	@Override
	public List<String> listActualDate(List<String> dateList, String community) throws ServiceException {
		if (CollectionUtils.isEmpty(dateList)) {
			throw new ServiceException(MessageConstant.DATE_LIST_IS_NULL);
		}
		if (StringUtils.isBlank(community)) {
			throw new ServiceException(MessageConstant.COMMUNITY_NAME_IS_NULLL);
		}
		return faceDataCommunitySexMapper.listActualDate(dateList, community);
	}
}
