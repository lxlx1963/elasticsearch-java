package com.du.elasticsearch.dao;

import com.xinchao.data.model.FaceDataDeviceTimeSex;
import com.xinchao.data.model.dto.SingleDeviceTimeSexDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author dxy
 * @date 2019/4/2 17:07
 */
public interface FaceDataDeviceTimeSexMapper {
	/**
	 * 插入数据
	 *
	 * @param faceDataDeviceTimeSex FaceDataDeviceTimeSex
	 */
	void insertFaceDataDeviceTimeSex(@Param("faceDataDeviceTimeSex") FaceDataDeviceTimeSex faceDataDeviceTimeSex);

	/**
	 * 批量插入数据
	 *
	 * @param faceDataDeviceTimeSexList List<FaceDataDeviceTimeSex>
	 */
	void insertFaceDataDeviceTimeSexBatch(@Param("faceDataDeviceTimeSexList") List<FaceDataDeviceTimeSex> faceDataDeviceTimeSexList);

	/**
	 * 获取客流人数、人次按时间、性别分组
	 *
	 * @param dateList 日期列表
	 * @param residenceTypeList 住宅类型列表
	 * @return List<SingleDeviceTimeSexDTO>
	 */
	List<SingleDeviceTimeSexDTO> listDeviceTimeSex(@Param("dateList") List<String> dateList, @Param("residenceTypeList") List<String> residenceTypeList);

	/**
	 * 获取实际日期列表
	 *
	 * @param dateList 日期列表
	 * @param residenceTypeList 住宅类型列表
	 * @return List<String>
	 */
	List<String> listActualDate(@Param("dateList") List<String> dateList, @Param("residenceTypeList") List<String> residenceTypeList);
}
