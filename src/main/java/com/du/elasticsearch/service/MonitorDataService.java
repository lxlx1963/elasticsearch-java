package com.du.elasticsearch.service;

import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.MonitorData;
import com.xinchao.data.model.dto.MonitorDataDTO;
import com.xinchao.data.model.dto.MonitorDateWatchDTO;

import java.util.List;

/**
 * 监播数据Service
 *
 * @author dxy
 * @date 2019/3/1 21:44
 */
public interface MonitorDataService {

    /**
     * 插入数据
     *
     * @param monitorData MonitorData
     * @throws ServiceException
     */
    void insertMonitorData(MonitorData monitorData) throws ServiceException;

    /**
     * 批量插入数据
     *
     * @param monitorDataList List<MonitorData>
     * @throws ServiceException
     */
    void insertMonitorDataBatch(List<MonitorData> monitorDataList) throws ServiceException;

    /**
     * 获取监播数据DTO
     *
     * @param dateList 日期列表
     * @return MonitorDataDTO
     */
    MonitorDataDTO getMonitorDataDTO(List<String> dateList) throws ServiceException;

    /**
     * 获取监播时间观看
     *
     * @param dateList 日期列表
     * @return List<MonitorDateWatchDTO>
     */
    List<MonitorDateWatchDTO> listMonitorDateWatch(List<String> dateList) throws ServiceException;

    /**
     * 获取实际日期列表
     *
     * @param dateList 日期列表
     * @return List<String>
     * @throws ServiceException
     */
    List<String> listActualDate(List<String> dateList) throws ServiceException;
}
