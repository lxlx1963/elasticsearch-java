package com.du.elasticsearch.service.impl;

import com.xinchao.data.constant.MessageConstant;
import com.xinchao.data.dao.FaceDataSexMapper;
import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.FaceDataSex;
import com.xinchao.data.model.dto.FaceDataSexDTO;
import com.xinchao.data.model.dto.SingleDeviceTimeSexDTO;
import com.xinchao.data.service.FaceDataSexService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 人脸数据（性别）Service实现类
 *
 * @author dxy
 * @date 2019/3/2 9:19
 */
@Service(value = "faceDataSexService")
public class FaceDataSexServiceImpl implements FaceDataSexService {
    @Autowired
    private FaceDataSexMapper faceDataSexMapper;

    /**
     * 插入数据
     *
     * @param faceDataSex FaceDataSex
     * @throws ServiceException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertFaceDataSex(FaceDataSex faceDataSex) throws ServiceException {
        if (faceDataSex == null) {
            throw new ServiceException(MessageConstant.PARAMETER_IS_NULL);
        }
        faceDataSexMapper.insertFaceDataSex(faceDataSex);
    }

    /**
     * 批量插入数据
     *
     * @param faceDataSexList List<FaceDataSex>
     * @throws ServiceException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertFaceDataSexBatch(List<FaceDataSex> faceDataSexList) throws ServiceException {
        if (CollectionUtils.isEmpty(faceDataSexList)) {
            throw new ServiceException(MessageConstant.PARAMETER_IS_NULL);
        }
        faceDataSexMapper.insertFaceDataSexBatch(faceDataSexList);
    }

    /**
     * 获取人脸数据性别DTO列表
     *
     * @param dateList 日期列表
     * @return List<FaceDataSexDTO>
     * @throws ServiceException
     */
    @Override
    public List<FaceDataSexDTO> listFaceDataSexDTO(List<String> dateList) throws ServiceException {
        if (CollectionUtils.isEmpty(dateList)) {
            throw new ServiceException(MessageConstant.DATE_LIST_IS_NULL);
        }
        return faceDataSexMapper.listFaceDataSexDTO(dateList);
    }

    /**
     * 获取客流人数、人次按时间、性别分组
     *
     * @param date          日期
     * @param residenceTypeList 住宅类型列表
     * @return List<DeviceTimeSexDTO>
     * @throws ServiceException
     */
    @Override
    public List<SingleDeviceTimeSexDTO> listDeviceTimeSex(String date,  List<String> residenceTypeList) throws ServiceException {
        if (StringUtils.isBlank(date)) {
            throw new ServiceException(MessageConstant.DATE_IS_NULLL);
        }
        if (CollectionUtils.isEmpty(residenceTypeList)) {
            throw new ServiceException(MessageConstant.RESIDENCE_TYPE_LIST_IS_NULLL);
        }
        return faceDataSexMapper.listDeviceTimeSex(date, residenceTypeList);
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
        return faceDataSexMapper.listActualDate(dateList);
    }

}
