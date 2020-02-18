package com.du.elasticsearch.service.impl;

import com.xinchao.data.constant.MessageConstant;
import com.xinchao.data.dao.FaceDataCommunityAgeMapper;
import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.FaceDataCommunityAge;
import com.xinchao.data.model.dto.FaceDataProvinceAgeDTO;
import com.xinchao.data.model.dto.PeopleNumAgeDTO;
import com.xinchao.data.service.FaceDataCommunityAgeService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author dxy
 * @date 2019/4/2 20:37
 */
@Service(value = "faceDataCommunityAgeService")
public class FaceDataCommunityAgeServiceImpl implements FaceDataCommunityAgeService{
	@Autowired
	private FaceDataCommunityAgeMapper faceDataCommunityAgeMapper;

	/**
	 * 插入数据
	 *
	 * @param faceDataCommunityAge FaceDataCommunityAge
	 * @throws ServiceException
	 */
	@Override
	public void insertFaceDataCommunityAge(FaceDataCommunityAge faceDataCommunityAge) throws ServiceException {
		if (faceDataCommunityAge == null) {
			throw new ServiceException(MessageConstant.PARAMETER_IS_NULL);
		}
		faceDataCommunityAgeMapper.insertFaceDataCommunityAge(faceDataCommunityAge);
	}

	/**
	 * 批量插入数据
	 *
	 * @param faceDataCommunityAgeList List<FaceDataCommunityAge>
	 * @throws ServiceException
	 */
	@Override
	public void insertFaceDataCommunityAgeBatch(List<FaceDataCommunityAge> faceDataCommunityAgeList) throws ServiceException {
		if (CollectionUtils.isEmpty(faceDataCommunityAgeList)) {
			throw new ServiceException(MessageConstant.PARAMETER_IS_NULL);
		}
		faceDataCommunityAgeMapper.insertFaceDataCommunityAgeBatch(faceDataCommunityAgeList);
	}

	/**
	 * 获取人数年龄列表
	 *
	 * @param dateList 日期列表
	 * @param community 小区
	 * @return List<PeopleNumAgeDTO>
	 * @throws ServiceException
	 */
	@Override
	public List<PeopleNumAgeDTO> listPeopleNumAge(List<String> dateList, String community) throws ServiceException {
		if (CollectionUtils.isEmpty(dateList) || StringUtils.isBlank(community)) {
			throw new ServiceException(MessageConstant.DATE_LIST_OR_COMMUNITY_NAME_IS_NULLL);
		}
		return faceDataCommunityAgeMapper.listPeopleNumAge(dateList, community);
	}

	/**
	 * 获取人脸数据列表（按省、年龄分组）
	 *
	 * @param date 日期
	 * @return List<FaceDataProvinceAgeDTO>
	 * @throws ServiceException
	 */
	@Override
	public List<FaceDataProvinceAgeDTO> listFaceDataProvinceAgeDTO(String date) throws ServiceException {
		if (StringUtils.isBlank(date)) {
			throw new ServiceException(MessageConstant.DATE_IS_NULLL);
		}
		return faceDataCommunityAgeMapper.listFaceDataProvinceAgeDTO(date);
	}
}
