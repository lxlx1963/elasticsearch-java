package com.du.elasticsearch.service.impl;

import com.google.common.collect.Lists;
import com.xinchao.data.cacahe.RedisCacheDao;
import com.xinchao.data.constant.BusinessConstant;
import com.xinchao.data.constant.CacheConstant;
import com.xinchao.data.constant.DateConstant;
import com.xinchao.data.constant.ElasticsearchConstant;
import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.dto.*;
import com.xinchao.data.model.vo.SummarizeMonitorVO;
import com.xinchao.data.model.vo.SummarizeRecentSevenVO;
import com.xinchao.data.model.vo.SummarizeTopTenVO;
import com.xinchao.data.service.*;
import com.xinchao.data.util.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author dxy
 * @date 2019/3/6 10:31
 */
@Service(value = "summarizeService")
public class SummarizeServiceImpl implements SummarizeService {
	@Autowired
	private FaceDataSexService faceDataSexService;
	@Autowired
	private FaceDataAgeService faceDataAgeService;
	@Autowired
	private FaceDataCommunityService faceDataCommunityService;
	@Autowired
	private MonitorDataService monitorDataService;
	@Autowired
	private FaceDataProvinceService faceDataProvinceService;
	@Autowired
	private DeviceInfoService deviceInfoService;
	@Autowired
	private FaceDataDateService faceDataDateService;
	@Autowired
	private FaceDataDeviceService faceDataDeviceService;
	@Autowired
	private FaceDataCommunityDeviceService faceDataCommunityDeviceService;
	@Autowired
	private RedisCacheDao redisCacheDao;

	/**
	 * 男女比列表
	 *
	 * @return List<SummarizeMaleFemaleDTO>
	 * @throws ServiceException
	 */
	@Override
	public List<SummarizeMaleFemaleDTO> listMaleFemaleRatio() throws ServiceException {
		// 获取近7天的日期
		List<String> recentDateList = DateUtils.getRecentDayList(7, DateConstant.DATE_YEAR_MONTH_DAY);

		List<FaceDataSexDTO> faceDataSexDTOList = faceDataSexService.listFaceDataSexDTO(recentDateList);
		// 获取实际日期列表
		List<String> actualDateList = faceDataSexService.listActualDate(recentDateList);
		// 实际天数
		int actualDayNum = actualDateList.size();

		List<SummarizeMaleFemaleDTO> maleFemaleDTOList = Lists.newArrayList();
		if (CollectionUtils.isEmpty(faceDataSexDTOList)) {
			// 男
			SummarizeMaleFemaleDTO maleDto = new SummarizeMaleFemaleDTO();
			maleDto.setSex(ElasticsearchConstant.SEX_MALE);
			maleDto.setRatio(BusinessConstant.FLOAT_ZERO);
			maleDto.setPeopleNum(0);
			maleFemaleDTOList.add(maleDto);
			// 女
			SummarizeMaleFemaleDTO femaleDto = new SummarizeMaleFemaleDTO();
			femaleDto.setSex(ElasticsearchConstant.SEX_FEMALE);
			femaleDto.setRatio(BusinessConstant.FLOAT_ZERO);
			femaleDto.setPeopleNum(0);
			maleFemaleDTOList.add(femaleDto);
		} else {
			// 男性人数
			Integer malePeopleNum = 0;
			// 女性人数
			Integer femalePeopleNum = 0;

			// 计算男性、女性人数（不需要把性别未知的人数算入男、女人数中）
			for (FaceDataSexDTO faceDataSexDTO : faceDataSexDTOList) {
				String sex = faceDataSexDTO.getSex();
				if (StringUtils.isBlank(sex)) {
					continue;
				}
				switch (sex) {
					case ElasticsearchConstant.SEX_MALE:
						Integer maleNum = faceDataSexDTO.getPeopleNum();
						if (maleNum == null) {
							maleNum = 0;
						}
						malePeopleNum = malePeopleNum + maleNum;
						break;
					case ElasticsearchConstant.SEX_FEMALE:
						Integer femaleNum = faceDataSexDTO.getPeopleNum();
						if (femaleNum == null) {
							femaleNum = 0;
						}
						femalePeopleNum = femalePeopleNum + femaleNum;
						break;
					default:
						break;
				}
			}

			// 总数
			int totalNum =  malePeopleNum + femalePeopleNum;

			// 男性
			SummarizeMaleFemaleDTO summarizeMaleDTO = new SummarizeMaleFemaleDTO();
			summarizeMaleDTO.setSex(ElasticsearchConstant.SEX_MALE);
			// 女性
			SummarizeMaleFemaleDTO summarizeFemaleDTO = new SummarizeMaleFemaleDTO();
			summarizeFemaleDTO.setSex(ElasticsearchConstant.SEX_FEMALE);
			if (totalNum == 0) {
				summarizeMaleDTO.setRatio(BusinessConstant.FLOAT_ZERO);
				summarizeFemaleDTO.setRatio(BusinessConstant.FLOAT_ZERO);
			} else {
				DecimalFormat decimalFormat = new DecimalFormat(BusinessConstant.ZERO_POINT_ZERO_ZERO);
				// 男性
				float malePeople = malePeopleNum.floatValue();
				String maleStr = decimalFormat.format(malePeople / totalNum);
				summarizeMaleDTO.setRatio(Float.valueOf(maleStr));
				// 男性平均数
				if (actualDayNum > 1) {
					int male = Math.round(malePeople / actualDayNum);
					summarizeMaleDTO.setPeopleNum(male);
				} else {
					summarizeMaleDTO.setPeopleNum(malePeopleNum);
				}

				// 女性
				float femalePeople = femalePeopleNum.floatValue();
				String femaleStr = decimalFormat.format(femalePeople / totalNum);
				summarizeFemaleDTO.setRatio(Float.valueOf(femaleStr));
				// 女性平均数
				if (actualDayNum > 1) {
					int female = Math.round(femalePeople / actualDayNum);
					summarizeFemaleDTO.setPeopleNum(female);
				} else {
					summarizeFemaleDTO.setPeopleNum(femalePeopleNum);
				}

			}
			maleFemaleDTOList.add(summarizeMaleDTO);
			maleFemaleDTOList.add(summarizeFemaleDTO);
		}
		return maleFemaleDTOList;
	}

	/**
	 * 年龄人数列表
	 *
	 * @return List<FaceDataAgeDTO>
	 * @throws ServiceException
	 */
	@Override
	public List<FaceDataAgeDTO> listAgePeopleNum() throws ServiceException {
		// 获取近7天的日期
		List<String> recentDateList = DateUtils.getRecentDayList(7, DateConstant.DATE_YEAR_MONTH_DAY);
		// 实际日期列表
		List<String> actualDateList = faceDataAgeService.listActualDate(recentDateList);
		// 实际天数
		int actualDayNum = actualDateList.size();
		
		List<FaceDataAgeDTO> faceDataAgeDTOList = faceDataAgeService.listFaceDataAgeDTO(recentDateList);

		// 当实际天数大于1时，才求平均数
		if (actualDayNum > 1 && CollectionUtils.isNotEmpty(faceDataAgeDTOList)) {
			for (FaceDataAgeDTO faceDataAgeDTO : faceDataAgeDTOList) {
				Integer peopleNumSum = faceDataAgeDTO.getPeopleNumSum();
				if (peopleNumSum == null) {
					faceDataAgeDTO.setPeopleNumSum(0);
				} else {
					// 人数=总数/实际天数
					float peopleNum = peopleNumSum.floatValue() / actualDayNum;
					int people = Math.round(peopleNum);
					faceDataAgeDTO.setPeopleNumSum(people);
				}
			}
		}
		return faceDataAgeDTOList;
	}

	/**
	 * 近7天客流人数列表
	 *
	 * @return List<SummarizeRecentSevenVO>
	 * @throws ServiceException
	 */
	@Override
	public List<SummarizeRecentSevenVO> listSummarizeRecentSevenDTO() throws ServiceException {
		// 近7天的日期
		List<String> recentDayList = DateUtils.getRecentDayList(7, DateConstant.DATE_YEAR_MONTH_DAY);
		List<DeviceDatePeopleSumDTO> deviceDatePeopleSumList = faceDataDeviceService.listDeviceDatePeopleSum(recentDayList);
		// 返回列表
		List<SummarizeRecentSevenVO> resultList = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(deviceDatePeopleSumList)) {
			// 保留两位小数
			DecimalFormat df = new DecimalFormat(BusinessConstant.ZERO_POINT_ZERO_ZERO);
			// 获取日期对应的机器数量
			Map<String, Integer> dateDeviceNumMap = faceDataProvinceService.getDateDeviceNumMap(recentDayList);
			// 每一个每台设备的平均数，客流总数/设备数量 = 平均数
			for (DeviceDatePeopleSumDTO dto : deviceDatePeopleSumList) {
				String date = dto.getDate();
				Integer peopleNumSum = dto.getPeopleSum();
				if (StringUtils.isBlank(date) || peopleNumSum == null) {
					continue;
				}
				SummarizeRecentSevenVO vo = new SummarizeRecentSevenVO();
				vo.setDate(date);
				// 获取每天的设备台数
				Integer deviceNum = dateDeviceNumMap.get(date);
				if (deviceNum == null || deviceNum == 0) {
					vo.setPeopleNumSum((float)peopleNumSum);
				} else {
					// 获取每台设备客流平均数，客流总数/设备数量 = 平均数
					String deviceNumAvg = df.format((float) peopleNumSum / deviceNum);
					vo.setPeopleNumSum(Float.valueOf(deviceNumAvg));
				}
				resultList.add(vo);
			}
		}
		return resultList;
	}

	/**
	 * 人数top10小区
	 *
	 * @return List<SummarizeTopTenVO>
	 * @throws ServiceException
	 */
	@Override
	public List<SummarizeTopTenVO> listPeopleNumTopTen() throws ServiceException {
		// 近7天的日期
		List<String> recentDayList = DateUtils.getRecentDayList(7, DateConstant.DATE_YEAR_MONTH_DAY);
		List<SummarizeTopTenVO> voList = new ArrayList<>();

		List<SummarizeTopTenDTO> summarizeTopTenDTOList = faceDataCommunityService.listSummarizeTopTenDTO(recentDayList);

		// 小区列表
		List<String> communityList = new ArrayList<>();
		//获取出小区列表
		for (SummarizeTopTenDTO summarizeTopTenDTO : summarizeTopTenDTOList) {
			String community = summarizeTopTenDTO.getCommunity();
			if (StringUtils.isBlank(community)) {
				continue;
			}
			communityList.add(community);
		}

		// 获取小区对应的终端数量Map
		Map<String, Integer> communityDeviceNumMap = faceDataCommunityDeviceService.getCommunityDeviceNumMap(recentDayList, communityList);

		// 获取在实际日期的日期
		List<String> dateList = faceDataCommunityService.listActualDate(recentDayList);

		// 从缓存中获取小区对应城市
		Map<Object, Object> communityCityMap = redisCacheDao.getAllCache(CacheConstant.COMMUNITY_CITY);
		// 如果缓存为空，从数据库查询
		if (communityCityMap == null  || communityCityMap.size() == 0) {
			// 获取小区对应的城市Map（此处数据过大，在查询是，不放入缓存）
			communityCityMap = deviceInfoService.getDeviceCommunityCityMap();
		}

		// 求平均数据
		if (CollectionUtils.isNotEmpty(summarizeTopTenDTOList)) {
			DecimalFormat df = new DecimalFormat(BusinessConstant.ZERO_POINT_ZERO_ZERO);
			// 实际天数
			int dayNum = dateList.size();
			// 求每一个小区人数的平均数，平均数 = 人数 / 实际天数
			for (SummarizeTopTenDTO summarizeTopTenDTO : summarizeTopTenDTOList) {
				String community = summarizeTopTenDTO.getCommunity();
				if (StringUtils.isBlank(community)) {
					continue;
				}
				// 人数
				Integer peopleNumSum = summarizeTopTenDTO.getPeopleNumSum();
				if (peopleNumSum == null) {
					peopleNumSum = 0;
				}
				SummarizeTopTenVO vo = new SummarizeTopTenVO();
				// 获取哦小区对应的城市
				Object city = communityCityMap.get(community);
				if (city == null) {
					city = "";
				}
				// 设置小区名称=城市名+小区名
				vo.setCommunity(city + community);

				// 获取小区对应的终端数据量
				Integer deviceSum = communityDeviceNumMap.get(community);
				if (deviceSum == null) {
					deviceSum = 0;
				}
				vo.setDeviceSum(deviceSum);

				// 如果实际天数大于1，才求平均数
				String peopleNumAvgStr = df.format((float) peopleNumSum / dayNum);
				vo.setPeopleNumAvg(Float.valueOf(peopleNumAvgStr));
				voList.add(vo);
			}
		}
		return voList;
	}

	/**
	 * 客流人数
	 *
	 * @return SummarizePeopleNumDTO
	 * @throws ServiceException
	 */
	@Override
	public SummarizePeopleNumDTO getSummarizePeopleNumDTO() throws ServiceException {
		// 获取昨天的日期
		String date = DateUtils.getPastDay(1, DateConstant.DATE_YEAR_MONTH_DAY);
		List<String> dateList = new ArrayList<>();
		dateList.add(date);

		SummarizePeopleNumDTO summarizePeopleNumDTO = new SummarizePeopleNumDTO();
		FaceDataDateDTO faceDataDateDTO = faceDataDateService.getFaceDataDateDTO(dateList);
		if (faceDataDateDTO != null) {
			// 人数
			Integer peopleNum = faceDataDateDTO.getPeopleNum();
			summarizePeopleNumDTO.setPeopleNumSum(peopleNum);
			// 人次
			Integer peopleTime = faceDataDateDTO.getPeopleTime();
			summarizePeopleNumDTO.setPeopleTimeSum(peopleTime);
			// 城市数量
			List<String> cityList = faceDataCommunityDeviceService.listCity(date);
			summarizePeopleNumDTO.setCityNum(cityList.size());
			// 终端数量
			List<String> deviceModelList = faceDataCommunityDeviceService.listDeviceCode(date);
			summarizePeopleNumDTO.setDeviceNum(deviceModelList.size());
		}
		return summarizePeopleNumDTO;
	}

	/**
	 * 监播数据
	 *
	 * @return SummarizeMonitorDTO
	 * @throws ServiceException
	 */
	@Override
	public SummarizeMonitorVO getSummarizeMonitor() throws ServiceException {
		// 近7天的日期
		List<String> recentDayList = DateUtils.getRecentDayList(7, DateConstant.DATE_YEAR_MONTH_DAY);
		MonitorDataDTO monitorDataDTO = monitorDataService.getMonitorDataDTO(recentDayList);

		// 近7天的日期获取实际日期列表（如果不到7天，只算实际天数）
		List<String> actualDateList = monitorDataService.listActualDate(recentDayList);
		// 实际天数
		int actualDateSize = actualDateList.size();

		// 获取客流人次、人数
		FaceDataDateDTO faceDataDateDTO = faceDataDateService.getFaceDataDateDTO(recentDayList);

		SummarizeMonitorVO summarizeMonitorVO = new SummarizeMonitorVO();
		
		DecimalFormat decimalFormat = new DecimalFormat(BusinessConstant.ZERO_POINT_ZERO_ZERO);
		
		// 日均出现次数=客流人次/客流人数
		if (faceDataDateDTO == null) {
			summarizeMonitorVO.setDayAvgPeopleTime(BusinessConstant.FLOAT_ZERO);
		} else {
			Integer peopleNum = faceDataDateDTO.getPeopleNum();
			Integer peopleTime = faceDataDateDTO.getPeopleTime();
			if (peopleNum > 0 && peopleTime != null) {
				float avg = peopleTime.floatValue() / peopleNum;
				String avgPeopleTime = decimalFormat.format(avg);
				summarizeMonitorVO.setDayAvgPeopleTime(Float.valueOf(avgPeopleTime));
			} else {
				summarizeMonitorVO.setDayAvgPeopleTime(BusinessConstant.FLOAT_ZERO);
			}
		}

		if (monitorDataDTO == null) {
			summarizeMonitorVO.setExposuresSum(0);
			summarizeMonitorVO.setTouchNumSum(0);
			summarizeMonitorVO.setTouchRatio(BusinessConstant.FLOAT_ZERO);
			summarizeMonitorVO.setWatchRatio(BusinessConstant.FLOAT_ZERO);
			summarizeMonitorVO.setWatchAvg(BusinessConstant.FLOAT_ZERO);
		} else {
			// 曝光人次
			Double exposuresSum = monitorDataDTO.getExposuresSum();
			// 触达人次
			Integer touchSum = monitorDataDTO.getTouchSum();
			// 观看人次
			Integer watchSum = monitorDataDTO.getWatchSum();
			// 播放时长
			Long playDurationSum = monitorDataDTO.getPlayDurationSum();

			// 如果为空，设置默认值
			if (exposuresSum == null) {
				exposuresSum = 0D;
			}

			if (touchSum == null) {
				touchSum = 0;
			}

			if (watchSum == null) {
				watchSum = 0;
			}
			if (playDurationSum == null) {
				playDurationSum = 0L;
			}

			// 计算触达率和观看率
			if (exposuresSum == 0) {
				summarizeMonitorVO.setTouchRatio(BusinessConstant.FLOAT_ZERO);
				summarizeMonitorVO.setWatchRatio(BusinessConstant.FLOAT_ZERO);
			} else {
				// 触达率：广告播放100次中，有多少次有人；即播放中有人的次数（只要有一个人都算一次）/播放总次数
				String touchRatio = decimalFormat.format((float)touchSum / exposuresSum);
				summarizeMonitorVO.setTouchRatio(Float.valueOf(touchRatio));
				// 观看率：观看人次/触达人次
				if (touchSum == 0) {
					summarizeMonitorVO.setWatchRatio(BusinessConstant.FLOAT_ZERO);
				} else {
					String watchRatio = decimalFormat.format( (float)watchSum / touchSum);
					summarizeMonitorVO.setWatchRatio(Float.valueOf(watchRatio));
				}
			}
			// 平均观看时长：观看该广告的平均时长     总观看时间/观看人数
			if (watchSum == 0) {
				summarizeMonitorVO.setWatchAvg(BusinessConstant.FLOAT_ZERO);
			} else {
				String watchAvg = decimalFormat.format( playDurationSum.doubleValue() / 1000 /  watchSum);
				summarizeMonitorVO.setWatchAvg(Float.valueOf(watchAvg));
			}

			// 平均曝光次数 = 总曝光次数/实际天数
			int exposures = 0;
			if (exposuresSum != 0) {
				if (actualDateSize > 1) {
					long round = Math.round(exposuresSum / actualDateSize);
					exposures = Integer.valueOf(String.valueOf(round));
				} else {
					exposures = exposuresSum.intValue();
				}
			}
			summarizeMonitorVO.setExposuresSum(exposures);

			// 平均触达人次=总触达人次/实际天数
			if (touchSum != 0 && actualDateSize > 1) {
				long round = Math.round(Float.valueOf(String.valueOf(touchSum)) / actualDateSize);
				touchSum = Integer.valueOf(String.valueOf(round));
			}
			summarizeMonitorVO.setTouchNumSum(touchSum);
		}
		return summarizeMonitorVO;
	}

	/**
	 * 获取汇总省人脸数据
	 *
	 * @return List<SummarizeFaceDataProvinceDTO>
	 * @throws ServiceException
	 */
	@Override
	public List<SummarizeFaceDataProvinceDTO> listSummarizeFaceDataProvinceDTO() throws ServiceException {
		// 获取昨天的日期
		String date = DateUtils.getPastDay(1, DateConstant.DATE_YEAR_MONTH_DAY);
		List<SummarizeFaceDataProvinceDTO> summarizeFaceDataProvinceDTOList = faceDataProvinceService.listSummarizeFaceDataProvinceDTO(date);
		for (SummarizeFaceDataProvinceDTO summarizeFaceDataProvinceDTO : summarizeFaceDataProvinceDTOList) {
			Integer cityNum = summarizeFaceDataProvinceDTO.getCityNum();
			if (cityNum == null) {
				cityNum = 0;
			}
			summarizeFaceDataProvinceDTO.setCityNum(cityNum);

			Integer communityNum = summarizeFaceDataProvinceDTO.getCommunityNum();
			summarizeFaceDataProvinceDTO.setCommunityNum(communityNum == null ? 0 : communityNum);

			Integer deviceNum = summarizeFaceDataProvinceDTO.getDeviceNum();
			summarizeFaceDataProvinceDTO.setDeviceNum(deviceNum == null ? 0 : deviceNum);

			Integer femaleNum = summarizeFaceDataProvinceDTO.getFemaleNum();
			femaleNum = femaleNum == null ? 0 : femaleNum;
			summarizeFaceDataProvinceDTO.setFemaleNum(femaleNum);

			Integer maleNum = summarizeFaceDataProvinceDTO.getMaleNum();
			maleNum = maleNum == null ? 0 : maleNum;
			summarizeFaceDataProvinceDTO.setMaleNum(maleNum);

			Integer peopleNum = summarizeFaceDataProvinceDTO.getPeopleNum();
			summarizeFaceDataProvinceDTO.setPeopleNum(peopleNum == null ? 0 : peopleNum);

		}
		return summarizeFaceDataProvinceDTOList;
	}
}
