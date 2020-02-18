package com.du.elasticsearch.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xinchao.data.constant.BusinessConstant;
import com.xinchao.data.constant.DateConstant;
import com.xinchao.data.constant.ElasticsearchConstant;
import com.xinchao.data.constant.MessageConstant;
import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.dto.FaceDataSexSumDTO;
import com.xinchao.data.model.dto.SingleDeviceTimeAgeDTO;
import com.xinchao.data.model.dto.SingleDeviceTimeDTO;
import com.xinchao.data.model.dto.SingleDeviceTimeSexDTO;
import com.xinchao.data.model.vo.SingleDeviceTimeSexVO;
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
 * 处理单设备
 *
 * @author dxy
 * @date 2019/3/9 11:04
 */
@Service(value = "singleDeviceService")
public class SingleDeviceServiceImpl implements SingleDeviceService {
	@Autowired
	private FaceDataDeviceSexService faceDataDeviceSexService;
	@Autowired
	private FaceDataDeviceService faceDataDeviceService;
	@Autowired
	private FaceDataDeviceTimeAgeService faceDataDeviceTimeAgeService;
	@Autowired
	private FaceDataDeviceTimeSexService faceDataDeviceTimeSexService;
	@Autowired
	private FaceDataDeviceTimeService faceDataDeviceTimeService;

	/**
	 * 获取昨天的客流人数、客流人次及男女分别的客流人数、客流人次
	 *
	 * @param residenceType 住宅类型
	 * @return SingleDeviceTimeSexVO
	 * @throws ServiceException
	 */
	@Override
	public SingleDeviceTimeSexVO getSingleDeviceTimeSex(String residenceType) throws ServiceException {
		if (StringUtils.isBlank(residenceType)) {
			throw new ServiceException(MessageConstant.RESIDENCE_TYPE_IS_NULLL);
		}
		// 获取近7天的日期
		List<String> recentDateList = DateUtils.getRecentDayList(7, DateConstant.DATE_YEAR_MONTH_DAY);

		// 商业综合体为写字楼和综合体
		List<String> residenceTypeList = Lists.newArrayList();
		if (BusinessConstant.COMMERCIAL_SYNTHESIS_BUILDING.equals(residenceType)) {
			residenceTypeList.add(BusinessConstant.SYNTHESIS_BUILDING);
			residenceTypeList.add(BusinessConstant.OFFICE_BUILDING);
		} else {
			residenceTypeList.add(residenceType);
		}

		List<FaceDataSexSumDTO> faceDataSexSumDTOList = faceDataDeviceSexService.listFaceDataSexSum(recentDateList, residenceTypeList);
		// 获取住宅类型下设备列表
		List<String> residenceTypeDeviceModelList = faceDataDeviceService.listResidenceTypeDeviceModel(recentDateList, residenceTypeList);
		// 获取实际日期列表
		List<String> actualDateList = faceDataDeviceSexService.listActualDate(recentDateList, residenceTypeList);
		// 实际天数
		int actualDayNum = actualDateList.size();

		SingleDeviceTimeSexVO vo = new SingleDeviceTimeSexVO();
		// 平均人数
		Integer peopleNumSum = 0;
		vo.setPeopleNumSum(peopleNumSum);

		// 平均人次
		Integer peopleTimeSum = 0;
		vo.setPeopleTimeSum(peopleTimeSum);

		// 男性平均人数
		Integer malePeopleNumSum = 0;
		vo.setMalePeopleNumSum(malePeopleNumSum);

		// 男性平均人次
		Integer malePeopleTimeSum = 0;
		vo.setMalePeopleTimeSum(malePeopleTimeSum);

		// 女性平均人数
		Integer femalePeopleNumSum = 0;
		vo.setFemalePeopleNumSum(femalePeopleNumSum);

		// 女性平均人次
		Integer femalePeopleTimeSum = 0;
		vo.setFemalePeopleTimeSum(femalePeopleTimeSum);

		if (CollectionUtils.isNotEmpty(faceDataSexSumDTOList) && CollectionUtils.isNotEmpty(residenceTypeDeviceModelList)) {
			// 设备台数
			int deviceNum = residenceTypeDeviceModelList.size();
			// 保留两位小数
			DecimalFormat df = new DecimalFormat(BusinessConstant.ZERO_POINT_ZERO_ZERO);

			for (FaceDataSexSumDTO faceDataSexSumDTO : faceDataSexSumDTOList) {
				String sex = faceDataSexSumDTO.getSex();
				if (StringUtils.isBlank(sex)) {
					continue;
				}
				// 人数
				Integer peopleNum = faceDataSexSumDTO.getPeopleNumSum();
				if (peopleNum == null) {
					peopleNum = 0;
				}
				// 人次
				Integer peopleTime = faceDataSexSumDTO.getPeopleTimeSum();
				if (peopleTime == null) {
					peopleTime = 0;
				}
				if (ElasticsearchConstant.SEX_MALE.equals(sex)) {
					malePeopleNumSum = malePeopleNumSum + peopleNum;
					malePeopleTimeSum = malePeopleTimeSum + peopleTime;
				}
				if (ElasticsearchConstant.SEX_FEMALE.equals(sex)) {
					femalePeopleNumSum = femalePeopleNumSum + peopleNum;
					femalePeopleTimeSum = femalePeopleTimeSum + peopleTime;
				}
				peopleNumSum = peopleNumSum + peopleNum;
				peopleTimeSum = peopleTimeSum + peopleTime;
			}
			// 人数
			String peopleNumSumStr = "";
			// 人次
			String peopleTimeSumStr = "";
			// 女性人数
			String femalePeopleNumSumStr = "";
			// 女性人次
			String femalePeopleTimeSumStr = "";
			// 男性人数
			String malePeopleNumSumStr = "";
			// 男性人次
			String malePeopleTimeSumStr = "";
			if (actualDayNum > 1){
				peopleNumSumStr = df.format((float)peopleNumSum / deviceNum / actualDayNum);
				// 人次
				peopleTimeSumStr = df.format( (float)peopleTimeSum / deviceNum / actualDayNum);
				// 女性人数
				femalePeopleNumSumStr = df.format( (float)femalePeopleNumSum / deviceNum / actualDayNum);
				// 女性人次
				femalePeopleTimeSumStr = df.format( (float)femalePeopleTimeSum / deviceNum / actualDayNum);
				// 男性人数
				malePeopleNumSumStr = df.format( (float)malePeopleNumSum / deviceNum / actualDayNum);
				// 男性人次
				malePeopleTimeSumStr = df.format( (float)malePeopleTimeSum / deviceNum / actualDayNum);
			} else {
				// 人数
				peopleNumSumStr = df.format((float)peopleNumSum / deviceNum);
				// 人次
				peopleTimeSumStr = df.format( (float)peopleTimeSum / deviceNum);
				// 女性人数
				femalePeopleNumSumStr = df.format( (float)femalePeopleNumSum / deviceNum);
				// 女性人次
				femalePeopleTimeSumStr = df.format( (float)femalePeopleTimeSum / deviceNum);
				// 男性人数
				malePeopleNumSumStr = df.format( (float)malePeopleNumSum / deviceNum);
				// 男性人次
				malePeopleTimeSumStr = df.format( (float)malePeopleTimeSum / deviceNum);
			}
			// 人数
			vo.setPeopleNumSum(Math.round(Float.valueOf(peopleNumSumStr)));
			// 人次
			vo.setPeopleTimeSum(Math.round(Float.valueOf(peopleTimeSumStr)));
			// 女性人数
			vo.setFemalePeopleNumSum(Math.round(Float.valueOf(femalePeopleNumSumStr)));
			// 女性人次
			vo.setFemalePeopleTimeSum(Math.round(Float.valueOf(femalePeopleTimeSumStr)));
			// 男性人数
			vo.setMalePeopleNumSum(Math.round(Float.valueOf(malePeopleNumSumStr)));
			// 男性人次
			vo.setMalePeopleTimeSum(Math.round(Float.valueOf(malePeopleTimeSumStr)));
		}
		return vo;
	}

	/**
	 * 昨日每小时的人员构成 (年龄分布)
	 *
	 * @param residenceType 住宅类型
	 * @return List<SingleDeviceTimeAgeDTO>
	 * @throws ServiceException
	 */
	@Override
	public List<SingleDeviceTimeAgeDTO> listDeviceTimeAge(String residenceType) throws ServiceException {
		if (StringUtils.isBlank(residenceType)) {
			throw new ServiceException(MessageConstant.RESIDENCE_TYPE_IS_NULLL);
		}
		// 获取近7天的日期
		List<String> recentDateList = DateUtils.getRecentDayList(7, DateConstant.DATE_YEAR_MONTH_DAY);

		// 商业综合体为写字楼和综合体
		List<String> residenceTypeList = Lists.newArrayList();
		if (BusinessConstant.COMMERCIAL_SYNTHESIS_BUILDING.equals(residenceType)) {
			residenceTypeList.add(BusinessConstant.SYNTHESIS_BUILDING);
			residenceTypeList.add(BusinessConstant.OFFICE_BUILDING);
		} else {
			residenceTypeList.add(residenceType);
		}
		List<SingleDeviceTimeAgeDTO> singleDeviceTimeAgeDTOList = faceDataDeviceTimeAgeService.listDeviceTimeAge(recentDateList, residenceTypeList);
		// 获取住宅类型下设备列表
		List<String> residenceTypeDeviceModelList = faceDataDeviceService.listResidenceTypeDeviceModel(recentDateList, residenceTypeList);
		// 获取实际日期列表
		List<String> actualDateList = faceDataDeviceTimeAgeService.listActualDate(recentDateList, residenceTypeList);
		// 实际天数
		int actualDayNum = actualDateList.size();

		// 设备数量
		int deviceNum = residenceTypeDeviceModelList.size();
		// 返回数据列表
		List<SingleDeviceTimeAgeDTO> resultList = Lists.newArrayList();

		// 处理数据，如果时间下没有年龄段，添加年龄段数据，人数、人次都是0
		if (CollectionUtils.isNotEmpty(singleDeviceTimeAgeDTOList)) {
			// 保留两位小数
			DecimalFormat df = new DecimalFormat(BusinessConstant.ZERO_POINT_ZERO_ZERO);
			// 已time为key的Map
			Map<String, List<SingleDeviceTimeAgeDTO>> timeAgeMap = Maps.newHashMap();

			for (SingleDeviceTimeAgeDTO singleDeviceTimeAgeDTO : singleDeviceTimeAgeDTOList) {
				String time = singleDeviceTimeAgeDTO.getTime();
				if (StringUtils.isBlank(time)) {
					continue;
				}
				// 获取时间对应的列表
				List<SingleDeviceTimeAgeDTO> dtoList = timeAgeMap.get(time);
				//如果列表不存在，创建列表
				if (CollectionUtils.isEmpty(dtoList)) {
					dtoList = Lists.newArrayList();
					timeAgeMap.put(time, dtoList);
				}
				dtoList.add(singleDeviceTimeAgeDTO);
			}
			// 固定年龄段列表
			List<String> ageRangeList = BusinessConstant.AGE_RANGE_LIST;

			// 如果同一时间下，不在年龄段列表的年龄段,添加此年龄段，但是该年龄段的人数人次，都为0
			for (Map.Entry<String, List<SingleDeviceTimeAgeDTO>> entry : timeAgeMap.entrySet()) {
				// 时间
				String time = entry.getKey();
				// 时间对应的年龄列表
				List<SingleDeviceTimeAgeDTO> valueList = entry.getValue();
				// 时间段下存在的年龄列表
				List<String> existAgeList = new ArrayList<>();

				// 计算平均值
				for (SingleDeviceTimeAgeDTO timeAgeDto : valueList) {
					String age = timeAgeDto.getAge();
					// 将年龄添加到时间段下存在的年龄列表中，用于计算不存在的年龄段
					existAgeList.add(age);
					// 设备数量大于1时，才求平均数据
					if (deviceNum > 1) {
						//人数
						Integer peopleNum = timeAgeDto.getPeopleNum();
						if (peopleNum == null) {
							peopleNum = 0;
						}
						if (peopleNum > 0) {
							// 人数平均数
							String peopleNumAvgStr = "";
							if (actualDayNum > 1) {
								peopleNumAvgStr = df.format((float) peopleNum / deviceNum / actualDayNum);
							} else {
								peopleNumAvgStr = df.format((float) peopleNum / deviceNum);
							}
							peopleNum = Math.round(Float.valueOf(peopleNumAvgStr));
						}
						timeAgeDto.setPeopleNum(peopleNum);

						// 人次
						Integer peopleTime = timeAgeDto.getPeopleTime();
						peopleTime = peopleTime == null ? 0 : peopleTime;
						if (peopleTime > 0) {
							// 人次平均数
							String peopleTimeStr = "";
							if (actualDayNum > 1) {
								peopleTimeStr = df.format((float) peopleTime / deviceNum / actualDayNum);
							} else {
								peopleTimeStr = df.format((float) peopleTime / deviceNum);
							}
							peopleTime = Math.round(Float.valueOf(peopleTimeStr));
						}
						timeAgeDto.setPeopleTime(peopleTime);
					}
					resultList.add(timeAgeDto);

				}
				// 时间段下不存在的年龄列表
				List<String> notExistAgeList = new ArrayList<>();
				for (String ageRange : ageRangeList) {
					if (existAgeList.contains(ageRange)) {
						continue;
					}
					notExistAgeList.add(ageRange);
				}
				// 给时间下不存在的时间段加上默认的人次、人数
				if (CollectionUtils.isNotEmpty(notExistAgeList)) {
					for (String age : notExistAgeList) {
						SingleDeviceTimeAgeDTO singleDeviceTimeAgeDTO = new SingleDeviceTimeAgeDTO();
						singleDeviceTimeAgeDTO.setAge(age);
						singleDeviceTimeAgeDTO.setTime(time);
						singleDeviceTimeAgeDTO.setPeopleNum(0);
						singleDeviceTimeAgeDTO.setPeopleTime(0);
						resultList.add(singleDeviceTimeAgeDTO);
					}
				}
			}
			// 存在时间集合
			Set<String> timeSet = timeAgeMap.keySet();
			//获取不存在的时间列表
			List<String> notExistTimeList = DateUtils.getNotExistElementList(BusinessConstant.TIME_POINT_LIST, timeSet);
			if (CollectionUtils.isNotEmpty(notExistTimeList)) {
				for (String time : notExistTimeList) {
					// 添加时间段下不存在的年龄段
					for (String ageRange : ageRangeList) {
						SingleDeviceTimeAgeDTO dto = new SingleDeviceTimeAgeDTO();
						dto.setAge(ageRange);
						dto.setTime(time);
						dto.setPeopleNum(0);
						dto.setPeopleTime(0);
						resultList.add(dto);
					}
				}
			}
		}
		// 排序，按照时间先后顺序排序
		Collections.sort(resultList, new Comparator<SingleDeviceTimeAgeDTO>() {
			@Override
			public int compare(SingleDeviceTimeAgeDTO o1, SingleDeviceTimeAgeDTO o2) {
				String timeStart = o1.getTime();
				String timeEnd = o2.getTime();
				return  SortUtils.sortTime(timeStart, timeEnd);
			}
		});
		return resultList;
	}

	/**
	 * deviceTimeSexDTOList
	 *
	 * @param residenceType 住宅类型
	 * @return List<SingleDeviceTimeSexDTO>
	 * @throws ServiceException
	 */
	@Override
	public List<SingleDeviceTimeSexDTO> listDeviceTimeSex(String residenceType) throws ServiceException {
		if (StringUtils.isBlank(residenceType)) {
			throw new ServiceException(MessageConstant.RESIDENCE_TYPE_IS_NULLL);
		}
		// 获取近7天的日期
		List<String> recentDateList = DateUtils.getRecentDayList(7, DateConstant.DATE_YEAR_MONTH_DAY);

		// 商业综合体为写字楼和综合体
		List<String> residenceTypeList = Lists.newArrayList();
		if (BusinessConstant.COMMERCIAL_SYNTHESIS_BUILDING.equals(residenceType)) {
			residenceTypeList.add(BusinessConstant.SYNTHESIS_BUILDING);
			residenceTypeList.add(BusinessConstant.OFFICE_BUILDING);
		} else {
			residenceTypeList.add(residenceType);
		}
		// 按时间、性别分组后的人数、人次
		List<SingleDeviceTimeSexDTO> deviceTimeSexDTOList = faceDataDeviceTimeSexService.listDeviceTimeSex(recentDateList, residenceTypeList);
		// 获取住宅类型下设备列表
		List<String> residenceTypeDeviceModelList = faceDataDeviceService.listResidenceTypeDeviceModel(recentDateList, residenceTypeList);
		// 设备数量
		int deviceNum = residenceTypeDeviceModelList.size();

		// 获取实际日期列表
		List<String> actualDateList = faceDataDeviceTimeSexService.listActualDate(recentDateList, residenceTypeList);
		// 实际天数
		int actualDayNum = actualDateList.size();

		// 返回数据列表
		List<SingleDeviceTimeSexDTO> resultList = new ArrayList<>();

		// 以时间为key的map
		Map<String, List<SingleDeviceTimeSexDTO>> timeMap = new HashMap<>();
		if (CollectionUtils.isNotEmpty(deviceTimeSexDTOList)) {
			for (SingleDeviceTimeSexDTO dto : deviceTimeSexDTOList ) {
				String time = dto.getTime();
				if (StringUtils.isBlank(time)) {
					continue;
				}
				// 如果时间前面是0，把0替换掉,余下字符串数字,如：“00”、“01”
				if (time.startsWith(ElasticsearchConstant.STRING_ZERO)) {
					//如果时间是00，则时间等于0
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
				timeMap.put(dto.getTime(), null);
			}
		}
		// 将具体数据分组到同一时间段下面
		for (Map.Entry<String, List<SingleDeviceTimeSexDTO>> timeEntry : timeMap.entrySet()) {
			String key = timeEntry.getKey();
			List<SingleDeviceTimeSexDTO>  deviceTimeSexList = new ArrayList<>();
			for (SingleDeviceTimeSexDTO dto : deviceTimeSexDTOList ) {
				String time = dto.getTime();
				// 如果有性别为未知的，不放入计算范围内
				if (ElasticsearchConstant.SEX_UNLIMITED.equals(dto.getSex()) || StringUtils.isBlank(time)) {
					continue;
				}
				if (key.equals(time)) {
					deviceTimeSexList.add(dto);
				}
			}
			timeMap.put(key, deviceTimeSexList);
		}
		// 保留两位小数
		DecimalFormat df = new DecimalFormat(BusinessConstant.ZERO_POINT_ZERO_ZERO);
		// 给时间段中没有男女性别的数据，给默认值
		for (Map.Entry<String, List<SingleDeviceTimeSexDTO>> timeEntry : timeMap.entrySet()) {
			String timeKey = timeEntry.getKey();
			List<SingleDeviceTimeSexDTO> list = timeEntry.getValue();
			// 如果列表条数等于2，则男女性别都有，不需要做默认值处理
			if (list.size() == 2) {
				//需要除以设备数
				for (SingleDeviceTimeSexDTO singleDeviceTimeSexDTO : list) {
					Integer peopleNumSum = singleDeviceTimeSexDTO.getPeopleNumSum();
					if (peopleNumSum == null) {
						peopleNumSum = 0;
					}
					if (peopleNumSum > 0) {
						// 人数平均数
						String peopleNumSumAvgStr = "";
						if (actualDayNum > 1) {
							peopleNumSumAvgStr = df.format((float) peopleNumSum / deviceNum / actualDayNum);
						} else {
							peopleNumSumAvgStr = df.format((float) peopleNumSum / deviceNum);
						}
						peopleNumSum = Math.round(Float.valueOf(peopleNumSumAvgStr));
					}
					singleDeviceTimeSexDTO.setPeopleNumSum(peopleNumSum);

					// 人次
					Integer peopleTimeSum = singleDeviceTimeSexDTO.getPeopleTimeSum();
					peopleTimeSum = peopleTimeSum == null ? 0 : peopleTimeSum;
					if (peopleTimeSum > 0) {
						// 人次平均数
						String peopleTimeSumAvgStr = "";
						if (actualDayNum > 1) {
							peopleTimeSumAvgStr = df.format((float) peopleTimeSum / deviceNum / actualDayNum);
						} else {
							peopleTimeSumAvgStr = df.format((float) peopleTimeSum / deviceNum);
						}
						peopleTimeSum = Math.round(Float.valueOf(peopleTimeSumAvgStr));
					}
					singleDeviceTimeSexDTO.setPeopleTimeSum(peopleTimeSum);
					resultList.add(singleDeviceTimeSexDTO);
				}
			} else {
				for (SingleDeviceTimeSexDTO singleDeviceTimeSexDTO : list) {
					String sex = singleDeviceTimeSexDTO.getSex();
					SingleDeviceTimeSexDTO dto = new SingleDeviceTimeSexDTO();
					dto.setTime(timeKey);
					dto.setPeopleNumSum(0);
					dto.setPeopleTimeSum(0);
					// 如果性别为男，则需要添加女
					if (ElasticsearchConstant.SEX_MALE.equals(sex)) {
						dto.setSex(ElasticsearchConstant.SEX_FEMALE);
					}
					// 如果性别为女，则需要添加男
					if (ElasticsearchConstant.SEX_FEMALE.equals(sex)) {
						dto.setSex(ElasticsearchConstant.SEX_MALE);
					}
					// 设备数量大于1时，才求平均数据
					if (deviceNum > 1) {
						//人数
						Integer peopleNumSum = singleDeviceTimeSexDTO.getPeopleNumSum();
						if (peopleNumSum == null) {
							peopleNumSum = 0;
						}
						if (peopleNumSum > 0) {
							// 人数平均数
							String peopleNumSumAvgStr = "";
							if (actualDayNum > 1) {
								peopleNumSumAvgStr = df.format((float) peopleNumSum / deviceNum / actualDayNum);
							} else {
								peopleNumSumAvgStr = df.format((float) peopleNumSum / deviceNum);
							}
							peopleNumSum = Math.round(Float.valueOf(peopleNumSumAvgStr));
						}
						singleDeviceTimeSexDTO.setPeopleNumSum(peopleNumSum);

						// 人次
						Integer peopleTimeSum = singleDeviceTimeSexDTO.getPeopleTimeSum();
						peopleTimeSum = peopleTimeSum == null ? 0 : peopleTimeSum;
						if (peopleTimeSum > 0) {
							// 人次平均数
							String peopleTimeSumAvgStr = "";
							if (actualDayNum > 1) {
								peopleTimeSumAvgStr = df.format((float) peopleTimeSum / deviceNum / actualDayNum);
							} else {
								peopleTimeSumAvgStr = df.format((float) peopleTimeSum / deviceNum);
							}
							peopleTimeSum = Math.round(Float.valueOf(peopleTimeSumAvgStr));
						}
						singleDeviceTimeSexDTO.setPeopleTimeSum(peopleTimeSum);
					}
					resultList.add(singleDeviceTimeSexDTO);
					resultList.add(dto);
				}
			}
		}
		// 存在时间集合
		Set<String> timeSet = timeMap.keySet();
		// 获取不存在的时间列表
		List<String> notExistTimeList = DateUtils.getNotExistElementList(BusinessConstant.TIME_POINT_LIST, timeSet);
		if (CollectionUtils.isNotEmpty(notExistTimeList)) {
			List<String> sexList = ElasticsearchConstant.SEX_LIST;
			for (String time : notExistTimeList) {
				// 添加时间段下不存在的性别
				for (String sex : sexList) {
					SingleDeviceTimeSexDTO dto = new SingleDeviceTimeSexDTO();
					dto.setTime(time);
					dto.setSex(sex);
					dto.setPeopleNumSum(0);
					dto.setPeopleTimeSum(0);
					resultList.add(dto);
				}
			}
		}
		// 排序，按时间先后顺序排序
		Collections.sort(resultList, new Comparator<SingleDeviceTimeSexDTO>() {
			@Override
			public int compare(SingleDeviceTimeSexDTO o1, SingleDeviceTimeSexDTO o2) {
				String time = o1.getTime();
				String timeEnd = o2.getTime();
				return SortUtils.sortTime(time, timeEnd);
			}
		});
		return resultList;
	}

	/**
	 * 昨日每小时平均客流人次和客流人数
	 *
	 * @param residenceType 住宅类型
	 * @return List<SingleDeviceTimeDTO>
	 * @throws ServiceException
	 */
	@Override
	public List<SingleDeviceTimeDTO> listDeviceTime(String residenceType) throws ServiceException {
		if (StringUtils.isBlank(residenceType)) {
			throw new SecurityException(MessageConstant.RESIDENCE_TYPE_IS_NULLL);
		}
		// 获取近7天的日期
		List<String> recentDateList = DateUtils.getRecentDayList(7, DateConstant.DATE_YEAR_MONTH_DAY);

		// 商业综合体为写字楼和综合体
		List<String> residenceTypeList = Lists.newArrayList();
		if (BusinessConstant.COMMERCIAL_SYNTHESIS_BUILDING.equals(residenceType)) {
			residenceTypeList.add(BusinessConstant.SYNTHESIS_BUILDING);
			residenceTypeList.add(BusinessConstant.OFFICE_BUILDING);
		} else {
			residenceTypeList.add(residenceType);
		}

		List<SingleDeviceTimeDTO> deviceTimeDTOList = faceDataDeviceTimeService.listDeviceTime(recentDateList, residenceTypeList);
		// 获取住宅类型下设备列表
		List<String> residenceTypeDeviceModelList = faceDataDeviceService.listResidenceTypeDeviceModel(recentDateList, residenceTypeList);
		// 设备数量
		int deviceNum = residenceTypeDeviceModelList.size();

		// 返回列表，
		List<SingleDeviceTimeDTO> returnList = Lists.newArrayList();

		// 保留两位小数
		DecimalFormat df = new DecimalFormat(BusinessConstant.ZERO_POINT_ZERO_ZERO);
		// 替换时间格式和0点到6点的数据
		if (CollectionUtils.isNotEmpty(deviceTimeDTOList)) {

			// 时间Set
			Set<String> timeSet = new HashSet<>();

			Map<String, String> timeMap = BusinessConstant.TIME_MAP;

			for (SingleDeviceTimeDTO deviceTimeDTO : deviceTimeDTOList) {
				String time = deviceTimeDTO.getTime();
				if (StringUtils.isBlank(time)) {
					continue;
				}

				// 如果时间前面是0，把0替换掉,余下字符串数字
				if (time.startsWith(ElasticsearchConstant.STRING_ZERO)) {
					if (time.equals(ElasticsearchConstant.STRING_ZERO_ZERO)) {
						time = ElasticsearchConstant.STRING_ZERO;
					} else {
						time = time.replace(ElasticsearchConstant.STRING_ZERO, "");
					}
				}

				timeSet.add(time);

				// 把余下的转化数字
				Integer timeInt = Integer.valueOf(time);

				// 因为前段只显示6点到23点，如果为6点以前的数据，不计入6点
				if (timeInt <= 5) {
					continue;
				}

				// 重新设置时间
				String timeStr = timeMap.get(time);
				deviceTimeDTO.setTime(timeStr);
				// 设备数量大于1时，才求平均数据
				if (deviceNum > 1) {
					// 人数
					Integer peopleNumSum = deviceTimeDTO.getPeopleNumSum();
					if (peopleNumSum == null) {
						peopleNumSum = 0;
					}
					if (peopleNumSum > 0) {
						// 人数平均数
						String peopleNumSumAvgStr = df.format((float) peopleNumSum / deviceNum);
						peopleNumSum = Math.round(Float.valueOf(peopleNumSumAvgStr));
					}
					deviceTimeDTO.setPeopleNumSum(peopleNumSum);

					// 人次
					Integer peopleTimeSum = deviceTimeDTO.getPeopleTimeSum();
					peopleTimeSum = peopleTimeSum == null ? 0 : peopleTimeSum;
					if (peopleTimeSum > 0) {
						// 人次平均数
						String peopleTimeSumAvgStr = df.format((float) peopleTimeSum / deviceNum);
						peopleTimeSum = Math.round(Float.valueOf(peopleTimeSumAvgStr));
					}
					deviceTimeDTO.setPeopleTimeSum(peopleTimeSum);
				}
				returnList.add(deviceTimeDTO);
			}
			// 获取不存在的时间列表
			List<String> notExistTimeList = DateUtils.getNotExistElementList(BusinessConstant.TIME_MAP.keySet(), timeSet);
			if (CollectionUtils.isNotEmpty(notExistTimeList)) {
				for (String time : notExistTimeList) {
					String timeStr = timeMap.get(time);
					SingleDeviceTimeDTO deviceTimeDto = new SingleDeviceTimeDTO();
					deviceTimeDto.setTime(timeStr);
					deviceTimeDto.setPeopleTimeSum(0);
					deviceTimeDto.setPeopleNumSum(0);
					returnList.add(deviceTimeDto);
				}
			}
		}

		// 排序，按时间先后顺序排序(排序前，已经过滤time为“”或null的情况)
		Collections.sort(returnList, new Comparator<SingleDeviceTimeDTO>() {
			@Override
			public int compare(SingleDeviceTimeDTO o1, SingleDeviceTimeDTO o2) {
				String timeStart = o1.getTime();
				String timeEnd = o2.getTime();
				//替换:00
				timeStart = timeStart.replace(BusinessConstant.TIME_COLON_ZERO_ZERO, "");
				timeEnd = timeEnd.replace(BusinessConstant.TIME_COLON_ZERO_ZERO, "");
				// 将字符串转化为数字
				Integer timeStartNum = Integer.valueOf(timeStart);
				Integer timeEndIntNum = Integer.valueOf(timeEnd);
				return timeStartNum.compareTo(timeEndIntNum);
			}
		});
		return returnList;
	}

}
