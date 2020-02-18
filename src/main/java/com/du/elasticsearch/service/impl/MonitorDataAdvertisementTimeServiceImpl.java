package com.du.elasticsearch.service.impl;

import com.xinchao.data.constant.MessageConstant;
import com.xinchao.data.dao.MonitorDataAdvertisementTimeMapper;
import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.MonitorDataAdvertisementTime;
import com.xinchao.data.model.dto.MonitorWatchTouchDTO;
import com.xinchao.data.service.MonitorDataAdvertisementTimeService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 监播数据（广告时间）Service实现类
 *
 * @author dxy
 * @date 2019/3/2 9:30
 */
@Service(value = "monitorDataAdvertisementTimeService")
public class MonitorDataAdvertisementTimeServiceImpl implements MonitorDataAdvertisementTimeService {
    @Autowired
    private MonitorDataAdvertisementTimeMapper monitorDataAdvertisementTimeMapper;

    /**
     * 插入数据
     *
     * @param monitorDataAdvertisementTime MonitorDataAdvertisementTime
     * @throws ServiceException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertMonitorDataAdvertisementTime(MonitorDataAdvertisementTime monitorDataAdvertisementTime) throws ServiceException {
        if (monitorDataAdvertisementTime == null) {
            throw new ServiceException(MessageConstant.PARAMETER_IS_NULL);
        }
        monitorDataAdvertisementTimeMapper.insertMonitorDataAdvertisementTime(monitorDataAdvertisementTime);
    }

    /**
     * 批量插入数据
     *
     * @param monitorDataAdvertisementTimeList List<MonitorDataAdvertisementTime>
     * @throws ServiceException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertMonitorDataAdvertisementTimeBatch(List<MonitorDataAdvertisementTime> monitorDataAdvertisementTimeList) throws ServiceException {
        if (CollectionUtils.isEmpty(monitorDataAdvertisementTimeList)) {
            throw new ServiceException(MessageConstant.PARAMETER_IS_NULL);
        }
        monitorDataAdvertisementTimeMapper.insertMonitorDataAdvertisementTimeBatch(monitorDataAdvertisementTimeList);
    }

    /**
     * 获取监播观看触达人次列表
     * @param dateList 日期列表
     * @return List<MonitorWatchTouchDTO>
     * @throws ServiceException
     */
    @Override
    public List<MonitorWatchTouchDTO> listMonitorWatchTouch(List<String> dateList) throws ServiceException {
        if (CollectionUtils.isEmpty(dateList)) {
            throw new ServiceException(MessageConstant.DATE_LIST_IS_NULL);
        }
        return monitorDataAdvertisementTimeMapper.listMonitorWatchTouch(dateList);
    }

    /**
     * 获取实际日期列表
     *
     * @param dateList 日期列表
     * @return List<String>
     */
    @Override
    public List<String> listActualDate(List<String> dateList) {
        return monitorDataAdvertisementTimeMapper.listActualDate(dateList);
    }
}
