package com.du.elasticsearch.service.impl;

import com.xinchao.data.constant.MessageConstant;
import com.xinchao.data.dao.FaceDataCommunityMapper;
import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.FaceDataCommunity;
import com.xinchao.data.model.dto.*;
import com.xinchao.data.service.FaceDataCommunityService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 人脸数据（小区）Service实现类
 *
 * @author dxy
 * @date 2019/3/2 9:07
 */
@Service(value = "faceDataCommunityService")
public class FaceDataCommunityServiceImpl implements FaceDataCommunityService {
	@Autowired
	private FaceDataCommunityMapper faceDataCommunityMapper;

	/**
	 * 插入数据
	 *
	 * @param faceDataCommunity FaceDataCommunity
	 * @throws ServiceException
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void insertFaceDataCommunity(FaceDataCommunity faceDataCommunity) throws ServiceException {
		if (faceDataCommunity == null) {
			throw new ServiceException(MessageConstant.PARAMETER_IS_NULL);
		}
		faceDataCommunityMapper.insertFaceDataCommunity(faceDataCommunity);
	}

	/**
	 * 批量插入数据
	 *
	 * @param faceDataCommunityList List<FaceDataCommunity>
	 * @throws ServiceException
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void insertFaceDataCommunityBatch(List<FaceDataCommunity> faceDataCommunityList) throws ServiceException {
		if (CollectionUtils.isEmpty(faceDataCommunityList)) {
			throw new ServiceException(MessageConstant.PARAMETER_IS_NULL);
		}
		faceDataCommunityMapper.insertFaceDataCommunityBatch(faceDataCommunityList);
	}

	/**
	 * 获取汇总近7天top10小区的人数列表
	 *
	 * @param dateList List<String>
	 * @return List<SummarizeTopTenDTO>
	 * @throws ServiceException
	 */
	@Override
	public List<SummarizeTopTenDTO> listSummarizeTopTenDTO(List<String> dateList) throws ServiceException {
		if (CollectionUtils.isEmpty(dateList)) {
			throw new ServiceException(MessageConstant.DATE_LIST_IS_NULL);
		}
		return faceDataCommunityMapper.listSummarizeTopTenDTO(dateList);
	}

	/**
	 * 获取人数年龄列表
	 *
	 * @param date      日期
	 * @param community 小区
	 * @return List<PeopleNumAgeDTO>
	 * @throws ServiceException
	 */
	@Override
	public List<PeopleNumAgeDTO> listPeopleNumAge(String date, String community) throws ServiceException {
		if (StringUtils.isBlank(date)) {
			throw new ServiceException(MessageConstant.DATE_IS_NULLL);
		}
		if (StringUtils.isBlank(community)) {
			throw new ServiceException(MessageConstant.COMMUNITY_NAME_IS_NULLL);
		}
		return faceDataCommunityMapper.listPeopleNumAge(date, community);
	}

	/**
	 * 获取人数人次时间列表
	 *
	 * @param dateList  日期列表
	 * @param community 小区
	 * @return List<PeopleNumTimeTimeDTO>
	 * @throws ServiceException
	 */
	@Override
	public List<PeopleNumTimeTimeDTO> listPeopleNumTimeTime(List<String> dateList, String community) throws ServiceException {
		if (CollectionUtils.isEmpty(dateList)) {
			throw new ServiceException(MessageConstant.DATE_LIST_IS_NULL);
		}
		if (StringUtils.isBlank(community)) {
			throw new ServiceException(MessageConstant.COMMUNITY_NAME_IS_NULLL);
		}
		return faceDataCommunityMapper.listPeopleNumTimeTime(dateList, community);
	}

	/**
	 * 获取人数人次年龄列表
	 *
	 * @param dateList  日期列表
	 * @param community 小区
	 * @return List<PeopleNumTimeAgeDTO>
	 * @throws ServiceException
	 */
	@Override
	public List<PeopleNumTimeAgeDTO> listPeopleNumTimeAge(List<String> dateList, String community) throws ServiceException {
		if (CollectionUtils.isEmpty(dateList)) {
			throw new ServiceException(MessageConstant.DATE_LIST_IS_NULL);
		}
		if (StringUtils.isBlank(community)) {
			throw new ServiceException(MessageConstant.COMMUNITY_NAME_IS_NULLL);
		}
		return faceDataCommunityMapper.listPeopleNumTimeAge(dateList, community);
	}

	/**
	 * 获取实际日期列表
	 *
	 * @param dateList 日期列表
	 * @return List<String>
	 * @throws ServiceException
	 */
	@Override
	public List<String> listActualDate(List<String> dateList) throws ServiceException {
		if (CollectionUtils.isEmpty(dateList)) {
			throw new ServiceException(MessageConstant.DATE_LIST_IS_NULL);
		}
		return faceDataCommunityMapper.listActualDate(dateList);
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
		return faceDataCommunityMapper.listCommunity(date);
	}

	/**
	 * 获取小区的人数人次
	 * @param dateList  日期列表
	 * @param community 小区
	 * @return PeopleNumTimeDTO
	 * @throws ServiceException
	 */
	@Override
	public PeopleNumTimeDTO getPeopleNumTime(List<String> dateList, String community) throws ServiceException {
		if (CollectionUtils.isEmpty(dateList) || StringUtils.isBlank(community)) {
			throw new ServiceException(MessageConstant.DATE_LIST_OR_COMMUNITY_NAME_IS_NULLL);
		}
		return faceDataCommunityMapper.getPeopleNumTime(dateList, community);
	}

	/**
	 * 获取省城市数量列表
	 *
	 * @param date 日期
	 * @return List<ProvinceCityNumDTO>
	 * @throws ServiceException
	 */
	@Override
	public List<ProvinceCityNumDTO> listProvinceCityNumDTO(String date) throws ServiceException {
		if (StringUtils.isBlank(date)) {
			throw new ServiceException(MessageConstant.DATE_IS_NULLL);
		}
		return faceDataCommunityMapper.listProvinceCityNumDTO(date);
	}

	/**
	 * 获取省小区数量列表
	 *
	 * @param date 日期
	 * @return List<ProvinceCommunityNumDTO>
	 * @throws ServiceException
	 */
	@Override
	public List<ProvinceCommunityNumDTO> listProvinceCommunityNumDTO(String date) throws ServiceException {
		if (StringUtils.isBlank(date)) {
			throw new ServiceException(MessageConstant.DATE_IS_NULLL);
		}
		return faceDataCommunityMapper.listProvinceCommunityNumDTO(date);
	}
}
