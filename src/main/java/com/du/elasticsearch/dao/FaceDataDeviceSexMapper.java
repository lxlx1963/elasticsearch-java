package com.du.elasticsearch.dao;

import com.xinchao.data.model.FaceDataDeviceSex;
import com.xinchao.data.model.dto.FaceDataSexSumDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author dxy
 * @date 2019/4/2 17:29
 */
public interface FaceDataDeviceSexMapper {
	/**
	 * 插入数据
	 *
	 * @param faceDataDeviceSex FaceDataDeviceSex
	 */
	void insertFaceDataDeviceSex(@Param("faceDataDeviceSex") FaceDataDeviceSex faceDataDeviceSex);

	/**
	 * 批量插入数据
	 *
	 * @param faceDataDeviceSexList List<FaceDataDeviceSex>
	 */
	void insertFaceDataDeviceSexBatch(@Param("faceDataDeviceSexList") List<FaceDataDeviceSex> faceDataDeviceSexList);

	/**
	 * 获取人数人次总数性别列表
	 *
	 * @param dateList 日期列表
	 * @param residenceTypeList 住宅类型列表
	 * @return List<FaceDataSexDTO>
	 */
	List<FaceDataSexSumDTO> listFaceDataSexSum(@Param("dateList") List<String> dateList, @Param("residenceTypeList") List<String> residenceTypeList);

	/**
	 * 获取实际日期列表
	 *
	 * @param dateList 日期列表
	 * @param residenceTypeList 住宅类型列表
	 * @return List<String>
	 */
	List<String> listActualDate(@Param("dateList") List<String> dateList, @Param("residenceTypeList") List<String> residenceTypeList);
}
