package com.du.elasticsearch.service;

import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.MonitorDataAdvertisementDate;
import com.xinchao.data.model.dto.MonitorAdvertisementWatchDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 监播数据（广告日期）Service
 *
 * @author dxy
 * @date 2019/3/1 21:50
 */
public interface MonitorDataAdvertisementDateService {
    /**
     * 插入数据
     *
     * @param monitorDataAdvertisementDate MonitorDataAdvertisementDate
     * @throws ServiceException
     */
    void insertMonitorDataAdvertisementDate(MonitorDataAdvertisementDate monitorDataAdvertisementDate) throws ServiceException;

    /**
     * 批量插入数据
     *
     * @param monitorDataAdvertisementDateList List<MonitorDataAdvertisementDate>
     * @throws ServiceException
     */
    void insertMonitorDataAdvertisementDateBatch(List<MonitorDataAdvertisementDate> monitorDataAdvertisementDateList) throws ServiceException;

    /**
     * 获取广告观看次数列表
     * @param dateList 日期列表
     * @param topSize  钱多少条
     * @return List<MonitorAdvertisementWatchDTO>
     * @throws ServiceException
     */
    List<MonitorAdvertisementWatchDTO> listMonitorAdvertisementWatch(@Param("dateList") List<String> dateList, @Param("topSize") int topSize) throws ServiceException;

    /**
     * 获取实际日期列表
     *
     * @param dateList 日期列表
     * @return List<String>
     */
    List<String> listActualDate(@Param("dateList") List<String> dateList) throws ServiceException;

}
