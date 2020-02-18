package com.du.elasticsearch.service;

import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.FaceDataTime;
import com.xinchao.data.model.dto.SingleDeviceTimeDTO;
import com.xinchao.data.model.dto.SummarizeRecentSevenDTO;

import java.util.List;

/**
 * 人脸数据(小区)Service
 *
 * @author dxy
 * @date 2019/3/1 21:19
 */
public interface FaceDataTimeService {
    /**
     * 插入数据
     *
     * @param faceDataTime FaceDataTime
     * @throws ServiceException
     */
    void insertFaceDataTime(FaceDataTime faceDataTime) throws ServiceException;

    /**
     * 批量插入数据
     *
     * @param faceDataTimeList List<FaceDataTime>
     * @throws ServiceException
     */
    void insertFaceDataTimeBatch(List<FaceDataTime> faceDataTimeList) throws ServiceException;


    /**
     * 获取汇总近7天DTO列表
     *
     * @param dateList 日期列表
     * @return List<SummarizeRecentSevenDTO>
     * @throws ServiceException
     */
    List<SummarizeRecentSevenDTO> listSummarizeRecentSevenDTO(List<String> dateList) throws ServiceException;

    /**
     * 获取客流人数、人次按时间分组
     *
     * @param date          日期
     * @param residenceTypeList 住宅类型列表
     * @return List<DeviceTimeDTO>
     */
    List<SingleDeviceTimeDTO> listDeviceTime(String date, List<String> residenceTypeList) throws ServiceException;
}
