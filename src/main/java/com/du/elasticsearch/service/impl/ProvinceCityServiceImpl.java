package com.du.elasticsearch.service.impl;

import com.google.common.collect.Maps;
import com.xinchao.data.dao.ProvinceCityMapper;
import com.xinchao.data.model.dto.ProvinceCenterDTO;
import com.xinchao.data.model.dto.ProvinceCityDTO;
import com.xinchao.data.service.ProvinceCityService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author dxy
 * @date 2019/4/4 14:11
 */
@Service(value = "provinceCityService")
public class ProvinceCityServiceImpl implements ProvinceCityService {
	@Autowired
	private ProvinceCityMapper provinceCityMapper;

	/**
	 * 获取省城市列表
	 *
	 * @return List<ProvinceCityDTO>
	 */
	@Override
	public List<ProvinceCityDTO> listProvinceCity() {
		return provinceCityMapper.listProvinceCity();
	}

	/**
	 * 城市省Map(以城市为Key,省为Value)
	 *
	 * @return Map<String, String>
	 */
	@Override
	public Map<String, String> getCityProvinceMap() {
		Map<String, String> cityProvinceMap = Maps.newHashMap();
		List<ProvinceCityDTO> provinceCityList = listProvinceCity();
		if (CollectionUtils.isNotEmpty(provinceCityList)) {
			for (ProvinceCityDTO provinceCityDto : provinceCityList) {
				String city = provinceCityDto.getCity();
				String province = provinceCityDto.getProvince();
				if (StringUtils.isBlank(city) || StringUtils.isBlank(province)) {
					continue;
				}
				cityProvinceMap.put(city, province);
			}
		}
		return cityProvinceMap;
	}

	/**
	 * 获取省会中心坐标列表
	 *
	 * @return List<ProvinceCenterDTO>
	 */
	@Override
	public List<ProvinceCenterDTO> listProvinceCenter() {
		return provinceCityMapper.listProvinceCenter();
	}

	/**
	 * 省中心坐标Map(以省为Key,省中心坐标为Value)
	 *
	 * @return Map<String, String>
	 */
	@Override
	public Map<String, String> getProvinceCenterMap() {
		Map<String, String> provinceCenterMap = Maps.newHashMap();
		List<ProvinceCenterDTO> provinceCityList = listProvinceCenter();
		if (CollectionUtils.isNotEmpty(provinceCityList)) {
			for (ProvinceCenterDTO provinceCenterDTO : provinceCityList) {
				String provinceCenter = provinceCenterDTO.getProvinceCenter();
				String province = provinceCenterDTO.getProvince();
				if (StringUtils.isBlank(provinceCenter) || StringUtils.isBlank(province)) {
					continue;
				}
				provinceCenterMap.put(province, provinceCenter);
			}
		}
		return provinceCenterMap;
	}
}
