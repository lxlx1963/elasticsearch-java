package com.du.elasticsearch.service;

import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.MonitorDataAdvertisementTime;
import com.xinchao.data.model.dto.MonitorWatchTouchDTO;

import java.util.List;

/**
 * 监播数据（广告时间）Service
 *
 * @author dxy
 * @date 2019/3/1 21:55
 */
public interface MonitorDataAdvertisementTimeService {
    /**
     * 插入数据
     *
     * @param monitorDataAdvertisementTime MonitorDataAdvertisementTime
     * @throws ServiceException
     */
    void insertMonitorDataAdvertisementTime(MonitorDataAdvertisementTime monitorDataAdvertisementTime) throws ServiceException;

    /**
     * 批量插入数据
     *
     * @param monitorDataAdvertisementTimeList List<MonitorDataAdvertisementTime>
     * @throws ServiceException
     */
    void insertMonitorDataAdvertisementTimeBatch(List<MonitorDataAdvertisementTime> monitorDataAdvertisementTimeList) throws ServiceException;

    /**
     * 获取监播观看触达人次列表
     *
     * @param dateList 日期列表
     * @return List<MonitorWatchTouchDTO>
     */
    List<MonitorWatchTouchDTO> listMonitorWatchTouch(List<String> dateList) throws ServiceException;

    /**
     * 获取实际日期列表
     *
     * @param dateList 日期列表
     * @return List<String>
     */
    List<String> listActualDate(List<String> dateList);
}
