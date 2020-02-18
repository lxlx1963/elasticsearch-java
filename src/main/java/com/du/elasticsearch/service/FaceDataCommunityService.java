package com.du.elasticsearch.service;

import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.FaceDataCommunity;
import com.xinchao.data.model.dto.*;

import java.util.List;

/**
 * 人脸数据（小区）Service
 */
public interface FaceDataCommunityService {
    /**
     * 插入数据
     *
     * @param faceDataCommunity FaceDataCommunity
     * @throws ServiceException
     */
    void insertFaceDataCommunity(FaceDataCommunity faceDataCommunity) throws ServiceException;

    /**
     * 批量插入数据
     *
     * @param faceDataCommunityList List<FaceDataCommunity>
     * @throws ServiceException
     */
    void insertFaceDataCommunityBatch(List<FaceDataCommunity> faceDataCommunityList) throws ServiceException;

    /**
     * 获取汇总近7天top10小区的人数列表
     *
     * @param dateList List<String>
     * @return List<SummarizeTopTenDTO>
     * @throws ServiceException
     */
    List<SummarizeTopTenDTO> listSummarizeTopTenDTO(List<String> dateList) throws ServiceException;

    /**
     * 获取人数年龄列表
     *
     * @param date      日期
     * @param community 小区
     * @return List<PeopleNumAgeDTO>
     */
    List<PeopleNumAgeDTO> listPeopleNumAge(String date, String community) throws ServiceException;

    /**
     * 获取人数人次时间列表
     *
     * @param dateList  日期列表
     * @param community 小区
     * @return List<PeopleNumTimeTimeDTO>
     */
    List<PeopleNumTimeTimeDTO> listPeopleNumTimeTime(List<String> dateList, String community) throws ServiceException;

    /**
     * 获取人数人次年龄列表
     *
     * @param dateList  日期列表
     * @param community 小区
     * @return List<PeopleNumTimeAgeDTO>
     */
    List<PeopleNumTimeAgeDTO> listPeopleNumTimeAge(List<String> dateList, String community) throws ServiceException;


    /**
     * 获取实际日期列表
     *
     * @param dateList 日期列表
     * @return List<String>
     * @throws ServiceException
     */
    List<String> listActualDate(List<String> dateList) throws ServiceException;

    /**
     * 获取小区列表
     *
     * @param date 日期
     * @return List<String>
     * @throws ServiceException
     */
    List<String> listCommunity(String date) throws ServiceException;

    /**
     * 获取小区的人数人次
     *
     * @param dateList 日期列表
     * @param community 小区
     * @return PeopleNumTimeDTO
     */
    PeopleNumTimeDTO getPeopleNumTime(List<String> dateList, String community) throws ServiceException;

    /**
     * 获取省城市数量列表
     *
     * @param date 日期
     * @return List<ProvinceCityNumDTO>
     */
    List<ProvinceCityNumDTO> listProvinceCityNumDTO(String date) throws ServiceException;

    /**
     * 获取省小区数量列表
     *
     * @param date 日期
     * @return List<ProvinceCommunityNumDTO>
     */
    List<ProvinceCommunityNumDTO> listProvinceCommunityNumDTO(String date) throws ServiceException;
}
