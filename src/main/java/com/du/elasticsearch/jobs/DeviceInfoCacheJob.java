package com.du.elasticsearch.jobs;

import com.xinchao.data.cacahe.RedisCacheDao;
import com.xinchao.data.constant.CacheConstant;
import com.xinchao.data.constant.LoggerConstant;
import com.xinchao.data.service.DeviceInfoService;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 终端信息Job
 *
 * @author dxy
 * @date 2019/6/3 11:39
 */
@Component
public class DeviceInfoCacheJob {
	private static Logger logger = LoggerFactory.getLogger(DeviceInfoCacheJob.class);
	@Autowired
	private RedisCacheDao redisCacheDao;
	@Autowired
	private DeviceInfoService deviceInfoService;

	/**
	 * 将小区城市放入缓存
	 */
	@Scheduled(cron = "0 40 5 * * ?")
	public void pushCommunityCity() {
		// 缓存Map
		Map<Object, Object> allCache = redisCacheDao.getAllCache(CacheConstant.COMMUNITY_CITY);
		if (MapUtils.isNotEmpty(allCache)) {
			return;
		}
		// 小区城市Map
		Map<Object, Object> deviceCommunityCityMap = deviceInfoService.getDeviceCommunityCityMap();
		if (MapUtils.isEmpty(deviceCommunityCityMap)) {
			logger.error(LoggerConstant.PUSH_COMMUNITY_CITY  + "小区城市数据为空");
			return;
		}
		// 放入缓存
		for (Map.Entry<Object, Object> entry : deviceCommunityCityMap.entrySet()) {
			Object key = entry.getKey();
			Object value = entry.getValue();
			if (key == null || value == null) {
				continue;
			}
			//放入缓存
			redisCacheDao.putCache(CacheConstant.COMMUNITY_CITY, key.toString(), value);
		}
		logger.info(LoggerConstant.PUSH_COMMUNITY_CITY + "成功将小区城市放入缓存");
	}


}
