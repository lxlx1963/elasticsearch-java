package com.du.elasticsearch.service;

import com.xinchao.data.exception.ServiceException;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.sort.Sort;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;

import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;

/**
 * @author dxy
 * @date 2019/2/25 11:34
 */
public interface ElasticsearchService {
	/**
	 * 查询(该方法只满足从具体索引，索引类型查询及排序的需求)
	 *
	 * @param indexName          索引名称
	 * @param indexType          索引类型
	 * @param sort               Sort
	 * @param aggregationBuilder AggregationBuilder
	 * @return SearchResult
	 * @throws IOException
	 */
	SearchResult doSearch(String indexName, String indexType, Sort sort, QueryBuilder queryBuilder, AggregationBuilder aggregationBuilder) throws IOException;

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
	SearchResult doSearch(Collection<? extends String> indexNames, String indexType, Collection<Sort> sorts, QueryBuilder queryBuilder, AggregationBuilder aggregationBuilder) throws IOException;

	/**
	 * 获取所有索引
	 *
	 * @return List<String>
	 */
	List<String> getAllIndices() throws IOException, ServiceException;

	/**
	 * 根据前缀获取所有列表
	 *
	 * @param allIndices 所有索引
	 * @param prefex     索引前缀
	 * @return List<String>
	 */
	List<String> getIndicesByPrefix(List<String> allIndices, String prefex) throws ServiceException, IOException;

	/**
	 * 删除索引
	 *
	 * @param indexName 索引名称
	 */
	void deleteIndex(String indexName) throws IOException, ServiceException;

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
	List<String> getDeletedIndices(List<String> allIndices, String indexPrefix, Integer saveNum) throws ServiceException, ParseException;
}
