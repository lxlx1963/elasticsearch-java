package com.du.elasticsearch.jobs;

import com.xinchao.data.constant.ElasticsearchConstant;
import com.xinchao.data.constant.LoggerConstant;
import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.service.ElasticsearchService;
import org.apache.commons.lang3.StringUtils;
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
 * 索引数据管理
 *
 * @author dxy
 * @date 2019/5/29 15:42
 */
@Component
public class IndexDataJob {
	private static Logger logger = LoggerFactory.getLogger(IndexDataJob.class);
	@Autowired
	private ElasticsearchService elasticsearchService;

	/**
	 * 删除索引
	 */
	@Scheduled(cron = "0 20 5 * * ?")
	public void deleteIndex() {
		// 索引前缀保留数量Map
		Map<String, Integer> indexPrefixSaveNumMap = ElasticsearchConstant.indexPrefixSaveNumMap;
		try {
			// 所有索引列表
			List<String> allIndices = elasticsearchService.getAllIndices();
			// Map不为空
			for (Map.Entry<String, Integer> entry : indexPrefixSaveNumMap.entrySet()) {
				// 索引前缀
				String indexPrefix = entry.getKey();
				if (StringUtils.isBlank(indexPrefix)) {
					continue;
				}
				// 保留数量
				Integer saveNum = entry.getValue();
				if (saveNum == null) {
					return;
				}
				// 删除索引列表
				List<String> deletedIndices = elasticsearchService.getDeletedIndices(allIndices, indexPrefix, saveNum);

				for (String deletedIndex : deletedIndices) {
					elasticsearchService.deleteIndex(deletedIndex);
					logger.info(LoggerConstant.DELETE_INDEX + "删除索引成功-----> {}", deletedIndex);
				}
			}
		} catch (ParseException | IOException | ServiceException e) {
			logger.error(LoggerConstant.DELETE_INDEX + " 删除索引失败-----> ", e);
		}
	}

}
