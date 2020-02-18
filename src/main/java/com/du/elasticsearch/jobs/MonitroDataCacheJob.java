package com.du.elasticsearch.jobs;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.xinchao.data.cacahe.RedisCacheDao;
import com.xinchao.data.constant.*;
import com.xinchao.data.model.dto.MonitorAgeTypeDTO;
import com.xinchao.data.model.dto.MonitorCommunityWatchDTO;
import com.xinchao.data.model.dto.MonitorWatchTouchDTO;
import com.xinchao.data.service.MonitorDataAdvertisementTimeService;
import com.xinchao.data.service.MonitorDataDeviceService;
import com.xinchao.data.util.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author dxy
 * @date 2019/6/3 11:39
 */
@Component
public class MonitroDataCacheJob {
	private static Logger logger = LoggerFactory.getLogger(MonitroDataCacheJob.class);
	@Autowired
	private RedisCacheDao redisCacheDao;
	@Autowired
	private MonitorDataAdvertisementTimeService monitorDataAdvertisementTimeService;
	@Autowired
	private MonitorDataDeviceService monitorDataDeviceService;

	/**
	 * 将监播数据观看人次、触达人次放入缓存
	 */
	@Scheduled(cron = "0 50 5 * * ?")
	public void pushMonitorWatchTouch() {
		try{
			// 所有缓存数据
			Map<Object, Object> allCache = redisCacheDao.getAllCache(CacheConstant.MONITOR_WATCH_TOUCH);

			// 如果缓存中没有数据，加载所有数据到缓存
			if (MapUtils.isEmpty(allCache)) {
				// 获取所有日期列表
				List<String> dateList = monitorDataAdvertisementTimeService.listActualDate(null);
				if (CollectionUtils.isNotEmpty(dateList)) {
					// 获取今天的日期
					String today = DateUtils.longTimeStampToStringDate(System.currentTimeMillis(), DateConstant.DATE_YEAR_MONTH_DAY);
					for (String date : dateList) {
						// 今天的数据不加载到缓存
						if (StringUtils.isBlank(date) || date.equals(today)) {
							continue;
						}
						List<String> paramDateList = new ArrayList<>();
						paramDateList.add(date);
						List<MonitorWatchTouchDTO> monitorWatchTouchList = monitorDataAdvertisementTimeService.listMonitorWatchTouch(paramDateList);
						if (CollectionUtils.isNotEmpty(monitorWatchTouchList)) {
							redisCacheDao.putCache(CacheConstant.MONITOR_WATCH_TOUCH, date, JSON.toJSONString(monitorWatchTouchList));
						}
					}
				}
			} else {
				// 加载前一天的数据到缓存
				// 获取昨天的日期
				String date = DateUtils.getPastDay(1, DateConstant.DATE_YEAR_MONTH_DAY);
				List<String> paramDateList = new ArrayList<>();
				paramDateList.add(date);
				List<MonitorWatchTouchDTO> monitorWatchTouchList = monitorDataAdvertisementTimeService.listMonitorWatchTouch(paramDateList);
				if (CollectionUtils.isNotEmpty(monitorWatchTouchList)) {
					redisCacheDao.putCache(CacheConstant.MONITOR_WATCH_TOUCH, date, JSON.toJSONString(monitorWatchTouchList));
				}
			}
			logger.info(LoggerConstant.PUSH_MONITOR_WATCH_TOUCH + MessageConstant.PUSHING_DATA_TO_REDIS_IS_NULL);
		} catch (Exception e) {
			logger.error(LoggerConstant.PUSH_MONITOR_WATCH_TOUCH, e);
		}
	}

	/**
	 * 小区观看人次数据
	 */
	@Scheduled(cron = "0 0 6 * * ?")
	public void pushCommunityWatchSum() {
		try{
			// 如果缓存中没有数据，则一次把以前的数据全部加载到缓存
			Map<Object, Object> allCache = redisCacheDao.getAllCache(CacheConstant.MONITOR_COMMUNITY_WATCH);
			if (MapUtils.isEmpty(allCache)) {
				// 获取所有日期
				List<String> dateList = monitorDataDeviceService.listActualDate(null);
				// 当前的日期
				DateUtils.longTimeStampToStringDate(System.currentTimeMillis(), DateConstant.DATE_YEAR_MONTH_DAY);
				for (String date : dateList) {
					List<String> paramList = Lists.newArrayList();
					paramList.add(date);
					List<MonitorCommunityWatchDTO> communityWatchDTOList = monitorDataDeviceService.listMonitorCommunityWatch(paramList);
					// 如果列表不为空，放入缓存
					if (CollectionUtils.isNotEmpty(communityWatchDTOList)) {
						redisCacheDao.putCache(CacheConstant.MONITOR_COMMUNITY_WATCH, date, JSON.toJSONString(communityWatchDTOList));
					}
				}
			} else {
				// 获取昨天的日期
				String date = DateUtils.getPastDay(1, DateConstant.DATE_YEAR_MONTH_DAY);
				List<String> paramList = Lists.newArrayList();
				paramList.add(date);
				List<MonitorCommunityWatchDTO> communityWatchDTOList = monitorDataDeviceService.listMonitorCommunityWatch(paramList);
				// 如果列表不为空，放入缓存
				if (CollectionUtils.isNotEmpty(communityWatchDTOList)) {
					redisCacheDao.putCache(CacheConstant.MONITOR_COMMUNITY_WATCH, date, JSON.toJSONString(communityWatchDTOList));
				}
			}
			logger.info(LoggerConstant.PUSH_COMMUNITY_WATCH_SUM + MessageConstant.PUSHING_DATA_TO_REDIS_IS_NULL);
		} catch (Exception e) {
			logger.error(LoggerConstant.PUSH_COMMUNITY_WATCH_SUM, e);
		}
	}

	/**
	 * 监播年龄段数据
	 */
	@Scheduled(cron = "0 10 6 * * ?")
	public void pushMonitorAgeType() {
		// 中高端住宅
		Map<Object, Object> middleHighCache = redisCacheDao.getAllCache(CacheConstant.MONITOR_AGE_TYPE_MIDDLE_HIGH);
		List<String> middleHighParamList = new ArrayList<>();
		middleHighParamList.add(BusinessConstant.MIDDLE_HIGH_END_RESIDENCE_BUILDING);
		if (middleHighCache == null || middleHighCache.size() == 0) {
			List<String> allDateList = getDateListNoToday();
			pushMonitorAgeTypeBatch(allDateList, middleHighParamList, CacheConstant.MONITOR_AGE_TYPE_MIDDLE_HIGH);
		} else {
			// 获取昨天的日期
			String date = DateUtils.getPastDay(1, DateConstant.DATE_YEAR_MONTH_DAY);
			List<String> paramList = Lists.newArrayList();
			paramList.add(date);
			pushMonitorAgeTypeBatch(paramList, middleHighParamList, CacheConstant.MONITOR_AGE_TYPE_MIDDLE_HIGH);
		}

		// 商住楼
		Map<Object, Object> commercialCache = redisCacheDao.getAllCache(CacheConstant.MONITOR_AGE_TYPE_COMMERCIAL);
		List<String> commercialParamList = new ArrayList<>();
		commercialParamList.add(BusinessConstant.COMMERCIAL_RESIDENTIAL_BUILDING);
		if (commercialCache == null || commercialCache.size() == 0) {
			List<String> allDateList = getDateListNoToday();
			pushMonitorAgeTypeBatch(allDateList, commercialParamList, CacheConstant.MONITOR_AGE_TYPE_COMMERCIAL);
		} else {
			// 获取昨天的日期
			String date = DateUtils.getPastDay(1, DateConstant.DATE_YEAR_MONTH_DAY);
			List<String> paramList = Lists.newArrayList();
			paramList.add(date);
			pushMonitorAgeTypeBatch(paramList, commercialParamList, CacheConstant.MONITOR_AGE_TYPE_COMMERCIAL);
		}

		// 商业综合体
		Map<Object, Object> commercialSynthesisCache = redisCacheDao.getAllCache(CacheConstant.MONITOR_AGE_TYPE_COMMERCIAL_SYNTHESIS);
		ArrayList<String> commercialSynthesisParamList = new ArrayList<>();
		commercialSynthesisParamList.add(BusinessConstant.OFFICE_BUILDING);
		commercialSynthesisParamList.add(BusinessConstant.SYNTHESIS_BUILDING);
		if (commercialSynthesisCache == null || commercialSynthesisCache.size() == 0) {
			List<String> allDateList = getDateListNoToday();
			pushMonitorAgeTypeBatch(allDateList, commercialSynthesisParamList, CacheConstant.MONITOR_AGE_TYPE_COMMERCIAL_SYNTHESIS);
		} else {
			//获取昨天的日期
			String date = DateUtils.getPastDay(1, DateConstant.DATE_YEAR_MONTH_DAY);
			List<String> paramList = Lists.newArrayList();
			paramList.add(date);
			pushMonitorAgeTypeBatch(paramList, commercialSynthesisParamList, CacheConstant.MONITOR_AGE_TYPE_COMMERCIAL_SYNTHESIS);
		}
		logger.info(LoggerConstant.PUSH_MONITOR_AGE_TYPE + MessageConstant.PUSHING_DATA_TO_REDIS_IS_NULL);
	}

	/**
	 * 获取日期列表（不包含今天）
	 * @return List<String>
	 */
	private List<String> getDateListNoToday() {
		// 所有日期
		List<String> allDateList = monitorDataDeviceService.listActualDate(null);
		if (CollectionUtils.isNotEmpty(allDateList)) {
			// 今天的日期
			String today = DateUtils.longTimeStampToStringDate(System.currentTimeMillis(), DateConstant.DATE_YEAR_MONTH_DAY);
			// 今天的数据不加入缓存
			for (String date : allDateList) {
				if (StringUtils.isBlank(date)) {
					continue;
				}
				// 今天数据不放入缓存
				if (date.equals(today)) {
					allDateList.remove(date);
				}
			}
		}
		return allDateList;
	}

	/**
	 * 将监播年龄数据放入缓存
	 * @param dateList 日期列表
	 * @param residenceTypeList 住宅类型
	 * @param cacheName 缓存名
	 */
	private void pushMonitorAgeTypeBatch(List<String> dateList, List<String> residenceTypeList, String cacheName) {
		if (CollectionUtils.isNotEmpty(dateList)) {
			List<String> paramDateList = new ArrayList<>();
			for (String date : dateList) {
				if (StringUtils.isBlank(date)) {
					continue;
				}
				paramDateList.add(date);
				List<MonitorAgeTypeDTO> monitorAgeTypeList = monitorDataDeviceService.listMonitorAgeType(paramDateList, residenceTypeList);
				if (CollectionUtils.isNotEmpty(monitorAgeTypeList)) {
					redisCacheDao.putCache(cacheName, date, JSON.toJSONString(monitorAgeTypeList));
				}
				paramDateList.remove(date);
			}
		}
	}

}
