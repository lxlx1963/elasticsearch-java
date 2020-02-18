package com.du.elasticsearch.dao;

import com.xinchao.data.model.dto.ProvinceCenterDTO;
import com.xinchao.data.model.dto.ProvinceCityDTO;

import java.util.List;

/**
 * @author dxy
 * @date 2019/3/4 16:58
 */
public interface ProvinceCityMapper {
	/**
	 * 获取所有省城市及省会中心坐标列表
	 *
	 * @return List<ProvinceCityDTO>
	 */
	List<ProvinceCityDTO> listProvinceCity();

	/**
	 * 获取省会中心坐标列表
	 *
	 * @return List<ProvinceCenterDTO>
	 */
	List<ProvinceCenterDTO> listProvinceCenter();

}
