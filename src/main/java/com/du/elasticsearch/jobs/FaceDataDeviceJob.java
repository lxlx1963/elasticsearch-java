package com.du.elasticsearch.jobs;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.xinchao.data.cacahe.RedisCacheDao;
import com.xinchao.data.config.CustomProperties;
import com.xinchao.data.constant.*;
import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.*;
import com.xinchao.data.service.*;
import com.xinchao.data.util.DateUtils;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.TermsAggregation;
import io.searchbox.core.search.aggregation.ValueCountAggregation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCountAggregationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

/**
 * @author dxy
 * @date 2019/4/2 21:52
 */
@Component
public class FaceDataDeviceJob {
	private static Logger logger = LoggerFactory.getLogger(FaceDataDeviceJob.class);
	@Autowired
	private ElasticsearchService elasticsearchService;
	@Autowired
	private FaceDataDeviceService faceDataDeviceService;
	@Autowired
	private FaceDataDeviceSexService faceDataDeviceSexService;
	@Autowired
	private FaceDataDeviceTimeService faceDataDeviceTimeService;
	@Autowired
	private FaceDataDeviceTimeAgeService faceDataDeviceTimeAgeService;
	@Autowired
	private FaceDataDeviceTimeSexService faceDataDeviceTimeSexService;
	@Autowired
	private DeviceInfoService deviceInfoService;
	@Autowired
	private CustomProperties customProperties;
	@Autowired
	private RedisCacheDao redisCacheDao;

	/**
	 * 保存人脸数据（终端编码）
	 */
	@Scheduled(cron = "0 0 4 * * ?")
	public void saveFaceDataDevice() {
		try {
			// 获取终端编码对应的住宅类型信息
			Map<String, String> deviceNumberResidenceTypeMap = deviceInfoService.getDeviceNumberResidenceTypeMap();
			// 如果没有配置终端信息，不继续执行
			if (MapUtils.isEmpty(deviceNumberResidenceTypeMap)) {
				logger.error(LoggerConstant.SAVE_FACE_DATA_DEVICE + MessageConstant.FRP_PROVINCE_CITY_TABLE_IS_NULL);
				return;
			}

			// 获取昨天的日期
			String date = DateUtils.getPastDay(DateConstant.YESTERDAY, DateConstant.DATE_YEAR_MONTH_DAY);
			// 索引名称
			String indexName = ElasticsearchConstant.FACE_DATA;
			// 索引类型
			String indexType = "";
			// 计算一天中的最小日期

			Long startTime = DateUtils.stringTimeToLongTimeStamp(date + " " + DateConstant.MIN_HOUR_MINUTE_SECOND_MILLISECOND, DateConstant.YEAR_MONTH_DAY_HMS_SSS);
			// 计算一天中的最大日期
			Long endTime = DateUtils.stringTimeToLongTimeStamp(date + " " + DateConstant.MAX_HOUR_MINUTE_SECOND_MILLISECOND, DateConstant.YEAR_MONTH_DAY_HMS_SSS);
			// 时间查询条件
			RangeQueryBuilder queryBuilder = QueryBuilders.rangeQuery(ElasticsearchConstant.ENTER_TIME)
					.from(startTime)
					.to(endTime)
					.format(ElasticsearchConstant.EPOCH_MILLIS);

			// 加入终端类型
			Integer deviceType = customProperties.getDeviceType();
			if (deviceType == null) {
				logger.error(LoggerConstant.SAVE_FACE_DATA_DEVICE + MessageConstant.DEVICE_TYPE_IS_NULLL);
				return;
			}
			MatchPhraseQueryBuilder deviceTypeMatchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.DEVICE_TYPE, deviceType);

			// 多条件查询
			BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
			MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
			boolQueryBuilder.must(matchAllQueryBuilder)
					.must(queryBuilder)
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
						MatchPhraseQueryBuilder matchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.DEVICE_CODE, deviceCode);
						deviceCodesBoolQueryBuilder.should(matchPhraseQueryBuilder);
					}
				}
			}

			if (deviceCodesBoolQueryBuilder != null) {
				boolQueryBuilder.mustNot(deviceCodesBoolQueryBuilder);
			}

			/** 查询人次 */
			// 按终端编码分组
			TermsAggregationBuilder trackIdDeviceCodeTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_DEVICE_CODE)
					.field(ElasticsearchConstant.DEVICE_CODE_KEYWORD)
					.size(Integer.MAX_VALUE);

			// 根据id来统计人次
			ValueCountAggregationBuilder trackIdCountAggregationBuilder = AggregationBuilders.count(ElasticsearchConstant.ID_COUNT)
					.field(ElasticsearchConstant.ID);

			trackIdDeviceCodeTermsAggregationBuilder.subAggregation(trackIdCountAggregationBuilder);

			SearchResult trackIdSearchResult = elasticsearchService.doSearch(indexName, indexType, null, boolQueryBuilder, trackIdDeviceCodeTermsAggregationBuilder);
			String trackIdErrorMessage = trackIdSearchResult.getErrorMessage();
			// 人次Map
			Map<String, Long> peopleTimeMap = new HashMap<>();

			if (StringUtils.isNotBlank(trackIdErrorMessage)) {
				logger.error(LoggerConstant.SAVE_FACE_DATA_DEVICE + ": {}", trackIdErrorMessage);
			} else {
				List<TermsAggregation.Entry> deviceCodeBuckets = trackIdSearchResult.getAggregations()
						.getTermsAggregation(ElasticsearchConstant.GROUP_BY_DEVICE_CODE)
						.getBuckets();

				// 终端编码桶列表
				for (TermsAggregation.Entry deviceCodeEntry : deviceCodeBuckets) {
					// 终端编码
					String deviceCode = deviceCodeEntry.getKey();

					// 人次
					Long peopleTime = deviceCodeEntry.getValueCountAggregation(ElasticsearchConstant.ID_COUNT).getValueCount();
					if (peopleTime == null) {
						peopleTime = 0L;
					}
					peopleTimeMap.put(deviceCode, peopleTime);
				}
			}

			/** 查询人数 */
			// 按终端编码分组
			TermsAggregationBuilder visitorIdDeviceCodeTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_DEVICE_CODE)
					.field(ElasticsearchConstant.DEVICE_CODE_KEYWORD)
					.size(Integer.MAX_VALUE);

			// 按visitorId分组，来统计人数
			TermsAggregationBuilder visitorIdTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_VISITOR_ID)
					.field(ElasticsearchConstant.VISITOR_ID)
					.size(Integer.MAX_VALUE);

			visitorIdDeviceCodeTermsAggregationBuilder.subAggregation(visitorIdTermsAggregationBuilder);

			//过滤性别为“”的数据，性别为“”的数据不计入人数,只查询查性别为“M”“F”
			MatchPhraseQueryBuilder maleMatchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.GENDER_KEYWORD, ElasticsearchConstant.STRING_M);
			MatchPhraseQueryBuilder femaleMatchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.GENDER_KEYWORD, ElasticsearchConstant.STRING_F);
			BoolQueryBuilder genderBoolQueryBuilder = QueryBuilders.boolQuery();
			genderBoolQueryBuilder.should(maleMatchPhraseQueryBuilder).should(femaleMatchPhraseQueryBuilder);

			//人脸ID为-1，不计入人数
			MatchPhraseQueryBuilder visitorIdMathcPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.VISITOR_ID, BusinessConstant.MINUS_ONE);
			////条件（性别，人脸ID）
			boolQueryBuilder.must(genderBoolQueryBuilder).mustNot(visitorIdMathcPhraseQueryBuilder);

			SearchResult visitorIdSearchResult = elasticsearchService.doSearch(indexName, indexType, null, boolQueryBuilder, visitorIdDeviceCodeTermsAggregationBuilder);
			String visitorIdErrorMessage = visitorIdSearchResult.getErrorMessage();


			if (StringUtils.isNotBlank(visitorIdErrorMessage)) {
				logger.error(LoggerConstant.SAVE_FACE_DATA_DEVICE + ": {}", visitorIdErrorMessage);
			} else {
				List<TermsAggregation.Entry> deviceCodeBuckets = visitorIdSearchResult.getAggregations()
						.getTermsAggregation(ElasticsearchConstant.GROUP_BY_DEVICE_CODE)
						.getBuckets();

				List<FaceDataDevice> faceDataDeviceList = new ArrayList<>();

				// 终端编码桶列表
				for (TermsAggregation.Entry deviceCodeEntry : deviceCodeBuckets) {
					// 终端编码
					String deviceCode = deviceCodeEntry.getKey();

					// 人数
					int peopleNum = deviceCodeEntry.getTermsAggregation(ElasticsearchConstant.GROUP_BY_VISITOR_ID).getBuckets().size();
					// 人次
					Long peopleTime = peopleTimeMap.get(deviceCode);
					if (peopleTime == null) {
						peopleTime = 0L;
					}

					FaceDataDevice faceDataDevice = new FaceDataDevice();
					faceDataDevice.setDate(date);
					faceDataDevice.setDeviceCode(deviceCode);
					faceDataDevice.setPeopleNum(peopleNum);
					String residenceType = deviceNumberResidenceTypeMap.get(deviceCode);
					faceDataDevice.setResidenceType(residenceType);
					faceDataDevice.setPeopleNum(peopleNum);
					faceDataDevice.setPeopleTime(peopleTime.intValue());
					faceDataDevice.setAddTime(System.currentTimeMillis());
					faceDataDeviceList.add(faceDataDevice);
				}
				// 保存数据到数据库
				if (CollectionUtils.isNotEmpty(faceDataDeviceList)) {
					faceDataDeviceService.insertFaceDataDeviceBatch(faceDataDeviceList);
					// 日志
					String nowTime = DateUtils.getNowDateTime("");
					logger.info("{} " + LoggerConstant.SAVE_FACE_DATA_DEVICE + MessageConstant.SAVING_IS_SUCCESSFUL, nowTime);
				}
			}
		} catch (ParseException | IOException | ServiceException e) {
			logger.error(LoggerConstant.SAVE_FACE_DATA_DEVICE, e);
		}
	}

	/**
	 * 保存人脸数据（终端编码性别）
	 * 此方法没有加性别过滤条件，是因为按性别分组，可以知道性别为“不限”的人次和人数
	 */
	@Scheduled(cron = "0 5 4 * * ?")
	public void saveFaceDataDeviceSex() {
		try {
			// 获取终端编码对应的住宅类型信息
			Map<String, String> deviceNumberResidenceTypeMap = deviceInfoService.getDeviceNumberResidenceTypeMap();
			// 如果没有配置终端信息，不继续执行
			if (deviceNumberResidenceTypeMap == null || deviceNumberResidenceTypeMap.size() == 0) {
				logger.error(LoggerConstant.SAVE_FACE_DATA_DEVICE_SEX + MessageConstant.FRP_PROVINCE_CITY_TABLE_IS_NULL);
				return;
			}

			// 获取昨天的日期
			String date = DateUtils.getPastDay(DateConstant.YESTERDAY, DateConstant.DATE_YEAR_MONTH_DAY);
			// 索引名称
			String indexName = ElasticsearchConstant.FACE_DATA;
			// 索引类型
			String indexType = "";
			// 计算一天中的最小日期

			Long startTime = DateUtils.stringTimeToLongTimeStamp(date + " " + DateConstant.MIN_HOUR_MINUTE_SECOND_MILLISECOND, DateConstant.YEAR_MONTH_DAY_HMS_SSS);
			// 计算一天中的最大日期
			Long endTime = DateUtils.stringTimeToLongTimeStamp(date + " " + DateConstant.MAX_HOUR_MINUTE_SECOND_MILLISECOND, DateConstant.YEAR_MONTH_DAY_HMS_SSS);
			//时间查询条件
			RangeQueryBuilder queryBuilder = QueryBuilders.rangeQuery(ElasticsearchConstant.ENTER_TIME)
					.from(startTime)
					.to(endTime)
					.format(ElasticsearchConstant.EPOCH_MILLIS);

			// 加入终端类型
			Integer deviceType = customProperties.getDeviceType();
			if (deviceType == null) {
				logger.error(LoggerConstant.SAVE_FACE_DATA_DEVICE_SEX + MessageConstant.DEVICE_TYPE_IS_NULLL);
				return;
			}
			MatchPhraseQueryBuilder deviceTypeMatchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.DEVICE_TYPE, deviceType);

			// 多条件查询
			BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
			MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
			boolQueryBuilder.must(matchAllQueryBuilder).must(queryBuilder).must(deviceTypeMatchPhraseQueryBuilder);

			// 把人数少的终端过滤掉，不计入计算
			BoolQueryBuilder deviceCodesBoolQueryBuilder = null;
			Object deviceCodePeopleCache = redisCacheDao.getCache(CacheConstant.FACE_PEOPLE_NUM_LESS_THAN_VALUE_DEVICE_CODE, date);
			if (deviceCodePeopleCache != null) {
				JSONObject jsonObject = JSON.parseObject(deviceCodePeopleCache.toString());
				Set<String> deviceCodeSet = jsonObject.keySet();
				if (CollectionUtils.isNotEmpty(deviceCodeSet)) {
					deviceCodesBoolQueryBuilder = QueryBuilders.boolQuery();
					for (String deviceCode : deviceCodeSet) {
						MatchPhraseQueryBuilder matchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.DEVICE_CODE, deviceCode);
						deviceCodesBoolQueryBuilder.should(matchPhraseQueryBuilder);
					}
				}
			}

			if (deviceCodesBoolQueryBuilder != null) {
				boolQueryBuilder.mustNot(deviceCodesBoolQueryBuilder);
			}

			/** 查询人次 */
			// 按终端编码分组
			TermsAggregationBuilder trackeIdDeviceCodeTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_DEVICE_CODE)
					.field(ElasticsearchConstant.DEVICE_CODE_KEYWORD)
					.size(Integer.MAX_VALUE);

			// 按性别分组
			TermsAggregationBuilder trackeIdGenderTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_GENDER)
					.field(ElasticsearchConstant.GENDER_KEYWORD)
					.size(Integer.MAX_VALUE);

			// 根据id来统计人次
			ValueCountAggregationBuilder trackIdCountAggregationBuilder = AggregationBuilders.count(ElasticsearchConstant.ID_COUNT)
					.field(ElasticsearchConstant.ID);

			trackeIdGenderTermsAggregationBuilder.subAggregation(trackIdCountAggregationBuilder);
			trackeIdDeviceCodeTermsAggregationBuilder.subAggregation(trackeIdGenderTermsAggregationBuilder);

			SearchResult trackIdSearchResult = elasticsearchService.doSearch(indexName, indexType, null, boolQueryBuilder, trackeIdDeviceCodeTermsAggregationBuilder);

			String trackIdErrorMessage = trackIdSearchResult.getErrorMessage();

			//key为终端编码+性别；value为人次
			Map<String, Long> trackIdMap = Maps.newHashMap();

			if (StringUtils.isBlank(trackIdErrorMessage)) {
				List<TermsAggregation.Entry> trackIdDeviceCodeBuckets = trackIdSearchResult.getAggregations()
						.getTermsAggregation(ElasticsearchConstant.GROUP_BY_DEVICE_CODE)
						.getBuckets();
				for (TermsAggregation.Entry trackIdDeviceCodeEntry :trackIdDeviceCodeBuckets) {
					String deviceCode = trackIdDeviceCodeEntry.getKey();
					TermsAggregation trackIdGenderTermsAggregation = trackIdDeviceCodeEntry.getTermsAggregation(ElasticsearchConstant.GROUP_BY_GENDER);

					List<TermsAggregation.Entry> trackIdGenderBuckets = trackIdGenderTermsAggregation.getBuckets();
					for (TermsAggregation.Entry trackIdGenderEntry : trackIdGenderBuckets) {
						String gender = trackIdGenderEntry.getKey();
						// 人次
						ValueCountAggregation valueCountAggregation = trackIdGenderEntry.getValueCountAggregation(ElasticsearchConstant.ID_COUNT);
						Long peopleTime = valueCountAggregation.getValueCount();
						if (peopleTime == null) {
							peopleTime = 0L;
						}
						String key = deviceCode + gender;
						trackIdMap.put(key, peopleTime);
					}
				}
			} else {
				logger.error(LoggerConstant.SAVE_FACE_DATA_DEVICE_SEX + ": {}", trackIdErrorMessage);
			}

			/** 查询人数 */
			// 按终端编码分组
			TermsAggregationBuilder visitorIdDeviceCodeTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_DEVICE_CODE)
					.field(ElasticsearchConstant.DEVICE_CODE_KEYWORD)
					.size(Integer.MAX_VALUE);

			// 按性别分组
			TermsAggregationBuilder visitorIdGenderTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_GENDER)
					.field(ElasticsearchConstant.GENDER_KEYWORD)
					.size(Integer.MAX_VALUE);

			// 按visitorId分组，来统计人数
			TermsAggregationBuilder visitorIdTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_VISITOR_ID)
					.field(ElasticsearchConstant.VISITOR_ID)
					.size(Integer.MAX_VALUE);

			visitorIdGenderTermsAggregationBuilder.subAggregation(visitorIdTermsAggregationBuilder);
			visitorIdDeviceCodeTermsAggregationBuilder.subAggregation(visitorIdGenderTermsAggregationBuilder);

			// 人脸ID为-1，不计入人数
			MatchPhraseQueryBuilder visitorIdMathcPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.VISITOR_ID, BusinessConstant.MINUS_ONE);
			// 加入条件（人脸ID）
			boolQueryBuilder.mustNot(visitorIdMathcPhraseQueryBuilder);

			SearchResult searchResult = elasticsearchService.doSearch(indexName, indexType, null, boolQueryBuilder, visitorIdDeviceCodeTermsAggregationBuilder);
			String errorMessage = searchResult.getErrorMessage();
			if (StringUtils.isNotBlank(errorMessage)) {
				logger.error(LoggerConstant.SAVE_FACE_DATA_DEVICE_SEX + ": {}", errorMessage);
			} else {

				List<TermsAggregation.Entry> deviceCodeBuckets = searchResult.getAggregations()
						.getTermsAggregation(ElasticsearchConstant.GROUP_BY_DEVICE_CODE)
						.getBuckets();

				List<FaceDataDeviceSex> faceDataDeviceSexList = new ArrayList<>();

				// 终端编码桶列表
				for (TermsAggregation.Entry deviceCodeEntry : deviceCodeBuckets) {
					// 终端编码
					String deviceCode = deviceCodeEntry.getKey();

					List<TermsAggregation.Entry> genderBuckets = deviceCodeEntry.getTermsAggregation(ElasticsearchConstant.GROUP_BY_GENDER)
							.getBuckets();

					// 性别桶列表
					for (TermsAggregation.Entry genderEntry : genderBuckets) {
						String gengder = genderEntry.getKey();

						String mapKey = deviceCode + gengder;

						switch (gengder) {
							case ElasticsearchConstant.STRING_F:
								gengder = ElasticsearchConstant.SEX_FEMALE;
								break;
							case ElasticsearchConstant.STRING_M:
								gengder = ElasticsearchConstant.SEX_MALE;
								break;
							default:
								gengder = ElasticsearchConstant.SEX_UNLIMITED;
								break;
						}
						// 人数
						int peopleNum = genderEntry.getTermsAggregation(ElasticsearchConstant.GROUP_BY_VISITOR_ID)
								.getBuckets()
								.size();
						// 人次
						Long peopleTime = trackIdMap.get(mapKey);
						if (peopleTime == null) {
							peopleTime = 0L;
						}

						FaceDataDeviceSex faceDataDeviceSex = new FaceDataDeviceSex();
						faceDataDeviceSex.setDate(date);
						faceDataDeviceSex.setDeviceCode(deviceCode);
						String residenceType = deviceNumberResidenceTypeMap.get(deviceCode);
						faceDataDeviceSex.setResidenceType(residenceType);
						faceDataDeviceSex.setSex(gengder);
						faceDataDeviceSex.setPeopleNum(peopleNum);
						faceDataDeviceSex.setPeopleTime(Integer.valueOf(String.valueOf(peopleTime)));
						faceDataDeviceSex.setAddTime(System.currentTimeMillis());
						faceDataDeviceSexList.add(faceDataDeviceSex);
					}
				}
				// 保存数据到数据库
				if (CollectionUtils.isNotEmpty(faceDataDeviceSexList)) {
					faceDataDeviceSexService.insertFaceDataDeviceSexBatch(faceDataDeviceSexList);
					// 日志
					String nowTime = DateUtils.getNowDateTime("");
					logger.info("{} " + LoggerConstant.SAVE_FACE_DATA_DEVICE_SEX + MessageConstant.SAVING_IS_SUCCESSFUL, nowTime);
				}
			}
		} catch (ParseException | IOException | ServiceException e) {
			logger.error(LoggerConstant.SAVE_FACE_DATA_DEVICE_SEX, e);
		}
	}

	/**
	 * 保存人脸数据（终端编码时间年龄）
	 */
	@Scheduled(cron = "0 10 4 * * ?")
	public void saveFaceDataDeviceTimeAge(){
		try {
			// 获取终端编码对应的住宅类型信息
			Map<String, String> deviceNumberResidenceTypeMap = deviceInfoService.getDeviceNumberResidenceTypeMap();
			// 如果没有配置终端信息，不继续执行
			if (deviceNumberResidenceTypeMap == null || deviceNumberResidenceTypeMap.size() == 0) {
				logger.error(LoggerConstant.SAVE_FACE_DATA_DEVICE_TIME_AGE + MessageConstant.FRP_PROVINCE_CITY_TABLE_IS_NULL);
				return;
			}

			// 获取昨天的日期
			String date = DateUtils.getPastDay(DateConstant.YESTERDAY, DateConstant.DATE_YEAR_MONTH_DAY);
			// 索引名称
			String indexName = ElasticsearchConstant.FACE_DATA;
			// 索引类型
			String indexType = "";
			// 计算一天中的最小日期

			Long startTime = DateUtils.stringTimeToLongTimeStamp(date + " " + DateConstant.MIN_HOUR_MINUTE_SECOND_MILLISECOND, DateConstant.YEAR_MONTH_DAY_HMS_SSS);
			// 计算一天中的最大日期
			Long endTime = DateUtils.stringTimeToLongTimeStamp(date + " " + DateConstant.MAX_HOUR_MINUTE_SECOND_MILLISECOND, DateConstant.YEAR_MONTH_DAY_HMS_SSS);
			// 时间查询条件
			RangeQueryBuilder queryBuilder = QueryBuilders.rangeQuery(ElasticsearchConstant.ENTER_TIME)
					.from(startTime)
					.to(endTime)
					.format(ElasticsearchConstant.EPOCH_MILLIS);

			// 加入终端类型
			Integer deviceType = customProperties.getDeviceType();
			if (deviceType == null) {
				logger.error(LoggerConstant.SAVE_FACE_DATA_DEVICE_TIME_AGE + MessageConstant.DEVICE_TYPE_IS_NULLL);
				return;
			}
			MatchPhraseQueryBuilder deviceTypeMatchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.DEVICE_TYPE, deviceType);

			//多条件查询
			BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
			MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
			boolQueryBuilder.must(matchAllQueryBuilder)
					.must(queryBuilder)
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
						MatchPhraseQueryBuilder matchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.DEVICE_CODE, deviceCode);
						deviceCodesBoolQueryBuilder.should(matchPhraseQueryBuilder);
					}
				}
			}

			if (deviceCodesBoolQueryBuilder != null) {
				boolQueryBuilder.mustNot(deviceCodesBoolQueryBuilder);
			}

			/** 查询人次 */
			// 按终端编码分组
			TermsAggregationBuilder trackIdDeviceCodeTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_DEVICE_CODE)
					.field(ElasticsearchConstant.DEVICE_CODE_KEYWORD)
					.size(Integer.MAX_VALUE);

			// 按时间分组
			TermsAggregationBuilder trackIdDateTimeTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_DATE_TIME)
					.field(ElasticsearchConstant.DATE_TIME_KEYWORD)
					.size(Integer.MAX_VALUE);
			// 按年龄分组
			TermsAggregationBuilder trackIdAgeTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_AGE_RANGE)
					.field(ElasticsearchConstant.AGE_RANGE_KEYWORD)
					.size(Integer.MAX_VALUE);

			// 根据id来统计人次
			ValueCountAggregationBuilder trackIdCountAggregationBuilder = AggregationBuilders.count(ElasticsearchConstant.ID_COUNT)
					.field(ElasticsearchConstant.ID);

			trackIdAgeTermsAggregationBuilder.subAggregation(trackIdCountAggregationBuilder);
			trackIdDateTimeTermsAggregationBuilder.subAggregation(trackIdAgeTermsAggregationBuilder);
			trackIdDeviceCodeTermsAggregationBuilder.subAggregation(trackIdDateTimeTermsAggregationBuilder);

			SearchResult trackIdSearchResult = elasticsearchService.doSearch(indexName, indexType, null, boolQueryBuilder, trackIdDeviceCodeTermsAggregationBuilder);
			String trackIdErrorMessage = trackIdSearchResult.getErrorMessage();

			// 人次Map
			Map<String, Long> peopleTimeMap = new HashMap<>();

			if (StringUtils.isNotBlank(trackIdErrorMessage)) {
				logger.error(LoggerConstant.SAVE_FACE_DATA_DEVICE_TIME_AGE + ": {}", trackIdErrorMessage);
			} else {
				List<TermsAggregation.Entry> deviceCodeBuckets = trackIdSearchResult.getAggregations()
						.getTermsAggregation(ElasticsearchConstant.GROUP_BY_DEVICE_CODE)
						.getBuckets();

				StringBuilder sb = new StringBuilder();

				// 终端编码桶列表
				for (TermsAggregation.Entry deviceCodeEntry : deviceCodeBuckets) {
					// 终端编码
					String deviceCode = deviceCodeEntry.getKey();

					List<TermsAggregation.Entry> dateTimeBuckets = deviceCodeEntry.getTermsAggregation(ElasticsearchConstant.GROUP_BY_DATE_TIME)
							.getBuckets();

					// 时间桶列表
					for (TermsAggregation.Entry dateTimeEntry : dateTimeBuckets) {
						String dateTime = dateTimeEntry.getKey();

						TermsAggregation ageTermsAggregation = dateTimeEntry.getTermsAggregation(ElasticsearchConstant.GROUP_BY_AGE_RANGE);
						List<TermsAggregation.Entry> ageBuckets = ageTermsAggregation.getBuckets();

						// 年龄桶列表
						for (TermsAggregation.Entry ageEntry : ageBuckets) {
							String age = ageEntry.getKey();

							sb.append(deviceCode).append(dateTime).append(age);

							// 人次
							Long peopleTime = ageEntry.getValueCountAggregation(ElasticsearchConstant.ID_COUNT).getValueCount();
							if (peopleTime == null) {
								peopleTime = 0L;
							}
							peopleTimeMap.put(sb.toString(), peopleTime);
							// 将StringBuilder的长度设置为0，便于下次利用
							sb.setLength(0);
						}
					}
				}
			}

			/** 查询人数 */
			// 按终端编码分组
			TermsAggregationBuilder visitorIdDevieCodeTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_DEVICE_CODE).field(ElasticsearchConstant.DEVICE_CODE_KEYWORD).size(Integer.MAX_VALUE);
			// 按时间分组
			TermsAggregationBuilder visitorIdDateTimeTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_DATE_TIME).field(ElasticsearchConstant.DATE_TIME_KEYWORD).size(Integer.MAX_VALUE);
			// 按年龄分组
			TermsAggregationBuilder visitorIdAgeTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_AGE_RANGE).field(ElasticsearchConstant.AGE_RANGE_KEYWORD).size(Integer.MAX_VALUE);

			// 按visitorId分组，来统计人数
			TermsAggregationBuilder visitorIdTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_VISITOR_ID).field(ElasticsearchConstant.VISITOR_ID).size(Integer.MAX_VALUE);

			visitorIdAgeTermsAggregationBuilder.subAggregation(visitorIdTermsAggregationBuilder);
			visitorIdDateTimeTermsAggregationBuilder.subAggregation(visitorIdAgeTermsAggregationBuilder);
			visitorIdDevieCodeTermsAggregationBuilder.subAggregation(visitorIdDateTimeTermsAggregationBuilder);

			// 过滤性别为“”的数据，性别为“”的数据不计入人数,只查询查性别为“M”“F”
			MatchPhraseQueryBuilder maleMatchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.GENDER_KEYWORD, ElasticsearchConstant.STRING_M);
			MatchPhraseQueryBuilder femaleMatchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.GENDER_KEYWORD, ElasticsearchConstant.STRING_F);
			BoolQueryBuilder genderBoolQueryBuilder = QueryBuilders.boolQuery();
			genderBoolQueryBuilder.should(maleMatchPhraseQueryBuilder).should(femaleMatchPhraseQueryBuilder);

			// 人脸ID为-1，不计入人数
			MatchPhraseQueryBuilder visitorIdMathcPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.VISITOR_ID, BusinessConstant.MINUS_ONE);
			// 条件（性别，人脸ID）
			boolQueryBuilder.must(genderBoolQueryBuilder).mustNot(visitorIdMathcPhraseQueryBuilder);

			SearchResult visitorIdSearchResult = elasticsearchService.doSearch(indexName, indexType, null, boolQueryBuilder, visitorIdDevieCodeTermsAggregationBuilder);
			String visitorIdErrorMessage = visitorIdSearchResult.getErrorMessage();

			if (StringUtils.isNotBlank(visitorIdErrorMessage)) {
				logger.error(LoggerConstant.SAVE_FACE_DATA_DEVICE_TIME_AGE + ": {}", visitorIdErrorMessage);
			} else {
				List<TermsAggregation.Entry> deviceCodeBuckets = visitorIdSearchResult.getAggregations()
						.getTermsAggregation(ElasticsearchConstant.GROUP_BY_DEVICE_CODE)
						.getBuckets();

				List<FaceDataDeviceTimeAge> faceDataDeviceTimeAgeList = new ArrayList<>();
				StringBuilder sb = new StringBuilder();

				// 终端编码桶列表
				for (TermsAggregation.Entry deviceCodeEntry : deviceCodeBuckets) {
					// 终端编码
					String deviceCode = deviceCodeEntry.getKey();
					List<TermsAggregation.Entry> dateTimeBuckets = deviceCodeEntry.getTermsAggregation(ElasticsearchConstant.GROUP_BY_DATE_TIME)
							.getBuckets();

					// 时间桶列表
					for (TermsAggregation.Entry dateTimeEntry : dateTimeBuckets) {
						String dateTime = dateTimeEntry.getKey();
						// 取dateTime的前八位作为年月日作为date,这样的数据才准确
						String dateStr = dateTime.substring(0, 8);
						// 取dateTime的最后两位小时作为dateTime,根据小时分组是，方便
						String dateTimeStr = dateTime.substring(8, 10);

						List<TermsAggregation.Entry> ageBuckets = dateTimeEntry.getTermsAggregation(ElasticsearchConstant.GROUP_BY_AGE_RANGE)
								.getBuckets();

						// 年龄桶列表
						for (TermsAggregation.Entry ageEntry : ageBuckets) {
							String age = ageEntry.getKey();
							String key = sb.append(deviceCode).append(dateTime).append(age).toString();
							// 人数
							int peopleNum = ageEntry.getTermsAggregation(ElasticsearchConstant.GROUP_BY_VISITOR_ID).getBuckets().size();
							// 人次
							Long peopleTime = peopleTimeMap.get(key);
							if (peopleTime == null) {
								peopleTime = 0L;
							}

							FaceDataDeviceTimeAge faceDataDeviceTimeAge = new FaceDataDeviceTimeAge();
							faceDataDeviceTimeAge.setDate(dateStr);
							faceDataDeviceTimeAge.setDeviceCode(deviceCode);
							String residenceType = deviceNumberResidenceTypeMap.get(deviceCode);
							faceDataDeviceTimeAge.setResidenceType(residenceType);
							faceDataDeviceTimeAge.setTime(dateTimeStr);
							faceDataDeviceTimeAge.setAge(age);
							faceDataDeviceTimeAge.setPeopleNum(peopleNum);
							faceDataDeviceTimeAge.setPeopleTime(peopleTime.intValue());
							faceDataDeviceTimeAge.setAddTime(System.currentTimeMillis());
							faceDataDeviceTimeAgeList.add(faceDataDeviceTimeAge);
							// 将StringBuilder的长度设置为0，便于下次利用
							sb.setLength(0);
						}
					}
				}
				// 保存数据到数据库
				if (CollectionUtils.isNotEmpty(faceDataDeviceTimeAgeList)) {
					faceDataDeviceTimeAgeService.insertFaceDataDeviceTimeAgeBatch(faceDataDeviceTimeAgeList);
					// 日志
					String nowTime = DateUtils.getNowDateTime("");
					logger.info("{} " + LoggerConstant.SAVE_FACE_DATA_DEVICE_TIME_AGE + MessageConstant.SAVING_IS_SUCCESSFUL, nowTime);
				}
			}
		} catch (ParseException | IOException | ServiceException e) {
			logger.error(LoggerConstant.SAVE_FACE_DATA_DEVICE_TIME_AGE, e);
		}
	}

	/**
	 * 保存人脸数据（终端编码时间）
	 */
	@Scheduled(cron = "0 15 4 * * ?")
	public void saveFaceDataDeviceTime(){
		try {
			// 获取终端编码对应的住宅类型信息
			Map<String, String> deviceNumberResidenceTypeMap = deviceInfoService.getDeviceNumberResidenceTypeMap();
			// 如果没有配置终端信息，不继续执行
			if (deviceNumberResidenceTypeMap == null || deviceNumberResidenceTypeMap.size() == 0) {
				logger.error(LoggerConstant.SAVE_FACE_DATA_DEVICE_TIME + MessageConstant.FRP_PROVINCE_CITY_TABLE_IS_NULL);
				return;
			}

			// 获取昨天的日期
			String date = DateUtils.getPastDay(DateConstant.YESTERDAY, DateConstant.DATE_YEAR_MONTH_DAY);
			// 索引名称
			String indexName = ElasticsearchConstant.FACE_DATA;
			// 索引类型
			String indexType = "";
			// 计算一天中的最小日期

			Long startTime = DateUtils.stringTimeToLongTimeStamp(date + " " + DateConstant.MIN_HOUR_MINUTE_SECOND_MILLISECOND, DateConstant.YEAR_MONTH_DAY_HMS_SSS);
			// 计算一天中的最大日期
			Long endTime = DateUtils.stringTimeToLongTimeStamp(date + " " + DateConstant.MAX_HOUR_MINUTE_SECOND_MILLISECOND, DateConstant.YEAR_MONTH_DAY_HMS_SSS);
			// 时间查询条件
			RangeQueryBuilder queryBuilder = QueryBuilders.rangeQuery(ElasticsearchConstant.ENTER_TIME)
					.from(startTime)
					.to(endTime)
					.format(ElasticsearchConstant.EPOCH_MILLIS);

			// 加入终端类型
			Integer deviceType = customProperties.getDeviceType();
			if (deviceType == null) {
				logger.error(LoggerConstant.SAVE_FACE_DATA_DEVICE_TIME + MessageConstant.DEVICE_TYPE_IS_NULLL);
				return;
			}
			MatchPhraseQueryBuilder deviceTypeMatchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.DEVICE_TYPE, deviceType);

			// 多条件查询
			BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
			MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
			boolQueryBuilder.must(matchAllQueryBuilder)
					.must(queryBuilder)
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
						MatchPhraseQueryBuilder matchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.DEVICE_CODE, deviceCode);
						deviceCodesBoolQueryBuilder.should(matchPhraseQueryBuilder);
					}
				}
			}

			if (deviceCodesBoolQueryBuilder != null) {
				boolQueryBuilder.mustNot(deviceCodesBoolQueryBuilder);
			}

			/** 查询人次 */
			// 按终端编码分组
			TermsAggregationBuilder trackIdDeviceCodeTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_DEVICE_CODE)
					.field(ElasticsearchConstant.DEVICE_CODE_KEYWORD)
					.size(Integer.MAX_VALUE);

			// 按时间分组
			TermsAggregationBuilder trackIdDateTimeTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_DATE_TIME)
					.field(ElasticsearchConstant.DATE_TIME_KEYWORD)
					.size(Integer.MAX_VALUE);

			// 根据id来统计人次
			ValueCountAggregationBuilder trackIdCountAggregationBuilder = AggregationBuilders.count(ElasticsearchConstant.ID_COUNT)
					.field(ElasticsearchConstant.ID);

			trackIdDateTimeTermsAggregationBuilder.subAggregation(trackIdCountAggregationBuilder);
			trackIdDeviceCodeTermsAggregationBuilder.subAggregation(trackIdDateTimeTermsAggregationBuilder);

			SearchResult trackIdSearchResult = elasticsearchService.doSearch(indexName, indexType, null, boolQueryBuilder, trackIdDeviceCodeTermsAggregationBuilder);
			String trackIdErrorMessage = trackIdSearchResult.getErrorMessage();
			//人次Map
			Map<String, Long> peopleTimeMap = new HashMap<>();

			if (StringUtils.isNotBlank(trackIdErrorMessage)) {
				logger.error(LoggerConstant.SAVE_FACE_DATA_DEVICE_TIME +": {}", trackIdErrorMessage);
			} else {
				List<TermsAggregation.Entry> deviceCodeBuckets = trackIdSearchResult.getAggregations()
						.getTermsAggregation(ElasticsearchConstant.GROUP_BY_DEVICE_CODE)
						.getBuckets();

				// 终端编码桶列表
				for (TermsAggregation.Entry deviceCodeEntry : deviceCodeBuckets) {
					// 终端编码
					String deviceCode = deviceCodeEntry.getKey();

					List<TermsAggregation.Entry> dateTimeBuckets = deviceCodeEntry.getTermsAggregation(ElasticsearchConstant.GROUP_BY_DATE_TIME)
							.getBuckets();

					for (TermsAggregation.Entry dateTimeEntry : dateTimeBuckets) {
						String dateTime = dateTimeEntry.getKey();

						//人次
						Long peopleTime = dateTimeEntry.getValueCountAggregation(ElasticsearchConstant.ID_COUNT)
								.getValueCount();

						if (peopleTime == null) {
							peopleTime = 0L;
						}
						String key = deviceCode + dateTime;
						peopleTimeMap.put(key, peopleTime);
					}
				}
			}

			/** 查询人数 */
			// 按终端编码分组
			TermsAggregationBuilder visitorIdDeviceCodeTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_DEVICE_CODE)
					.field(ElasticsearchConstant.DEVICE_CODE_KEYWORD)
					.size(Integer.MAX_VALUE);

			// 按时间分组
			TermsAggregationBuilder visitorIdDateTimeTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_DATE_TIME)
					.field(ElasticsearchConstant.DATE_TIME_KEYWORD)
					.size(Integer.MAX_VALUE);

			// 按visitorId分组，来统计人数
			TermsAggregationBuilder visitorIdTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_VISITOR_ID)
					.field(ElasticsearchConstant.VISITOR_ID)
					.size(Integer.MAX_VALUE);

			visitorIdDateTimeTermsAggregationBuilder.subAggregation(visitorIdTermsAggregationBuilder);
			visitorIdDeviceCodeTermsAggregationBuilder.subAggregation(visitorIdDateTimeTermsAggregationBuilder);

			//过滤性别为“”的数据，性别为“”的数据不计入人数,只查询查性别为“M”“F”
			MatchPhraseQueryBuilder maleMatchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.GENDER_KEYWORD, ElasticsearchConstant.STRING_M);
			MatchPhraseQueryBuilder femaleMatchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.GENDER_KEYWORD, ElasticsearchConstant.STRING_F);
			BoolQueryBuilder genderBoolQueryBuilder = QueryBuilders.boolQuery();
			genderBoolQueryBuilder.should(maleMatchPhraseQueryBuilder)
					.should(femaleMatchPhraseQueryBuilder);

			//人脸ID为-1，不计入人数
			MatchPhraseQueryBuilder visitorIdMathcPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.VISITOR_ID, BusinessConstant.MINUS_ONE);
			////条件（性别，人脸ID）
			boolQueryBuilder.must(genderBoolQueryBuilder).mustNot(visitorIdMathcPhraseQueryBuilder);

			SearchResult visitorIdSearchResult = elasticsearchService.doSearch(indexName, indexType, null, boolQueryBuilder, visitorIdDeviceCodeTermsAggregationBuilder);
			String visitorIdErrorMessage = visitorIdSearchResult.getErrorMessage();

			if (StringUtils.isNotBlank(visitorIdErrorMessage)) {
				logger.error(LoggerConstant.SAVE_FACE_DATA_DEVICE_TIME +": {}", visitorIdErrorMessage);
			} else {
				List<TermsAggregation.Entry> deviceCodeBuckets = visitorIdSearchResult.getAggregations()
						.getTermsAggregation(ElasticsearchConstant.GROUP_BY_DEVICE_CODE)
						.getBuckets();

				List<FaceDataDeviceTime> faceDataDeviceTimeList = new ArrayList<>();

				// 终端编码桶列表
				for (TermsAggregation.Entry deviceCodeEntry : deviceCodeBuckets) {
					// 终端编码
					String deviceCode = deviceCodeEntry.getKey();

					List<TermsAggregation.Entry> dateTimeBuckets = deviceCodeEntry.getTermsAggregation(ElasticsearchConstant.GROUP_BY_DATE_TIME)
							.getBuckets();

					for (TermsAggregation.Entry dateTimeEntry : dateTimeBuckets) {
						String dateTime = dateTimeEntry.getKey();
						// 取dateTime的前八位作为年月日作为date,这样的数据才准确
						String dateStr = dateTime.substring(0, 8);
						// 取dateTime的最后两位小时作为dateTime,根据小时分组是，方便
						String dateTimeStr = dateTime.substring(8, 10);

						// 人数
						int peopleNum = dateTimeEntry.getTermsAggregation(ElasticsearchConstant.GROUP_BY_VISITOR_ID)
								.getBuckets()
								.size();
						// 人次
						String key = deviceCode + dateTime;
						Long peopleTime = peopleTimeMap.get(key);
						if (peopleTime == null) {
							peopleTime = 0L;
						}

						FaceDataDeviceTime faceDataDeviceTime = new FaceDataDeviceTime();
						faceDataDeviceTime.setDeviceCode(deviceCode);
						String residenceType = deviceNumberResidenceTypeMap.get(deviceCode);
						faceDataDeviceTime.setResidenceType(residenceType);
						faceDataDeviceTime.setDate(dateStr);
						faceDataDeviceTime.setTime(dateTimeStr);
						faceDataDeviceTime.setPeopleNum(peopleNum);
						faceDataDeviceTime.setPeopleTime(peopleTime.intValue());
						faceDataDeviceTime.setAddTime(System.currentTimeMillis());
						faceDataDeviceTimeList.add(faceDataDeviceTime);
					}
				}
				//保存数据到数据库
				if (CollectionUtils.isNotEmpty(faceDataDeviceTimeList)) {
					faceDataDeviceTimeService.insertFaceDataDeviceTimeBatch(faceDataDeviceTimeList);
					// 日志
					String nowTime = DateUtils.getNowDateTime("");
					logger.info("{} " + LoggerConstant.SAVE_FACE_DATA_DEVICE_TIME + MessageConstant.SAVING_IS_SUCCESSFUL, nowTime);
				}
			}
		} catch (ParseException | IOException | ServiceException e) {
			logger.error(LoggerConstant.SAVE_FACE_DATA_DEVICE_TIME, e);
		}
	}

	/**
	 * 保存人脸数据（终端编码时间性别）
	 */
	@Scheduled(cron = "0 20 4 * * ?")
	public void saveFaceDataDeviceTimeSex(){
		try {
			// 获取终端编码对应的住宅类型信息
			Map<String, String> deviceNumberResidenceTypeMap = deviceInfoService.getDeviceNumberResidenceTypeMap();
			// 如果没有配置终端信息，不继续执行
			if (deviceNumberResidenceTypeMap == null || deviceNumberResidenceTypeMap.size() == 0) {
				logger.error(LoggerConstant.SAVE_FACE_DATA_DEVICE_TIME_SEX + MessageConstant.FRP_PROVINCE_CITY_TABLE_IS_NULL);
				return;
			}

			// 获取昨天的日期
			String date = DateUtils.getPastDay(DateConstant.YESTERDAY, DateConstant.DATE_YEAR_MONTH_DAY);
			// 索引名称
			String indexName = ElasticsearchConstant.FACE_DATA;
			// 索引类型
			String indexType = "";
			// 计算一天中的最小日期

			Long startTime = DateUtils.stringTimeToLongTimeStamp(date + " " + DateConstant.MIN_HOUR_MINUTE_SECOND_MILLISECOND, DateConstant.YEAR_MONTH_DAY_HMS_SSS);
			// 计算一天中的最大日期
			Long endTime = DateUtils.stringTimeToLongTimeStamp(date + " " + DateConstant.MAX_HOUR_MINUTE_SECOND_MILLISECOND, DateConstant.YEAR_MONTH_DAY_HMS_SSS);
			// 时间查询条件
			RangeQueryBuilder queryBuilder = QueryBuilders.rangeQuery(ElasticsearchConstant.ENTER_TIME)
					.from(startTime)
					.to(endTime)
					.format(ElasticsearchConstant.EPOCH_MILLIS);

			// 加入终端类型
			Integer deviceType = customProperties.getDeviceType();
			if (deviceType == null) {
				logger.error(LoggerConstant.SAVE_FACE_DATA_DEVICE_TIME_SEX + MessageConstant.DEVICE_TYPE_IS_NULLL);
				return;
			}
			MatchPhraseQueryBuilder deviceTypeMatchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.DEVICE_TYPE, deviceType);

			//多条件查询
			BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
			MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
			boolQueryBuilder.must(matchAllQueryBuilder)
					.must(queryBuilder)
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
						MatchPhraseQueryBuilder matchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.DEVICE_CODE, deviceCode);
						deviceCodesBoolQueryBuilder.should(matchPhraseQueryBuilder);
					}
				}
			}

			if (deviceCodesBoolQueryBuilder != null) {
				boolQueryBuilder.mustNot(deviceCodesBoolQueryBuilder);
			}

			/** 查询人次 */
			// 按终端编码分组
			TermsAggregationBuilder trackIdDeviceCodeTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_DEVICE_CODE)
					.field(ElasticsearchConstant.DEVICE_CODE_KEYWORD)
					.size(Integer.MAX_VALUE);

			// 按时间分组
			TermsAggregationBuilder trackIdDateTimeTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_DATE_TIME)
					.field(ElasticsearchConstant.DATE_TIME_KEYWORD)
					.size(Integer.MAX_VALUE);

			// 按性别分组
			TermsAggregationBuilder trackIdAgeTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_GENDER)
					.field(ElasticsearchConstant.GENDER_KEYWORD)
					.size(Integer.MAX_VALUE);

			// 根据id来统计人次
			ValueCountAggregationBuilder trackIdCountAggregationBuilder = AggregationBuilders.count(ElasticsearchConstant.ID_COUNT)
					.field(ElasticsearchConstant.ID);

			trackIdAgeTermsAggregationBuilder.subAggregation(trackIdCountAggregationBuilder);
			trackIdDateTimeTermsAggregationBuilder.subAggregation(trackIdAgeTermsAggregationBuilder);
			trackIdDeviceCodeTermsAggregationBuilder.subAggregation(trackIdDateTimeTermsAggregationBuilder);

			SearchResult trackIdSearchResult = elasticsearchService.doSearch(indexName, indexType, null, boolQueryBuilder, trackIdDeviceCodeTermsAggregationBuilder);
			String trackeIdErrorMessage = trackIdSearchResult.getErrorMessage();

			//人次map（key:终端编码+时间+性别；value:人次）
			Map<String, Long> trackIdMap = Maps.newHashMap();

			if (StringUtils.isNotBlank(trackeIdErrorMessage)) {
				logger.error(LoggerConstant.SAVE_FACE_DATA_DEVICE_TIME_SEX + ": {}", trackeIdErrorMessage);
			} else {
				List<TermsAggregation.Entry> trackIdDeviceCodeBuckets = trackIdSearchResult.getAggregations()
						.getTermsAggregation(ElasticsearchConstant.GROUP_BY_DEVICE_CODE)
						.getBuckets();

				// 终端编码桶列表
				for (TermsAggregation.Entry trackIdDeviceCodeEntry : trackIdDeviceCodeBuckets) {
					// 住址类型
					String deviceCode = trackIdDeviceCodeEntry.getKey();

					List<TermsAggregation.Entry> trackIdDateTimeBuckets = trackIdDeviceCodeEntry.getTermsAggregation(ElasticsearchConstant.GROUP_BY_DATE_TIME)
							.getBuckets();

					// 时间桶列表
					for (TermsAggregation.Entry trackIdDateTimeEntry : trackIdDateTimeBuckets) {
						String dateTime = trackIdDateTimeEntry.getKey();
						List<TermsAggregation.Entry> trackIdGenderBuckets = trackIdDateTimeEntry.getTermsAggregation(ElasticsearchConstant.GROUP_BY_GENDER)
								.getBuckets();

						// 年龄桶列表
						for (TermsAggregation.Entry trackIdGenderEntry : trackIdGenderBuckets) {
							String gender = trackIdGenderEntry.getKey();
							String key = deviceCode + dateTime + gender;
							//人次
							Long peopleTime = trackIdGenderEntry.getValueCountAggregation(ElasticsearchConstant.ID_COUNT)
									.getValueCount();
							if (peopleTime == null) {
								peopleTime = 0L;
							}
							trackIdMap.put(key, peopleTime);
						}
					}
				}
			}

			/** 查询人数 */
			// 按终端编码分组
			TermsAggregationBuilder visitorIdDeviceCodeTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_DEVICE_CODE)
					.field(ElasticsearchConstant.DEVICE_CODE_KEYWORD)
					.size(Integer.MAX_VALUE);

			// 按时间分组
			TermsAggregationBuilder visitorIdDateTimeTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_DATE_TIME)
					.field(ElasticsearchConstant.DATE_TIME_KEYWORD)
					.size(Integer.MAX_VALUE);

			// 按性别分组
			TermsAggregationBuilder visitorIdAgeTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_GENDER)
					.field(ElasticsearchConstant.GENDER_KEYWORD)
					.size(Integer.MAX_VALUE);

			// 按visitorId分组，来统计人数
			TermsAggregationBuilder visitorIdTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_VISITOR_ID)
					.field(ElasticsearchConstant.VISITOR_ID)
					.size(Integer.MAX_VALUE);

			visitorIdAgeTermsAggregationBuilder.subAggregation(visitorIdTermsAggregationBuilder);
			visitorIdDateTimeTermsAggregationBuilder.subAggregation(visitorIdAgeTermsAggregationBuilder);
			visitorIdDeviceCodeTermsAggregationBuilder.subAggregation(visitorIdDateTimeTermsAggregationBuilder);

			// 人脸ID为-1，不计入人数
			MatchPhraseQueryBuilder visitorIdMathcPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.VISITOR_ID, BusinessConstant.MINUS_ONE);

			// 加入条件（人脸ID）
			boolQueryBuilder.mustNot(visitorIdMathcPhraseQueryBuilder);

			SearchResult searchResult = elasticsearchService.doSearch(indexName, indexType, null, boolQueryBuilder, visitorIdDeviceCodeTermsAggregationBuilder);
			String errorMessage = searchResult.getErrorMessage();

			// 根据id来统计人次
			if (StringUtils.isNotBlank(errorMessage)) {
				logger.error(LoggerConstant.SAVE_FACE_DATA_DEVICE_TIME_SEX + ": {}", errorMessage);
			} else {
				List<TermsAggregation.Entry> deviceCodeBuckets = searchResult.getAggregations()
						.getTermsAggregation(ElasticsearchConstant.GROUP_BY_DEVICE_CODE)
						.getBuckets();

				List<FaceDataDeviceTimeSex> faceDataDeviceTimeSexList = new ArrayList<>();

				// 终端编码桶列表
				for (TermsAggregation.Entry deviceCodeEntry : deviceCodeBuckets) {
					// 住址类型
					String deviceCode = deviceCodeEntry.getKey();

					List<TermsAggregation.Entry> dateTimeBuckets = deviceCodeEntry.getTermsAggregation(ElasticsearchConstant.GROUP_BY_DATE_TIME)
							.getBuckets();
					// 时间桶列表
					for (TermsAggregation.Entry dateTimeEntry : dateTimeBuckets) {
						String dateTime = dateTimeEntry.getKey();
						// 取dateTime的前八位作为年月日作为date,这样的数据才准确
						// 取dateTime的最后两位小时作为dateTime,根据小时分组是，方便
						String dateTimeStr = dateTime.substring(8, 10);

						List<TermsAggregation.Entry> genderBuckets = dateTimeEntry.getTermsAggregation(ElasticsearchConstant.GROUP_BY_GENDER)
								.getBuckets();

						// 年龄桶列表
						for (TermsAggregation.Entry genderEntry : genderBuckets) {
							String gender = genderEntry.getKey();
							String key = deviceCode + dateTime + gender;

							switch (gender) {
								case ElasticsearchConstant.STRING_F:
									gender = ElasticsearchConstant.SEX_FEMALE;
									break;
								case ElasticsearchConstant.STRING_M:
									gender = ElasticsearchConstant.SEX_MALE;
									break;
								default:
									gender = ElasticsearchConstant.SEX_UNLIMITED;
									break;
							}

							//人数
							int peopleNum = genderEntry.getTermsAggregation(ElasticsearchConstant.GROUP_BY_VISITOR_ID)
									.getBuckets()
									.size();

							// 人次
							Long peopleTime = trackIdMap.get(key);
							if (peopleTime == null) {
								peopleTime = 0L;
							}

							FaceDataDeviceTimeSex faceDataDeviceTimeSex = new FaceDataDeviceTimeSex();
							faceDataDeviceTimeSex.setDate(date);
							faceDataDeviceTimeSex.setDeviceCode(deviceCode);
							String residenceType = deviceNumberResidenceTypeMap.get(deviceCode);
							faceDataDeviceTimeSex.setResidenceType(residenceType);
							faceDataDeviceTimeSex.setTime(dateTimeStr);
							faceDataDeviceTimeSex.setSex(gender);
							faceDataDeviceTimeSex.setPeopleNum(peopleNum);
							faceDataDeviceTimeSex.setPeopleTime(Integer.valueOf(String.valueOf(peopleTime)));
							faceDataDeviceTimeSex.setAddTime(System.currentTimeMillis());
							faceDataDeviceTimeSexList.add(faceDataDeviceTimeSex);
						}
					}
				}
				//保存数据到数据库
				if (CollectionUtils.isNotEmpty(faceDataDeviceTimeSexList)) {
					faceDataDeviceTimeSexService.insertFaceDataDeviceTimeSexBatch(faceDataDeviceTimeSexList);
					// 日志
					String nowTime = DateUtils.getNowDateTime("");
					logger.info("{} " + LoggerConstant.SAVE_FACE_DATA_DEVICE_TIME_SEX + MessageConstant.SAVING_IS_SUCCESSFUL, nowTime);
				}
			}
		} catch (ParseException | IOException | ServiceException e) {
			logger.error(LoggerConstant.SAVE_FACE_DATA_DEVICE_TIME_SEX, e);
		}
	}

}
