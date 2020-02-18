package com.du.elasticsearch.service.impl;

import com.xinchao.data.constant.MessageConstant;
import com.xinchao.data.dao.FaceDataTimeMapper;
import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.FaceDataTime;
import com.xinchao.data.model.dto.SingleDeviceTimeDTO;
import com.xinchao.data.model.dto.SummarizeRecentSevenDTO;
import com.xinchao.data.service.FaceDataTimeService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 人脸数据(小区)Service实现类
 *
 * @author dxy
 * @date 2019/3/2 9:12
 */
@Service(value = "faceDataTimeService")
public class FaceDataTimeServiceImpl implements FaceDataTimeService {
    @Autowired
    private FaceDataTimeMapper faceDataTimeMapper;

    /**
     * 插入数据
     *
     * @param faceDataTime FaceDataTime
     * @throws ServiceException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertFaceDataTime(FaceDataTime faceDataTime) throws ServiceException {
        if (faceDataTime == null) {
            throw new ServiceException(MessageConstant.PARAMETER_IS_NULL);
        }
        faceDataTimeMapper.insertFaceDataTime(faceDataTime);
    }

    /**
     * 批量插入数据
     *
     * @param faceDataTimeList List<FaceDataTime>
     * @throws ServiceException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertFaceDataTimeBatch(List<FaceDataTime> faceDataTimeList) throws ServiceException {
        if (CollectionUtils.isEmpty(faceDataTimeList)) {
            throw new ServiceException(MessageConstant.PARAMETER_IS_NULL);
        }
        faceDataTimeMapper.insertFaceDataTimeBatch(faceDataTimeList);
    }

    /**
     * 获取汇总近7天DTO列表
     *
     * @param dateList 日期列表
     * @return List<SummarizeRecentSevenDTO>
     * @throws ServiceException
     */
    @Override
    public List<SummarizeRecentSevenDTO> listSummarizeRecentSevenDTO(List<String> dateList) throws ServiceException {
        if (CollectionUtils.isEmpty(dateList)) {
            throw new ServiceException(MessageConstant.DATE_LIST_IS_NULL);
        }
        return faceDataTimeMapper.listSummarizeRecentSevenDTO(dateList);
    }

    /**
     * 获取客流人数、人次按时间分组
     *
     * @param date          日期
     * @param residenceTypeList 住宅类型列表
     * @return List<DeviceTimeDTO>
     * @throws ServiceException
     */
    @Override
    public List<SingleDeviceTimeDTO> listDeviceTime(String date,  List<String> residenceTypeList) throws ServiceException {
        if (StringUtils.isBlank(date)) {
            throw new ServiceException(MessageConstant.DATE_IS_NULLL);
        }
        if (CollectionUtils.isEmpty(residenceTypeList)) {
            throw new ServiceException(MessageConstant.RESIDENCE_TYPE_LIST_IS_NULLL);
        }
        return faceDataTimeMapper.listDeviceTime(date, residenceTypeList);
    }
}
