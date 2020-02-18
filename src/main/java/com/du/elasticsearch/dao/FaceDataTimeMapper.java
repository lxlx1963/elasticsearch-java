package com.du.elasticsearch.dao;

import com.xinchao.data.model.FaceDataTime;
import com.xinchao.data.model.dto.SingleDeviceTimeDTO;
import com.xinchao.data.model.dto.SummarizeRecentSevenDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 人脸数据（日期）
 *
 * @author dxy
 * @date 2019/3/1 21:19
 */
public interface FaceDataTimeMapper {
    /**
     * 插入数据
     *
     * @param faceDataTime FaceDataTime
     */
    void insertFaceDataTime(@Param("faceDataTime") FaceDataTime faceDataTime);

    /**
     * 批量插入数据
     *
     * @param faceDataTimeList List<FaceDataTime>
     */
    void insertFaceDataTimeBatch(@Param("faceDataTimeList") List<FaceDataTime> faceDataTimeList);

    /**
     * 获取汇总近7天DTO列表
     *
     * @param dateList 日期列表
     * @return List<SummarizeRecentSevenDTO>
     */
    List<SummarizeRecentSevenDTO> listSummarizeRecentSevenDTO(@Param("dateList") List<String> dateList);

    /**
     * 获取客流人数、人次按时间分组
     *
     * @param date          日期
     * @param residenceTypeList 住宅类型列表
     * @return List<SingleDeviceTimeDTO>
     */
    List<SingleDeviceTimeDTO> listDeviceTime(@Param("date") String date, @Param("residenceTypeList") List<String> residenceTypeList);

}
