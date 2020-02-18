package com.du.elasticsearch.service.impl;

import com.xinchao.data.constant.MessageConstant;
import com.xinchao.data.dao.FaceDataDateMapper;
import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.FaceDataDate;
import com.xinchao.data.model.dto.FaceDataDateDTO;
import com.xinchao.data.service.FaceDataDateService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author dxy
 * @date 2019/4/1 22:29
 */
@Service(value = "faceDataDateService")
public class FaceDataDateServiceImpl implements FaceDataDateService {
    @Autowired
    private FaceDataDateMapper faceDataDateMapper;

    /**
     * 插入数据
     *
     * @param faceDataDate FaceDataDate
     * @throws ServiceException
     */
    @Override
    public void insertFaceDataDate(FaceDataDate faceDataDate) throws ServiceException {
        if (faceDataDate == null) {
            throw new ServiceException(MessageConstant.PARAMETER_IS_NULL);
        }
        faceDataDateMapper.insertFaceDataDate(faceDataDate);
    }

    /**
     * 批量插入数据
     *
     * @param faceDataDateList List<FaceDataDate>
     * @throws ServiceException
     */
    @Override
    public void insertFaceDataDateBatch(List<FaceDataDate> faceDataDateList) throws ServiceException {
        if (CollectionUtils.isNotEmpty(faceDataDateList)) {
            throw new ServiceException(MessageConstant.PARAMETER_IS_NULL);
        }
        faceDataDateMapper.insertFaceDataDateBatch(faceDataDateList);
    }

    /**
     * 获取人数人次Dto
     *
     * @param dateList 日期列表
     * @return FaceDataDateDTO
     * @throws ServiceException
     */
    @Override
    public FaceDataDateDTO getFaceDataDateDTO(List<String> dateList) throws ServiceException {
        if (CollectionUtils.isEmpty(dateList)) {
            throw new ServiceException(MessageConstant.DATE_LIST_IS_NULL);
        }
        return faceDataDateMapper.getFaceDataDateDTO(dateList);
    }
}
