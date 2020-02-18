package com.du.elasticsearch.jobs;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.xinchao.data.cacahe.RedisCacheDao;
import com.xinchao.data.config.CustomProperties;
import com.xinchao.data.constant.*;
import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.*;
import com.xinchao.data.model.dto.DeviceInfoDTO;
import com.xinchao.data.service.*;
import com.xinchao.data.util.DateUtils;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.TermsAggregation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 处理监播数据Job
 *
 * @author dxy
 * @date 2019/3/2 8:50
 */
@Component
public class MonitorDataJob {
	private static Logger logger = LoggerFactory.getLogger(MonitorDataJob.class);
	@Autowired
	private ElasticsearchService elasticsearchService;
	@Autowired
	private MonitorDataAdvertisementService monitorDataAdvertisementService;
	@Autowired
	private MonitorDataAdvertisementDateService monitorDataAdvertisementDateService;
	@Autowired
	private MonitorDataAdvertisementTimeService monitorDataAdvertisementTimeService;
	@Autowired
	private DeviceInfoService deviceInfoService;
	@Autowired
	private MonitorDataDeviceService monitorDataDeviceService;
	@Autowired
	private MonitorDataService monitorDataService;
	@Autowired
	private MonitorDataDeviceDateService monitorDataDeviceDateService;
	@Autowired
	private MonitorDataAgeService monitorDataAgeService;
	@Autowired
	private CustomProperties customProperties;
	@Autowired
	private RedisCacheDao redisCacheDao;

	/**
	 * 使用Elasticsearch将监播按照广告名称、时间、年龄、性别维度统计曝光次数、观看人次、触达人次、播放时长
	 */
	@Scheduled(cron = "0 50 4 * * ?")
	public void saveMonitorDataAdvertisement() {
		// 获取昨天的日期
		String date = DateUtils.getPastDay(DateConstant.YESTERDAY, DateConstant.DATE_YEAR_MONTH_DAY);
		// 索引名称
		String indexName = ElasticsearchConstant.FACE_MONITOR;
		// 索引类型
		String indexType = "";

		// 按广告名称分组
		TermsAggregationBuilder nameTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_ADVERTISEMENT_NAME)
				.field(ElasticsearchConstant.ADVERTISEMENT_NAME_KEYWORD)
				.size(Integer.MAX_VALUE);

		// 按日期时间分组
		TermsAggregationBuilder dateTimeTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_DATE_TIME)
				.field(ElasticsearchConstant.DATE_TIME_KEYWORD)
				.size(Integer.MAX_VALUE);

		// 按年龄分组
		TermsAggregationBuilder ageTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_AGE)
				.field(ElasticsearchConstant.AGE_KEYWORD)
				.size(Integer.MAX_VALUE);

		// 按性别分组
		TermsAggregationBuilder sexTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_SEX)
				.field(ElasticsearchConstant.SEX)
				.size(Integer.MAX_VALUE);

		ageTermsAggregationBuilder.subAggregation(sexTermsAggregationBuilder);
		dateTimeTermsAggregationBuilder.subAggregation(ageTermsAggregationBuilder);
		nameTermsAggregationBuilder.subAggregation(dateTimeTermsAggregationBuilder);

		//统计曝光次数
		SumAggregationBuilder exposuresSumAggregationBuilder = AggregationBuilders.sum(ElasticsearchConstant.EXPOSURES_SUM)
				.field(ElasticsearchConstant.EXPOSURES_NUMBER);

		//统计观看人次
		SumAggregationBuilder watchSumAggregationBuilder = AggregationBuilders.sum(ElasticsearchConstant.WATCH_SUM)
				.field(ElasticsearchConstant.WATCH_NUMBER);

		//统计触达人次
		SumAggregationBuilder touchSumAggregationBuilder = AggregationBuilders.sum(ElasticsearchConstant.TOUCH_SUM)
				.field(ElasticsearchConstant.TOUCH_NUMBER);

		//统计播放时长
		SumAggregationBuilder playDurationSumAggregationBuilder = AggregationBuilders.sum(ElasticsearchConstant.PLAY_DURATION_SUM)
				.field(ElasticsearchConstant.PLAY_DURATION);

		sexTermsAggregationBuilder.subAggregation(exposuresSumAggregationBuilder);
		sexTermsAggregationBuilder.subAggregation(watchSumAggregationBuilder);
		sexTermsAggregationBuilder.subAggregation(touchSumAggregationBuilder);
		sexTermsAggregationBuilder.subAggregation(playDurationSumAggregationBuilder);

		try {
			// 计算一天中的最小日期
			Long startTime = DateUtils.stringTimeToLongTimeStamp(date + " " + DateConstant.MIN_HOUR_MINUTE_SECOND_MILLISECOND, DateConstant.YEAR_MONTH_DAY_HMS_SSS);
			// 计算一天中的最大日期
			Long endTime = DateUtils.stringTimeToLongTimeStamp(date + " " + DateConstant.MAX_HOUR_MINUTE_SECOND_MILLISECOND, DateConstant.YEAR_MONTH_DAY_HMS_SSS);
			// 必须加入时间条件查询
			RangeQueryBuilder queryBuilder = QueryBuilders.rangeQuery(ElasticsearchConstant.AT_TIME_STAMP)
					.from(startTime)
					.to(endTime)
					.format(ElasticsearchConstant.EPOCH_MILLIS);

			// 2019/4/12 目前只看adx的监播数据，所以只加入adx为参数去查询，以后如果需要多维度查看，projectName为最顶级分组
			// 加入项目名称条件（只查询adx的广告）
			MatchPhraseQueryBuilder projectNameMatchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.PROJECT_NAME, ElasticsearchConstant.PROJECT_NAME_ADX);
			// 加入屏幕类型条件（只获取上屏的数据）
			MatchPhraseQueryBuilder screenTypeMatchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.SCREEN_TYPE, ElasticsearchConstant.SCREEN_TYPE_ABOVE);

			// 加入终端类型
			Integer deviceType = customProperties.getDeviceType();
			if (deviceType == null) {
				logger.error(LoggerConstant.SAVE_MONITOR_DATA_ADVERTISEMENT + MessageConstant.DEVICE_TYPE_IS_NULLL);
				return;
			}
			MatchPhraseQueryBuilder deviceTypeMatchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.DEVICE_TYPE, deviceType);

			BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
			MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();

			boolQueryBuilder.must(matchAllQueryBuilder)
					.must(queryBuilder)
					.must(projectNameMatchPhraseQueryBuilder)
					.must(screenTypeMatchPhraseQueryBuilder)
					.must(deviceTypeMatchPhraseQueryBuilder);

			// 把人数少的终端过滤掉，不计入计算
			BoolQueryBuilder deviceCodesBoolQueryBuilder = null;
			Object deviceCodePeopleCache = redisCacheDao.getCache(CacheConstant.FACE_PEOPLE_NUM_LESS_THAN_VALUE_DEVICE_CODE, date);
			if (deviceCodePeopleCache != null) {
				JSONObject jsonObject = JSON.parseObject(deviceCodePeopleCache.toString());
				Set<String> deviceCodeSet = jsonObject.keySet();
				if (CollectionUtils.isNotEmpty(deviceCodeSet)) {
					deviceCodesBoolQueryBuilder = QueryBuilders.boolQuery();
					for (String deviceCode : deviceCodeSet) {
						MatchPhraseQueryBuilder matchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.DEVICE_NUMBER, deviceCode);
						deviceCodesBoolQueryBuilder.should(matchPhraseQueryBuilder);
					}
				}
			}

			if (deviceCodesBoolQueryBuilder != null) {
				boolQueryBuilder.mustNot(deviceCodesBoolQueryBuilder);
			}

			SearchResult searchResult = elasticsearchService.doSearch(indexName, indexType, null, boolQueryBuilder, nameTermsAggregationBuilder);
			String errorMessage = searchResult.getErrorMessage();
			if (StringUtils.isNotBlank(errorMessage)) {
				logger.error(LoggerConstant.SAVE_MONITOR_DATA_ADVERTISEMENT + ": {}", errorMessage);
			} else {
				List<TermsAggregation.Entry> advertisementNameBuckets = searchResult.getAggregations()
						.getTermsAggregation(ElasticsearchConstant.GROUP_BY_ADVERTISEMENT_NAME)
						.getBuckets();

				// 监播数据广告列表
				List<MonitorDataAdvertisement> monitorDataAdvertisementList = Lists.newArrayList();
				// 保留三位小数
				DecimalFormat decimalFormat = new DecimalFormat(BusinessConstant.ZERO_POINT_ZERO_ZERO);

				// 广告名称桶列表
				for (TermsAggregation.Entry nameEntry : advertisementNameBuckets) {
					String advertisementName = nameEntry.getKey();
					List<TermsAggregation.Entry> dateTimeBuckets = nameEntry.getTermsAggregation(ElasticsearchConstant.GROUP_BY_DATE_TIME)
							.getBuckets();

					// 日期时间桶列表
					for (TermsAggregation.Entry dateTimeEntry : dateTimeBuckets) {
						String dateTime = dateTimeEntry.getKeyAsString();
						// 取dateTime的最后两位小时作为dateTime,根据小时分组是，方便
						String dateTimeStr = dateTime.substring(8, 10);
						List<TermsAggregation.Entry> ageBuckets = dateTimeEntry.getTermsAggregation(ElasticsearchConstant.GROUP_BY_AGE)
								.getBuckets();

						// 年龄桶列表
						for (TermsAggregation.Entry ageEntry : ageBuckets) {
							String age = ageEntry.getKey();
							List<TermsAggregation.Entry> sexBuckets = ageEntry.getTermsAggregation(ElasticsearchConstant.GROUP_BY_SEX)
									.getBuckets();

							// 性别桶列表
							for (TermsAggregation.Entry sexEntry : sexBuckets) {
								String sex = sexEntry.getKey();
								switch (sex) {
									case ElasticsearchConstant.STRING_ZERO:
										sex = ElasticsearchConstant.SEX_FEMALE;
										break;
									case ElasticsearchConstant.STRING_ONE:
										sex = ElasticsearchConstant.SEX_MALE;
										break;
									case ElasticsearchConstant.STRING_THREE :
										sex = ElasticsearchConstant.SEX_UNKNOW;
										break;
									case ElasticsearchConstant.STRING_TWO:
										sex = ElasticsearchConstant.SEX_UNLIMITED;
										break;
									default:
										sex = ElasticsearchConstant.SEX_UNKNOW;

								}
								// 曝光次数总计
								Double exposuresSum = sexEntry.getSumAggregation(ElasticsearchConstant.EXPOSURES_SUM)
										.getSum();

								if (exposuresSum == null) {
									exposuresSum = 0.0;
								} else {
									String exopsuresSumStr = decimalFormat.format(exposuresSum);
									exposuresSum = Double.valueOf(exopsuresSumStr);
								}
								// 触达人次总计
								Double touchSum = sexEntry.getSumAggregation(ElasticsearchConstant.TOUCH_SUM)
										.getSum();

								// 观看人次总计
								Double watchSum = sexEntry.getSumAggregation(ElasticsearchConstant.WATCH_SUM)
										.getSum();

								// 播放时长总计
								Double playDurationSum = sexEntry.getSumAggregation(ElasticsearchConstant.PLAY_DURATION_SUM)
										.getSum();

								MonitorDataAdvertisement monitorDataAdvertisement = new MonitorDataAdvertisement();
								monitorDataAdvertisement.setAdvertisementName(advertisementName);
								monitorDataAdvertisement.setDate(date);
								monitorDataAdvertisement.setTime(dateTimeStr);
								String ageStr = "";
								// 如果年龄为未知，不需要转化
								if (BusinessConstant.AGE_STRING_UNKNOW.equals(age)) {
									ageStr = age;
								} else {
									// 鉴于前端需要使用"20~25岁"这种格式的数据，所以需要把"20岁~25岁"格式转化为"20~25岁"
									ageStr = BusinessConstant.AGE_MAP.get(age);
								}
								monitorDataAdvertisement.setAge(ageStr);
								monitorDataAdvertisement.setSex(sex);
								monitorDataAdvertisement.setExposuresSum(exposuresSum);
								monitorDataAdvertisement.setWatchSum(watchSum == null ? 0 : watchSum.intValue());
								monitorDataAdvertisement.setTouchSum(touchSum == null ? 0 : touchSum.intValue());
								monitorDataAdvertisement.setPlayDurationSum(playDurationSum == null ? 0 : playDurationSum.longValue());
								monitorDataAdvertisement.setAddTime(System.currentTimeMillis());

								monitorDataAdvertisementList.add(monitorDataAdvertisement);
							}
						}
					}
				}
				//保存数据到数据库
				if (CollectionUtils.isNotEmpty(monitorDataAdvertisementList)) {
					monitorDataAdvertisementService.insertMonitorDataAdvertisementBatch(monitorDataAdvertisementList);
					// 日志
					String nowTime = DateUtils.getNowDateTime("");
					logger.info("{} " + LoggerConstant.SAVE_MONITOR_DATA_ADVERTISEMENT + MessageConstant.SAVING_IS_SUCCESSFUL, nowTime);
				}
			}
		} catch (IOException | ParseException | ServiceException e) {
			logger.error(LoggerConstant.SAVE_MONITOR_DATA_ADVERTISEMENT, e);
		}
	}

	/**
	 * 使用Elasticsearch将监播按照终端编码、时间、年龄、性别维度统计曝光次数、观看人次、触达人次、播放时长
	 */
	@Scheduled(cron = "0 55 4 * * ?")
	public void saveMonitorDataDevice() {
		// 获取终端信息DTOMap(已终端编码为key)
		Map<String, DeviceInfoDTO> deviceInfoDTOMap = deviceInfoService.getDeviceInfoDTOMap();
		if (deviceInfoDTOMap == null || deviceInfoDTOMap.size() == 0) {
			logger.error(LoggerConstant.SAVE_MONITOR_DATA_DEVICE + MessageConstant.FRP_PROVINCE_CITY_TABLE_IS_NULL);
			return;
		}
		// 获取昨天的日期
		String date = DateUtils.getPastDay(DateConstant.YESTERDAY, DateConstant.DATE_YEAR_MONTH_DAY);
		// 索引名称
		String indexName = ElasticsearchConstant.FACE_MONITOR;
		// 索引类型
		String indexType = "";

		// 按终端编码分组
		TermsAggregationBuilder deviceNumberTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_DEVICE_NUMBER)
				.field(ElasticsearchConstant.DEVICE_NUMBER_KEYWORD)
				.size(Integer.MAX_VALUE);

		// 按日期时间分组
		TermsAggregationBuilder dateTimeTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_DATE_TIME)
				.field(ElasticsearchConstant.DATE_TIME_KEYWORD)
				.size(Integer.MAX_VALUE);

		// 按年龄分组
		TermsAggregationBuilder ageTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_AGE)
				.field(ElasticsearchConstant.AGE_KEYWORD)
				.size(Integer.MAX_VALUE);

		// 按性别分组
		TermsAggregationBuilder sexTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_SEX)
				.field(ElasticsearchConstant.SEX)
				.size(Integer.MAX_VALUE);

		ageTermsAggregationBuilder.subAggregation(sexTermsAggregationBuilder);
		dateTimeTermsAggregationBuilder.subAggregation(ageTermsAggregationBuilder);
		deviceNumberTermsAggregationBuilder.subAggregation(dateTimeTermsAggregationBuilder);

		//统计曝光次数
		SumAggregationBuilder exposuresSumAggregationBuilder = AggregationBuilders.sum(ElasticsearchConstant.EXPOSURES_SUM)
				.field(ElasticsearchConstant.EXPOSURES_NUMBER);

		//统计观看人次
		SumAggregationBuilder watchSumAggregationBuilder = AggregationBuilders.sum(ElasticsearchConstant.WATCH_SUM)
				.field(ElasticsearchConstant.WATCH_NUMBER);

		//统计触达人次
		SumAggregationBuilder touchSumAggregationBuilder = AggregationBuilders.sum(ElasticsearchConstant.TOUCH_SUM)
				.field(ElasticsearchConstant.TOUCH_NUMBER);

		//统计播放时长
		SumAggregationBuilder playDurationSumAggregationBuilder = AggregationBuilders.sum(ElasticsearchConstant.PLAY_DURATION_SUM)
				.field(ElasticsearchConstant.PLAY_DURATION);

		sexTermsAggregationBuilder.subAggregation(exposuresSumAggregationBuilder);
		sexTermsAggregationBuilder.subAggregation(watchSumAggregationBuilder);
		sexTermsAggregationBuilder.subAggregation(touchSumAggregationBuilder);
		sexTermsAggregationBuilder.subAggregation(playDurationSumAggregationBuilder);

		try {
			// 计算一天中的最小日期
			Long startTime = DateUtils.stringTimeToLongTimeStamp(date + " " + DateConstant.MIN_HOUR_MINUTE_SECOND_MILLISECOND, DateConstant.YEAR_MONTH_DAY_HMS_SSS);
			// 计算一天中的最大日期
			Long endTime = DateUtils.stringTimeToLongTimeStamp(date + " " + DateConstant.MAX_HOUR_MINUTE_SECOND_MILLISECOND, DateConstant.YEAR_MONTH_DAY_HMS_SSS);
			// 必须加入时间条件查询
			RangeQueryBuilder queryBuilder = QueryBuilders.rangeQuery(ElasticsearchConstant.AT_TIME_STAMP)
					.from(startTime)
					.to(endTime)
					.format(ElasticsearchConstant.EPOCH_MILLIS);

			// 2019/4/12 目前只看adx的监播数据，所以只加入adx为参数去查询，以后如果需要多维度查看，projectName为最顶级分组
			// 加入项目名称条件（只查询adx的广告）
			MatchPhraseQueryBuilder projectNameMatchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.PROJECT_NAME, ElasticsearchConstant.PROJECT_NAME_ADX);
			// 加入屏幕类型条件（只获取上屏的数据）
			MatchPhraseQueryBuilder screenTypeMatchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.SCREEN_TYPE, ElasticsearchConstant.SCREEN_TYPE_ABOVE);

			// 加入终端类型
			Integer deviceType = customProperties.getDeviceType();
			if (deviceType == null) {
				logger.error(LoggerConstant.SAVE_MONITOR_DATA_DEVICE + MessageConstant.DEVICE_TYPE_IS_NULLL);
				return;
			}
			MatchPhraseQueryBuilder deviceTypeMatchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.DEVICE_TYPE, deviceType);


			//多条件查询
			BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
			MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();

			boolQueryBuilder.must(matchAllQueryBuilder)
					.must(queryBuilder)
					.must(projectNameMatchPhraseQueryBuilder)
					.must(screenTypeMatchPhraseQueryBuilder)
					.must(deviceTypeMatchPhraseQueryBuilder);

			// 把人数少的终端过滤掉，不计入计算
			BoolQueryBuilder deviceCodesBoolQueryBuilder = null;
			Object deviceCodePeopleCache = redisCacheDao.getCache(CacheConstant.FACE_PEOPLE_NUM_LESS_THAN_VALUE_DEVICE_CODE, date);
			if (deviceCodePeopleCache != null) {
				JSONObject jsonObject = JSON.parseObject(deviceCodePeopleCache.toString());
				Set<String> deviceCodeSet = jsonObject.keySet();
				if (CollectionUtils.isNotEmpty(deviceCodeSet)) {
					deviceCodesBoolQueryBuilder = QueryBuilders.boolQuery();
					for (String deviceCode : deviceCodeSet) {
						MatchPhraseQueryBuilder matchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.DEVICE_NUMBER, deviceCode);
						deviceCodesBoolQueryBuilder.should(matchPhraseQueryBuilder);
					}
				}
			}

			if (deviceCodesBoolQueryBuilder != null) {
				boolQueryBuilder.mustNot(deviceCodesBoolQueryBuilder);
			}

			SearchResult searchResult = elasticsearchService.doSearch(indexName, indexType, null, boolQueryBuilder, deviceNumberTermsAggregationBuilder);
			String errorMessage = searchResult.getErrorMessage();
			if (StringUtils.isNotBlank(errorMessage)) {
				logger.error(LoggerConstant.SAVE_MONITOR_DATA_DEVICE + ": {}", errorMessage);
			} else {
				List<TermsAggregation.Entry> advertisementNameBuckets = searchResult.getAggregations()
						.getTermsAggregation(ElasticsearchConstant.GROUP_BY_DEVICE_NUMBER)
						.getBuckets();

				// 监播数据终端列表
				List<MonitorDataDevice> monitorDataDeviceList = Lists.newArrayList();
				DecimalFormat decimalFormat = new DecimalFormat(BusinessConstant.ZERO_POINT_ZERO_ZERO);

				// 终端编码桶列表
				for (TermsAggregation.Entry deviceNumberEntry : advertisementNameBuckets) {
					String deviceNumber = deviceNumberEntry.getKey();
					List<TermsAggregation.Entry> dateTimeBuckets = deviceNumberEntry.getTermsAggregation(ElasticsearchConstant.GROUP_BY_DATE_TIME)
							.getBuckets();

					// 日期时间桶列表
					for (TermsAggregation.Entry dateTimeEntry : dateTimeBuckets) {
						String dateTime = dateTimeEntry.getKeyAsString();
						// 取dateTime的最后两位小时作为dateTime,根据小时分组是，方便
						String dateTimeStr = dateTime.substring(8, 10);
						List<TermsAggregation.Entry> ageBuckets = dateTimeEntry.getTermsAggregation(ElasticsearchConstant.GROUP_BY_AGE)
								.getBuckets();

						// 年龄桶列表
						for (TermsAggregation.Entry ageEntry : ageBuckets) {
							String age = ageEntry.getKey();
							List<TermsAggregation.Entry> sexBuckets = ageEntry.getTermsAggregation(ElasticsearchConstant.GROUP_BY_SEX)
									.getBuckets();

							// 性别桶列表
							for (TermsAggregation.Entry sexEntry : sexBuckets) {
								String sex = sexEntry.getKey();
								switch (sex) {
									case ElasticsearchConstant.STRING_ZERO:
										sex = ElasticsearchConstant.SEX_FEMALE;
										break;
									case ElasticsearchConstant.STRING_ONE:
										sex = ElasticsearchConstant.SEX_MALE;
										break;
									case ElasticsearchConstant.STRING_THREE :
										sex = ElasticsearchConstant.SEX_UNKNOW;
										break;
									case ElasticsearchConstant.STRING_TWO:
										sex = ElasticsearchConstant.SEX_UNLIMITED;
										break;
									default:
										sex = ElasticsearchConstant.SEX_UNKNOW;
								}
								// 曝光次数总计
								Double exposuresSum = sexEntry.getSumAggregation(ElasticsearchConstant.EXPOSURES_SUM)
										.getSum();

								// 观看人次总计
								Double watchSum = sexEntry.getSumAggregation(ElasticsearchConstant.WATCH_SUM)
										.getSum();

								// 触达人次总计
								Double touchSum = sexEntry.getSumAggregation(ElasticsearchConstant.TOUCH_SUM)
										.getSum();

								// 播放时长总计
								Double playDurationSum = sexEntry.getSumAggregation(ElasticsearchConstant.PLAY_DURATION_SUM)
										.getSum();

								MonitorDataDevice monitorDataDevice = new MonitorDataDevice();
								monitorDataDevice.setDeviceModel(deviceNumber);
								monitorDataDevice.setDate(date);
								monitorDataDevice.setTime(dateTimeStr);
								String ageStr = "";
								// 如果年龄为未知，不需要转化
								if (BusinessConstant.AGE_STRING_UNKNOW.equals(age)) {
									ageStr = age;
								} else {
									// 鉴于前端需要使用"20~25岁"这种格式的数据，所以需要把"20岁~25岁"格式转化为"20~25岁"
									ageStr = BusinessConstant.AGE_MAP.get(age);
								}
								monitorDataDevice.setAge(ageStr);
								monitorDataDevice.setSex(sex);
								// 获取城市、小区、住宅类型
								DeviceInfoDTO deviceInfoDTO = deviceInfoDTOMap.get(deviceNumber);
								if (deviceInfoDTO != null) {
									monitorDataDevice.setCity(deviceInfoDTO.getCity());
									monitorDataDevice.setCommunity(deviceInfoDTO.getCommunity());
									monitorDataDevice.setResidenceType(deviceInfoDTO.getResidenceType());
								}
								// 曝光次数保留三位小数
								if (exposuresSum == null) {
									exposuresSum = 0.0D;
								} else {
									String exposuresSumStr = decimalFormat.format(exposuresSum);
									exposuresSum = Double.valueOf(exposuresSumStr);
								}
								monitorDataDevice.setExposuresSum(exposuresSum);
								monitorDataDevice.setWatchSum(watchSum == null ? 0 : watchSum.intValue());
								monitorDataDevice.setTouchSum(touchSum == null ? 0 : touchSum.intValue());
								monitorDataDevice.setPlayDurationSum(playDurationSum == null ? 0 : playDurationSum.longValue());
								monitorDataDevice.setAddTime(System.currentTimeMillis());

								monitorDataDeviceList.add(monitorDataDevice);
							}
						}
					}
				}
				// 保存数据到数据库
				if (CollectionUtils.isNotEmpty(monitorDataDeviceList)) {
					monitorDataDeviceService.insertMonitorDataDeviceBatch(monitorDataDeviceList);
					// 日志
					String nowTime = DateUtils.getNowDateTime("");
					logger.info("{} " + LoggerConstant.SAVE_MONITOR_DATA_DEVICE + MessageConstant.SAVING_IS_SUCCESSFUL, nowTime);
				}
			}
		} catch (IOException | ParseException | ServiceException e) {
			logger.error(LoggerConstant.SAVE_MONITOR_DATA_DEVICE, e);
		}
	}

	/**
	 * 保存监播数据（按广告日期）
	 */
	@Scheduled(cron = "0 0 5 * * ?")
	public void saveMonitorDataAdvertisementDate() {
		//获取昨天的日期
		String date = DateUtils.getPastDay(DateConstant.YESTERDAY, DateConstant.DATE_YEAR_MONTH_DAY);
		try {
			List<MonitorDataAdvertisementDate> monitorDataAdvertisementDateList = monitorDataAdvertisementService.listMonitorDataAdvertisementDate(date);
			if (CollectionUtils.isNotEmpty(monitorDataAdvertisementDateList)) {
				//设置addTime
				long currentTimeMillis = System.currentTimeMillis();
				for (MonitorDataAdvertisementDate monitorDataAdvertisementDate : monitorDataAdvertisementDateList) {
					monitorDataAdvertisementDate.setAddTime(currentTimeMillis);
				}
				monitorDataAdvertisementDateService.insertMonitorDataAdvertisementDateBatch(monitorDataAdvertisementDateList);
				// 日志
				String nowTime = DateUtils.getNowDateTime("");
				logger.info("{} " + LoggerConstant.SAVE_MONITOR_DATA_ADVERTISEMENT_DATE + MessageConstant.SAVING_IS_SUCCESSFUL, nowTime);
			}
		} catch (ServiceException e) {
			logger.error(LoggerConstant.SAVE_MONITOR_DATA_ADVERTISEMENT_DATE, e);
		}
	}

	/**
	 * 保存监播数据（按广告时间）
	 */
	@Scheduled(cron = "0 0 5 * * ?")
	public void saveMonitorDataAdvertisementTime() {
		//获取昨天的日期
		String date = DateUtils.getPastDay(DateConstant.YESTERDAY, DateConstant.DATE_YEAR_MONTH_DAY);
		try {
			List<MonitorDataAdvertisementTime> monitorDataAdvertisementTimeList = monitorDataAdvertisementService.listMonitorDataAdvertisementTime(date);
			if (CollectionUtils.isNotEmpty(monitorDataAdvertisementTimeList)) {
				//设置addTime
				long currentTimeMillis = System.currentTimeMillis();
				for (MonitorDataAdvertisementTime monitorDataAdvertisementTime : monitorDataAdvertisementTimeList) {
					monitorDataAdvertisementTime.setAddTime(currentTimeMillis);
				}
				monitorDataAdvertisementTimeService.insertMonitorDataAdvertisementTimeBatch(monitorDataAdvertisementTimeList);
				// 日志
				String nowTime = DateUtils.getNowDateTime("");
				logger.info("{} " + LoggerConstant.SAVE_MONITOR_DATA_ADVERTISEMENT_TIME + MessageConstant.SAVING_IS_SUCCESSFUL, nowTime);
			}
		} catch (ServiceException e) {
			logger.error(LoggerConstant.SAVE_MONITOR_DATA_ADVERTISEMENT_TIME, e);
		}
	}

	/**
	 * 保存监播数据（按日期统计）
	 */
	@Scheduled(cron = "0 0 5 * * ?")
	public void saveMonitorDataAge(){
		//获取昨天的日期
		String date = DateUtils.getPastDay(DateConstant.YESTERDAY, DateConstant.DATE_YEAR_MONTH_DAY);
		try {
			List<MonitorDataAge> monitorDataAgeList = monitorDataAdvertisementService.listMonitorDataAge(date);
			if (CollectionUtils.isNotEmpty(monitorDataAgeList)) {
				//设置addTime
				long currentTimeMillis = System.currentTimeMillis();
				for (MonitorDataAge monitorDataAge : monitorDataAgeList) {
					monitorDataAge.setAddTime(currentTimeMillis);
				}
				monitorDataAgeService.insertMonitorDataAgeBatch(monitorDataAgeList);
				// 日志
				String nowTime = DateUtils.getNowDateTime("");
				logger.info("{} " + LoggerConstant.SAVE_MONITOR_DATA_AGE + MessageConstant.SAVING_IS_SUCCESSFUL, nowTime);
			}
		} catch (ServiceException e) {
			logger.error(LoggerConstant.SAVE_MONITOR_DATA_AGE, e);
		}
	}

	/**
	 * 保存监播数据（按日期统计）
	 */
	@Scheduled(cron = "0 0 5 * * ?")
	public void saveMonitorData() {
		//获取昨天的日期
		String date = DateUtils.getPastDay(DateConstant.YESTERDAY, DateConstant.DATE_YEAR_MONTH_DAY);
		try {
			List<MonitorData> monitorDataList = monitorDataAdvertisementService.listMonitorData(date);
			if (CollectionUtils.isNotEmpty(monitorDataList)) {
				//设置addTime
				long currentTimeMillis = System.currentTimeMillis();
				for (MonitorData monitorData : monitorDataList) {
					monitorData.setAddTime(currentTimeMillis);
				}
				monitorDataService.insertMonitorDataBatch(monitorDataList);
				// 日志
				String nowTime = DateUtils.getNowDateTime("");
				logger.info("{} " + LoggerConstant.SAVE_MONITOR_DATA + MessageConstant.SAVING_IS_SUCCESSFUL, nowTime);
			}
		} catch (ServiceException e) {
			logger.error(LoggerConstant.SAVE_MONITOR_DATA, e);
		}
	}

	/**
	 * 保存监播数据设备（按日期统计）
	 */
	@Scheduled(cron = "0 0 5 * * ?")
	public void saveMonitorDataDeviceDate() {
		//获取昨天的日期
		String date = DateUtils.getPastDay(DateConstant.YESTERDAY, DateConstant.DATE_YEAR_MONTH_DAY);
		try {
			List<MonitorDataDeviceDate> monitorDataDeviceDateList = monitorDataDeviceService.listMonitorDataDeviceDate(date);
			if (CollectionUtils.isNotEmpty(monitorDataDeviceDateList)) {
				//设置addTime
				long currentTimeMillis = System.currentTimeMillis();
				for (MonitorDataDeviceDate monitorDataDeviceDate : monitorDataDeviceDateList) {
					monitorDataDeviceDate.setAddTime(currentTimeMillis);
				}
				monitorDataDeviceDateService.insertMonitorDataDeviceDateBatch(monitorDataDeviceDateList);
				// 日志
				String nowTime = DateUtils.getNowDateTime("");
				logger.info("{} " + LoggerConstant.SAVE_MONITOR_DATA + MessageConstant.SAVING_IS_SUCCESSFUL, nowTime);
			}
		} catch (ServiceException e) {
			logger.error(LoggerConstant.SAVE_MONITOR_DATA, e);
		}
	}

}
