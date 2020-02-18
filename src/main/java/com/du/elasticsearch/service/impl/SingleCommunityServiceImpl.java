package com.du.elasticsearch.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xinchao.data.constant.BusinessConstant;
import com.xinchao.data.constant.DateConstant;
import com.xinchao.data.constant.ElasticsearchConstant;
import com.xinchao.data.constant.MessageConstant;
import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.dto.*;
import com.xinchao.data.model.vo.PeopleNumAgeVO;
import com.xinchao.data.model.vo.PeopleNumSexVO;
import com.xinchao.data.model.vo.PeopleNumTimeVO;
import com.xinchao.data.service.*;
import com.xinchao.data.util.DateUtils;
import com.xinchao.data.util.SortUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.*;

/**
 * @author dxy
 * @date 2019/3/11 16:26
 */
@Service(value = "singleCommunityService")
public class SingleCommunityServiceImpl implements SingleCommunityService {
	@Autowired
	private FaceDataCommunitySexService faceDataCommunitySexService;
	@Autowired
	private FaceDataCommunityService faceDataCommunityService;
	@Autowired
	private FaceDataCommunityDeviceService faceDataCommunityDeviceService;
	@Autowired
	private FaceDataCommunityAgeService faceDataCommunityAgeService;
	@Autowired
	private FaceDataCommunityTimeService faceDataCommunityTimeService;
	@Autowired
	private FaceDataCommunityTimeAgeService faceDataCommunityTimeAgeService;

	/**
	 * 昨日小区列表
	 *
	 * @return List<String>
	 * @throws ServiceException
	 */
	@Override
	public List<String> listCommunity() throws ServiceException {
		// 获取昨天的日期
		String date = DateUtils.getPastDay(DateConstant.YESTERDAY, DateConstant.DATE_YEAR_MONTH_DAY);
		List<String> communityList = faceDataCommunityService.listCommunity(date);
		// 2019/4/11 为了今天下午的演示，暂时过滤“兴元华盛一二期”,不在列表中显示
		List<String> resultList = new ArrayList<>();
		for (String str : communityList) {
			if (BusinessConstant.FILTERING_COMMUNITY_NAME.equals(str)) {
				continue;
			}
			resultList.add(str);
		}
		return resultList;
	}

	/**
	 * 昨日客流人数男女占比
	 *
	 * @param community 小区名称
	 * @return PeopleNumSexVO
	 * @throws ServiceException
	 */
	@Override
	public List<PeopleNumSexVO> listPeopleNumSex(String community) throws ServiceException {
		if (StringUtils.isBlank(community)) {
			throw new ServiceException(MessageConstant.COMMUNITY_NAME_IS_NULLL);
		}
		// 获取近7天的日期
		List<String> recentDateList = DateUtils.getRecentDayList(7, DateConstant.DATE_YEAR_MONTH_DAY);

		List<PeopleNumSexDTO> peopleNumSexDTOList = faceDataCommunitySexService.listPeopleNumSex(recentDateList, community);

		// 获取实际日期列表
		List<String> actualDateList = faceDataCommunitySexService.listActualDate(recentDateList, community);
		// 实际天数
		int actualDayNum = actualDateList.size();

		// 返回列表
		List<PeopleNumSexVO> voList = Lists.newArrayList();

		if (CollectionUtils.isEmpty(peopleNumSexDTOList)) {
			// 男
			PeopleNumSexVO maleVO = new PeopleNumSexVO();
			maleVO.setSex(ElasticsearchConstant.SEX_MALE);
			maleVO.setRatio(0.0F);
			voList.add(maleVO);
			// 女
			PeopleNumSexVO femaleVO = new PeopleNumSexVO();
			femaleVO.setSex(ElasticsearchConstant.SEX_FEMALE);
			femaleVO.setRatio(0.0F);
			voList.add(femaleVO);
		} else {
			Map<String, Integer> sexMap = Maps.newHashMap();
			// 总人数
			int totalNum = 0;
			for (PeopleNumSexDTO dto: peopleNumSexDTOList) {
				String sex = dto.getSex();
				// 如果有性别为未知的，不放入计算范围内
				if (StringUtils.isBlank(sex) || ElasticsearchConstant.SEX_UNLIMITED.equals(sex)) {
					continue;
				}
				Integer peopleNum = dto.getPeopleNum();
				if (peopleNum == null) {
					peopleNum = 0;
				}
				totalNum = totalNum + peopleNum;
				sexMap.put(sex, peopleNum);
			}
			List<String> sexList = ElasticsearchConstant.SEX_LIST;
			if (totalNum == 0) {
				for (String sex : sexList) {
					PeopleNumSexVO vo = new PeopleNumSexVO();
					vo.setSex(sex);
					vo.setRatio(0.0F);
					vo.setPeopleNum(0);
					voList.add(vo);
				}
			} else {
				if (sexMap.size() == 0) {
					for (String sex : sexList) {
						PeopleNumSexVO vo = new PeopleNumSexVO();
						vo.setSex(sex);
						vo.setRatio(0.0F);
						vo.setPeopleNum(0);
						voList.add(vo);
					}
				} else {
					// 保留两位小数
					DecimalFormat decimalFormat = new DecimalFormat(BusinessConstant.ZERO_POINT_ZERO_ZERO);
					for (Map.Entry<String, Integer> entry : sexMap.entrySet()) {
						String key = entry.getKey();
						Integer value = entry.getValue();
						float peopleNumSum = value.floatValue();
						String format = decimalFormat.format(peopleNumSum  / totalNum);
						Float ratio = Float.valueOf(format);
						PeopleNumSexVO peopleNumSexVO = new PeopleNumSexVO();
						peopleNumSexVO.setSex(key);
						peopleNumSexVO.setRatio(ratio);
						// 平均人数
						if (actualDayNum > 1) {
							float peopleNum = peopleNumSum / actualDayNum;
							value = Math.round(peopleNum);
						}
						peopleNumSexVO.setPeopleNum(value);
						voList.add(peopleNumSexVO);
					}
				}
			}
		}
		return voList;
	}

	/**
	 * 昨日客流
	 *
	 * @param community 小区名称
	 * @return PeopleNumTimeVO
	 * @throws ServiceException
	 */
	@Override
	public PeopleNumTimeVO getPeopleNumTime(String community) throws ServiceException {
		if (StringUtils.isBlank(community)) {
			throw new ServiceException(MessageConstant.COMMUNITY_NAME_IS_NULLL);
		}
		// 获取近7天的日期
		List<String> recentDateList = DateUtils.getRecentDayList(7, DateConstant.DATE_YEAR_MONTH_DAY);

		// 获取小区点位数
		Integer countCommnuity = faceDataCommunityDeviceService.countCommnuity(recentDateList, community);
		if (countCommnuity == null) {
			countCommnuity = 0;
		}

		// 人数人次DTO
		PeopleNumTimeDTO peopleNumTimeDTO = faceDataCommunityService.getPeopleNumTime(recentDateList, community);

		// 实际日期列表
		List<String> actualDateList = faceDataCommunityService.listActualDate(recentDateList);
		//实际天数
		int actualDayNum = actualDateList.size();

		// 人数人次VO
		PeopleNumTimeVO vo = new PeopleNumTimeVO();
		if (peopleNumTimeDTO == null) {
			vo.setDeviceNum(countCommnuity);
			vo.setPeopleNum(0);
			vo.setPeopleTime(0);
		} else {
			vo.setDeviceNum(countCommnuity);
			Integer peopleTime = peopleNumTimeDTO.getPeopleTime();
			if (peopleTime == null) {
				peopleTime = 0;
			}
			// 平均人次
			if (actualDayNum > 1) {
				float time = peopleTime.floatValue() / actualDayNum;
				peopleTime = Math.round(time);
			}
			vo.setPeopleTime(peopleTime);

			Integer peopleNum = peopleNumTimeDTO.getPeopleNum();
			if (peopleNum == null) {
				peopleNum = 0;
			}
			// 平均人数
			if (actualDayNum > 1) {
				float num = peopleNum.floatValue() / actualDayNum;
				peopleNum = Math.round(num);
			}
			vo.setPeopleNum(peopleNum);
		}
		return vo;
	}

	/**
	 * 昨日客流人数年龄分布
	 *
	 * @param community 小区名称
	 * @return List<PeopleNumAgeVO>
	 * @throws ServiceException
	 */
	@Override
	public List<PeopleNumAgeVO> listPeopleNumAge(String community) throws ServiceException {
		if (StringUtils.isBlank(community)) {
			throw new ServiceException(MessageConstant.COMMUNITY_NAME_IS_NULLL);
		}

		// 获取近7天的日期
		List<String> recentDateList = DateUtils.getRecentDayList(7, DateConstant.DATE_YEAR_MONTH_DAY);

		// 人数年龄VO列表
		List<PeopleNumAgeVO> voList = Lists.newArrayList();
		// 人数年龄DTO列表
		List<PeopleNumAgeDTO> peopleNumAgeDTOList = faceDataCommunityAgeService.listPeopleNumAge(recentDateList, community);
		if (CollectionUtils.isNotEmpty(peopleNumAgeDTOList)) {
			// 保留两位小数
			DecimalFormat decimalFormat = new DecimalFormat(BusinessConstant.ZERO_POINT_ZERO_ZERO);
			// 已年龄为key,人数为value的map
			Map<String, Integer> agePeopleNumMap = Maps.newHashMap();
			int totalNum = 0;
			for (PeopleNumAgeDTO dto : peopleNumAgeDTOList) {
				String age = dto.getAge();
				if (StringUtils.isBlank(age)) {
					continue;
				}
				Integer peopleNum = dto.getPeopleNum();
				if (peopleNum == null) {
					peopleNum = 0;
				}
				totalNum = totalNum + peopleNum;
				agePeopleNumMap.put(age, peopleNum);
			}
			// 计算年龄段人数比率
			if (totalNum == 0) {
				for (String ageRange : BusinessConstant.AGE_RANGE_LIST) {
					PeopleNumAgeVO vo = new PeopleNumAgeVO();
					vo.setAge(ageRange);
					vo.setRatio(0.0F);
					voList.add(vo);
				}
			} else {
				// 如果年龄段数据为空
				if (agePeopleNumMap.size() == 0) {
					for (String ageRange : BusinessConstant.AGE_RANGE_LIST) {
						PeopleNumAgeVO vo = new PeopleNumAgeVO();
						vo.setAge(ageRange);
						vo.setRatio(0.0F);
						voList.add(vo);
					}
				} else {
					// 年龄段数据不为空
					for (Map.Entry<String, Integer> entry : agePeopleNumMap.entrySet()) {
						//人数
						Integer value = entry.getValue();
						//年龄
						String key = entry.getKey();
						PeopleNumAgeVO peopleNumAgeVO = new PeopleNumAgeVO();
						String ratio = decimalFormat.format((float) value / (float) totalNum);
						Float peopleNumRatio = Float.valueOf(ratio);
						peopleNumAgeVO.setAge(key);
						peopleNumAgeVO.setRatio(peopleNumRatio);
						voList.add(peopleNumAgeVO);
					}
				}
			}

		} else {
			// 如果没有数据，设置默认值
			for (String ageRange : BusinessConstant.AGE_RANGE_LIST) {
				PeopleNumAgeVO vo = new PeopleNumAgeVO();
				vo.setAge(ageRange);
				vo.setRatio(0.0F);
				voList.add(vo);
			}
		}
		return voList;
	}

	/**
	 * 近7天24小时平均客流人次&客流人数
	 *
	 * @param community 小区名称
	 * @return List<PeopleNumTimeTimeDTO>
	 */
	@Override
	public List<PeopleNumTimeTimeDTO> listPeopleNumTimeTime(String community) throws ServiceException {
		if (StringUtils.isBlank(community)) {
			throw new ServiceException(MessageConstant.COMMUNITY_NAME_IS_NULLL);
		}
		// 近7天的日期
		List<String> dateList = DateUtils.getRecentDayList(7, DateConstant.DATE_YEAR_MONTH_DAY);
		List<PeopleNumTimeTimeDTO> peopleNumTimeTimeDTOList = faceDataCommunityTimeService.listPeopleNumTimeTime(dateList, community);

		// 实际天数列表
		List<String> actualDateList = faceDataCommunityTimeService.listActualDate(dateList);
		// 实际天数
		int dayNum = actualDateList.size();

		// 返回列表
		List<PeopleNumTimeTimeDTO> resultList = new ArrayList<>();

		// 用于存放列表中存在的时间
		List<String> existTimeList = new ArrayList<>();
		// 保留两位小数
		DecimalFormat df = new DecimalFormat(BusinessConstant.ZERO_POINT_ZERO_ZERO);

		// 判断是否列表中有时间为23
		for (PeopleNumTimeTimeDTO peopleNumTimeTimeDTO : peopleNumTimeTimeDTOList) {
			String time = peopleNumTimeTimeDTO.getTime();
			if (StringUtils.isBlank(time)) {
				continue;
			}

			// 如果时间前面是0，把0替换掉,余下字符串数字,如：“00”、“01”
			if (time.startsWith(ElasticsearchConstant.STRING_ZERO)) {
				// 如果时间是00，则时间等于0
				if (time.equals(ElasticsearchConstant.STRING_ZERO_ZERO)) {
					time = ElasticsearchConstant.STRING_ZERO;
				} else {
					time = time.replace(ElasticsearchConstant.STRING_ZERO, "");
				}
			}
			// 把余下的转化数字
			Integer timeInt = Integer.valueOf(time);
			// 因为前段只显示6点到23点，如果为6点以前的数据，不计入6点
			if (timeInt <= 5) {
				continue;
			}
			if (dayNum > 1) {
				// 人数
				Integer peopleNum = peopleNumTimeTimeDTO.getPeopleNum();
				if (peopleNum == null) {
					peopleNum = 0;
				}
				String peopleNumAvgStr = df.format(peopleNum / dayNum);
				peopleNum = Math.round(Float.valueOf(peopleNumAvgStr));
				peopleNumTimeTimeDTO.setPeopleNum(peopleNum);

				// 人次
				Integer peopleTime = peopleNumTimeTimeDTO.getPeopleTime();
				if (peopleTime == null) {
					peopleTime = 0;
				}
				String peopleTimeAvgStr = df.format(peopleTime / dayNum);
				peopleTime = Math.round(Float.valueOf(peopleTimeAvgStr));
				peopleNumTimeTimeDTO.setPeopleTime(peopleTime);
			}
			resultList.add(peopleNumTimeTimeDTO);
			existTimeList.add(peopleNumTimeTimeDTO.getTime());
		}
		// 获取不存在的时间列表
		List<String> notExistTimeList = DateUtils.getNotExistElementList(BusinessConstant.TIME_POINT_LIST, existTimeList);
		if (CollectionUtils.isNotEmpty(notExistTimeList)) {
			for (String time : notExistTimeList) {
				PeopleNumTimeTimeDTO peopleNumTimeTimeDTO = new PeopleNumTimeTimeDTO();
				peopleNumTimeTimeDTO.setTime(time);
				peopleNumTimeTimeDTO.setPeopleNum(0);
				peopleNumTimeTimeDTO.setPeopleTime(0);
				resultList.add(peopleNumTimeTimeDTO);
			}
			// 排序
			Collections.sort(resultList, new Comparator<PeopleNumTimeTimeDTO>() {
				@Override
				public int compare(PeopleNumTimeTimeDTO o1, PeopleNumTimeTimeDTO o2) {
					String time = o1.getTime();
					String timeEnd = o2.getTime();
					return  SortUtils.sortTime(time, timeEnd);
				}
			});
		}
		return resultList;
	}

	/**
	 * 近7天各年龄段出入时段分析
	 *
	 * @param community
	 * @return List<PeopleNumTimeAgeDTO>
	 * @throws ServiceException
	 */
	@Override
	public List<PeopleNumTimeAgeDTO> listPeopleNumTimeAge(String community) throws ServiceException {
		if (StringUtils.isBlank(community)) {
			throw new ServiceException(MessageConstant.COMMUNITY_NAME_IS_NULLL);
		}
		// 近7天的日期
		List<String> dateList = DateUtils.getRecentDayList(7, DateConstant.DATE_YEAR_MONTH_DAY);
		List<PeopleNumTimeAgeDTO> peopleNumTimeAgeDTOList = faceDataCommunityTimeAgeService.listPeopleNumTimeAge(dateList,community);

		// 返回列表
		List<PeopleNumTimeAgeDTO> resultList = new ArrayList<>();

		// 时间Map
		Map<String, List<String>> timeMap = new HashMap<>();

		for (PeopleNumTimeAgeDTO peopleNumTimeAgeDTO : peopleNumTimeAgeDTOList) {
			String time = peopleNumTimeAgeDTO.getTime();
			if (StringUtils.isBlank(time)) {
				continue;
			}
			// 如果时间前面是0，把0替换掉,余下字符串数字,如：“00”、“01”
			if (time.startsWith(ElasticsearchConstant.STRING_ZERO)) {
				// 如果时间是00，则时间等于0
				if (time.equals(ElasticsearchConstant.STRING_ZERO_ZERO)) {
					time = ElasticsearchConstant.STRING_ZERO;
				} else {
					time = time.replace(ElasticsearchConstant.STRING_ZERO, "");
				}
			}
			// 把余下的转化数字
			Integer timeInt = Integer.valueOf(time);
			// 因为前段只显示6点到23点，如果为6点以前的数据，不计入6点
			if (timeInt <= 5) {
				continue;
			}
			resultList.add(peopleNumTimeAgeDTO);
			timeMap.put(peopleNumTimeAgeDTO.getTime(), null);
		}

		// 将同一时间下的年龄数据归类到同一时间节点下
		for (String time : timeMap.keySet()) {
			List<String> ageRangeList = new ArrayList<>();
			for (PeopleNumTimeAgeDTO dto : peopleNumTimeAgeDTOList) {
				String peopleNumTime = dto.getTime();
				if (StringUtils.isBlank(peopleNumTime)) {
					continue;
				}
				if (time.equals(peopleNumTime)) {
					ageRangeList.add(dto.getAge());
				}
			}
			timeMap.put(time, ageRangeList);
		}

		List<String> baseAgeList = BusinessConstant.AGE_RANGE_LIST;

		// 如果相同时间节点下，有的年龄段数据没有，则加上改年龄段，数据都为0
		for (Map.Entry<String, List<String>> entry : timeMap.entrySet()) {
			String time = entry.getKey();
			List<String> ageRangeList = entry.getValue();
			for (String age : baseAgeList) {
				if (ageRangeList.contains(age)) {
					continue;
				}
				PeopleNumTimeAgeDTO  dto = new PeopleNumTimeAgeDTO();
				dto.setTime(time);
				dto.setAge(age);
				dto.setPeopleNum(0);
				peopleNumTimeAgeDTOList.add(dto);
			}

		}

		// 获取不存在的时间列表
		List<String> notExistTimeList = DateUtils.getNotExistElementList(BusinessConstant.TIME_POINT_LIST, timeMap.keySet());
		if (CollectionUtils.isNotEmpty(notExistTimeList)) {
			for (String time : notExistTimeList) {
				List<String> ageRangeList = BusinessConstant.AGE_RANGE_LIST;
				for (String ageRange : ageRangeList) {
					PeopleNumTimeAgeDTO peopleNumTimeAgeDTO = new PeopleNumTimeAgeDTO();
					peopleNumTimeAgeDTO.setTime(time);
					peopleNumTimeAgeDTO.setAge(ageRange);
					peopleNumTimeAgeDTO.setPeopleNum(0);
					resultList.add(peopleNumTimeAgeDTO);
				}
			}
			// 按时间排序
			Collections.sort(resultList, new Comparator<PeopleNumTimeAgeDTO>() {
				@Override
				public int compare(PeopleNumTimeAgeDTO o1, PeopleNumTimeAgeDTO o2) {
					String time = o1.getTime();
					String timeEnd = o2.getTime();
					return  SortUtils.sortTime(time, timeEnd);
				}
			});
		}
		return resultList;
	}
}
