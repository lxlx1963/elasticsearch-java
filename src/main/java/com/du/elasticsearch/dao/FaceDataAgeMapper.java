package com.du.elasticsearch.dao;

import com.xinchao.data.model.FaceDataAge;
import com.xinchao.data.model.dto.FaceDataAgeDTO;
import com.xinchao.data.model.dto.SingleDeviceTimeAgeDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 人脸数据（年龄）
 *
 * @author dxy
 * @date 2019/3/1 21:19
 */
public interface FaceDataAgeMapper {
	/**
	 * 插入数据
	 *
	 * @param faceDataAge FaceDataAge
	 */
	void insertFaceDataAge(@Param("faceDataAge") FaceDataAge faceDataAge);

	/**
	 * 批量插入数据
	 *
	 * @param faceDataAgeList List<FaceDataAge>
	 */
	void insertFaceDataAgeBatch(@Param("faceDataAgeList") List<FaceDataAge> faceDataAgeList);

	/**
	 * 获取人脸数据年龄DTO列表
	 *
	 * @param dateList 日期列表
	 * @return List<FaceDataAgeDTO>
	 */
	List<FaceDataAgeDTO> listFaceDataAgeDTO(@Param("dateList") List<String> dateList);

	/**
	 * 获取时间下的年龄段列表
	 *
	 * @param date              日期
	 * @param residenceTypeList 住宅类型列表
	 * @return List<SingleDeviceTimeAgeDTO>
	 */
	List<SingleDeviceTimeAgeDTO> listDeviceTimeAge(@Param("date") String date, @Param("residenceTypeList") List<String> residenceTypeList);

	/**
	 * 获取实际日期列表
	 *
	 * @param dateList 日期列表
	 * @return List<String>
	 */
	List<String> listActualDate(@Param("dateList") List<String> dateList);
}
