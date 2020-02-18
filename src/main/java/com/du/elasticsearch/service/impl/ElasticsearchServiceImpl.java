package com.du.elasticsearch.service.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.xinchao.data.constant.DateConstant;
import com.xinchao.data.constant.ElasticsearchConstant;
import com.xinchao.data.constant.MessageConstant;
import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.service.ElasticsearchService;
import com.xinchao.data.util.DateUtils;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Cat;
import io.searchbox.core.CatResult;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.sort.Sort;
import io.searchbox.indices.DeleteIndex;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author dxy
 * @date 2019/2/25 11:35
 */
@Service(value = "elasticsearchService")
public class ElasticsearchServiceImpl implements ElasticsearchService {
	@Autowired
	private JestClient jestClient;

	/**
	 * 查询
	 *
	 * @param indexName          索引名称
	 * @param indexType          类型类型
	 * @param sort               Sort
	 * @param queryBuilder       QueryBuilder
	 * @param aggregationBuilder AggregationBuilder
	 * @return SearchResult
	 * @throws IOException
	 */
	@Override
	public SearchResult doSearch(String indexName, String indexType, Sort sort, QueryBuilder queryBuilder, AggregationBuilder aggregationBuilder) throws IOException {
		// 创建SearchSourceBuilder
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		// 添加QueryBuilder
		if (queryBuilder != null) {
			searchSourceBuilder.query(queryBuilder);
		}
		// 添加AggregationBuilder
		if (aggregationBuilder != null) {
			searchSourceBuilder.aggregation(aggregationBuilder);
		}
		// 创建Search.Builder
		Search.Builder builder = new Search.Builder(searchSourceBuilder.toString());
		// 添加索引名称
		if (StringUtils.isNotBlank(indexName)) {
			builder.addIndex(indexName);
		}
		// 添加索引类型
		if (StringUtils.isNotBlank(indexType)) {
			builder.addType(indexType);
		}
		// 添加Sort
		if (sort != null) {
			builder.addSort(sort);
		}
		// 构建Search
		Search search = builder.build();
		return jestClient.execute(search);
	}

	/**
	 * 查询
	 *
	 * @param indexNames         索引名称集合
	 * @param indexType          索引类型集合
	 * @param sorts              Sort
	 * @param queryBuilder       QueryBuilder
	 * @param aggregationBuilder AggregationBuilder
	 * @return SearchResult
	 * @throws IOException
	 */
	@Override
	public SearchResult doSearch(Collection<? extends String> indexNames, String indexType, Collection<Sort> sorts, QueryBuilder queryBuilder, AggregationBuilder aggregationBuilder) throws IOException {
		// 创建SearchSourceBuilder
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		// 添加QueryBuilder
		if (queryBuilder != null) {
			searchSourceBuilder.query(queryBuilder);
		}
		// 添加AggregationBuilder
		if (aggregationBuilder != null) {
			searchSourceBuilder.aggregation(aggregationBuilder);
		}
		// 创建Search.Builder
		Search.Builder builder = new Search.Builder(searchSourceBuilder.toString());
		// 添加索引名称
		if (CollectionUtils.isNotEmpty(indexNames)) {
			builder.addIndices(indexNames);
		}
		// 添加索引类型
		if (StringUtils.isNotBlank(indexType)) {
			builder.addType(indexType);
		}
		// 添加Sort
		if (CollectionUtils.isNotEmpty(sorts)) {
			builder.addSort(sorts);
		}
		// 构建Search
		Search search = builder.build();
		return jestClient.execute(search);
	}

	/**
	 * 获取所有索引
	 *
	 * @return List<String>
	 * @throws IOException
	 * @throws ServiceException
	 */
	@Override
	public List<String> getAllIndices() throws IOException, ServiceException {
		// es中，从“_cat/indices”查询所有的索引
		Cat cat = new Cat.IndicesBuilder().build();
		CatResult catResult = jestClient.execute(cat);
		// es抛出的错误信息
		String errorMessage = catResult.getErrorMessage();
		// 如果有错误信息，抛给调用者
		if (StringUtils.isNotBlank(errorMessage)) {
			throw new ServiceException(errorMessage);
		}
		// 索引列表
		List<String> allIndices = new ArrayList<>();
		// 从返回信息中获取JsonObject
		JsonObject jsonObject = catResult.getJsonObject();
		JsonArray jsonArray = jsonObject.getAsJsonArray(ElasticsearchConstant.RESULT);
		// 解析JSON对象
		if (jsonArray != null && jsonArray.size() != 0) {
			for (int i = 0; i < jsonArray.size(); i++) {
				JsonElement jsonElement = jsonArray.get(i);
				JsonObject elementJsonObject = jsonElement.getAsJsonObject();
				String index = elementJsonObject.get(ElasticsearchConstant.INDEX).getAsString();
				allIndices.add(index);

			}
		}
		return allIndices;
	}

	/**
	 * 根据前缀获取所有列表
	 *
	 * @param prefex 索引前缀
	 * @return List<String>
	 * @throws ServiceException
	 * @throws IOException
	 */
	@Override
	public List<String> getIndicesByPrefix(List<String> allIndices, String prefex) throws ServiceException {
		if (CollectionUtils.isEmpty(allIndices) || StringUtils.isBlank(prefex)) {
			throw new ServiceException(MessageConstant.INDEX_LIST_IS_NULL + "或" + MessageConstant.INDEX_PREFIX_IS_NULL);
		}
		// 索引列表
		List<String> indices = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(allIndices)) {
			for (String index : allIndices) {
				if (StringUtils.isBlank(index)) {
					continue;
				}
				if (index.startsWith(prefex)) {
					indices.add(index);
				}
			}
		}
		return indices;
	}

	/**
	 * 删除索引
	 *
	 * @param indexName 索引名称
	 * @throws IOException
	 * @throws ServiceException
	 */
	@Override
	public void deleteIndex(String indexName) throws IOException, ServiceException {
		if (StringUtils.isBlank(indexName)) {
			throw new ServiceException(MessageConstant.INDEX_NAME_IS_NULL);
		}
		DeleteIndex deleteIndex = new DeleteIndex.Builder(indexName).build();
		JestResult jestResult = jestClient.execute(deleteIndex);
		String errorMessage = jestResult.getErrorMessage();
		if (StringUtils.isNotBlank(errorMessage)) {
			throw new ServiceException(errorMessage);
		}
	}

	/**
	 * 获取删除索引列表
	 *
	 * @param allIndices  所有索引列表
	 * @param indexPrefix 所有前缀
	 * @param saveNum     保留数量
	 * @return List<String>
	 * @throws ServiceException
	 * @throws ParseException
	 */
	@Override
	public List<String> getDeletedIndices(List<String> allIndices, String indexPrefix, Integer saveNum) throws ServiceException, ParseException {
		if (CollectionUtils.isEmpty(allIndices) || StringUtils.isBlank(indexPrefix)) {
			throw new ServiceException(MessageConstant.INDEX_LIST_IS_NULL + "或" + MessageConstant.INDEX_PREFIX_IS_NULL);
		}
		if (saveNum == null || saveNum < 0) {
			throw new ServiceException(MessageConstant.SAVING_NUMBER_IS_NULL_OR_LESS_THAN_ZERO);
		}
		// 根据索引前缀获取的索引列表
		List<String> indices = getIndicesByPrefix(allIndices, indexPrefix);

		// 删除日期列表
		List<String> deleteDateList = new ArrayList<>();

		// 筛选出不需要保留的所有日期
		if (CollectionUtils.isNotEmpty(indices)) {
			// 日期数字列表
			List<Long> dateList = new ArrayList<>();

			// 将字符串日期转化为数字，便于排序及再转转化为“yyyy.MM.dd”类型的日期
			for (String index : indices) {
				// 自定义所有，最后10位数字格式都是“yyyy.MM.dd”，所以取最后10位
				String date = index.substring(index.length() - 10, index.length());
				Long dateNum = DateUtils.stringTimeToLongTimeStamp(date, DateConstant.DATE_SHORT);
				dateList.add(dateNum);
			}

			// 升序
			Collections.sort(dateList);

			// 保留最近X天数的日期
			int noSaveSize = dateList.size() - saveNum;

			// 将不需要保留的索引日期放入删除日期列表
			for (int i = 0; i < noSaveSize; i++) {
				Long date = dateList.get(i);
				String dateStr = DateUtils.longTimeStampToStringDate(date, DateConstant.DATE_SHORT);
				String indexName = indexPrefix + dateStr;
				deleteDateList.add(indexName);
			}
		}
		return deleteDateList;
	}

}
