package com.du.elasticsearch.service;

import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.MonitorDataAge;
import com.xinchao.data.model.dto.MonitorAgeWatchDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 监播数据（年龄）Service
 *
 * @author dxy
 * @date 2019/3/2 9:34
 */
public interface MonitorDataAgeService {
    /**
     * 插入数据
     *
     * @param monitorDataAge MonitorDataAge
     * @throws ServiceException
     */
    void insertMonitorDataAge(MonitorDataAge monitorDataAge) throws ServiceException;

    /**
     * 批量插入数据
     *
     * @param monitorDataAgeList List<MonitorDataAge>
     * @throws ServiceException
     */
    void insertMonitorDataAgeBatch(List<MonitorDataAge> monitorDataAgeList) throws ServiceException;

    /**
     * 获取监播年龄观看人次列表
     *
     * @param dateList 日期列表
     * @return List<MonitorAgeWatchDTO>
     */
    List<MonitorAgeWatchDTO> listMonitorAgeWatch(@Param("dateList") List<String> dateList) throws ServiceException;

    /**
     * 获取实际日期列表
     * @param dateList 日期列表
     * @return List<String>
     * @throws ServiceException
     */
    List<String> listActualDate(@Param("dateList") List<String> dateList) throws ServiceException;
}
