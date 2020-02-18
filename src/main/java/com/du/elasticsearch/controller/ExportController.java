package com.du.elasticsearch.controller;

import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import com.xinchao.data.constant.FileSuffixConstant;
import com.xinchao.data.constant.LoggerConstant;
import com.xinchao.data.constant.MessageConstant;
import com.xinchao.data.service.FaceDataExportService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author dxy
 * @date 2019/7/17 11:27
 */
@Controller
@RequestMapping(value = "/export")
public class ExportController {
	private static Logger logger = LoggerFactory.getLogger(ExportController.class);
	@Autowired
	private FaceDataExportService faceDataExportService;

	/**
	 * 导出广告
	 */
	@GetMapping("/exportAdvertisement")
	public void exportAdvertisement(HttpServletResponse response, String startDate, String endDate) {
		// 设置请求编码
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());

		try (OutputStream out = response.getOutputStream()) {
			if (StringUtils.isBlank(startDate) &&  StringUtils.isBlank(endDate)) {
				response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.HTML_UTF_8.toString());
				out.write(MessageConstant.STARTTIME_AND_ENDTIME_IS_NULL.getBytes());
				return;
			}
			if (Integer.parseInt(startDate) > Integer.parseInt(endDate)) {
				response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.HTML_UTF_8.toString());
				out.write(MessageConstant.STARTTIME_GRTTER_THAN_ENDTIME.getBytes());
				return;
			}
			// 设置输出类型
			response.setContentType(MediaType.MICROSOFT_EXCEL.toString() + ";charset=utf-8");
			// 文件名
			String fileName = "attachment;filename="
					+ new String(("人脸识别数据-监播-广告" + FileSuffixConstant.EXCEL_XLSX).getBytes(), StandardCharsets.ISO_8859_1.name());
			// 设置文件名
			response.setHeader(HttpHeaders.CONTENT_DISPOSITION, fileName);
			// 导出excel
			faceDataExportService.getAdvertisementWorkbook(startDate, endDate).write(out);
		} catch (Exception e) {
			logger.error(LoggerConstant.EXPORT_ADVERTISEMENT, e);
		}
	}

	/**
	 * 导出设备数量
	 */
	@GetMapping("/exportDeviceNum")
	public void exportDeviceNum(HttpServletResponse response, String date) {
		// 设置请求编码
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());

		try (OutputStream out = response.getOutputStream()) {
			if (StringUtils.isBlank(date)) {
				response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.HTML_UTF_8.toString());
				out.write(MessageConstant.DATE_IS_NULLL.getBytes());
				return;
			}
			// 设置输出类型
			response.setContentType(MediaType.MICROSOFT_EXCEL.toString() + ";charset=utf-8");
			// 文件名称
			String fileName = "attachment;filename=" + new String(("各城市人脸识别设备数量-" + date + FileSuffixConstant.EXCEL_XLSX).getBytes(), StandardCharsets.ISO_8859_1.name());

			// 设置文件名
			response.setHeader(HttpHeaders.CONTENT_DISPOSITION, fileName);
			// 导出excel
			faceDataExportService.getCiytResidenceTypeWorkbook(date).write(out);
		} catch (Exception e) {
			logger.error(LoggerConstant.EXPORT_DEVICE_NUM, e);
		}
	}

}
