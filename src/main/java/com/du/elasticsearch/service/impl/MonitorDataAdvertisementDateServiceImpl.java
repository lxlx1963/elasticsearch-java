package com.du.elasticsearch.service.impl;

import com.xinchao.data.constant.MessageConstant;
import com.xinchao.data.dao.MonitorDataAdvertisementDateMapper;
import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.MonitorDataAdvertisementDate;
import com.xinchao.data.model.dto.MonitorAdvertisementWatchDTO;
import com.xinchao.data.service.MonitorDataAdvertisementDateService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 监播数据（广告日期）Service实现类
 *
 * @author dxy
 * @date 2019/3/2 9:24
 */
@Service(value = "monitorDataAdvertisementDateService")
public class MonitorDataAdvertisementDateServiceImpl implements MonitorDataAdvertisementDateService {
    @Autowired
    private MonitorDataAdvertisementDateMapper monitorDataAdvertisementDateMapper;

    /**
     * 插入数据
     *
     * @param monitorDataAdvertisementDate MonitorDataAdvertisementDate
     * @throws ServiceException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertMonitorDataAdvertisementDate(MonitorDataAdvertisementDate monitorDataAdvertisementDate) throws ServiceException {
        if (monitorDataAdvertisementDate == null) {
            throw new ServiceException(MessageConstant.PARAMETER_IS_NULL);
        }
        monitorDataAdvertisementDateMapper.insertMonitorDataAdvertisementDate(monitorDataAdvertisementDate);
    }

    /**
     * 批量插入数据
     *
     * @param monitorDataAdvertisementDateList List<MonitorDataAdvertisementDate>
     * @throws ServiceException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertMonitorDataAdvertisementDateBatch(List<MonitorDataAdvertisementDate> monitorDataAdvertisementDateList) throws ServiceException {
        if (CollectionUtils.isEmpty(monitorDataAdvertisementDateList)) {
            throw new ServiceException(MessageConstant.PARAMETER_IS_NULL);
        }
        monitorDataAdvertisementDateMapper.insertMonitorDataAdvertisementDateBatch(monitorDataAdvertisementDateList);
    }

    /**
     * 获取广告观看次数列表
     * @param dateList 日期列表
     * @param topSize  钱多少条
     * @return List<MonitorAdvertisementWatchDTO>
     * @throws ServiceException
     */
    @Override
    public List<MonitorAdvertisementWatchDTO> listMonitorAdvertisementWatch(List<String> dateList, int topSize) throws ServiceException {
        if (CollectionUtils.isEmpty(dateList)) {
            throw new ServiceException(MessageConstant.DATE_LIST_IS_NULL);
        }
        return monitorDataAdvertisementDateMapper.listMonitorAdvertisementWatch(dateList, topSize);
    }

    /**
     * 获取实际日期列表
     *
     * @param dateList 日期列表
     * @return List<String>
     * @throws ServiceException
     */
    @Override
    public List<String> listActualDate(List<String> dateList) throws ServiceException {
        if (CollectionUtils.isEmpty(dateList)) {
            throw new ServiceException(MessageConstant.DATE_LIST_IS_NULL);
        }
        return monitorDataAdvertisementDateMapper.listActualDate(dateList);
    }
}
