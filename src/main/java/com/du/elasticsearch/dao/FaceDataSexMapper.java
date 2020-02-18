package com.du.elasticsearch.dao;

import com.xinchao.data.model.FaceDataSex;
import com.xinchao.data.model.dto.FaceDataSexDTO;
import com.xinchao.data.model.dto.SingleDeviceTimeSexDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 人脸数据（性别）
 *
 * @author dxy
 * @date 2019/3/1 21:40
 */
public interface FaceDataSexMapper {
	/**
	 * 插入数据
	 *
	 * @param faceDataSex FaceDataSex
	 */
	void insertFaceDataSex(@Param("faceDataSex") FaceDataSex faceDataSex);

	/**
	 * 批量插入数据
	 *
	 * @param faceDataSexList List<FaceDataSex>
	 */
	void insertFaceDataSexBatch(@Param("faceDataSexList") List<FaceDataSex> faceDataSexList);

	/**
	 * 获取人脸数据性别DTO列表
	 *
	 * @param dateList 日期列表
	 * @return List<FaceDataSexDTO>
	 */
	List<FaceDataSexDTO> listFaceDataSexDTO(@Param("dateList") List<String> dateList);

	/**
	 * 获取客流人数、人次按时间、性别分组
	 *
	 * @param date          日期
	 * @param residenceTypeList 住宅类型列表
	 * @return List<SingleDeviceTimeSexDTO>
	 */
	List<SingleDeviceTimeSexDTO> listDeviceTimeSex(@Param("date") String date, @Param("residenceTypeList") List<String> residenceTypeList);

	/**
	 * 获取实际日期列表
	 *
	 * @param dateList 日期列表
	 * @return List<String>
	 */
	List<String> listActualDate(@Param("dateList") List<String> dateList);
}
