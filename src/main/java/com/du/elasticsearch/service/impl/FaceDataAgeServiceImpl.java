package com.du.elasticsearch.service.impl;

import com.xinchao.data.constant.MessageConstant;
import com.xinchao.data.dao.FaceDataAgeMapper;
import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.FaceDataAge;
import com.xinchao.data.model.dto.FaceDataAgeDTO;
import com.xinchao.data.model.dto.SingleDeviceTimeAgeDTO;
import com.xinchao.data.service.FaceDataAgeService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 人脸数据（年龄）Service实现类
 *
 * @author dxy
 * @date 2019/3/2 8:58
 */
@Service(value = "faceDataAgeService")
public class FaceDataAgeServiceImpl implements FaceDataAgeService {
    @Autowired
    private FaceDataAgeMapper faceDataAgeMapper;

    /**
     * 插入数据
     *
     * @param faceDataAge FaceDataAge
     * @throws ServiceException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertFaceDataAge(FaceDataAge faceDataAge) throws ServiceException {
        if (faceDataAge == null) {
            throw new ServiceException(MessageConstant.PARAMETER_IS_NULL);
        }
        faceDataAgeMapper.insertFaceDataAge(faceDataAge);
    }

    /**
     * 批量插入数据
     *
     * @param faceDataAgeList List<FaceDataAge>
     * @throws ServiceException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertFaceDataAgeBatch(List<FaceDataAge> faceDataAgeList) throws ServiceException {
        if (CollectionUtils.isEmpty(faceDataAgeList)) {
            throw new ServiceException(MessageConstant.PARAMETER_IS_NULL);
        }
        faceDataAgeMapper.insertFaceDataAgeBatch(faceDataAgeList);
    }

    /**
     * 获取人脸数据年龄DTO列表
     *
     * @param dateList 日期列表
     * @return List<FaceDataAgeDTO>
     * @throws ServiceException
     */
    @Override
    public List<FaceDataAgeDTO> listFaceDataAgeDTO(List<String> dateList) throws ServiceException {
        if (CollectionUtils.isEmpty(dateList)) {
            throw new ServiceException(MessageConstant.DATE_LIST_IS_NULL);
        }
        return faceDataAgeMapper.listFaceDataAgeDTO(dateList);
    }

    /**
     * 获取时间下的年龄段列表
     *
     * @param date          日期
     * @param residenceTypeList 住宅类型列表
     * @return List<DeviceTimeAgeDTO>
     * @throws ServiceException
     */
    @Override
    public List<SingleDeviceTimeAgeDTO> listDeviceTimeAge(String date, List<String> residenceTypeList) throws ServiceException {
        if (StringUtils.isBlank(date)) {
            throw new ServiceException(MessageConstant.DATE_IS_NULLL);
        }
        if (CollectionUtils.isEmpty(residenceTypeList)) {
            throw new ServiceException(MessageConstant.RESIDENCE_TYPE_LIST_IS_NULLL);
        }
        return faceDataAgeMapper.listDeviceTimeAge(date, residenceTypeList);
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
        return faceDataAgeMapper.listActualDate(dateList);
    }
}
