package com.du.elasticsearch.service;

import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.dto.SingleDeviceTimeAgeDTO;
import com.xinchao.data.model.dto.SingleDeviceTimeDTO;
import com.xinchao.data.model.dto.SingleDeviceTimeSexDTO;
import com.xinchao.data.model.vo.SingleDeviceTimeSexVO;

import java.util.List;

/**
 * @author dxy
 * @date 2019/3/8 21:07
 */
public interface SingleDeviceService {
    /**
     * 获取昨天的客流人数、客流人次及男女分别的客流人数、客流人次
     *
     * @param residenceType 住宅类型
     * @return SingleDeviceTimeSexVO
     * @throws ServiceException
     */
    SingleDeviceTimeSexVO getSingleDeviceTimeSex(String residenceType) throws ServiceException;

    /**
     * 昨日每小时的人员构成 (年龄分布)
     *
     * @param residenceType 住宅类型
     * @return List<SingleDeviceTimeAgeDTO>
     * @throws ServiceException
     */
    List<SingleDeviceTimeAgeDTO> listDeviceTimeAge(String residenceType) throws ServiceException;

    /**
     * 昨日每小时的人员构成 (性别分布)
     *
     * @param residenceType 住宅类型
     * @return List<SingleDeviceTimeSexDTO>
     */
    List<SingleDeviceTimeSexDTO> listDeviceTimeSex(String residenceType) throws ServiceException;

    /**
     * 昨日每小时平均客流人次和客流人数
     *
     * @param residenceType 住宅类型
     * @return List<SingleDeviceTimeDTO>
     * @throws ServiceException
     */
    List<SingleDeviceTimeDTO> listDeviceTime(String residenceType) throws ServiceException;
}
