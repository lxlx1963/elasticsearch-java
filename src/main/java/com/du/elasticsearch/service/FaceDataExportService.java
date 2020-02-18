package com.du.elasticsearch.service;

import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.dto.ExportAdvertisementResidenceTypeDTO;
import com.xinchao.data.model.dto.ExportCityResidenceTypeDeviceNumberDTO;
import com.xinchao.data.model.dto.ExportDeviceInfoDTO;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * 人脸数据导出Service
 *
 * @author dxy
 * @date 2019/7/16 15:21
 */
public interface FaceDataExportService {
	/**
	 * 获取广告对应住宅类型人脸数据
	 *
	 * @param startDate 开始日期
	 * @param endDate 结束日期
	 * @return Map<String, Map<String, ExportAdvertisementResidenceTypeDTO>>
	 * @throws ServiceException
	 */
	Map<String, Map<String, ExportAdvertisementResidenceTypeDTO>> getFaceDataAdvertisementResidentType(String startDate, String endDate) throws ServiceException;

	/**
	 * 获取广告的Workbook
	 *
	 * @param startDate 开始日期
	 * @param endDate 结束日期
	 * @return Workbook
	 * @throws ServiceException
	 */
	Workbook getAdvertisementWorkbook(String startDate, String endDate) throws ServiceException;

	/**
	 * 获取设备数据
	 *
	 * @param date 日期
	 * @return List<ExportDeviceInfoDTO>
	 * @throws ServiceException
	 */
	List<ExportDeviceInfoDTO> listExportDeviceInfoDTO(String date) throws ServiceException, ParseException, IOException;

	/**
	 * 获取城市住宅类型对应设备数量
	 *
	 * @param date 日期
	 * @return Map<String, Map<String, ExportCityResidenceTypeDeviceNumberDTO>>
	 * @throws ServiceException
	 */
	Map<String, Map<String, ExportCityResidenceTypeDeviceNumberDTO>> getFaceDataCityResidenceTypeDeviceNumber(String date) throws ServiceException;

	/**
	 * 获取城市住宅类型Workbook
	 *
	 * @param date 日期
	 * @return Workbook
	 * @throws ServiceException
	 */
	Workbook getCiytResidenceTypeWorkbook(String date) throws ServiceException;


}
