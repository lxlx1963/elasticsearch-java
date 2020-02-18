package com.du.elasticsearch.dao;

import com.xinchao.data.model.FaceDataDeviceTimeAge;
import com.xinchao.data.model.dto.SingleDeviceTimeAgeDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author dxy
 * @date 2019/4/2 17:33
 */
public interface FaceDataDeviceTimeAgeMapper {
	/**
	 * 插入数据
	 *
	 * @param faceDataDeviceTimeAge FaceDataDeviceTimeAge
	 */
	void insertFaceDataDeviceTimeAge(@Param("faceDataDeviceTimeAge") FaceDataDeviceTimeAge faceDataDeviceTimeAge);

	/**
	 * 批量插入数据
	 *
	 * @param faceDataDeviceTimeAgeList List<FaceDataDeviceTimeAge>
	 */
	void insertFaceDataDeviceTimeAgeBatch(@Param("faceDataDeviceTimeAgeList") List<FaceDataDeviceTimeAge> faceDataDeviceTimeAgeList);

	/**
	 * 获取时间下的年龄段列表
	 *
	 * @param dateList 日期列表
	 * @param residenceTypeList 住宅类型列表
	 * @return List<SingleDeviceTimeAgeDTO>
	 */
	List<SingleDeviceTimeAgeDTO> listDeviceTimeAge(@Param("dateList") List<String> dateList, @Param("residenceTypeList") List<String> residenceTypeList);

	/**
	 * 获取实际日期列表
	 *
	 * @param dateList 日期列表
	 * @param residenceTypeList 住宅类型列表
	 * @return List<String>
	 */
	List<String> listActualDate(@Param("dateList") List<String> dateList, @Param("residenceTypeList") List<String> residenceTypeList);
}
