package com.du.elasticsearch.jobs;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xinchao.data.cacahe.RedisCacheDao;
import com.xinchao.data.config.CustomProperties;
import com.xinchao.data.constant.*;
import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.*;
import com.xinchao.data.model.dto.*;
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
 * 处理人脸数据Job
 * @author dxy
 * @date 2019/3/2 8:49
 */
@Component
public class FaceDataJob {
    private static Logger logger = LoggerFactory.getLogger(FaceDataJob.class);
    @Autowired
    private ElasticsearchService elasticsearchService;
    @Autowired
    private FaceDataAgeService faceDataAgeService;
    @Autowired
    private FaceDataTimeService faceDataTimeService;
    @Autowired
    private FaceDataSexService faceDataSexService;
    @Autowired
    private FaceDataProvinceService faceDataProvinceService;
    @Autowired
    private FaceDataDateService faceDataDateService;
    @Autowired
    private FaceDataCommunityService faceDataCommunityService;
    @Autowired
    private FaceDataCommunitySexService faceDataCommunitySexService;
    @Autowired
    private FaceDataCommunityAgeService faceDataCommunityAgeService;
    @Autowired
    private ProvinceCityService provinceCityService;
    @Autowired
    private FaceDataCommunityDeviceService faceDataCommunityDeviceService;
    @Autowired
    private CustomProperties customProperties;
    @Autowired
    private RedisCacheDao redisCacheDao;


    /**
     * 保存人脸数据（按年龄统计）
     */
    @Scheduled(cron = "0 25 4  * * ?")
    public void saveFaceDataAge(){
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
		        logger.error(LoggerConstant.SAVE_FACE_DATA_AGE + MessageConstant.DEVICE_TYPE_IS_NULLL);
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
            //按性别分组
            TermsAggregationBuilder trackIdAgeTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_AGE_RANGE)
                    .field(ElasticsearchConstant.AGE_RANGE_KEYWORD)
                    .size(Integer.MAX_VALUE);

            //根据id来统计人次
            ValueCountAggregationBuilder trackIdCountAggregationBuilder = AggregationBuilders.count(ElasticsearchConstant.ID_COUNT)
                    .field(ElasticsearchConstant.ID);

            trackIdAgeTermsAggregationBuilder.subAggregation(trackIdCountAggregationBuilder);

            SearchResult trackIdSearchResult = elasticsearchService.doSearch(indexName, indexType, null, boolQueryBuilder, trackIdAgeTermsAggregationBuilder);
            String trackIdErrorMessage = trackIdSearchResult.getErrorMessage();

            //人次Map
            Map<String, Long> peopleTimeMap = new HashMap<>();

            if (StringUtils.isNotBlank(trackIdErrorMessage)) {
                logger.error(LoggerConstant.SAVE_FACE_DATA_AGE + ": {}", trackIdErrorMessage);
            } else {
                List<TermsAggregation.Entry> ageBuckets = trackIdSearchResult.getAggregations()
                        .getTermsAggregation(ElasticsearchConstant.GROUP_BY_AGE_RANGE)
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
                    peopleTimeMap.put(age, peopleTime);
                }
            }

            /** 查询人数 */
            // 按性别分组
            TermsAggregationBuilder visitorIdAgeTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_AGE_RANGE)
                    .field(ElasticsearchConstant.AGE_RANGE_KEYWORD)
                    .size(Integer.MAX_VALUE);
            // 按visitorId分组，来统计人数
            TermsAggregationBuilder visitorIdTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_VISITOR_ID)
                    .field(ElasticsearchConstant.VISITOR_ID)
                    .size(Integer.MAX_VALUE);

            visitorIdAgeTermsAggregationBuilder.subAggregation(visitorIdTermsAggregationBuilder);

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

            SearchResult visitorIdSearchResult = elasticsearchService.doSearch(indexName, indexType, null, boolQueryBuilder, visitorIdAgeTermsAggregationBuilder);
            String visitorIdErrorMessage = visitorIdSearchResult.getErrorMessage();
            
            if (StringUtils.isNotBlank(visitorIdErrorMessage)) {
                logger.error(LoggerConstant.SAVE_FACE_DATA_AGE + ": {}", visitorIdErrorMessage);
            } else {
                List<TermsAggregation.Entry> ageBuckets = visitorIdSearchResult.getAggregations()
                        .getTermsAggregation(ElasticsearchConstant.GROUP_BY_AGE_RANGE)
                        .getBuckets();

                List<FaceDataAge> faceDataAgeList = new ArrayList<>();

                // 年龄桶列表
                for (TermsAggregation.Entry ageEntry : ageBuckets) {
                    String age = ageEntry.getKey();
                    // 人次
                    Long peopleTime = peopleTimeMap.get(age);
                    if (peopleTime == null) {
		                peopleTime = 0L;
	                }
                    //人数
                    int peopleNum = ageEntry.getTermsAggregation(ElasticsearchConstant.GROUP_BY_VISITOR_ID)
                            .getBuckets()
                            .size();

                    FaceDataAge faceDataAge = new FaceDataAge();
                    faceDataAge.setDate(date);
                    faceDataAge.setAge(age);
                    faceDataAge.setPeopleNum(peopleNum);
                    faceDataAge.setPeopleTime(peopleTime.intValue());
                    faceDataAge.setAddTime(System.currentTimeMillis());
                    faceDataAgeList.add(faceDataAge);
                }
                // 保存数据到数据库
                if (CollectionUtils.isNotEmpty(faceDataAgeList)) {
                    faceDataAgeService.insertFaceDataAgeBatch(faceDataAgeList);
                    // 日志
                    String nowTime = DateUtils.getNowDateTime("");
                    logger.info("{} " + LoggerConstant.SAVE_FACE_DATA_AGE + MessageConstant.SAVING_IS_SUCCESSFUL, nowTime);
                }
            }
        } catch (ParseException | IOException | ServiceException e) {
            logger.error(LoggerConstant.SAVE_FACE_DATA_AGE, e);
        }
    }

    /**
     * 保存人脸数据（性别）
     * 此方法没有加性别过滤条件，是因为按性别分组，可以知道性别为“不限”的人次和人数
     */
    @Scheduled(cron = "0 30 4  * * ?")
    public void saveFaceDataSex(){
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
		        logger.error(LoggerConstant.SAVE_FACE_DATA_SEX + MessageConstant.DEVICE_TYPE_IS_NULLL);
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

            /**查询人次*/
            // 按性别分组
            TermsAggregationBuilder trackIdGenderTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_GENDER)
                    .field(ElasticsearchConstant.GENDER_KEYWORD)
                    .size(Integer.MAX_VALUE);

            // 根据id来统计人次
            ValueCountAggregationBuilder trackIdCountAggregationBuilder = AggregationBuilders.count(ElasticsearchConstant.ID_COUNT)
                    .field(ElasticsearchConstant.ID);

            trackIdGenderTermsAggregationBuilder.subAggregation(trackIdCountAggregationBuilder);

            SearchResult trackIdsearchResult = elasticsearchService.doSearch(indexName, indexType, null, boolQueryBuilder, trackIdGenderTermsAggregationBuilder);
            String trackIdErrorMessage = trackIdsearchResult.getErrorMessage();

            //key为性别；value为人次
            Map<String, Long> trackIdMap = Maps.newHashMap();

            if (StringUtils.isNotBlank(trackIdErrorMessage)) {
                logger.error(LoggerConstant.SAVE_FACE_DATA_SEX + ": {}", trackIdErrorMessage);
            } else {
                List<TermsAggregation.Entry> trackIdGenderBuckets = trackIdsearchResult.getAggregations()
                        .getTermsAggregation(ElasticsearchConstant.GROUP_BY_GENDER)
                        .getBuckets();

                // 性别桶列表
                for (TermsAggregation.Entry trackIdGenderEntry : trackIdGenderBuckets) {
                    String gengder = trackIdGenderEntry.getKey();
                    //人次
                    Long peopleTime = trackIdGenderEntry.getValueCountAggregation(ElasticsearchConstant.ID_COUNT)
                            .getValueCount();
                    if (peopleTime == null) {
                        peopleTime = 0L;
                    }
                    trackIdMap.put(gengder, peopleTime);
                }
            }

            /** 查询人数 */
            // 按性别分组
            TermsAggregationBuilder visitorIdGenderTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_GENDER)
                    .field(ElasticsearchConstant.GENDER_KEYWORD)
                    .size(Integer.MAX_VALUE);

            // 按visitorId分组，来统计人数
            TermsAggregationBuilder visitorIdTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_VISITOR_ID)
                    .field(ElasticsearchConstant.VISITOR_ID)
                    .size(Integer.MAX_VALUE);

            visitorIdGenderTermsAggregationBuilder.subAggregation(visitorIdTermsAggregationBuilder);

            // 人脸ID为-1，不计入人数
            MatchPhraseQueryBuilder visitorIdMathcPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.VISITOR_ID, BusinessConstant.MINUS_ONE);
            // 条件（性别，人脸ID）
            boolQueryBuilder.mustNot(visitorIdMathcPhraseQueryBuilder);

            SearchResult searchResult = elasticsearchService.doSearch(indexName, indexType, null, boolQueryBuilder, visitorIdGenderTermsAggregationBuilder);
            String errorMessage = searchResult.getErrorMessage();
            if (StringUtils.isNotBlank(errorMessage)) {
                logger.error(LoggerConstant.SAVE_FACE_DATA_SEX + ": {}", errorMessage);
            } else {
                List<TermsAggregation.Entry> genderBuckets = searchResult.getAggregations()
                        .getTermsAggregation(ElasticsearchConstant.GROUP_BY_GENDER)
                        .getBuckets();

                List<FaceDataSex> faceDataSexList = new ArrayList<>();

                //性别桶列表
                for (TermsAggregation.Entry genderEntry : genderBuckets) {
                    String gengder = genderEntry.getKey();
                    //人次
                    Long peopleTime = trackIdMap.get(gengder);
                    if (peopleTime == null) {
                        peopleTime = 0L;
                    }

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

                    FaceDataSex faceDataSex = new FaceDataSex();
                    faceDataSex.setDate(date);
                    faceDataSex.setSex(gengder);
                    faceDataSex.setPeopleNum(peopleNum);
                    faceDataSex.setPeopleTime(Integer.valueOf(String.valueOf(peopleTime)));
                    faceDataSex.setAddTime(System.currentTimeMillis());
                    faceDataSexList.add(faceDataSex);
                }
                //保存数据到数据库
                if (CollectionUtils.isNotEmpty(faceDataSexList)) {
                    faceDataSexService.insertFaceDataSexBatch(faceDataSexList);
                    // 日志
                    String nowTime = DateUtils.getNowDateTime("");
                    logger.info("{} " + LoggerConstant.SAVE_FACE_DATA_SEX + MessageConstant.SAVING_IS_SUCCESSFUL, nowTime);
                }
            }
        } catch (ParseException | IOException | ServiceException e) {
             logger.error(LoggerConstant.SAVE_FACE_DATA_SEX, e);
        }
    }

	/**
	 * 保存人脸数据（按日期统计）
	 */
	@Scheduled(cron = "0 35 4  * * ?")
	public void saveFaceDataDate(){
		// 获取昨天的日期
		String date = DateUtils.getPastDay(DateConstant.YESTERDAY, DateConstant.DATE_YEAR_MONTH_DAY);
		// 索引名称
		String indexName = ElasticsearchConstant.FACE_DATA;
		// 索引类型
		String indexType = "";
		try {
			// 计算一天中的最小日期
			Long fromTime = DateUtils.stringTimeToLongTimeStamp(date + " " + DateConstant.MIN_HOUR_MINUTE_SECOND_MILLISECOND, DateConstant.YEAR_MONTH_DAY_HMS_SSS);
			// 计算一天中的最大日期
			Long toTime = DateUtils.stringTimeToLongTimeStamp(date + " " + DateConstant.MAX_HOUR_MINUTE_SECOND_MILLISECOND, DateConstant.YEAR_MONTH_DAY_HMS_SSS);
			// 查询索引中所有文档
			MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
			// 时间条件
			RangeQueryBuilder queryBuilder = QueryBuilders.rangeQuery(ElasticsearchConstant.ENTER_TIME)
                    .from(fromTime)
                    .to(toTime)
                    .format(ElasticsearchConstant.EPOCH_MILLIS);

			// 加入终端类型
			Integer deviceType = customProperties.getDeviceType();
			if (deviceType == null) {
				logger.error(LoggerConstant.SAVE_FACE_DATA_DATE + MessageConstant.DEVICE_TYPE_IS_NULLL);
				return;
			}
			MatchPhraseQueryBuilder deviceTypeMatchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.DEVICE_TYPE, deviceType);

			// 多条件合并查询
			BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
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

			// 人次查询
			ValueCountAggregationBuilder trackIdCountAggregationBuilder = AggregationBuilders.count(ElasticsearchConstant.ID_COUNT)
                    .field(ElasticsearchConstant.ID);
			// 人数查询
			TermsAggregationBuilder visitorIdTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_VISITOR_ID)
                    .field(ElasticsearchConstant.VISITOR_ID)
                    .size(Integer.MAX_VALUE);

			// FaceDataDate
			FaceDataDate faceDataDate = new FaceDataDate();
			faceDataDate.setDate(date);

			/**查询人次*/
			SearchResult trackIdCountSearchResult = elasticsearchService.doSearch(indexName, indexType, null, boolQueryBuilder, trackIdCountAggregationBuilder);
			String trackIdErrorMessage = trackIdCountSearchResult.getErrorMessage();
			if (StringUtils.isNotBlank(trackIdErrorMessage)) {
				logger.error(LoggerConstant.SAVE_FACE_DATA_DATE + ": {}", trackIdErrorMessage);
			} else {
				Long trackIdCount = trackIdCountSearchResult.getAggregations()
                        .getValueCountAggregation(ElasticsearchConstant.ID_COUNT)
                        .getValueCount();

				if (trackIdCount == null) {
					trackIdCount = 0L;
				}
				faceDataDate.setPeopleTime(Integer.valueOf(String.valueOf(trackIdCount)));
			}

            /**中查询人数*/
            //过滤性别为“”的数据，性别为“”的数据不计入人数,只查询查性别为“M”“F”
            MatchPhraseQueryBuilder maleMatchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.GENDER_KEYWORD, ElasticsearchConstant.STRING_M);
            MatchPhraseQueryBuilder femaleMatchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.GENDER_KEYWORD, ElasticsearchConstant.STRING_F);

            BoolQueryBuilder genderBoolQueryBuilder = QueryBuilders.boolQuery();
            genderBoolQueryBuilder.should(maleMatchPhraseQueryBuilder)
                    .should(femaleMatchPhraseQueryBuilder);

            // 人脸ID为-1，不计入人数
            MatchPhraseQueryBuilder visitorIdMathcPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.VISITOR_ID, BusinessConstant.MINUS_ONE);
            // 条件（性别，人脸ID）
            boolQueryBuilder.must(genderBoolQueryBuilder).mustNot(visitorIdMathcPhraseQueryBuilder);

            SearchResult visitorIdSearchResult = elasticsearchService.doSearch(indexName, indexType, null, boolQueryBuilder, visitorIdTermsAggregationBuilder);
			String visitorIdErrorMessage = visitorIdSearchResult.getErrorMessage();

			if (StringUtils.isNotBlank(visitorIdErrorMessage)) {
                logger.error(LoggerConstant.SAVE_FACE_DATA_DATE + ": {}", trackIdErrorMessage);
			} else {
			    // 人数
				int visitorIdCount = visitorIdSearchResult.getAggregations()
                        .getTermsAggregation(ElasticsearchConstant.GROUP_BY_VISITOR_ID)
                        .getBuckets()
                        .size();
				faceDataDate.setPeopleNum(visitorIdCount);
			}
			faceDataDate.setAddTime(System.currentTimeMillis());
			faceDataDateService.insertFaceDataDate(faceDataDate);
            // 日志
            String nowTime = DateUtils.getNowDateTime("");
            logger.info("{} " + LoggerConstant.SAVE_FACE_DATA_SEX + MessageConstant.SAVING_IS_SUCCESSFUL, nowTime);
		} catch (ServiceException | ParseException | IOException e) {
			logger.error(LoggerConstant.SAVE_FACE_DATA_SEX, e);
		}
	}

    /**
     * 保存人脸数据（按时间统计）
     */
    @Scheduled(cron = "0 40 4  * * ?")
    public void saveFaceDataTime(){
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
		        logger.error(LoggerConstant.SAVE_FACE_DATA_TIME + MessageConstant.DEVICE_TYPE_IS_NULLL);
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

            /** 查询人次 */
            // 按性别分组
            TermsAggregationBuilder trackIdDateTimeTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_DATE_TIME)
                    .field(ElasticsearchConstant.DATE_TIME_KEYWORD)
                    .size(Integer.MAX_VALUE);

            // 根据id来统计人次
            ValueCountAggregationBuilder trackIdCountAggregationBuilder = AggregationBuilders.count(ElasticsearchConstant.ID_COUNT)
                    .field(ElasticsearchConstant.ID);

            trackIdDateTimeTermsAggregationBuilder.subAggregation(trackIdCountAggregationBuilder);

            SearchResult trackIdSearchResult = elasticsearchService.doSearch(indexName, indexType, null, boolQueryBuilder, trackIdDateTimeTermsAggregationBuilder);
            String trackIdErrorMessage = trackIdSearchResult.getErrorMessage();

            //人次Map
            Map<String, Long> peopleTimeMap = new HashMap<>();

            if (StringUtils.isNotBlank(trackIdErrorMessage)) {
                logger.error(LoggerConstant.SAVE_FACE_DATA_TIME + ": {}", trackIdErrorMessage);
            } else {
                List<TermsAggregation.Entry> dateTimeBuckets = trackIdSearchResult.getAggregations()
                        .getTermsAggregation(ElasticsearchConstant.GROUP_BY_DATE_TIME)
                        .getBuckets();

                //时间桶列表
                for (TermsAggregation.Entry dateTimeEntry : dateTimeBuckets) {
                    String dateTime = dateTimeEntry.getKey();
                    //人次
                    Long peopleTime = dateTimeEntry.getValueCountAggregation(ElasticsearchConstant.ID_COUNT)
                            .getValueCount();
                    if (peopleTime == null) {
                        peopleTime = 0L;
                    }
                    peopleTimeMap.put(dateTime, peopleTime);
                }
            }

            /** 查询人数 */
            // 按性别分组
            TermsAggregationBuilder visitorIdDateTimeTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_DATE_TIME)
                    .field(ElasticsearchConstant.DATE_TIME_KEYWORD)
                    .size(Integer.MAX_VALUE);

            // 按visitorId分组，来统计人数
            TermsAggregationBuilder visitorIdTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_VISITOR_ID)
                    .field(ElasticsearchConstant.VISITOR_ID)
                    .size(Integer.MAX_VALUE);

            visitorIdDateTimeTermsAggregationBuilder.subAggregation(visitorIdTermsAggregationBuilder);

            //过滤性别为“”的数据，性别为“”的数据不计入人数,只查询查性别为“M”“F”
            MatchPhraseQueryBuilder maleMatchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.GENDER_KEYWORD, ElasticsearchConstant.STRING_M);
            MatchPhraseQueryBuilder femaleMatchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.GENDER_KEYWORD, ElasticsearchConstant.STRING_F);

            BoolQueryBuilder genderBoolQueryBuilder = QueryBuilders.boolQuery();
            genderBoolQueryBuilder.should(maleMatchPhraseQueryBuilder)
                    .should(femaleMatchPhraseQueryBuilder);

            // 人脸ID为-1，不计入人数
            MatchPhraseQueryBuilder visitorIdMathcPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.VISITOR_ID, BusinessConstant.MINUS_ONE);
            // 条件（性别，人脸ID）
            boolQueryBuilder.must(genderBoolQueryBuilder).mustNot(visitorIdMathcPhraseQueryBuilder);

            SearchResult visitorIdSearchResult = elasticsearchService.doSearch(indexName, indexType, null, boolQueryBuilder, visitorIdDateTimeTermsAggregationBuilder);
            String visitorIdErrorMessage = visitorIdSearchResult.getErrorMessage();
            if (StringUtils.isNotBlank(visitorIdErrorMessage)) {
                logger.error(LoggerConstant.SAVE_FACE_DATA_TIME + ": {}", visitorIdErrorMessage);
            } else {
                List<TermsAggregation.Entry> dateTimeBuckets = visitorIdSearchResult.getAggregations()
                        .getTermsAggregation(ElasticsearchConstant.GROUP_BY_DATE_TIME)
                        .getBuckets();

                List<FaceDataTime> faceDataTimeList = new ArrayList<>();

                //时间桶列表
                for (TermsAggregation.Entry dateTimeEntry : dateTimeBuckets) {
                    String dateTime = dateTimeEntry.getKey();
                    //人次
                    Long peopleTime = peopleTimeMap.get(dateTime);
                    if (peopleTime == null) {
		                peopleTime = 0L;
	                }
                    //人数
                    int peopleNum = dateTimeEntry.getTermsAggregation(ElasticsearchConstant.GROUP_BY_VISITOR_ID)
                            .getBuckets()
                            .size();

                    FaceDataTime faceDataTime = new FaceDataTime();
                    //取dateTime的前八位作为年月日作为date,这样的数据才准确
                    String dateStr = dateTime.substring(0, 8);
                    //取dateTime的最后两位小时作为dateTime,根据小时分组是，方便
                    String dateTimeStr = dateTime.substring(8, 10);
                    faceDataTime.setDate(dateStr);
                    faceDataTime.setTime(dateTimeStr);
                    faceDataTime.setPeopleNum(peopleNum);
                    faceDataTime.setPeopleTime(peopleTime.intValue());
                    faceDataTime.setAddTime(System.currentTimeMillis());
                    faceDataTimeList.add(faceDataTime);
                }
                //保存数据到数据库
                if (CollectionUtils.isNotEmpty(faceDataTimeList)) {
                    faceDataTimeService.insertFaceDataTimeBatch(faceDataTimeList);
                    // 日志
                    String nowTime = DateUtils.getNowDateTime("");
                    logger.info("{} " + LoggerConstant.SAVE_FACE_DATA_TIME + MessageConstant.SAVING_IS_SUCCESSFUL, nowTime);
                }
            }
        } catch (ParseException | IOException | ServiceException e) {
            logger.error(LoggerConstant.SAVE_FACE_DATA_TIME, e);
        }
    }

    /**
     * 保存人脸数据（按省统计）
     */
    @Scheduled(cron = "0 45 4 * * ?")
    public void saveFaceDataProvince(){
        // 获取省中心坐标Map
        Map<String, String> provinceCenterMap = provinceCityService.getProvinceCenterMap();
        if (MapUtils.isEmpty(provinceCenterMap)) {
            logger.error(LoggerConstant.SAVE_FACE_DATA_PROVINCE + MessageConstant.FRP_PROVINCE_CITY_TABLE_IS_NULL);
            return;
        }

        // 获取昨天的日期
        String date = DateUtils.getPastDay(DateConstant.YESTERDAY, DateConstant.DATE_YEAR_MONTH_DAY);
        try {
            // 获取省城市数列表
            List<ProvinceCityNumDTO> provinceCityNumDTOList = faceDataCommunityService.listProvinceCityNumDTO(date);
            // 省城市数Map(key为省，value为城市数)
            Map<String, Integer> provinceCityNumMap = new HashMap<>();
            for (ProvinceCityNumDTO proviceCityNum : provinceCityNumDTOList) {
                String province = proviceCityNum.getProvince();
                if (StringUtils.isBlank(province)) {
                    continue;
                }
                provinceCityNumMap.put(province, proviceCityNum.getCityNum());
            }

            // 获取省终端数列表
            List<ProvinceDeviceNumDTO> provinceDeviceNumDTOList = faceDataCommunityDeviceService.listProvinceDeviceNumDTO(date);
            // 省城市数Map(key为省，value为终端数)
            Map<String, Integer> provinceDeviceNumMap = new HashMap<>();
            for (ProvinceDeviceNumDTO provinceDeviceNum : provinceDeviceNumDTOList) {
                String province = provinceDeviceNum.getProvince();
                if (StringUtils.isBlank(province)) {
                    continue;
                }
                provinceDeviceNumMap.put(province, provinceDeviceNum.getDeviceNum());
            }

            // 获取省小区数列表
            List<ProvinceCommunityNumDTO> provinceCommunityNumDTOList = faceDataCommunityService.listProvinceCommunityNumDTO(date);
            // 省城市数Map(key为省，value为小区数)
            Map<String, Integer> provinceCommunityNumMap = new HashMap<>();
            for (ProvinceCommunityNumDTO provinceCommunityNum : provinceCommunityNumDTOList) {
                String province = provinceCommunityNum.getProvince();
                if (StringUtils.isBlank(province)) {
                    continue;
                }
                provinceCommunityNumMap.put(province, provinceCommunityNum.getCommunityNum());
            }

            if (provinceCityNumMap.size() > 0) {
                // 获取省、性别、人数
                List<FaceDataProvinceSexDTO> faceDataProvinceSexDTOList = faceDataCommunitySexService.listFaceDataProvinceSexDTO(date);
                // 获取主力年龄
                List<FaceDataProvinceAgeDTO> faceDataProvinceAgeDTOList = faceDataCommunityAgeService.listFaceDataProvinceAgeDTO(date);

                List<FaceDataProvince> faceDataProvinceList = Lists.newArrayList();

                for (Map.Entry<String, Integer> entry : provinceCityNumMap.entrySet()) {
                    String province = entry.getKey();
                    Integer cityNum = entry.getValue();
                    // 获取省的中心坐标
                    String provinceCenter = provinceCenterMap.get(province);

                    FaceDataProvince faceDataProvince = new FaceDataProvince();
                    faceDataProvince.setDate(date);
                    faceDataProvince.setProvince(province);
                    faceDataProvince.setProvinceCenter(provinceCenter);
                    faceDataProvince.setCityNum(cityNum);
                    faceDataProvince.setCommunityNum(provinceCommunityNumMap.get(province));
                    faceDataProvince.setDeviceNum(provinceDeviceNumMap.get(province));

                    // 计算男性、女性人数（不需要把性别未知的人数算入男、女人数中）
                    for (FaceDataProvinceSexDTO faceDataProvinceSexDTO : faceDataProvinceSexDTOList) {
                        //省
                        String provinceSex = faceDataProvinceSexDTO.getProvince();
                        //性别
                        String sex = faceDataProvinceSexDTO.getSex();
                        if (StringUtils.isBlank(sex)) {
                            continue;
                        }
                        if (province.equals(provinceSex)) {
                            switch (sex) {
                                case ElasticsearchConstant.SEX_FEMALE:
                                    faceDataProvince.setFemaleNum(faceDataProvinceSexDTO.getPeopleNum());
                                    break;
                                case ElasticsearchConstant.SEX_MALE:
                                    faceDataProvince.setMaleNum(faceDataProvinceSexDTO.getPeopleNum());
                                    break;
                                default:
                                    //不需要把性别未知的人数算入男、女人数中
                                    break;
                            }
                        }
                    }
                    // 男性人数
                    Integer  maleNum = faceDataProvince.getMaleNum();
	                if (maleNum == null) {
		                maleNum = 0;
	                }
                    // 女性人数
                    Integer femaleNum = faceDataProvince.getFemaleNum();
	                if (femaleNum == null) {
		                femaleNum = 0;
	                }

                    faceDataProvince.setMaleNum(maleNum);
                    faceDataProvince.setFemaleNum(femaleNum);

                    // 总人数=男性人数 + 女性人数
                    int peopleNum = maleNum + femaleNum;
                    faceDataProvince.setPeopleNum(peopleNum);

                    // 计算主力年龄
                    for (FaceDataProvinceAgeDTO faceDataProvinceAgeDTO : faceDataProvinceAgeDTOList) {
                        String provinceAge = faceDataProvinceAgeDTO.getProvince();
                        if (province.equals(provinceAge)) {
                            faceDataProvince.setMainAge(faceDataProvinceAgeDTO.getAge());
                        }
                    }
                    // 添加时间
                    faceDataProvince.setAddTime(System.currentTimeMillis());

                    faceDataProvinceList.add(faceDataProvince);
                }
                // 保存数据到数据库
                if (CollectionUtils.isNotEmpty(faceDataProvinceList)) {
                    faceDataProvinceService.insertFaceDataProvinceBatch(faceDataProvinceList);
                    // 日志
                    String nowTime = DateUtils.getNowDateTime("");
                    logger.info("{} " + LoggerConstant.SAVE_FACE_DATA_PROVINCE + MessageConstant.SAVING_IS_SUCCESSFUL, nowTime);
                }
            }
        } catch (ServiceException e) {
            logger.error(LoggerConstant.SAVE_FACE_DATA_PROVINCE, e);
        }
    }

}