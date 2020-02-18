package com.du.elasticsearch.dao;

import com.xinchao.data.model.FaceDataDate;
import com.xinchao.data.model.dto.FaceDataDateDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 人脸数据（日期）
 *
 * @author dxy
 * @date 2019/3/1 21:19
 */
public interface FaceDataDateMapper {
	/**
	 * 插入数据
	 *
	 * @param faceDataDate FaceDataDate
	 */
	void insertFaceDataDate(@Param("faceDataDate") FaceDataDate faceDataDate);

	/**
	 * 批量插入数据
	 *
	 * @param faceDataDateList List<FaceDataDate>
	 */
	void insertFaceDataDateBatch(@Param("faceDataDateList") List<FaceDataDate> faceDataDateList);

	/**
	 * 获取人数人次Dto
	 *
	 * @param dateList 日期列表
	 * @return FaceDataDateDTO
	 */
	FaceDataDateDTO getFaceDataDateDTO(@Param("dateList") List<String> dateList);

}
