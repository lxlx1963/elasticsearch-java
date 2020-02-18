package com.du.elasticsearch.service.impl;

import com.xinchao.data.constant.MessageConstant;
import com.xinchao.data.dao.MonitorDataMapper;
import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.MonitorData;
import com.xinchao.data.model.dto.MonitorDataDTO;
import com.xinchao.data.model.dto.MonitorDateWatchDTO;
import com.xinchao.data.service.MonitorDataService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 监播数据Service实现类
 *
 * @author dxy
 * @date 2019/3/2 9:41
 */
@Service(value = "monitorDataService")
public class MonitorDataServiceImpl implements MonitorDataService {
    @Autowired
    private MonitorDataMapper monitorDataMapper;

    /**
     * 插入数据
     *
     * @param monitorData MonitorData
     * @throws ServiceException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertMonitorData(MonitorData monitorData) throws ServiceException {
        if (monitorData == null) {
            throw new ServiceException(MessageConstant.PARAMETER_IS_NULL);
        }
        monitorDataMapper.insertMonitorData(monitorData);
    }

    /**
     * 批量插入数据
     *
     * @param monitorDataList List<MonitorData>
     * @throws ServiceException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertMonitorDataBatch(List<MonitorData> monitorDataList) throws ServiceException {
        if (CollectionUtils.isEmpty(monitorDataList)) {
            throw new ServiceException(MessageConstant.PARAMETER_IS_NULL);
        }
        monitorDataMapper.insertMonitorDataBatch(monitorDataList);
    }

    /**
     * 获取监播数据DTO
     *
     * @param dateList 日期列表
     * @return MonitorDataDTO
     * @throws ServiceException
     */
    @Override
    public MonitorDataDTO getMonitorDataDTO(List<String> dateList) throws ServiceException {
        if (CollectionUtils.isEmpty(dateList)) {
            throw new ServiceException(MessageConstant.DATE_LIST_IS_NULL);
        }
        return monitorDataMapper.getMonitorDataDTO(dateList);
    }

    /**
     * 获取监播时间观看
     *
     * @param dateList 日期列表
     * @return List<MonitorDateWatchDTO>
     * @throws ServiceException
     */
    @Override
    public List<MonitorDateWatchDTO> listMonitorDateWatch(List<String> dateList) throws ServiceException {
        if (CollectionUtils.isEmpty(dateList)) {
            throw new ServiceException(MessageConstant.DATE_LIST_IS_NULL);
        }
        return monitorDataMapper.listMonitorDateWatch(dateList);
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
        return monitorDataMapper.listActualDate(dateList);
    }
}
