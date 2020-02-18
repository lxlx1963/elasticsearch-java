package com.du.elasticsearch.service.impl;

import com.xinchao.data.constant.MessageConstant;
import com.xinchao.data.dao.FaceDataCommunityTimeMapper;
import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.FaceDataCommunityTime;
import com.xinchao.data.model.dto.PeopleNumTimeTimeDTO;
import com.xinchao.data.service.FaceDataCommunityTimeService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author dxy
 * @date 2019/4/2 20:46
 */
@Service(value = "faceDataCommunityTimeService")
public class FaceDataCommunityTimeServiceImpl implements FaceDataCommunityTimeService {
	@Autowired
	private FaceDataCommunityTimeMapper faceDataCommunityTimeMapper;

	/**
	 * 插入数据
	 *
	 * @param faceDataCommunityTime FaceDataCommunityTime
	 * @throws ServiceException
	 */
	@Override
	public void insertFaceDataCommunityTime(FaceDataCommunityTime faceDataCommunityTime) throws ServiceException {
		if (faceDataCommunityTime == null) {
			throw new ServiceException(MessageConstant.PARAMETER_IS_NULL);
		}
		faceDataCommunityTimeMapper.insertFaceDataCommunityTime(faceDataCommunityTime);
	}

	/**
	 * 批量插入数据
	 *
	 * @param faceDataCommunityTimeList List<FaceDataCommunityTime>
	 * @throws ServiceException
	 */
	@Override
	public void insertFaceDataCommunityTimeBatch(List<FaceDataCommunityTime> faceDataCommunityTimeList) throws ServiceException {
		if (CollectionUtils.isEmpty(faceDataCommunityTimeList)) {
			throw new ServiceException(MessageConstant.PARAMETER_IS_NULL);
		}
		faceDataCommunityTimeMapper.insertFaceDataCommunityTimeBatch(faceDataCommunityTimeList);
	}

	/**
	 * 获取人数人次时间列表
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
		return faceDataCommunityTimeMapper.listPeopleNumTimeTime(dateList, community);
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
		return faceDataCommunityTimeMapper.listActualDate(dateList);
	}
}
