package com.du.elasticsearch.service.impl;

import com.xinchao.data.constant.MessageConstant;
import com.xinchao.data.dao.FaceDataCommunityTimeAgeMapper;
import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.FaceDataCommunityTimeAge;
import com.xinchao.data.model.dto.PeopleNumTimeAgeDTO;
import com.xinchao.data.service.FaceDataCommunityTimeAgeService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author dxy
 * @date 2019/4/2 20:42
 */
@Service(value = "faceDataCommunityTimeAgeService")
public class FaceDataCommunityTimeAgeServiceImpl implements FaceDataCommunityTimeAgeService{
	@Autowired
	private FaceDataCommunityTimeAgeMapper faceDataCommunityTimeAgeMapper;

	/**
	 * 插入数据
	 *
	 * @param faceDataCommunityTimeAge FaceDataCommunityTimeAge
	 * @throws ServiceException
	 */
	@Override
	public void insertFaceDataCommunityTimeAge(FaceDataCommunityTimeAge faceDataCommunityTimeAge) throws ServiceException {
		if (faceDataCommunityTimeAge == null) {
			throw new ServiceException(MessageConstant.PARAMETER_IS_NULL);
		}
		faceDataCommunityTimeAgeMapper.insertFaceDataCommunityTimeAge(faceDataCommunityTimeAge);
	}

	/**
	 * 批量插入数据
	 *
	 * @param faceDataCommunityTimeAgeList List<FaceDataCommunityTimeAge>
	 * @throws ServiceException
	 */
	@Override
	public void insertFaceDataCommunityTimeAgeBatch(List<FaceDataCommunityTimeAge> faceDataCommunityTimeAgeList) throws ServiceException {
		if (CollectionUtils.isEmpty(faceDataCommunityTimeAgeList)) {
			throw new ServiceException(MessageConstant.PARAMETER_IS_NULL);
		}
		faceDataCommunityTimeAgeMapper.insertFaceDataCommunityTimeAgeBatch(faceDataCommunityTimeAgeList);
	}

	/**
	 * 获取人数人次年龄列表
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
		return faceDataCommunityTimeAgeMapper.listPeopleNumTimeAge(dateList, community);
	}
}
