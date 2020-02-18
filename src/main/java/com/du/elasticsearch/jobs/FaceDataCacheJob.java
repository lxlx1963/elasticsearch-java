package com.du.elasticsearch.jobs;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.xinchao.data.cacahe.RedisCacheDao;
import com.xinchao.data.config.CustomProperties;
import com.xinchao.data.constant.*;
import com.xinchao.data.service.ElasticsearchService;
import com.xinchao.data.util.DateUtils;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.TermsAggregation;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * 人脸数据缓存Job
 *
 * @author dxy
 * @date 2019/6/6 17:06
 */
@Component
public class FaceDataCacheJob {
	private static Logger logger = LoggerFactory.getLogger(FaceDataCacheJob.class);
	@Autowired
	private RedisCacheDao redisCacheDao;
	@Autowired
	private CustomProperties customProperties;
	@Autowired
	private ElasticsearchService elasticsearchService;

	/**
	 * 将人数小于给定值的终端列表放入缓存(该定时任务，先于人脸、监播清洗任务执行)
	 */
	@Scheduled(cron = "0 0 3 * * ?")
	public void pushDeviceCodePeopleNum() {
		try {
			//从配置文件中获取终端类型
			Integer deviceType = customProperties.getDeviceType();
			if (deviceType == null) {
				logger.error(LoggerConstant.PUSH_DEVICE_CODE_PEOPLE_NUM + MessageConstant.DEVICE_TYPE_IS_NULLL);
				return;
			}
			//从配置文件中获取限定人数
			Integer limitPeopleNum = customProperties.getLimitPeopleNum();
			if (limitPeopleNum == null) {
				logger.error(LoggerConstant.PUSH_DEVICE_CODE_PEOPLE_NUM + MessageConstant.LIMIT_PEOPLE_NUM_IS_NULLL);
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
			MatchPhraseQueryBuilder deviceTypeMatchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.DEVICE_TYPE, deviceType);

			// 多条件查询
			BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
			MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
			boolQueryBuilder.must(matchAllQueryBuilder).must(queryBuilder).must(deviceTypeMatchPhraseQueryBuilder);

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

			// 过滤性别为“”的数据，性别为“”的数据不计入人数,只查询查性别为“M”“F”
			MatchPhraseQueryBuilder maleMatchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.GENDER_KEYWORD, ElasticsearchConstant.STRING_M);
			MatchPhraseQueryBuilder femaleMatchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.GENDER_KEYWORD, ElasticsearchConstant.STRING_F);
			BoolQueryBuilder genderBoolQueryBuilder = QueryBuilders.boolQuery();
			genderBoolQueryBuilder.should(maleMatchPhraseQueryBuilder)
					.should(femaleMatchPhraseQueryBuilder);

			// 人脸ID为-1，不计入人数
			MatchPhraseQueryBuilder visitorIdMathcPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.VISITOR_ID, BusinessConstant.MINUS_ONE);
			// 条件（性别，人脸ID）
			boolQueryBuilder.must(genderBoolQueryBuilder)
					.mustNot(visitorIdMathcPhraseQueryBuilder);

			SearchResult visitorIdSearchResult = elasticsearchService.doSearch(indexName, indexType, null, boolQueryBuilder, visitorIdDeviceCodeTermsAggregationBuilder);
			String visitorIdErrorMessage = visitorIdSearchResult.getErrorMessage();

			if (StringUtils.isNotBlank(visitorIdErrorMessage)) {
				logger.error(LoggerConstant.PUSH_DEVICE_CODE_PEOPLE_NUM + ": {}", visitorIdErrorMessage);
				return;
			}

			List<TermsAggregation.Entry> deviceCodeBuckets = visitorIdSearchResult.getAggregations()
					.getTermsAggregation(ElasticsearchConstant.GROUP_BY_DEVICE_CODE)
					.getBuckets();

			Map<String, Integer> deviceCodePeopleNumMap = Maps.newHashMap();

			//终端编码桶列表
			for (TermsAggregation.Entry deviceCodeEntry : deviceCodeBuckets) {
				//终端编码
				String deviceCode = deviceCodeEntry.getKey();
				//人数
				int peopleNum = deviceCodeEntry.getTermsAggregation(ElasticsearchConstant.GROUP_BY_VISITOR_ID)
						.getBuckets()
						.size();
				deviceCodePeopleNumMap.put(deviceCode, peopleNum);
			}

			//人数小于给定值map
			Map<String, Integer> lessThanValuePeopleNumMap = Maps.newHashMap();

			//筛选出小于给定值的终端编码及人数
			for (Map.Entry<String, Integer> entry : deviceCodePeopleNumMap.entrySet()) {
				String key = entry.getKey();
				Integer value = entry.getValue();
				if (limitPeopleNum <= value) {
					continue;
				}
				lessThanValuePeopleNumMap.put(key, value);
			}
			//放入缓存
			if (lessThanValuePeopleNumMap.size() > 0) {
				redisCacheDao.putCache(CacheConstant.FACE_PEOPLE_NUM_LESS_THAN_VALUE_DEVICE_CODE, date, JSON.toJSONString(lessThanValuePeopleNumMap));
				logger.info(LoggerConstant.PUSH_DEVICE_CODE_PEOPLE_NUM + "将人数小于给定值的终端列表放入缓存成功");
			}
		} catch (ParseException | IOException e) {
			logger.error(LoggerConstant.PUSH_DEVICE_CODE_PEOPLE_NUM, e);
		}
	}
}
