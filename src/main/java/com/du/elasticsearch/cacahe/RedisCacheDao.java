package com.du.elasticsearch.cacahe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@Repository(value = "redisCacheDao")
public class RedisCacheDao {

	@Autowired
	private RedisTemplate<Object,Object> redisTemplate;

	/**
	 * 添加缓存
	 * @param cachename 缓存名
	 * @param map   Map<String, Object>变量
	 */
	public void putAllCache(String cachename, Map<String, Object> map){
		redisTemplate.opsForHash().putAll(cachename,map);
	}

	/**
	 * 添加缓存
	 * @param cachename 缓存名
	 * @param key   缓存Key
	 * @param val   缓存Value
	 */
	public void putCache(String cachename, String key, Object val){
		redisTemplate.opsForHash().put(cachename,key,val);
	}

	/**
	 * 获取Value
	 * @param cachename 缓存名
	 * @param key   缓存Key
	 * @return  Object
	 */
	public Object getCache(String cachename, String key){
		return redisTemplate.opsForHash().get(cachename,key);
	}

	/**
	 * 获取缓存长度
	 * @param cachename 缓存名
	 * @return  缓存Size
	 */
	public Long getSize(String cachename){
		return redisTemplate.opsForHash().size(cachename);
	}

	/**
	 * 根据key删除缓存
	 * @param cachename 缓存名
	 * @param key   缓存Key
	 */
	public void deleteByKey(String cachename, Object key){
		redisTemplate.opsForHash().delete(cachename,key);
	}

	/**
	 * 删除所有缓存
	 * @param cachename 缓存名
	 */
	public void deleteAll(String cachename){
		Set<Object> set = redisTemplate.opsForHash().keys(cachename);
		for (Object key : set){
			redisTemplate.opsForHash().delete(cachename,key);
		}
	}

	/**
	 * 获取缓存中的所有数据 不排序
	 * @param cachename 缓存名
	 * @return  Map<Object, Object>
	 */
	public Map<Object, Object> getAllCache(String cachename){
		return redisTemplate.opsForHash().entries(cachename);
	}

	/**
	 * 获取缓存中的所有key值
	 * @param cachename
	 * @return
	 */
	public Set<Object> getAllKeys(String cachename){
		return redisTemplate.opsForHash().keys(cachename);
	}

	/**
	 * 获取所有缓存（已排序）
	 * @param cachename 缓存名
	 * @return  Map<Object,Object>类型返回值
	 */
	public Map<Object,Object> getAllCacheOrder(String cachename){
		Set<Object> keys = getAllKeys(cachename);
		if(keys.isEmpty()){
			return null;
		}
		Map<Object,Object> map = new TreeMap<>();
		for (Object key : keys){
			map.put(key,getCache(cachename, String.valueOf(key)));
		}
		return map;
	}

}
