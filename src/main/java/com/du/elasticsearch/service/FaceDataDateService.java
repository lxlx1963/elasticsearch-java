package com.du.elasticsearch.service;

import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.FaceDataDate;
import com.xinchao.data.model.dto.FaceDataDateDTO;

import java.util.List;

/**
 * 人脸数据（日期）
 * @author dxy
 * @date 2019/4/1 22:29
 */
public interface FaceDataDateService {
    /**
     * 插入数据
     *
     * @param faceDataDate FaceDataDate
     * @throws ServiceException
     */
    void insertFaceDataDate(FaceDataDate faceDataDate) throws ServiceException;

    /**
     * 批量插入数据
     *
     * @param faceDataDateList List<FaceDataDate>
     * @throws ServiceException
     */
    void insertFaceDataDateBatch(List<FaceDataDate> faceDataDateList) throws ServiceException;

    /**
     * 获取人数人次Dto
     *
     * @param dateList 日期列表
     * @return FaceDataDateDTO
     * @throws ServiceException
     */
    FaceDataDateDTO getFaceDataDateDTO(List<String> dateList) throws ServiceException;

}
