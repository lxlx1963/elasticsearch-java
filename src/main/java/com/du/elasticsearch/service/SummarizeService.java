package com.du.elasticsearch.service;

import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.dto.FaceDataAgeDTO;
import com.xinchao.data.model.dto.SummarizeFaceDataProvinceDTO;
import com.xinchao.data.model.dto.SummarizeMaleFemaleDTO;
import com.xinchao.data.model.dto.SummarizePeopleNumDTO;
import com.xinchao.data.model.vo.SummarizeMonitorVO;
import com.xinchao.data.model.vo.SummarizeRecentSevenVO;
import com.xinchao.data.model.vo.SummarizeTopTenVO;

import java.util.List;

/**
 * 汇总Service
 *
 * @author dxy
 * @date 2019/3/5 17:12
 */
public interface SummarizeService {
	/**
	 * 男女比列表
	 *
	 * @return List<SummarizeMaleFemaleDTO>
	 * @throws ServiceException
	 */
	List<SummarizeMaleFemaleDTO> listMaleFemaleRatio() throws ServiceException;

	/**
	 * 年龄人数列表
	 *
	 * @return List<FaceDataAgeDTO>
	 * @throws ServiceException
	 */
	List<FaceDataAgeDTO> listAgePeopleNum() throws ServiceException;

	/**
	 * 近7天客流人数列表
	 *
	 * @return List<SummarizeRecentSevenVO>
	 * @throws ServiceException
	 */
	List<SummarizeRecentSevenVO> listSummarizeRecentSevenDTO() throws ServiceException;

	/**
	 * 人数top10小区
	 *
	 * @return List<SummarizeTopTenVO>
	 * @throws ServiceException
	 */
	List<SummarizeTopTenVO> listPeopleNumTopTen() throws ServiceException;

	/**
	 * 客流人数
	 * @return SummarizePeopleNumDTO
	 * @throws ServiceException
	 */
	SummarizePeopleNumDTO getSummarizePeopleNumDTO() throws ServiceException;

	/**
	 * 监播数据
	 * @return SummarizeMonitorDTO
	 * @throws ServiceException
	 */
	SummarizeMonitorVO getSummarizeMonitor() throws ServiceException;

	/**
	 * 获取汇总省人脸数据
	 * @return List<FaceDataProvince>
	 * @throws ServiceException
	 */
	List<SummarizeFaceDataProvinceDTO> listSummarizeFaceDataProvinceDTO() throws ServiceException;

}
