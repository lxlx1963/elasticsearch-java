package com.du.elasticsearch.service;

import com.xinchao.data.model.dto.ProvinceCenterDTO;
import com.xinchao.data.model.dto.ProvinceCityDTO;

import java.util.List;
import java.util.Map;

/**
 * @author dxy
 * @date 2019/4/4 14:05
 */
public interface ProvinceCityService {
	/**
	 * 获取省城市列表
	 *
	 * @return List<ProvinceCityDTO>
	 */
	List<ProvinceCityDTO> listProvinceCity();

	/**
	 * 城市省Map(以城市为Key,省为Value)
	 *
	 * @return Map<String, String>
	 */
	Map<String, String> getCityProvinceMap();

	/**
	 * 获取省会中心坐标列表
	 *
	 * @return List<ProvinceCenterDTO>
	 */
	List<ProvinceCenterDTO> listProvinceCenter();

	/**
	 * 省中心坐标Map(以省为Key,省中心坐标为Value)
	 *
	 * @return Map<String, String>
	 */
	Map<String, String> getProvinceCenterMap();

}
