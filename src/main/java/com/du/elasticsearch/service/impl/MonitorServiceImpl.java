package com.du.elasticsearch.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xinchao.data.cacahe.RedisCacheDao;
import com.xinchao.data.constant.BusinessConstant;
import com.xinchao.data.constant.CacheConstant;
import com.xinchao.data.constant.DateConstant;
import com.xinchao.data.constant.ElasticsearchConstant;
import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.dto.*;
import com.xinchao.data.model.vo.MonitorAgeTypeVO;
import com.xinchao.data.model.vo.MonitorAgeWatchVO;
import com.xinchao.data.model.vo.MonitorDateWatchVO;
import com.xinchao.data.model.vo.SummarizeMonitorVO;
import com.xinchao.data.service.*;
import com.xinchao.data.util.DateUtils;
import com.xinchao.data.util.SortUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

/**
 * @author dxy
 * @date 2019/3/7 15:22
 */
@Service(value = "monitorService")
public class MonitorServiceImpl implements MonitorService {
	@Autowired
	private MonitorDataAgeService monitorDataAgeService;
	@Autowired
	private SummarizeService summarizeService;
	@Autowired
	private MonitorDataService monitorDataService;
	@Autowired
	private MonitorDataDeviceService monitorDataDeviceService;
	@Autowired
	private MonitorDataAdvertisementDateService monitorDataAdvertisementDateService;
	@Autowired
	private MonitorDataAdvertisementTimeService monitorDataAdvertisementTimeService;
	@Autowired
	private DeviceInfoService deviceInfoService;
	@Autowired
	private FaceDataCommunityDeviceService faceDataCommunityDeviceService;
	@Autowired
	private RedisCacheDao redisCacheDao;
	/**
	 * 近7天各年龄段的观看率&观看时长
	 *
	 * @return List<MonitorAgeWatchVO>
	 * @throws ServiceException
	 */
	@Override
	public List<MonitorAgeWatchVO> listMonitorAgeWatch() throws ServiceException {
		// 近7天的日期
		List<String> recentDateList = DateUtils.getRecentDayList(7, DateConstant.DATE_YEAR_MONTH_DAY);
		List<MonitorAgeWatchDTO> monitorAgeWatchDTOList = monitorDataAgeService.listMonitorAgeWatch(recentDateList);
		// 获取实际天数
		List<String> actualDateList = monitorDataAgeService.listActualDate(recentDateList);
		// 返回参数列表
		List<MonitorAgeWatchVO> monitorAgeWatchVOList = Lists.newArrayList();
		// 处理平均观看率、观看时长
		if (CollectionUtils.isNotEmpty(actualDateList)) {
			// 实际天数
			int dayNum = actualDateList.size();

			for (MonitorAgeWatchDTO watchDTO : monitorAgeWatchDTOList) {
				String age = watchDTO.getAge();
				// 如果年龄为“未知”，不计算，也不放入列表，因为“未知”年龄没有触达人次、观看人次、播放时长
				if (StringUtils.isBlank(age) || BusinessConstant.AGE_STRING_UNKNOW.equals(age)) {
					continue;
				}
				// 触达人数
				Double touchSum = watchDTO.getTouchSum();
				// 观看人次
				Integer watchSum = watchDTO.getWatchSum();
				// 播放时长
				Long playDurationSum = watchDTO.getPlayDurationSum();

				// 如果为空，给默认值0
				if (touchSum == null) {
					touchSum = 0.0;
				}
				// 如果为空，给默认值0
				if (watchSum == null) {
					watchSum = 0;
				}
				// 观看率：观看人次/触达人次
				// 计算平均观看率
				float watchRatio = 0.0F;
				DecimalFormat decimalFormat = new DecimalFormat(BusinessConstant.ZERO_POINT_ZERO_ZERO);
				if (touchSum != 0) {
					// 观看人次平均数据
					String watchSumStr = decimalFormat.format(watchSum / dayNum);
					Float watchAvg = Float.valueOf(watchSumStr);
					// 触达人次平均数据
					String touchSumStr = decimalFormat.format(touchSum / dayNum);
					Float touchAvg = Float.valueOf(touchSumStr);
					String watchRatioStr = decimalFormat.format( watchAvg / touchAvg);
					watchRatio = Float.valueOf(watchRatioStr);
				}

				MonitorAgeWatchVO watchVO = new MonitorAgeWatchVO();
				watchVO.setAge(age);
				watchVO.setWatchRatio(watchRatio);
				// 观看时长为毫秒，需要除以1000
				if (playDurationSum == null) {
					playDurationSum = 0L;
				}
				// 平均观看时长
				String playDurationSumStr = decimalFormat.format((double) playDurationSum / 1000 / dayNum);
				watchVO.setWatchDuration(Double.valueOf(playDurationSumStr));
				monitorAgeWatchVOList.add(watchVO);
			}
		}
		return monitorAgeWatchVOList;
	}

	/**
	 * 昨日监播数据
	 * 因为此功能已在SummarizeService中实现，故直接使用
	 *
	 * @return SummarizeMonitorVO
	 * @throws ServiceException
	 */
	@Override
	public SummarizeMonitorVO getSummarizeMonitor() throws ServiceException {
		return summarizeService.getSummarizeMonitor();
	}

	/**
	 * 近7天日均广告平均观看时长
	 *
	 * @return List<MonitorDateWatchVO>
	 * @throws ServiceException
	 */
	@Override
	public List<MonitorDateWatchVO> listMonitorSevenWatch() throws ServiceException {
		// 近7天的日期
		List<String> recentDateList = DateUtils.getRecentDayList(7, DateConstant.DATE_YEAR_MONTH_DAY);
		List<MonitorDateWatchDTO> monitorDateWatchDTOList = monitorDataService.listMonitorDateWatch(recentDateList);
		List<MonitorDateWatchVO> monitorDateWatchVOList = Lists.newArrayList();
		for (MonitorDateWatchDTO dateWatchDTO : monitorDateWatchDTOList) {
			String date = dateWatchDTO.getDate();
			if (StringUtils.isBlank(date)) {
				continue;
			}
			// 观看人次
			Integer watchSum = dateWatchDTO.getWatchSum();
			if (watchSum == null) {
				watchSum = 0;
			}
			// 播放时长
			Long playDurationSum = dateWatchDTO.getPlayDurationSum();
			if (playDurationSum == null) {
				playDurationSum = 0L;
			}
			// 计算平均观看时长
			double avgWatch = 0f;
			if (watchSum != 0) {
				NumberFormat numberFormat = NumberFormat.getInstance();
				numberFormat.setMaximumFractionDigits(2);
				playDurationSum = playDurationSum / 1000;
				String avgWatchStr = numberFormat.format((double) playDurationSum / (double) watchSum);
				avgWatch = Double.valueOf(avgWatchStr);
			}
			MonitorDateWatchVO dateWatchVO = new MonitorDateWatchVO();
			dateWatchVO.setDate(date);
			dateWatchVO.setAvgWatch(avgWatch);
			monitorDateWatchVOList.add(dateWatchVO);
		}
		return monitorDateWatchVOList;
	}

	/**
	 * 各年龄段的人员观看率
	 *
	 * @return Map<String, List<MonitorAgeTypeVO>>
	 */
	@Override
	public Map<String, List<MonitorAgeTypeVO>> getMonitorAgeTypeMap() {
		// 中高端住宅参数列表
		ArrayList<String> middleHighParamList = new ArrayList<>();
		middleHighParamList.add(BusinessConstant.MIDDLE_HIGH_END_RESIDENCE_BUILDING);
		List<MonitorAgeTypeDTO> middleHighList = getMonitorAgeTypeDTOList(CacheConstant.MONITOR_AGE_TYPE_MIDDLE_HIGH, middleHighParamList);


		// 商住楼参数列表
		ArrayList<String> commercialParamList = new ArrayList<>();
		commercialParamList.add(BusinessConstant.COMMERCIAL_RESIDENTIAL_BUILDING);
		List<MonitorAgeTypeDTO> commercialList = getMonitorAgeTypeDTOList(CacheConstant.MONITOR_AGE_TYPE_COMMERCIAL, commercialParamList);

		// 商业综合体参数列表
		ArrayList<String> commercialSynthesisParamList = new ArrayList<>();
		commercialSynthesisParamList.add(BusinessConstant.OFFICE_BUILDING);
		commercialSynthesisParamList.add(BusinessConstant.SYNTHESIS_BUILDING);
		List<MonitorAgeTypeDTO> commercialSynthesisList = getMonitorAgeTypeDTOList(CacheConstant.MONITOR_AGE_TYPE_COMMERCIAL_SYNTHESIS, commercialSynthesisParamList);

		// 中高端住宅,求各个年龄段的看率
		List<MonitorAgeTypeVO> middleVOList = getMonitorAgeTypeVOList(middleHighList, BusinessConstant.MIDDLE_HIGH_END_RESIDENCE_BUILDING);

		// 商住楼,求各个年龄段的看率
		List<MonitorAgeTypeVO> commercialVOList = getMonitorAgeTypeVOList(commercialList, BusinessConstant.COMMERCIAL_RESIDENTIAL_BUILDING);

		// 商业综合体，由于是写字楼和综合体的合体，需要重新按年龄分组
		List<MonitorAgeTypeVO> commercialSynthesisVOList = getMonitorAgeTypeVOList(commercialSynthesisList, BusinessConstant.COMMERCIAL_SYNTHESIS_BUILDING);

		Map<String, List<MonitorAgeTypeVO>> returnMap = Maps.newHashMap();
		returnMap.put(BusinessConstant.MIDDLE_HIGH_END_RESIDENCE_BUILDING, middleVOList);
		returnMap.put(BusinessConstant.COMMERCIAL_RESIDENTIAL_BUILDING, commercialVOList);
		returnMap.put(BusinessConstant.COMMERCIAL_SYNTHESIS_BUILDING, commercialSynthesisVOList);
		return returnMap;
	}

	/**
	 * 获取监播年龄段列表
	 * @param cacheName 缓存名称
	 * @param residenceTypeList 住宅类型列表
	 * @return List<MonitorAgeTypeDTO>
	 */
	private List<MonitorAgeTypeDTO> getMonitorAgeTypeDTOList(String cacheName, List<String> residenceTypeList) {
		// 近7天的日期
		List<String> recentDateList = DateUtils.getRecentDayList(7, DateConstant.DATE_YEAR_MONTH_DAY);
		List<MonitorAgeTypeDTO> cacheList = Lists.newArrayList();
		for (String date : recentDateList) {
			Object cache = redisCacheDao.getCache(cacheName, date);
			if (cache == null) {
				continue;
			}
			List<MonitorAgeTypeDTO> monitorAgeTypeList = JSON.parseArray(cache.toString(), MonitorAgeTypeDTO.class);
			cacheList.addAll(monitorAgeTypeList);
		}
		// 如果缓存中没有数据
		if (CollectionUtils.isEmpty(cacheList)) {
			//中高端住宅列表
			return  monitorDataDeviceService.listMonitorAgeType(recentDateList, residenceTypeList);
		} else {
			List<MonitorAgeTypeDTO> resultList = Lists.newArrayList();
			// 按年龄分组
			Map<String, List<MonitorAgeTypeDTO>> ageMap = Maps.newHashMap();
			List<MonitorAgeTypeDTO> tempList;
			for (MonitorAgeTypeDTO monitorAgeTypeDTO : cacheList) {
				String age = monitorAgeTypeDTO.getAge();
				// 年龄为“未知”，不放入列表中
				if (StringUtils.isBlank(age) || BusinessConstant.AGE_STRING_UNKNOW.equals(age)) {
					continue;
				}
				tempList = ageMap.get(age);
				if (CollectionUtils.isEmpty(tempList)) {
					tempList = Lists.newArrayList();
					ageMap.put(age, tempList);
				}
				tempList.add(monitorAgeTypeDTO);
			}
			// 将同组内的观看人次、触达人次分别累加
			for (Map.Entry<String, List<MonitorAgeTypeDTO>> entry :  ageMap.entrySet()) {
				String age = entry.getKey();
				List<MonitorAgeTypeDTO> dtoList = entry.getValue();
				// 触达人次
				double touchSum = 0;
				// 观看人次
				int wathcSum = 0;
				// 住宅类型

				for (MonitorAgeTypeDTO dto : dtoList ) {
					// 触达人次
					Double touch = dto.getTouchSum();
					if (touch != null) {
						touchSum = touchSum + touch;
					}
					// 观看人次
					Integer watch = dto.getWatchSum();
					if (watch != null) {
						wathcSum = wathcSum + watch;
					}
				}
				MonitorAgeTypeDTO monitorAgeType = new MonitorAgeTypeDTO();
				monitorAgeType.setAge(age);
				monitorAgeType.setWatchSum(wathcSum);
				monitorAgeType.setTouchSum(touchSum);
				resultList.add(monitorAgeType);
			}
			return resultList;
		}
	}

	/**
	 * 获取监播年龄段VO列表
	 * 
	 * @param monitorAgeTypeDTOList List<MonitorAgeTypeDTO> monitorAgeTypeDTOList
	 * @param residenceType 住宅类型
	 * @return List<MonitorAgeTypeVO>
	 */
	private List<MonitorAgeTypeVO> getMonitorAgeTypeVOList(List<MonitorAgeTypeDTO> monitorAgeTypeDTOList, String residenceType) {
		List<MonitorAgeTypeVO> monitorAgeTypeVOList = Lists.newArrayList();
		for (MonitorAgeTypeDTO monitorAgeTypeDTO : monitorAgeTypeDTOList) {
			String age = monitorAgeTypeDTO.getAge();
			// 如果年龄为“未知”，不计算，也不放入列表，因为“未知”年龄没有触达人次、观看人次、播放时长
			if (StringUtils.isBlank(age) || BusinessConstant.AGE_STRING_UNKNOW.equals(age)) {
				continue;
			}
			// 触达人次
			Double exposuresSum = monitorAgeTypeDTO.getTouchSum();
			if (exposuresSum == null) {
				exposuresSum = 0.0;
			}
			// 观看人次
			Integer watchSum = monitorAgeTypeDTO.getWatchSum();
			if (watchSum == null) {
				watchSum = 0;
			}
			float watchRatio = 0.0f;
			// 计算观看率
			if (exposuresSum != 0) {
				DecimalFormat decimalFormat = new DecimalFormat(BusinessConstant.ZERO_POINT_ZERO_ZERO);
				//男性
				String watchRatioStr = decimalFormat.format( watchSum / exposuresSum);
				watchRatio = Float.valueOf(watchRatioStr);
			}
			MonitorAgeTypeVO vo = new MonitorAgeTypeVO();
			vo.setResidenceType(residenceType);
			vo.setAge(monitorAgeTypeDTO.getAge());
			vo.setWatchRatio(watchRatio);
			monitorAgeTypeVOList.add(vo);
		}
		return monitorAgeTypeVOList;
	}
	/**
	 * 近7天日均观看时长top10点位
	 *
	 * @return List<MonitorCommunityWatchDTO>
	 * @throws ServiceException
	 */
	@Override
	public List<MonitorCommunityWatchDTO> listMonitorCommunityWatch() throws ServiceException {
		// 近7天的日期
		List<String> recentDateList = DateUtils.getRecentDayList(7, DateConstant.DATE_YEAR_MONTH_DAY);
		// 监播数据缓存列表
		List<MonitorCommunityWatchDTO> monitorCommunityWatchCacheList = Lists.newArrayList();
		// 实际天数
		int dayNum = 0;

		List<MonitorCommunityWatchDTO> monitorCommunityWatchDTOList = Lists.newArrayList();

		for (String date : recentDateList) {
			Object cache = redisCacheDao.getCache(CacheConstant.MONITOR_COMMUNITY_WATCH, date);
			if (cache == null) {
				continue;
			}
			List<MonitorCommunityWatchDTO> communityWatchDTOList = JSON.parseArray(cache.toString(), MonitorCommunityWatchDTO.class);
			monitorCommunityWatchCacheList.addAll(communityWatchDTOList);
			// 数据存在，实际天数+1
			dayNum++;
		}

		List<MonitorCommunityWatchDTO> communityWatchDTOList = Lists.newArrayList();

		// 如果缓存中没有数据，则从数据库中查询
		if (CollectionUtils.isEmpty(monitorCommunityWatchCacheList)) {
			// 获取所有小区对应的观看时长
			monitorCommunityWatchDTOList = monitorDataDeviceService.listMonitorCommunityWatch(recentDateList);
			// 获取具体天数，如日期列表有7天，但是有可能时间天数只有2天
			List<String> actualDateList = monitorDataDeviceService.listActualDate(recentDateList);
			// 实际天数
			dayNum = actualDateList.size();
		} else {
			// 按小区分组
			// key: 小区； value: List<MonitorCommunityWatchDTO>
			Map<String, List<MonitorCommunityWatchDTO>> communityWatchMap = Maps.newHashMap();
			List<MonitorCommunityWatchDTO> tempList;
			for (MonitorCommunityWatchDTO monitorCommunityWatch :monitorCommunityWatchCacheList) {
				String community = monitorCommunityWatch.getCommunity();
				if (StringUtils.isBlank(community)) {
					continue;
				}
				tempList = communityWatchMap.get(community);
				if (CollectionUtils.isEmpty(tempList)) {
					tempList = Lists.newArrayList();
					communityWatchMap.put(community, tempList);
				}
				tempList.add(monitorCommunityWatch);
			}
			// 将同一个小区里观看人次累加
			for (Map.Entry<String, List<MonitorCommunityWatchDTO>> entry : communityWatchMap.entrySet()) {
				// 小区
				String key = entry.getKey();
				// 观看人次总数
				float watchSum = 0;
				List<MonitorCommunityWatchDTO> tempDtoList = entry.getValue();
				MonitorCommunityWatchDTO  monitorCommunityWatchDTO = new MonitorCommunityWatchDTO();
				monitorCommunityWatchDTO.setCommunity(key);
				for (MonitorCommunityWatchDTO tempDto : tempDtoList) {
					Float watch = tempDto.getWatchSum();
					if (watch == null) {
						continue;
					}
					watchSum = watchSum + watch;
				}
				monitorCommunityWatchDTO.setWatchSum(watchSum);
				monitorCommunityWatchDTOList.add(monitorCommunityWatchDTO);
			}
		}

		// 获取小区列表
		List<String> communityList = new ArrayList<>();
		for (MonitorCommunityWatchDTO dto: monitorCommunityWatchDTOList) {
			String community = dto.getCommunity();
			if (StringUtils.isBlank(community)){
				continue;
			}
			communityList.add(community);
		}

		// 获取小区对应的终端数量
		Map<String, Integer> communityDeviceNumMap = faceDataCommunityDeviceService.getCommunityDeviceNumMap(recentDateList, communityList);

		// 获取小区对应城市Map
		Map<Object, Object> communityCityMap = redisCacheDao.getAllCache(CacheConstant.COMMUNITY_CITY);
		// 缓存中不存在，从数据库中获取
		if (communityCityMap == null || communityCityMap.size() == 0) {
			// 获取小区对应的城市Map（此处数据过大，在查询是，不放入缓存）
			communityCityMap = deviceInfoService.getDeviceCommunityCityMap();
		}

		// 保留两位小数
		DecimalFormat df = new DecimalFormat(BusinessConstant.ZERO_POINT_ZERO_ZERO);
		// 如果小区名称为空，不放入返回列表中
		for (MonitorCommunityWatchDTO monitorCommunityWatchDTO : monitorCommunityWatchDTOList) {
			String community = monitorCommunityWatchDTO.getCommunity();
			if (StringUtils.isBlank(community)) {
				continue;
			}
			Float watchSum = monitorCommunityWatchDTO.getWatchSum();
			if (watchSum == null) {
				watchSum = 0F;
			}

			// 获取小区对应的终端数量
			Integer deviceNum = communityDeviceNumMap.get(community);
			if (deviceNum == null) {
				deviceNum = 0;
			}
			
			if (dayNum > 1) {
				float watchAvg = 0;
				// 转化为秒,求天数的平均数
				watchAvg = watchSum / 1000 / dayNum;
				// 求每台设备的平均数
				if (deviceNum > 1) {
					watchAvg = watchAvg / deviceNum;
				}
				String watchAvgStr = df.format(watchAvg);
				watchSum = Float.valueOf(watchAvgStr);
			}
			monitorCommunityWatchDTO.setWatchSum(watchSum);
			// 获取小区对应的城市名
			Object city = communityCityMap.get(community);
			if (city == null) {
				city = "";
			}
			// 重新设置小区名=城市名+小区名
			monitorCommunityWatchDTO.setCommunity(city + community);
			communityWatchDTOList.add(monitorCommunityWatchDTO);

		}
		// 按平均观看时长排序
		Collections.sort(communityWatchDTOList, new Comparator<MonitorCommunityWatchDTO>() {
			@Override
			public int compare(MonitorCommunityWatchDTO o1, MonitorCommunityWatchDTO o2) {
				Float watchSumFrom = o1.getWatchSum();
				Float watchSumTo = o2.getWatchSum();
				return watchSumTo.compareTo(watchSumFrom);
			}
		});
		// 取平均观看时长前10
		if (communityWatchDTOList.size() > 10) {
			return communityWatchDTOList.subList(0, 9);
		}
		return communityWatchDTOList;
	}

	/**
	 * 近7天观看次数TOP10广告
	 *
	 * @return List<MonitorAdvertisementWatchDTO>
	 * @throws ServiceException
	 */
	@Override
	public List<MonitorAdvertisementWatchDTO> listMonitorAdvertisementWatch() throws ServiceException {
		//近7天的日期
		List<String> recentDateList = DateUtils.getRecentDayList(7, DateConstant.DATE_YEAR_MONTH_DAY);
		return monitorDataAdvertisementDateService.listMonitorAdvertisementWatch(recentDateList, BusinessConstant.TOP_SIZE);
	}

	/**
	 * 单广告近7天每小时平均：观看人次&触达人次
	 *
	 * @return List<MonitorWatchTouchDTO>
	 * @throws ServiceException
	 */
	@Override
	public List<MonitorWatchTouchDTO> listMonitorWatchTouch() throws ServiceException {
		// 近7天的日期
		List<String> recentDateList = DateUtils.getRecentDayList(7, DateConstant.DATE_YEAR_MONTH_DAY);
		// 脚脖数据缓存列表
		List<MonitorWatchTouchDTO> monitorWatchTouchCacheList = Lists.newArrayList();
		// 监播数据列表
		List<MonitorWatchTouchDTO> monitorWatchTouchDTOList = Lists.newArrayList();

		// 实际天数
		int dayNum = 0;

		// 从缓存中获取数据
		for (String date : recentDateList) {
			Object cache = redisCacheDao.getCache(CacheConstant.MONITOR_WATCH_TOUCH, date);
			if (cache == null) {
				continue;
			}
			List<MonitorWatchTouchDTO> monitorWatchTouchs = JSON.parseArray(cache.toString(), MonitorWatchTouchDTO.class);
			monitorWatchTouchCacheList.addAll(monitorWatchTouchs);
			// 如果缓存中数据不为空，实际天数+1
			dayNum ++;
		}

		// 缓存没有数据，从数据库中查询
		if (CollectionUtils.isEmpty(monitorWatchTouchCacheList)) {
			monitorWatchTouchDTOList = monitorDataAdvertisementTimeService.listMonitorWatchTouch(recentDateList);
			// 实际日期天数
			List<String> actualDateList = monitorDataAdvertisementTimeService.listActualDate(recentDateList);
			// 实际天数
			dayNum = actualDateList.size();
		} else {
			// key:time, value:List<MonitorWatchTouchDTO>
			Map<String, List<MonitorWatchTouchDTO>> timeMonitorWatchTouchMap = Maps.newHashMap();
			List<MonitorWatchTouchDTO> tempList;
			// 如过缓存中有数据，需要对列表职工的数据，按time字段分组
			for (MonitorWatchTouchDTO monitorWatchTouchDTO : monitorWatchTouchCacheList) {
				String timeKey = monitorWatchTouchDTO.getTime();
				if (StringUtils.isBlank(timeKey)) {
					continue;
				}
				tempList = timeMonitorWatchTouchMap.get(timeKey);
				if (CollectionUtils.isEmpty(tempList)) {
					tempList = Lists.newArrayList();
					timeMonitorWatchTouchMap.put(timeKey, tempList);
				}
				tempList.add(monitorWatchTouchDTO);
			}
			// 将同组内的人数、人次累加
			for (Map.Entry<String, List<MonitorWatchTouchDTO>> entry : timeMonitorWatchTouchMap.entrySet()) {
				// 时间
				String time = entry.getKey();

				MonitorWatchTouchDTO tempDto = new MonitorWatchTouchDTO();
				tempDto.setTime(time);
				// 触达人次
				int touchSum =0;
				// 观看人次
				int watchSum = 0;
				List<MonitorWatchTouchDTO> dtoList = entry.getValue();

				for (MonitorWatchTouchDTO dto: dtoList) {
					// 触达人次
					Integer touchNum = dto.getTouchSum();
					if (touchNum == null) {
						touchNum = 0;
					}
					touchSum = touchSum + touchNum;
					// 观看人次
					Integer watchNum = dto.getWatchSum();
					if (watchNum == null) {
						watchNum = 0;
					}
					watchSum = watchSum + watchNum;
				}
				tempDto.setWatchSum(watchSum);
				tempDto.setTouchSum(touchSum);
				monitorWatchTouchDTOList.add(tempDto);
			}
		}

		// 返回列表
		List<MonitorWatchTouchDTO> resultList = new ArrayList<>();

		// 用于存放列表中存在的时间
		List<String> existTimeList = new ArrayList<>();

		// 保留两位小数
		DecimalFormat df = new DecimalFormat(BusinessConstant.ZERO_POINT_ZERO_ZERO);

		for (MonitorWatchTouchDTO watchTouchDTO : monitorWatchTouchDTOList) {
			String time = watchTouchDTO.getTime();
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
			// 如果实际天数大于1，求每个小时的平均数=每个小时总数/实际天数
			if (dayNum > 1) {
				Integer touchSum = watchTouchDTO.getTouchSum();
				if (touchSum == null) {
					touchSum = 0;
				}
				String touchSumStr = df.format((float) touchSum / dayNum);
				touchSum = Math.round(Float.valueOf(touchSumStr));

				watchTouchDTO.setTouchSum(touchSum);

				Integer watchSum = watchTouchDTO.getWatchSum();
				if (watchSum == null) {
					watchSum = 0;
				}
				String watchSumStr = df.format((float) watchSum / dayNum);
				watchSum = Math.round(Float.valueOf(watchSumStr));
				watchTouchDTO.setWatchSum(watchSum);
			}
			existTimeList.add(time);
			watchTouchDTO.setTime(time);
			resultList.add(watchTouchDTO);
		}

		// 基本时间集合
		Set<String> stringSet = BusinessConstant.TIME_MAP.keySet();

		// 获取不存在的时间列表
		List<String> notExistTimeList = DateUtils.getNotExistElementList(stringSet, existTimeList);
		if (CollectionUtils.isNotEmpty(notExistTimeList)) {
			for (String time : notExistTimeList) {
				MonitorWatchTouchDTO monitorWatchTouchDTO = new MonitorWatchTouchDTO();
				monitorWatchTouchDTO.setTime(time);
				monitorWatchTouchDTO.setWatchSum(0);
				monitorWatchTouchDTO.setTouchSum(0);
				resultList.add(monitorWatchTouchDTO);
			}
		}
		// 按时间排序
		Collections.sort(resultList, new Comparator<MonitorWatchTouchDTO>() {
			@Override
			public int compare(MonitorWatchTouchDTO o1, MonitorWatchTouchDTO o2) {
				String time = o1.getTime();
				String timeEnd = o2.getTime();
				return  SortUtils.sortTime(time, timeEnd);
			}
		});
		// 重新设置时间
		for (MonitorWatchTouchDTO watchTouch : resultList) {
			String time = watchTouch.getTime();
			String timeStr = BusinessConstant.TIME_MAP.get(time);
			watchTouch.setTime(timeStr);
		}
		return resultList;
	}
}
