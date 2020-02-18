package com.du.elasticsearch.dao;

import com.xinchao.data.model.FaceDataProvince;
import com.xinchao.data.model.dto.DateDeviceNumDTO;
import com.xinchao.data.model.dto.SummarizeFaceDataProvinceDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author dxy
 * @date 2019/3/5 10:16
 */
public interface FaceDataProvinceMapper {

	/**
	 * 插入人数数据(省)
	 *
	 * @param faceDataProvince FaceDataProvince
	 */
	void insertFaceDataProvince(@Param("faceDataProvince") FaceDataProvince faceDataProvince);

	/**
	 * 批量插入人数数据(省)
	 *
	 * @param faceDataProvinceList List<FaceDataProvince>
	 */
	void insertFaceDataProvinceBatch(@Param("faceDataProvinceList") List<FaceDataProvince> faceDataProvinceList);

	/**
	 * 获取汇总省人脸数据列表
	 *
	 * @param date 日期
	 * @return List<SummarizeFaceDataProvinceDTO>
	 */
	List<SummarizeFaceDataProvinceDTO> listSummarizeFaceDataProvinceDTO(String date);

	/**
	 * 获取日期对应机器数量列表
	 *
	 * @param dateList 日期列表
	 * @return List<DateDeviceNumDTO>
	 */
	List<DateDeviceNumDTO> listDateDeviceNum(@Param("dateList") List<String> dateList);
}
