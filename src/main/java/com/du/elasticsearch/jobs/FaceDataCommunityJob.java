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
 * @date 2019/4/2 20:15
 */
@Component
public class FaceDataCommunityJob {
	private static Logger logger = LoggerFactory.getLogger(FaceDataCommunityJob.class);
	@Autowired
	private ElasticsearchService elasticsearchService;
	@Autowired
	private FaceDataCommunityService faceDataCommunityService;
	@Autowired
	private FaceDataCommunitySexService faceDataCommunitySexService;
	@Autowired
	private FaceDataCommunityAgeService faceDataCommunityAgeService;
	@Autowired
	private FaceDataCommunityTimeService faceDataCommunityTimeService;
	@Autowired
	private FaceDataCommunityTimeAgeService faceDataCommunityTimeAgeService;
	@Autowired
	private FaceDataCommunityDeviceService faceDataCommunityDeviceService;
	@Autowired
	private ProvinceCityService provinceCityService;
	@Autowired
	private CustomProperties customProperties;
	@Autowired
	private RedisCacheDao redisCacheDao;

	/**
	 * 保存人脸数据（小区）
	 */
	@Scheduled(cron = "0 30 3 * * ?")
	public void saveFaceDataCommunity(){
		// 获取城市省Map
		Map<String, String> cityProvinceMap = provinceCityService.getCityProvinceMap();
		if (MapUtils.isEmpty(cityProvinceMap)){
			logger.error(LoggerConstant.SAVE_FACE_DATA_COMMUNITY + MessageConstant.FRP_PROVINCE_CITY_TABLE_IS_NULL);
			return;
		}
		// 获取昨天的日期
		String date = DateUtils.getPastDay(DateConstant.YESTERDAY, DateConstant.DATE_YEAR_MONTH_DAY);
		// 索引名称
		String indexName = ElasticsearchConstant.FACE_DATA;
		// 索引类型
		String indexType = "";
		// 计算一天中的最小日期
		try {
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
				logger.error(LoggerConstant.SAVE_FACE_DATA_COMMUNITY + MessageConstant.DEVICE_TYPE_IS_NULLL);
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

			// 按小区分组(人次)
			TermsAggregationBuilder trackIdgenderTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_COMMUNITY)
					.field(ElasticsearchConstant.COMMUNITY_KEYWORD)
					.size(Integer.MAX_VALUE);

			// 根据id来统计人次
			ValueCountAggregationBuilder trackIdCountAggregationBuilder = AggregationBuilders.count(ElasticsearchConstant.ID_COUNT)
					.field(ElasticsearchConstant.ID);

			trackIdgenderTermsAggregationBuilder.subAggregation(trackIdCountAggregationBuilder);

			/** 查询人次 */
			SearchResult trackIdsearchResult = elasticsearchService.doSearch(indexName, indexType, null, boolQueryBuilder, trackIdgenderTermsAggregationBuilder);
			String trackIdErrorMessage = trackIdsearchResult.getErrorMessage();

			if (StringUtils.isNotBlank(trackIdErrorMessage)) {
				logger.error(LoggerConstant.SAVE_FACE_DATA_COMMUNITY + ": {}", trackIdErrorMessage);
				return;
			}

			// 人次Map
			Map<String, Long> peopleTimeMap = new HashMap<>();

			List<TermsAggregation.Entry> communityPeopleNumBuckets = trackIdsearchResult.getAggregations()
					.getTermsAggregation(ElasticsearchConstant.GROUP_BY_COMMUNITY)
					.getBuckets();

			// 小区桶列表
			for (TermsAggregation.Entry communityPeopleNumEntry : communityPeopleNumBuckets) {
				// 小区名-城市
				String communityCity = communityPeopleNumEntry.getKey();
				// 人次
				Long peopleTime = communityPeopleNumEntry.getValueCountAggregation(ElasticsearchConstant.ID_COUNT)
						.getValueCount();

				if (peopleTime == null) {
					peopleTime = 0L;
				}
				peopleTimeMap.put(communityCity, peopleTime);
			}

			if (MapUtils.isEmpty(peopleTimeMap)) {
				logger.info(LoggerConstant.SAVE_FACE_DATA_COMMUNITY_AGE + MessageConstant.PEOPLE_NUMBER_IS_NULL);
				return;
			}

			/** 查询人数 */
			// 按小区分组(人数)
			TermsAggregationBuilder visitorIdgenderTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_COMMUNITY).field(ElasticsearchConstant.COMMUNITY_KEYWORD).size(Integer.MAX_VALUE);
			// 按visitorId分组，来统计人数
			TermsAggregationBuilder visitorIdTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_VISITOR_ID).field(ElasticsearchConstant.VISITOR_ID).size(Integer.MAX_VALUE);
			visitorIdgenderTermsAggregationBuilder.subAggregation(visitorIdTermsAggregationBuilder);

			// 过滤性别为“”的数据，性别为“”的数据不计入人数,只查询查性别为“M”“F”
			MatchPhraseQueryBuilder maleMatchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.GENDER_KEYWORD, ElasticsearchConstant.STRING_M);
			MatchPhraseQueryBuilder femaleMatchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.GENDER_KEYWORD, ElasticsearchConstant.STRING_F);
			BoolQueryBuilder genderBoolQueryBuilder = QueryBuilders.boolQuery();
			genderBoolQueryBuilder.should(maleMatchPhraseQueryBuilder)
					.should(femaleMatchPhraseQueryBuilder);

			// 人脸ID为-1，不计入人数
			MatchPhraseQueryBuilder visitorIdMathcPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.VISITOR_ID, BusinessConstant.MINUS_ONE);

			// 条件（性别，人脸ID）
			boolQueryBuilder.must(genderBoolQueryBuilder).mustNot(visitorIdMathcPhraseQueryBuilder);

			// 查询人数
			SearchResult visitorIdsearchResult = elasticsearchService.doSearch(indexName, indexType, null, boolQueryBuilder, visitorIdgenderTermsAggregationBuilder);
			// 处理错误
			String visitorIdErrorMessage = visitorIdsearchResult.getErrorMessage();
			if (StringUtils.isNotBlank(visitorIdErrorMessage)) {
				logger.error(LoggerConstant.SAVE_FACE_DATA_COMMUNITY + ": {}", trackIdErrorMessage);
				return;
			}

			List<TermsAggregation.Entry> communityPeopleTimeBuckets = visitorIdsearchResult.getAggregations()
					.getTermsAggregation(ElasticsearchConstant.GROUP_BY_COMMUNITY)
					.getBuckets();

			List<FaceDataCommunity> faceDataCommunityList = new ArrayList<>();

			// 小区桶列表
			for (TermsAggregation.Entry communityPeopleTimeEntry : communityPeopleTimeBuckets) {
				// 小区名-城市
				String communityCity = communityPeopleTimeEntry.getKey();

				// 人数
				int peopleNum = communityPeopleTimeEntry.getTermsAggregation(ElasticsearchConstant.GROUP_BY_VISITOR_ID)
						.getBuckets()
						.size();

				FaceDataCommunity faceDataCommunity = new FaceDataCommunity();
				faceDataCommunity.setDate(date);
				// 处理城市和小区
				// 小区
				String community = "";
				// 城市
				String city = "";
				// 以“-”切割
				String[] strArr = communityCity.split("-");
				if (strArr != null && strArr.length > 0) {
					community = strArr[0];
					city = strArr[1];
				}
				// 如果城市包含了“市”，替换掉“市”（因为frp_device_info和frp_province_city表，城市去掉了“市”）
				if (city.endsWith(BusinessConstant.CHINA_SHI)) {
					city = city.substring(0, city.length() - 1);
				}
				// 根据城市获取省
				String province = cityProvinceMap.get(city);
				faceDataCommunity.setCommunity(community);
				faceDataCommunity.setCity(city);
				faceDataCommunity.setProvince(province);
				faceDataCommunity.setPeopleNum(peopleNum);
				// 获取人次
				Long peopleTime = peopleTimeMap.get(communityCity);
				if (peopleTime == null) {
					peopleTime = 0L;
				}
				faceDataCommunity.setPeopleTime(peopleTime.intValue());
				faceDataCommunity.setAddTime(System.currentTimeMillis());
				faceDataCommunityList.add(faceDataCommunity);
			}
			// 保存数据到数据库
			if (CollectionUtils.isNotEmpty(faceDataCommunityList)) {
				faceDataCommunityService.insertFaceDataCommunityBatch(faceDataCommunityList);
				// 日志
				String nowTime = DateUtils.getNowDateTime("");
				logger.info("{} " + LoggerConstant.SAVE_FACE_DATA_COMMUNITY + MessageConstant.SAVING_IS_SUCCESSFUL, nowTime);
			}
		} catch (ParseException | IOException | ServiceException e) {
			logger.error(LoggerConstant.SAVE_FACE_DATA_COMMUNITY, e);
		}
	}

	/**
	 * 保存人脸数据（小区年龄）
	 */
	@Scheduled(cron = "0 35 3 * * ?")
	public void saveFaceDataCommunityAge(){
		// 获取城市省Map
		Map<String, String> cityProvinceMap = provinceCityService.getCityProvinceMap();
		if (cityProvinceMap == null || cityProvinceMap.size() == 0){
			logger.error(LoggerConstant.SAVE_FACE_DATA_COMMUNITY_AGE + MessageConstant.FRP_PROVINCE_CITY_TABLE_IS_NULL);
			return;
		}
		// 获取昨天的日期
		String date = DateUtils.getPastDay(DateConstant.YESTERDAY, DateConstant.DATE_YEAR_MONTH_DAY);
		// 索引名称
		String indexName = ElasticsearchConstant.FACE_DATA;
		// 索引类型
		String indexType = "";
		// 计算一天中的最小日期
		try {
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
				logger.error(LoggerConstant.SAVE_FACE_DATA_COMMUNITY_AGE + MessageConstant.DEVICE_TYPE_IS_NULLL);
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

			//按小区分组(人次)
			TermsAggregationBuilder tractIdCommunityTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_COMMUNITY)
					.field(ElasticsearchConstant.COMMUNITY_KEYWORD)
					.size(Integer.MAX_VALUE);
			//按年龄分组(人次)
			TermsAggregationBuilder tractIdAgeTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_AGE_RANGE)
					.field(ElasticsearchConstant.AGE_RANGE_KEYWORD)
					.size(Integer.MAX_VALUE);

			//根据id来统计人次
			ValueCountAggregationBuilder trackIdCountAggregationBuilder = AggregationBuilders.count(ElasticsearchConstant.ID_COUNT)
					.field(ElasticsearchConstant.ID);

			tractIdAgeTermsAggregationBuilder.subAggregation(trackIdCountAggregationBuilder);
			tractIdCommunityTermsAggregationBuilder.subAggregation(tractIdAgeTermsAggregationBuilder);

			// 查询
			SearchResult trackIdSearchResult = elasticsearchService.doSearch(indexName, indexType, null, boolQueryBuilder, tractIdCommunityTermsAggregationBuilder);

			// 处理错误
			String trackIdErrorMessage = trackIdSearchResult.getErrorMessage();
			if (StringUtils.isNotBlank(trackIdErrorMessage)) {
				logger.error(LoggerConstant.SAVE_FACE_DATA_COMMUNITY_AGE + ": {}", trackIdErrorMessage);
				return;
			}

			//人次Map
			Map<String, Long> peopleTimeMap = new HashMap<>();
			List<TermsAggregation.Entry> communityPeopleNumBuckets = trackIdSearchResult.getAggregations()
					.getTermsAggregation(ElasticsearchConstant.GROUP_BY_COMMUNITY)
					.getBuckets();

			//小区桶列表
			for (TermsAggregation.Entry communityPeopleNumEntry : communityPeopleNumBuckets) {
				//小区名-城市
				String communityCity = communityPeopleNumEntry.getKey();
				List<TermsAggregation.Entry> ageBuckets = communityPeopleNumEntry.getTermsAggregation(ElasticsearchConstant.GROUP_BY_AGE_RANGE)
						.getBuckets();

				//年龄桶列表
				for (TermsAggregation.Entry ageEntry : ageBuckets) {
					String age = ageEntry.getKey();
					//人次
					Long peopleTime = ageEntry.getValueCountAggregation(ElasticsearchConstant.ID_COUNT)
							.getValueCount();

					if (peopleTime == null) {
						peopleTime = 0L;
					}
					peopleTimeMap.put(communityCity + age, peopleTime);
				}
			}

			if (MapUtils.isEmpty(peopleTimeMap)) {
				logger.info(LoggerConstant.SAVE_FACE_DATA_COMMUNITY_AGE + MessageConstant.PEOPLE_NUMBER_IS_NULL);
				return;
			}

			//按visitorId分组，来统计人数
			TermsAggregationBuilder visitorIdTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_VISITOR_ID)
					.field(ElasticsearchConstant.VISITOR_ID)
					.size(Integer.MAX_VALUE);

			tractIdAgeTermsAggregationBuilder.subAggregation(visitorIdTermsAggregationBuilder);

			//按小区分组(人数)
			TermsAggregationBuilder visitorIdCommunityTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_COMMUNITY)
					.field(ElasticsearchConstant.COMMUNITY_KEYWORD)
					.size(Integer.MAX_VALUE);

			//按年龄分组(人数)
			TermsAggregationBuilder visitorIdAgeTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_AGE_RANGE)
					.field(ElasticsearchConstant.AGE_RANGE_KEYWORD)
					.size(Integer.MAX_VALUE);

			visitorIdAgeTermsAggregationBuilder.subAggregation(visitorIdTermsAggregationBuilder);
			visitorIdCommunityTermsAggregationBuilder.subAggregation(visitorIdAgeTermsAggregationBuilder);

			//过滤性别为“”的数据，性别为“”的数据不计入人数,只查询查性别为“M”“F”
			MatchPhraseQueryBuilder maleMatchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.GENDER_KEYWORD, ElasticsearchConstant.STRING_M);
			MatchPhraseQueryBuilder femaleMatchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.GENDER_KEYWORD, ElasticsearchConstant.STRING_F);
			BoolQueryBuilder genderBoolQueryBuilder = QueryBuilders.boolQuery();
			genderBoolQueryBuilder.should(maleMatchPhraseQueryBuilder)
					.should(femaleMatchPhraseQueryBuilder);

			//人脸ID为-1，不计入人数
			MatchPhraseQueryBuilder visitorIdMathcPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.VISITOR_ID, BusinessConstant.MINUS_ONE);

			//条件（性别，人脸ID）
			boolQueryBuilder.must(genderBoolQueryBuilder)
					.mustNot(visitorIdMathcPhraseQueryBuilder);

			// 查询人数
			SearchResult viditorIdSearchResult = elasticsearchService.doSearch(indexName, indexType, null, boolQueryBuilder, visitorIdCommunityTermsAggregationBuilder);
			// 处理错误
			String viditorIdErrorMessage = viditorIdSearchResult.getErrorMessage();
			if (StringUtils.isNotBlank(viditorIdErrorMessage)) {
				logger.error(LoggerConstant.SAVE_FACE_DATA_COMMUNITY_AGE + ": {}", viditorIdErrorMessage);
				return;
			}

			List<TermsAggregation.Entry> communityPeopleTimeBuckets = viditorIdSearchResult.getAggregations()
					.getTermsAggregation(ElasticsearchConstant.GROUP_BY_COMMUNITY)
					.getBuckets();

			List<FaceDataCommunityAge> faceDataCommunityAgeList = new ArrayList<>();

			//小区桶列表
			for (TermsAggregation.Entry communityPeopleTimeEntry : communityPeopleTimeBuckets) {
				//小区名-城市
				String communityCity = communityPeopleTimeEntry.getKey();

				//处理城市和小区
				//小区
				String community = "";
				//城市
				String city = "";
				//以“-”切割
				String[] strArr = communityCity.split("-");
				if (strArr != null && strArr.length > 0) {
					community = strArr[0];
					city = strArr[1];
				}
				//如果城市包含了“市”，替换掉“市”（因为frp_device_info和frp_province_city表，城市去掉了“市”）
				if (city.endsWith(BusinessConstant.CHINA_SHI)) {
					city = city.substring(0, city.length() - 1);
				}
				//根据城市获取省
				String province = cityProvinceMap.get(city);
				List<TermsAggregation.Entry> ageBuckets = communityPeopleTimeEntry.getTermsAggregation(ElasticsearchConstant.GROUP_BY_AGE_RANGE)
						.getBuckets();

				//年龄桶列表
				for (TermsAggregation.Entry ageEntry : ageBuckets) {
					String age = ageEntry.getKey();
					//人数
					int peopleNum = ageEntry.getTermsAggregation(ElasticsearchConstant.GROUP_BY_VISITOR_ID)
							.getBuckets()
							.size();

					FaceDataCommunityAge faceDataCommunityAge = new FaceDataCommunityAge();
					faceDataCommunityAge.setDate(date);
					faceDataCommunityAge.setCommunity(community);
					faceDataCommunityAge.setCity(city);
					faceDataCommunityAge.setProvince(province);
					faceDataCommunityAge.setAge(age);
					faceDataCommunityAge.setPeopleNum(peopleNum);
					//获取人次
					Long peopleTime = peopleTimeMap.get(communityCity + age);
					if (peopleTime == null) {
						peopleTime = 0L;
					}
					faceDataCommunityAge.setPeopleTime(peopleTime.intValue());
					faceDataCommunityAge.setAddTime(System.currentTimeMillis());
					faceDataCommunityAgeList.add(faceDataCommunityAge);
				}
			}
			//保存数据到数据库
			if (CollectionUtils.isNotEmpty(faceDataCommunityAgeList)) {
				faceDataCommunityAgeService.insertFaceDataCommunityAgeBatch(faceDataCommunityAgeList);
				// 日志
				String nowTime = DateUtils.getNowDateTime("");
				logger.info("{} " + LoggerConstant.SAVE_FACE_DATA_COMMUNITY_AGE + MessageConstant.SAVING_IS_SUCCESSFUL, nowTime);
			}

		} catch (ParseException  | IOException | ServiceException e) {
			logger.error(LoggerConstant.SAVE_FACE_DATA_COMMUNITY_AGE, e);
		}
	}

	/**
	 * 保存人脸数据（小区性别）
	 * 此方法没有加性别过滤条件，是因为按性别分组，可以知道性别为“不限”的人次和人数
	 */
	@Scheduled(cron = "0 40 3 * * ?")
	public void saveFaceDataCommunitySex(){
		//获取城市省Map
		Map<String, String> cityProvinceMap = provinceCityService.getCityProvinceMap();
		if (cityProvinceMap == null || cityProvinceMap.size() == 0){
			logger.error(LoggerConstant.SAVE_FACE_DATA_COMMUNITY_SEX + MessageConstant.FRP_PROVINCE_CITY_TABLE_IS_NULL);
			return;
		}
		//获取昨天的日期
		String date = DateUtils.getPastDay(DateConstant.YESTERDAY, DateConstant.DATE_YEAR_MONTH_DAY);
		//索引名称
		String indexName = ElasticsearchConstant.FACE_DATA;
		//索引类型
		String indexType = "";
		//计算一天中的最小日期
		try {
			Long startTime = DateUtils.stringTimeToLongTimeStamp(date + " " + DateConstant.MIN_HOUR_MINUTE_SECOND_MILLISECOND, DateConstant.YEAR_MONTH_DAY_HMS_SSS);
			//计算一天中的最大日期
			Long endTime = DateUtils.stringTimeToLongTimeStamp(date + " " + DateConstant.MAX_HOUR_MINUTE_SECOND_MILLISECOND, DateConstant.YEAR_MONTH_DAY_HMS_SSS);
			//时间查询条件
			RangeQueryBuilder queryBuilder = QueryBuilders.rangeQuery(ElasticsearchConstant.ENTER_TIME)
					.from(startTime)
					.to(endTime)
					.format(ElasticsearchConstant.EPOCH_MILLIS);

			//加入终端类型
			Integer deviceType = customProperties.getDeviceType();
			if (deviceType == null) {
				logger.error(LoggerConstant.SAVE_FACE_DATA_COMMUNITY_SEX + MessageConstant.DEVICE_TYPE_IS_NULLL);
				return;
			}
			MatchPhraseQueryBuilder deviceTypeMatchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.DEVICE_TYPE, deviceType);

			//多条件查询
			BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
			MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
			boolQueryBuilder.must(matchAllQueryBuilder)
					.must(queryBuilder)
					.must(deviceTypeMatchPhraseQueryBuilder);

			//把人数少的终端过滤掉，不计入计算
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

			/** 查询人次*/
			//小区分组
			TermsAggregationBuilder treckIdCommunityTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_COMMUNITY)
					.field(ElasticsearchConstant.COMMUNITY_KEYWORD)
					.size(Integer.MAX_VALUE);
			//按性别分组
			TermsAggregationBuilder trackeIdGenderTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_GENDER)
					.field(ElasticsearchConstant.GENDER_KEYWORD)
					.size(Integer.MAX_VALUE);
			//根据id来统计人次
			ValueCountAggregationBuilder trackIdCountAggregationBuilder = AggregationBuilders.count(ElasticsearchConstant.ID_COUNT)
					.field(ElasticsearchConstant.ID);

			treckIdCommunityTermsAggregationBuilder.subAggregation(trackeIdGenderTermsAggregationBuilder);
			trackeIdGenderTermsAggregationBuilder.subAggregation(trackIdCountAggregationBuilder);

			SearchResult trackIdSearchResult = elasticsearchService.doSearch(indexName, indexType, null, boolQueryBuilder, treckIdCommunityTermsAggregationBuilder);
			// 处处理错误
			String trackIdErrorMessage = trackIdSearchResult.getErrorMessage();
			if (StringUtils.isNotBlank(trackIdErrorMessage)) {
				logger.error(LoggerConstant.SAVE_FACE_DATA_COMMUNITY_SEX + ": {}", trackIdErrorMessage);
				return;
			}

			//key为小区名称+性别；value为人次
			Map<String, Long> trackIdMap = Maps.newHashMap();
			List<TermsAggregation.Entry> trackIdCommunityBuckets = trackIdSearchResult.getAggregations()
					.getTermsAggregation(ElasticsearchConstant.GROUP_BY_COMMUNITY)
					.getBuckets();

			for (TermsAggregation.Entry trackIdCommunityEntry :trackIdCommunityBuckets) {
				String trackIdCommunity = trackIdCommunityEntry.getKey();
				List<TermsAggregation.Entry> trackIdGenderBuckets = trackIdCommunityEntry.getTermsAggregation(ElasticsearchConstant.GROUP_BY_GENDER)
						.getBuckets();
				for (TermsAggregation.Entry trackIdGenderEntry : trackIdGenderBuckets) {
					String trackIdGender = trackIdGenderEntry.getKey();
					//人次
					Long count  = trackIdGenderEntry.getValueCountAggregation(ElasticsearchConstant.ID_COUNT)
							.getValueCount();
					if (count == null) {
						count = 0L;
					}
					String key = trackIdCommunity + trackIdGender;
					trackIdMap.put(key, count);
				}
			}

			if (MapUtils.isEmpty(trackIdMap)) {
				logger.error(LoggerConstant.SAVE_FACE_DATA_COMMUNITY_SEX + MessageConstant.PEOPLE_NUMBER_IS_NULL);
				return;
			}
			/** 查询人数 */
			//小区分组
			TermsAggregationBuilder visitorIdCommunityTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_COMMUNITY)
					.field(ElasticsearchConstant.COMMUNITY_KEYWORD)
					.size(Integer.MAX_VALUE);

			//按性别分组
			TermsAggregationBuilder visitorIdGenderTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_GENDER)
					.field(ElasticsearchConstant.GENDER_KEYWORD)
					.size(Integer.MAX_VALUE);

			//按visitorId分组，来统计人数
			TermsAggregationBuilder visitorIdTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_VISITOR_ID)
					.field(ElasticsearchConstant.VISITOR_ID)
					.size(Integer.MAX_VALUE);

			visitorIdGenderTermsAggregationBuilder.subAggregation(visitorIdTermsAggregationBuilder);
			visitorIdCommunityTermsAggregationBuilder.subAggregation(visitorIdGenderTermsAggregationBuilder);

			//人脸ID为-1，不计入人数
			MatchPhraseQueryBuilder visitorIdMathcPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.VISITOR_ID, BusinessConstant.MINUS_ONE);

			//加入条件（人脸ID）
			boolQueryBuilder.mustNot(visitorIdMathcPhraseQueryBuilder);

			SearchResult searchResult = elasticsearchService.doSearch(indexName, indexType, null, boolQueryBuilder, visitorIdCommunityTermsAggregationBuilder);
			// 处理错误
			String errorMessage = searchResult.getErrorMessage();
			if (StringUtils.isNotBlank(errorMessage)) {
				logger.error(LoggerConstant.SAVE_FACE_DATA_COMMUNITY_SEX + ": {}", errorMessage);
				return;
			}

			List<TermsAggregation.Entry> communityBuckets = searchResult.getAggregations()
					.getTermsAggregation(ElasticsearchConstant.GROUP_BY_COMMUNITY)
					.getBuckets();

			List<FaceDataCommunitySex> faceDataCommunitySexList = new ArrayList<>();

			//小区桶列表
			for (TermsAggregation.Entry communityEntry : communityBuckets) {
				//小区名-城市
				String communityCity = communityEntry.getKey();

				//处理城市和小区
				//小区
				String community = "";
				//城市
				String city = "";
				//以“-”切割
				String[] strArr = communityCity.split("-");
				if (strArr != null && strArr.length > 0) {
					community = strArr[0];
					city = strArr[1];
				}
				//如果城市包含了“市”，替换掉“市”（因为frp_device_info和frp_province_city表，城市去掉了“市”）
				if (city.endsWith(BusinessConstant.CHINA_SHI)) {
					city = city.substring(0, city.length() - 1);
				}
				//根据城市获取省
				String province = cityProvinceMap.get(city);
				List<TermsAggregation.Entry> genderBuckets = communityEntry.getTermsAggregation(ElasticsearchConstant.GROUP_BY_GENDER)
						.getBuckets();

				for (TermsAggregation.Entry genderEntry : genderBuckets) {
					String gengder = genderEntry.getKey();

					String mapKey = communityCity + gengder;

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
					//人数
					int peopleNum = genderEntry.getTermsAggregation(ElasticsearchConstant.GROUP_BY_VISITOR_ID)
							.getBuckets()
							.size();

					//人次
					Long peopleTime = trackIdMap.get(mapKey);
					if (peopleTime == null) {
						peopleTime = 0L;
					}

					FaceDataCommunitySex faceDataCommunitySex = new FaceDataCommunitySex();
					faceDataCommunitySex.setDate(date);
					faceDataCommunitySex.setCommunity(community);
					faceDataCommunitySex.setCity(city);
					faceDataCommunitySex.setProvince(province);
					faceDataCommunitySex.setSex(gengder);
					faceDataCommunitySex.setPeopleNum(peopleNum);
					faceDataCommunitySex.setPeopleTime(Integer.valueOf(String.valueOf(peopleTime)));
					faceDataCommunitySex.setAddTime(System.currentTimeMillis());
					faceDataCommunitySexList.add(faceDataCommunitySex);
				}
			}
			//保存数据到数据库
			if (CollectionUtils.isNotEmpty(faceDataCommunitySexList)) {
				faceDataCommunitySexService.insertFaceDataCommunitySexBatch(faceDataCommunitySexList);
				// 日志
				String nowTime = DateUtils.getNowDateTime("");
				logger.info("{} " + LoggerConstant.SAVE_FACE_DATA_COMMUNITY_SEX + MessageConstant.SAVING_IS_SUCCESSFUL, nowTime);
			}
		} catch (ParseException | IOException | ServiceException e) {
			logger.error(LoggerConstant.SAVE_FACE_DATA_COMMUNITY_SEX, e);
		}
	}


	/**
	 * 保存人脸数据（小区时间）
	 */
	@Scheduled(cron = "0 45 3 * * ?")
	public void saveFaceDataCommunityTime(){
		// 获取昨天的日期
		String date = DateUtils.getPastDay(DateConstant.YESTERDAY, DateConstant.DATE_YEAR_MONTH_DAY);
		// 索引名称
		String indexName = ElasticsearchConstant.FACE_DATA;
		// 索引类型
		String indexType = "";
		// 计算一天中的最小日期
		try {
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
				logger.error(LoggerConstant.SAVE_FACE_DATA_COMMUNITY_TIME + MessageConstant.DEVICE_TYPE_IS_NULLL);
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

			// 查询人次
			// 小区分组（人次）
			TermsAggregationBuilder trackIdCommunityTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_COMMUNITY)
					.field(ElasticsearchConstant.COMMUNITY_KEYWORD)
					.size(Integer.MAX_VALUE);

			// 按时间分组（人次）
			TermsAggregationBuilder trackIdDateTimeTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_DATE_TIME)
					.field(ElasticsearchConstant.DATE_TIME_KEYWORD)
					.size(Integer.MAX_VALUE);

			// 根据id来统计人次
			ValueCountAggregationBuilder trackIdCountAggregationBuilder = AggregationBuilders.count(ElasticsearchConstant.ID_COUNT)
					.field(ElasticsearchConstant.ID);

			trackIdDateTimeTermsAggregationBuilder.subAggregation(trackIdCountAggregationBuilder);
			trackIdCommunityTermsAggregationBuilder.subAggregation(trackIdDateTimeTermsAggregationBuilder);

			// 查询
			SearchResult trackIdSearchResult = elasticsearchService.doSearch(indexName, indexType, null, boolQueryBuilder, trackIdCommunityTermsAggregationBuilder);
			// 处理错误
			String trackIdErrorMessage = trackIdSearchResult.getErrorMessage();
			if (StringUtils.isNotBlank(trackIdErrorMessage)) {
				logger.error(LoggerConstant.SAVE_FACE_DATA_COMMUNITY_TIME + ": {}", trackIdErrorMessage);
				return;
			}

			//人次Map
			Map<String, Long> peopleTimeMap = new HashMap<>();

			List<TermsAggregation.Entry> communityPeopleNumBuckets = trackIdSearchResult.getAggregations()
					.getTermsAggregation(ElasticsearchConstant.GROUP_BY_COMMUNITY)
					.getBuckets();

			//小区桶列表
			for (TermsAggregation.Entry communityPeopleNumEntry : communityPeopleNumBuckets) {
				//小区名-城市
				String communityCity = communityPeopleNumEntry.getKey();
				List<TermsAggregation.Entry> dateTimeBuckets = communityPeopleNumEntry.getTermsAggregation(ElasticsearchConstant.GROUP_BY_DATE_TIME)
						.getBuckets();

				for (TermsAggregation.Entry dateTimeEntry : dateTimeBuckets) {
					String dateTime = dateTimeEntry.getKey();
					//人次
					Long peopleTime = dateTimeEntry.getValueCountAggregation(ElasticsearchConstant.ID_COUNT)
							.getValueCount();

					if (peopleTime == null) {
						peopleTime = 0L;
					}
					String key = communityCity + dateTime;
					peopleTimeMap.put(key, peopleTime);
				}
			}

			if (MapUtils.isEmpty(peopleTimeMap)) {
				logger.error(LoggerConstant.SAVE_FACE_DATA_COMMUNITY_TIME + MessageConstant.PEOPLE_NUMBER_IS_NULL);
				return;
			}

			// 查询人数
			// 小区分组（人数）
			TermsAggregationBuilder visitorIdCommunityTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_COMMUNITY)
					.field(ElasticsearchConstant.COMMUNITY_KEYWORD)
					.size(Integer.MAX_VALUE);
			// 按时间分组（人数）
			TermsAggregationBuilder visitorIdDateTimeTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_DATE_TIME)
					.field(ElasticsearchConstant.DATE_TIME_KEYWORD)
					.size(Integer.MAX_VALUE);
			// 按visitorId分组，来统计人数
			TermsAggregationBuilder visitorIdTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_VISITOR_ID)
					.field(ElasticsearchConstant.VISITOR_ID)
					.size(Integer.MAX_VALUE);

			visitorIdDateTimeTermsAggregationBuilder.subAggregation(visitorIdTermsAggregationBuilder);
			visitorIdCommunityTermsAggregationBuilder.subAggregation(visitorIdDateTimeTermsAggregationBuilder);

			// 过滤性别为“”的数据，性别为“”的数据不计入人数,只查询查性别为“M”“F”
			MatchPhraseQueryBuilder maleMatchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.GENDER_KEYWORD, ElasticsearchConstant.STRING_M);
			MatchPhraseQueryBuilder femaleMatchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.GENDER_KEYWORD, ElasticsearchConstant.STRING_F);
			BoolQueryBuilder genderBoolQueryBuilder = QueryBuilders.boolQuery();
			genderBoolQueryBuilder.should(maleMatchPhraseQueryBuilder)
					.should(femaleMatchPhraseQueryBuilder);

			// 人脸ID为-1，不计入人数
			MatchPhraseQueryBuilder visitorIdMathcPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.VISITOR_ID, BusinessConstant.MINUS_ONE);

			// 条件（性别，人脸ID）
			boolQueryBuilder.must(genderBoolQueryBuilder).mustNot(visitorIdMathcPhraseQueryBuilder);
			// 查询
			SearchResult visitorIdSearchResult = elasticsearchService.doSearch(indexName, indexType, null, boolQueryBuilder, visitorIdCommunityTermsAggregationBuilder);

			// 处理错误
			String visitorIdErrorMessage = visitorIdSearchResult.getErrorMessage();
			if (StringUtils.isNotBlank(visitorIdErrorMessage)) {
				logger.error(LoggerConstant.SAVE_FACE_DATA_COMMUNITY_TIME + ": {}", visitorIdErrorMessage);
				return;
			}

			List<TermsAggregation.Entry> communityPeopleTimeBuckets = visitorIdSearchResult.getAggregations()
					.getTermsAggregation(ElasticsearchConstant.GROUP_BY_COMMUNITY)
					.getBuckets();

			List<FaceDataCommunityTime> faceDataCommunityTimeList = new ArrayList<>();

			// 小区桶列表
			for (TermsAggregation.Entry communityPeopleTimeEntry : communityPeopleTimeBuckets) {
				// 小区名-城市
				String communityCity = communityPeopleTimeEntry.getKey();

				// 处理城市和小区
				// 小区
				String community = "";
				// 以“-”切割
				String[] strArr = communityCity.split("-");
				if (strArr != null && strArr.length > 0) {
					community = strArr[0];
				}

				List<TermsAggregation.Entry> dateTimeBuckets = communityPeopleTimeEntry.getTermsAggregation(ElasticsearchConstant.GROUP_BY_DATE_TIME)
						.getBuckets();

				for (TermsAggregation.Entry dateTimeEntry : dateTimeBuckets) {
					String dateTime = dateTimeEntry.getKey();
					String key = communityCity + dateTime;
					// 取dateTime的前八位作为年月日作为date,这样的数据才准确
					String dateStr = dateTime.substring(0, 8);
					// 取dateTime的最后两位小时作为dateTime,根据小时分组是，方便
					String dateTimeStr = dateTime.substring(8, 10);

					// 人数
					int peopleNum = dateTimeEntry.getTermsAggregation(ElasticsearchConstant.GROUP_BY_VISITOR_ID)
							.getBuckets()
							.size();
					// 人次
					Long peopleTime = peopleTimeMap.get(key);
					if (peopleTime == null) {
						peopleTime = 0L;
					}

					FaceDataCommunityTime faceDataCommunityTime = new FaceDataCommunityTime();
					faceDataCommunityTime.setDate(dateStr);
					faceDataCommunityTime.setTime(dateTimeStr);
					faceDataCommunityTime.setCommunity(community);
					faceDataCommunityTime.setPeopleNum(peopleNum);
					faceDataCommunityTime.setPeopleTime(peopleTime.intValue());
					faceDataCommunityTime.setAddTime(System.currentTimeMillis());
					faceDataCommunityTimeList.add(faceDataCommunityTime);
				}
			}
			// 保存数据到数据库
			if (CollectionUtils.isNotEmpty(faceDataCommunityTimeList)) {
				faceDataCommunityTimeService.insertFaceDataCommunityTimeBatch(faceDataCommunityTimeList);
				// 日志
				String nowTime = DateUtils.getNowDateTime("");
				logger.info("{} " + LoggerConstant.SAVE_FACE_DATA_COMMUNITY_TIME + MessageConstant.SAVING_IS_SUCCESSFUL, nowTime);
			}
		} catch (ParseException | IOException | ServiceException e) {
			logger.error(LoggerConstant.SAVE_FACE_DATA_COMMUNITY_TIME, e);
		}
	}

	/**
	 * 保存人脸数据（小区时间年龄）
	 */
	@Scheduled(cron = "0 50 3 * * ?")
	public void saveFaceDataCommunityTimeAge(){
		// 获取昨天的日期
		String date = DateUtils.getPastDay(DateConstant.YESTERDAY, DateConstant.DATE_YEAR_MONTH_DAY);
		// 索引名称
		String indexName = ElasticsearchConstant.FACE_DATA;
		// 索引类型
		String indexType = "";
		// 计算一天中的最小日期
		try {
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
				logger.error(LoggerConstant.SAVE_FACE_DATA_COMMUNITY_TIME_AGE + MessageConstant.DEVICE_TYPE_IS_NULLL);
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

			/**查询人次*/
			// 小区分组(人次)
			TermsAggregationBuilder trackIdCommunityTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_COMMUNITY)
					.field(ElasticsearchConstant.COMMUNITY_KEYWORD)
					.size(Integer.MAX_VALUE);

			// 按时间分组(人次)
			TermsAggregationBuilder trackIdDateTimeTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_DATE_TIME)
					.field(ElasticsearchConstant.DATE_TIME_KEYWORD)
					.size(Integer.MAX_VALUE);

			// 按年龄分组(人次)
			TermsAggregationBuilder trackIdAgeTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_AGE_RANGE)
					.field(ElasticsearchConstant.AGE_RANGE_KEYWORD)
					.size(Integer.MAX_VALUE);

			// 根据id来统计人次
			ValueCountAggregationBuilder trackIdCountAggregationBuilder = AggregationBuilders.count(ElasticsearchConstant.ID_COUNT)
					.field(ElasticsearchConstant.ID);

			trackIdAgeTermsAggregationBuilder.subAggregation(trackIdCountAggregationBuilder);
			trackIdDateTimeTermsAggregationBuilder.subAggregation(trackIdAgeTermsAggregationBuilder);
			trackIdCommunityTermsAggregationBuilder.subAggregation(trackIdDateTimeTermsAggregationBuilder);

			// 查询
			SearchResult trackIdSearchResult = elasticsearchService.doSearch(indexName, indexType, null, boolQueryBuilder, trackIdCommunityTermsAggregationBuilder);
			// 处理错误
			String trackIdErrorMessage = trackIdSearchResult.getErrorMessage();
			if (StringUtils.isNotBlank(trackIdErrorMessage)) {
				logger.error(LoggerConstant.SAVE_FACE_DATA_COMMUNITY_TIME_AGE + ": {}", trackIdErrorMessage);
				return;
			}
			// 人次Map
			Map<String, Long> peopleTimeMap = new HashMap<>();

			List<TermsAggregation.Entry> communityPeopleNumBuckets = trackIdSearchResult.getAggregations()
					.getTermsAggregation(ElasticsearchConstant.GROUP_BY_COMMUNITY)
					.getBuckets();

			// 小区桶列表
			for (TermsAggregation.Entry communityPeopleNumEntry : communityPeopleNumBuckets) {
				// 小区名-城市
				String communityCity = communityPeopleNumEntry.getKey();

				List<TermsAggregation.Entry> dateTimeBuckets = communityPeopleNumEntry.getTermsAggregation(ElasticsearchConstant.GROUP_BY_DATE_TIME)
						.getBuckets();

				// 时间桶列表
				for (TermsAggregation.Entry dateTimeEntry : dateTimeBuckets) {
					String dateTime = dateTimeEntry.getKey();

					TermsAggregation ageTermsAggregation = dateTimeEntry.getTermsAggregation(ElasticsearchConstant.GROUP_BY_AGE_RANGE);

					List<TermsAggregation.Entry> ageBuckets = ageTermsAggregation.getBuckets();
					// 年龄桶列表
					for (TermsAggregation.Entry ageEntry : ageBuckets) {
						String age = ageEntry.getKey();
						String key = communityCity + dateTime + age;
						// 人次
						Long peopleTime = ageEntry.getValueCountAggregation(ElasticsearchConstant.ID_COUNT)
								.getValueCount();
						if (peopleTime == null) {
							peopleTime = 0L;
						}
						peopleTimeMap.put(key, peopleTime);
					}
				}
			}

			if (MapUtils.isEmpty(peopleTimeMap)) {
				logger.error(LoggerConstant.SAVE_FACE_DATA_COMMUNITY_TIME_AGE + MessageConstant.PEOPLE_NUMBER_IS_NULL);
				return;
			}

			/**查询人数*/
			// 小区分组(人数)
			TermsAggregationBuilder visitorIdCommunityTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_COMMUNITY)
					.field(ElasticsearchConstant.COMMUNITY_KEYWORD)
					.size(Integer.MAX_VALUE);
			// 按时间分组(人数)
			TermsAggregationBuilder visitorIdDateTimeTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_DATE_TIME)
					.field(ElasticsearchConstant.DATE_TIME_KEYWORD)
					.size(Integer.MAX_VALUE);
			// 按年龄分组(人数)
			TermsAggregationBuilder visitorIdAgeTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_AGE_RANGE)
					.field(ElasticsearchConstant.AGE_RANGE_KEYWORD)
					.size(Integer.MAX_VALUE);
			// 按visitorId分组，来统计人数
			TermsAggregationBuilder visitorIdTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_VISITOR_ID)
					.field(ElasticsearchConstant.VISITOR_ID)
					.size(Integer.MAX_VALUE);

			visitorIdAgeTermsAggregationBuilder.subAggregation(visitorIdTermsAggregationBuilder);
			visitorIdDateTimeTermsAggregationBuilder.subAggregation(visitorIdAgeTermsAggregationBuilder);
			visitorIdCommunityTermsAggregationBuilder.subAggregation(visitorIdDateTimeTermsAggregationBuilder);

			// 过滤性别为“”的数据，性别为“”的数据不计入人数,只查询查性别为“M”“F”
			MatchPhraseQueryBuilder maleMatchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.GENDER_KEYWORD, ElasticsearchConstant.STRING_M);
			MatchPhraseQueryBuilder femaleMatchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.GENDER_KEYWORD, ElasticsearchConstant.STRING_F);
			BoolQueryBuilder genderBoolQueryBuilder = QueryBuilders.boolQuery();
			genderBoolQueryBuilder.should(maleMatchPhraseQueryBuilder)
					.should(femaleMatchPhraseQueryBuilder);

			// 人脸ID为-1，不计入人数
			MatchPhraseQueryBuilder visitorIdMathcPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.VISITOR_ID, BusinessConstant.MINUS_ONE);

			// 条件（性别，人脸ID）
			boolQueryBuilder.must(genderBoolQueryBuilder).mustNot(visitorIdMathcPhraseQueryBuilder);
			// 查询
			SearchResult visitorIdSearchResult = elasticsearchService.doSearch(indexName, indexType, null, boolQueryBuilder, visitorIdCommunityTermsAggregationBuilder);
			// 处理错误
			String visitorIdErrorMessage = visitorIdSearchResult.getErrorMessage();
			if (StringUtils.isNotBlank(visitorIdErrorMessage)) {
				logger.error(LoggerConstant.SAVE_FACE_DATA_COMMUNITY_TIME_AGE + ": {}", visitorIdErrorMessage);
				return;
			}

			List<TermsAggregation.Entry> communityPeopleTimeBuckets = visitorIdSearchResult.getAggregations()
					.getTermsAggregation(ElasticsearchConstant.GROUP_BY_COMMUNITY)
					.getBuckets();

			List<FaceDataCommunityTimeAge> faceDataCommunityTimeAgeList = new ArrayList<>();

			// 小区桶列表
			for (TermsAggregation.Entry communityPeopleTimeEntry : communityPeopleTimeBuckets) {
				// 小区名-城市
				String communityCity = communityPeopleTimeEntry.getKey();

				// 处理城市和小区
				// 小区
				String community = "";
				// 以“-”切割
				String[] strArr = communityCity.split("-");
				if (strArr != null && strArr.length > 0) {
					community = strArr[0];
				}

				List<TermsAggregation.Entry> dateTimeBuckets = communityPeopleTimeEntry.getTermsAggregation(ElasticsearchConstant.GROUP_BY_DATE_TIME)
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
						String key = communityCity + dateTime + age;

						// 人数
						int peopleNum = ageEntry.getTermsAggregation(ElasticsearchConstant.GROUP_BY_VISITOR_ID)
								.getBuckets()
								.size();

						// 人次
						Long peopleTime = peopleTimeMap.get(key);
						if (peopleTime == null) {
							peopleTime = 0L;
						}

						FaceDataCommunityTimeAge faceDataCommunityTimeAge = new FaceDataCommunityTimeAge();
						faceDataCommunityTimeAge.setCommunity(community);
						faceDataCommunityTimeAge.setDate(dateStr);
						faceDataCommunityTimeAge.setTime(dateTimeStr);
						faceDataCommunityTimeAge.setAge(age);
						faceDataCommunityTimeAge.setPeopleNum(peopleNum);
						faceDataCommunityTimeAge.setPeopleTime(peopleTime.intValue());
						faceDataCommunityTimeAge.setAddTime(System.currentTimeMillis());
						faceDataCommunityTimeAgeList.add(faceDataCommunityTimeAge);
					}
				}
			}
			//保存数据到数据库
			if (CollectionUtils.isNotEmpty(faceDataCommunityTimeAgeList)) {
				faceDataCommunityTimeAgeService.insertFaceDataCommunityTimeAgeBatch(faceDataCommunityTimeAgeList);
				// 日志
				String nowTime = DateUtils.getNowDateTime("");
				logger.info("{} " + LoggerConstant.SAVE_FACE_DATA_COMMUNITY_TIME_AGE + MessageConstant.SAVING_IS_SUCCESSFUL, nowTime);
			}
		} catch (ParseException |IOException | ServiceException e) {
			logger.error(LoggerConstant.SAVE_FACE_DATA_COMMUNITY_TIME_AGE, e);
		}
	}


	/**
	 * 保存人脸数据（小区终端）
	 */
	@Scheduled(cron = "0 55 3 * * ?")
	public void saveFaceDataCommunityDevice(){
		// 获取城市省Map
		Map<String, String> cityProvinceMap = provinceCityService.getCityProvinceMap();
		if (cityProvinceMap == null || cityProvinceMap.size() == 0){
			logger.error(LoggerConstant.SAVE_FACE_DATA_COMMUNITY_DEVICE + MessageConstant.FRP_PROVINCE_CITY_TABLE_IS_NULL);
			return;
		}
		// 获取昨天的日期
		String date = DateUtils.getPastDay(DateConstant.YESTERDAY, DateConstant.DATE_YEAR_MONTH_DAY);
		// 索引名称
		String indexName = ElasticsearchConstant.FACE_DATA;
		// 索引类型
		String indexType = "";
		// 计算一天中的最小日期
		try {
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
				logger.error(LoggerConstant.SAVE_FACE_DATA_COMMUNITY_DEVICE + MessageConstant.DEVICE_TYPE_IS_NULLL);
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

			// 小区分组
			TermsAggregationBuilder communityTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_COMMUNITY)
					.field(ElasticsearchConstant.COMMUNITY_KEYWORD)
					.size(Integer.MAX_VALUE);

			// 按终端编码分组
			TermsAggregationBuilder deviceCodeTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_DEVICE_CODE)
					.field(ElasticsearchConstant.DEVICE_CODE_KEYWORD)
					.size(Integer.MAX_VALUE);

			communityTermsAggregationBuilder.subAggregation(deviceCodeTermsAggregationBuilder);

			SearchResult searchResult = elasticsearchService.doSearch(indexName, indexType, null, boolQueryBuilder, communityTermsAggregationBuilder);
			// 处理错误
			String errorMessage = searchResult.getErrorMessage();
			if (StringUtils.isNotBlank(errorMessage)) {
				logger.error(LoggerConstant.SAVE_FACE_DATA_COMMUNITY_DEVICE + ": {}", errorMessage);
				return;
			}

			List<TermsAggregation.Entry> communityBuckets = searchResult.getAggregations()
					.getTermsAggregation(ElasticsearchConstant.GROUP_BY_COMMUNITY)
					.getBuckets();

			List<FaceDataCommunityDevice> faceDataCommunityDeviceList = new ArrayList<>();

			// 小区桶列表
			for (TermsAggregation.Entry communityEntry : communityBuckets) {
				// 小区名-城市
				String communityCity = communityEntry.getKey();

				// 处理城市和小区
				// 小区
				String community = "";
				// 城市
				String city = "";
				// 以“-”切割
				String[] strArr = communityCity.split("-");
				if (strArr != null && strArr.length > 0) {
					community = strArr[0];
					city = strArr[1];
				}
				// 如果城市包含了“市”，替换掉“市”（因为frp_device_info和frp_province_city表，城市去掉了“市”）
				if (city.endsWith(BusinessConstant.CHINA_SHI)) {
					city = city.substring(0, city.length() - 1);
				}
				// 根据城市获取省份
				String province = cityProvinceMap.get(city);
				List<TermsAggregation.Entry> deviceCodeBuckets = communityEntry.getTermsAggregation(ElasticsearchConstant.GROUP_BY_DEVICE_CODE)
						.getBuckets();

				// 终端编码桶列表
				for (TermsAggregation.Entry deviceCodeEntry : deviceCodeBuckets) {
					String deviceCode = deviceCodeEntry.getKey();
					FaceDataCommunityDevice faceDataCommunityDevice = new FaceDataCommunityDevice();
					faceDataCommunityDevice.setDate(date);
					faceDataCommunityDevice.setCommunity(community);
					faceDataCommunityDevice.setCity(city);
					faceDataCommunityDevice.setProvince(province);
					faceDataCommunityDevice.setDeviceCode(deviceCode);
					faceDataCommunityDevice.setAddTime(System.currentTimeMillis());
					faceDataCommunityDeviceList.add(faceDataCommunityDevice);
				}
			}
			// 保存数据到数据库
			if (CollectionUtils.isNotEmpty(faceDataCommunityDeviceList)) {
				faceDataCommunityDeviceService.insertFaceDataCommunityDeviceBatch(faceDataCommunityDeviceList);
				// 日志
				String nowTime = DateUtils.getNowDateTime("");
				logger.info("{} " + LoggerConstant.SAVE_FACE_DATA_COMMUNITY_DEVICE + MessageConstant.SAVING_IS_SUCCESSFUL, nowTime);
			}
		} catch (ParseException | IOException | ServiceException e) {
			logger.error(LoggerConstant.SAVE_FACE_DATA_COMMUNITY_DEVICE, e);
		}
	}

}
