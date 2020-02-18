package com.du.elasticsearch.service;

import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.dto.PeopleNumTimeAgeDTO;
import com.xinchao.data.model.dto.PeopleNumTimeTimeDTO;
import com.xinchao.data.model.vo.PeopleNumAgeVO;
import com.xinchao.data.model.vo.PeopleNumSexVO;
import com.xinchao.data.model.vo.PeopleNumTimeVO;

import java.util.List;

/**
 * 单小区
 *
 * @author dxy
 * @date 2019/3/11 9:44
 */
public interface SingleCommunityService {
	/**
	 * 昨日小区列表
	 *
	 * @return List<String>
	 * @throws ServiceException
	 */
	List<String> listCommunity() throws ServiceException;

	/**
	 * 昨日客流人数男女占比列表
	 *
	 * @param community 小区名称
	 * @return PeopleNumSexVO
	 * @throws ServiceException
	 */
	List<PeopleNumSexVO> listPeopleNumSex(String community) throws ServiceException;

	/**
	 * 昨日客流
	 *
	 * @param community 小区名称
	 * @return PeopleNumTimeVO
	 * @throws ServiceException
	 */
	PeopleNumTimeVO getPeopleNumTime(String community) throws ServiceException;

	/**
	 * 昨日客流人数年龄分布
	 *
	 * @param community 小区名称
	 * @return List<PeopleNumAgeVO>
	 * @throws ServiceException
	 */
	List<PeopleNumAgeVO> listPeopleNumAge(String community) throws ServiceException;


	/**
	 * 近7天24小时平均客流人次&客流人数
	 *
	 * @param community 小区名称
	 * @return List<PeopleNumTimeTimeDTO>
	 */
	List<PeopleNumTimeTimeDTO> listPeopleNumTimeTime(String community) throws ServiceException;

	/**
	 * 近7天各年龄段出入时段分析
	 *
	 * @param community
	 * @return List<PeopleNumTimeAgeDTO>
	 * @throws ServiceException
	 */
	List<PeopleNumTimeAgeDTO> listPeopleNumTimeAge(String community) throws ServiceException;

}
