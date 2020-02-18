package com.du.elasticsearch.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xinchao.data.constant.*;
import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.dto.ExportAdvertisementResidenceTypeDTO;
import com.xinchao.data.model.dto.ExportCityResidenceTypeDeviceNumberDTO;
import com.xinchao.data.model.dto.ExportDeviceInfoDTO;
import com.xinchao.data.model.dto.MonitorDataDTO;
import com.xinchao.data.service.DeviceInfoService;
import com.xinchao.data.service.ElasticsearchService;
import com.xinchao.data.service.FaceDataExportService;
import com.xinchao.data.util.DateUtils;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.TermsAggregation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Map;


/**
 * 人脸数据导出Service实现类
 *
 * @author dxy
 * @date 2019/7/16 15:33
 */
@Service(value = "FaceDataExportService")
public class FaceDataExportServiceImpl implements FaceDataExportService {
	private static Logger logger = LoggerFactory.getLogger(FaceDataExportServiceImpl.class);
	@Autowired
	private ElasticsearchService elasticsearchService;
	@Autowired
	private DeviceInfoService deviceInfoService;

	/**
	 * 获取广告对应住宅类型人脸数据
	 *
	 * @param startDate 开始日期
	 * @param endDate   结束日期
	 * @return Map<String, Map<String, ExportAdvertisementResidenceTypeDTO>>
	 * @throws ServiceException
	 */
	@Override
	public Map<String, Map<String, ExportAdvertisementResidenceTypeDTO>> getFaceDataAdvertisementResidentType(String startDate, String endDate) throws ServiceException {
		if (StringUtils.isBlank(startDate) || StringUtils.isBlank(endDate)) {
			throw new ServiceException(MessageConstant.STARTTIME_AND_ENDTIME_IS_NULL);
		}
		// 终端编码对应的住宅类型
		Map<String, String> deviceNumberResidenceTypeMap = deviceInfoService.getDeviceNumberResidenceTypeMap();
		if (deviceNumberResidenceTypeMap.isEmpty()) {
			throw new ServiceException(MessageConstant.DEVICE_NUMBER_RESIDENCE_TYPE_MAP_IS_NULLL);
		}

		// 索引名称
		String indexName = ElasticsearchConstant.FACE_MONITOR;

		// 返回Map(key: 广告名称， value: Map<String, List<ExportAdvertisementResidenceTypeDTO>>)
		Map<String, Map<String, ExportAdvertisementResidenceTypeDTO>> advertisementResidentTypeMap = Maps.newHashMap();

		try {
			// 最小日期
			Long startTime = DateUtils.stringTimeToLongTimeStamp(startDate + " " + DateConstant.MIN_HOUR_MINUTE_SECOND_MILLISECOND, DateConstant.YEAR_MONTH_DAY_HMS_SSS);
			// 最大日期
			Long endTime = DateUtils.stringTimeToLongTimeStamp(endDate + " " + DateConstant.MAX_HOUR_MINUTE_SECOND_MILLISECOND, DateConstant.YEAR_MONTH_DAY_HMS_SSS);

			// matchAllQueryBuilder（查询所有）
			MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
			// 时间条件查询
			RangeQueryBuilder dateTimeQueryBuilder = QueryBuilders.rangeQuery(ElasticsearchConstant.AT_TIME_STAMP)
					.from(startTime)
					.to(endTime)
					.format(ElasticsearchConstant.EPOCH_MILLIS);

			// 项目名称条件（只查询adx的广告）
			MatchPhraseQueryBuilder projectNameMatchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.PROJECT_NAME, ElasticsearchConstant.PROJECT_NAME_ADX);

			// 屏幕类型条件（只获取上屏的数据）
			MatchPhraseQueryBuilder screenTypeMatchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.SCREEN_TYPE, ElasticsearchConstant.SCREEN_TYPE_ABOVE);

			// BoolQueryBuilder(连接多条件)
			BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
			boolQueryBuilder.must(matchAllQueryBuilder)
					.must(dateTimeQueryBuilder)
					.must(projectNameMatchPhraseQueryBuilder)
					.must(screenTypeMatchPhraseQueryBuilder);

			// 按“广告名称”分组
			TermsAggregationBuilder advertisementNameAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_ADVERTISEMENT_NAME)
					.field(ElasticsearchConstant.ADVERTISEMENT_NAME_KEYWORD)
					.size(Integer.MAX_VALUE);

			// 按“终端编码”分组
			TermsAggregationBuilder deviceNumberAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_DEVICE_NUMBER)
					.field(ElasticsearchConstant.DEVICE_NUMBER_KEYWORD)
					.size(Integer.MAX_VALUE);

			// 先按“广告名称”分组，按“终端编码”分组
			advertisementNameAggregationBuilder.subAggregation(deviceNumberAggregationBuilder);

			// 统计曝光次数
			SumAggregationBuilder exposuresSumAggregationBuilder = AggregationBuilders.sum(ElasticsearchConstant.EXPOSURES_SUM).field(ElasticsearchConstant.EXPOSURES_NUMBER);
			// 统计观看人次
			SumAggregationBuilder watchSumAggregationBuilder = AggregationBuilders.sum(ElasticsearchConstant.WATCH_SUM).field(ElasticsearchConstant.WATCH_NUMBER);
			// 统计触达人次
			SumAggregationBuilder touchSumAggregationBuilder = AggregationBuilders.sum(ElasticsearchConstant.TOUCH_SUM).field(ElasticsearchConstant.TOUCH_NUMBER);
			// 统计播放时长
			SumAggregationBuilder playDurationSumAggregationBuilder = AggregationBuilders.sum(ElasticsearchConstant.PLAY_DURATION_SUM).field(ElasticsearchConstant.PLAY_DURATION);

			// 在“终端编码”分组下统计“曝光次数”、“观看人次”、“触达人次”、“播放时长”
			deviceNumberAggregationBuilder.subAggregation(exposuresSumAggregationBuilder);
			deviceNumberAggregationBuilder.subAggregation(watchSumAggregationBuilder);
			deviceNumberAggregationBuilder.subAggregation(touchSumAggregationBuilder);
			deviceNumberAggregationBuilder.subAggregation(playDurationSumAggregationBuilder);

			// 执行查询
			SearchResult searchResult = elasticsearchService.doSearch(indexName, null, null, boolQueryBuilder, advertisementNameAggregationBuilder);
			// 处理错误
			String errorMessage = searchResult.getErrorMessage();
			if (StringUtils.isNotBlank(errorMessage)) {
				throw new ServiceException(errorMessage);
			}

			//保留三位小数
			DecimalFormat decimalFormat = new DecimalFormat(BusinessConstant.ZERO_POINT_ZERO_ZERO);

			// “广告名称”分组桶
			List<TermsAggregation.Entry> advertisementNameBuckets = searchResult.getAggregations()
					.getTermsAggregation(ElasticsearchConstant.GROUP_BY_ADVERTISEMENT_NAME)
					.getBuckets();

			for (TermsAggregation.Entry advertisementNameEntry : advertisementNameBuckets) {
				// 广告名称
				String advertisementName = advertisementNameEntry.getKey();
				// “终端编码分组桶”
				List<TermsAggregation.Entry> deviceNumberBuckets = advertisementNameEntry.getTermsAggregation(ElasticsearchConstant.GROUP_BY_DEVICE_NUMBER)
						.getBuckets();

				// residenceTypeMap(key: 住宅类型， value: List<MonitorDataDTO>)
				Map<String, List<MonitorDataDTO>> residenceTypeMonitorDataMap = Maps.newHashMap();
				// 生成默认的住宅类型对应的数据列表
				for (String residenceType : BusinessConstant.RESIDENCE_TYPE_LIST) {
					List<MonitorDataDTO> monitorDataDTOS = Lists.newArrayList();
					residenceTypeMonitorDataMap.put(residenceType, monitorDataDTOS);
				}

				// 将同一住宅类型的数据分到同一组
				for (TermsAggregation.Entry deviceNumberEntry : deviceNumberBuckets) {
					// 终端编码
					String deviceNumber = deviceNumberEntry.getKey();

					// 通过终端编码获取住宅类型
					String residenceType = deviceNumberResidenceTypeMap.get(deviceNumber);
					if (StringUtils.isBlank(residenceType)) {
						continue;
					}

					// 曝光次数总计
					Double exposuresSum = deviceNumberEntry.getSumAggregation(ElasticsearchConstant.EXPOSURES_SUM)
							.getSum();

					if (exposuresSum == null) {
						exposuresSum = 0.0;
					} else {
						String exopsuresSumStr = decimalFormat.format(exposuresSum);
						exposuresSum = Double.valueOf(exopsuresSumStr);
					}

					// 触达人次总计
					Double touchSum = deviceNumberEntry.getSumAggregation(ElasticsearchConstant.TOUCH_SUM)
							.getSum();

					// 观看人次总计
					Double watchSum = deviceNumberEntry.getSumAggregation(ElasticsearchConstant.WATCH_SUM)
							.getSum();

					// 播放时长总计
					Double playDurationSum = deviceNumberEntry.getSumAggregation(ElasticsearchConstant.PLAY_DURATION_SUM)
							.getSum();

					MonitorDataDTO monitorDataDTO = new MonitorDataDTO();

					monitorDataDTO.setExposuresSum(exposuresSum);
					monitorDataDTO.setTouchSum(touchSum.intValue());
					monitorDataDTO.setWatchSum(watchSum.intValue());
					monitorDataDTO.setPlayDurationSum(playDurationSum.longValue());

					// 获取住宅类型对应的列表
					List<MonitorDataDTO> monitorDataDTOList = residenceTypeMonitorDataMap.get(residenceType);
					monitorDataDTOList.add(monitorDataDTO);
				}

				// key: 住宅类型；value: List<ExportAdvertisementResidenceTypeDTO>
				Map<String, ExportAdvertisementResidenceTypeDTO> residenceTypeMap = Maps.newHashMap();
				// 设备总数
				Integer deviveSum = 0;
				// 播放总数
				Double playSum = 0D;
				// 触达总数
				Double touchSum = 0D;
				// 观看总数
				Double watchSum = 0D;
				// 平均观看时长
				Double watchDurationAvg = 0.0D;
				// 观看总时长
				Long playDurationSum = 0L;

				// 计算同一类型的设备总数、播放总数、触达总数、观看总数、平均观看时长
				for (Map.Entry<String, List<MonitorDataDTO>> residenceTypeEntry : residenceTypeMonitorDataMap.entrySet()) {
					// 住宅类型
					String residenceType = residenceTypeEntry.getKey();
					// 数据列表
					List<MonitorDataDTO> valueList = residenceTypeEntry.getValue();

					deviveSum = valueList.size();

					ExportAdvertisementResidenceTypeDTO exportAdvertisementResidenceTypeDTO = new ExportAdvertisementResidenceTypeDTO();
					if (CollectionUtils.isNotEmpty(valueList)) {
						// 计算播放总数、触达总数、观看总数、观看总时长
						for (MonitorDataDTO monitorData : valueList) {
							playSum = playSum + monitorData.getExposuresSum();
							touchSum = touchSum + monitorData.getTouchSum();
							watchSum = watchSum + monitorData.getTouchSum();
							playDurationSum = playDurationSum + monitorData.getPlayDurationSum();
						}
						// 平均观看时长=总观看时间/观看人数
						if (watchSum != null && watchSum != 0
								&& playDurationSum != null && playDurationSum != 0) {
								watchDurationAvg = playDurationSum.floatValue() / watchSum / 1000;
						}
					}

					exportAdvertisementResidenceTypeDTO.setDeviveSum(deviveSum);
					exportAdvertisementResidenceTypeDTO.setPlaySum(playSum.intValue());
					exportAdvertisementResidenceTypeDTO.setTouchSum(touchSum.intValue());
					exportAdvertisementResidenceTypeDTO.setWatchSum(watchSum.intValue());
					exportAdvertisementResidenceTypeDTO.setWatchDurationAvg(watchDurationAvg);
					residenceTypeMap.put(residenceType, exportAdvertisementResidenceTypeDTO);
				}

				advertisementResidentTypeMap.put(advertisementName, residenceTypeMap);
			}
		} catch (ParseException | IOException e) {
			logger.error(LoggerConstant.GET_FACE_DATA_ADVERTISEMENT_RESIDENT_TYPE, e);
		}
		return advertisementResidentTypeMap;
	}

	/**
	 * 获取广告的Workbook
	 *
	 * @param startDate 开始日期
	 * @param endDate   结束日期
	 * @return Workbook
	 * @throws ServiceException
	 */
	@Override
	public Workbook getAdvertisementWorkbook(String startDate, String endDate) throws ServiceException {
		// 创建HSSFWorkbook对象(excel的文档对象)
		Workbook wb = new XSSFWorkbook();
		// 行
		Row row = null;
		// 单元格
		Cell cell = null;
		// 建立新的sheet对象（excel的表单） 并设置sheet名字
		Sheet sheet = wb.createSheet(ExcelConstant.SHEET_NAME_ADVERTIMENT);
		// 设置默认宽度
		sheet.setDefaultColumnWidth(13);
		// 设置缺省列宽
		sheet.setDefaultRowHeightInPoints(30);
		// ----------------标题样式---------------------
		// 标题样式
		CellStyle titleStyle = wb.createCellStyle();
		titleStyle.setAlignment(CellStyle.ALIGN_CENTER);
		titleStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		//下边框
		titleStyle.setBorderBottom(CellStyle.BORDER_THIN);
		//左边框
		titleStyle.setBorderLeft(CellStyle.BORDER_THIN);
		//上边框
		titleStyle.setBorderTop(CellStyle.BORDER_THIN);
		//右边框
		titleStyle.setBorderRight(CellStyle.BORDER_THIN);
		Font ztFont = wb.createFont();
		// 设置字体为斜体字
		ztFont.setItalic(false);
		// 将字体设置为“红色”
		ztFont.setColor(Font.COLOR_NORMAL);
		// 将字体大小设置为18px
		ztFont.setFontHeightInPoints((short) 20);
		// 将“宋体”字体应用到当前单元格上
		ztFont.setFontName(ExcelConstant.FONT_NAME_SONG_TYPEFACE);
		// 加粗
		ztFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		titleStyle.setFont(ztFont);
		//-------------------------------------------

		//----------------二级标题格样式----------------------------------
		// 表格样式
		CellStyle titleStyle2 = wb.createCellStyle();
		titleStyle2.setAlignment(CellStyle.ALIGN_CENTER);
		titleStyle2.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		//下边框
		titleStyle2.setBorderBottom(CellStyle.BORDER_THIN);
		//左边框
		titleStyle2.setBorderLeft(CellStyle.BORDER_THIN);
		//上边框
		titleStyle2.setBorderTop(CellStyle.BORDER_THIN);
		//右边框
		titleStyle2.setBorderRight(CellStyle.BORDER_THIN);
		Font ztFont2 = wb.createFont();
		// 设置字体为斜体字
		ztFont2.setItalic(false);
		// 将字体设置为“红色”
		ztFont2.setColor(Font.COLOR_NORMAL);
		// 将字体大小设置为18px
		ztFont2.setFontHeightInPoints((short) 12);
		// 字体应用到当前单元格上
		ztFont2.setFontName(ExcelConstant.FONT_NAME_SONG_TYPEFACE);
		// 加粗
		ztFont2.setBoldweight(Font.BOLDWEIGHT_BOLD);
		titleStyle2.setFont(ztFont2);
		//----------------------------------------------------------
		//----------------单元格样式----------------------------------
		//表格样式
		CellStyle cellStyle = wb.createCellStyle();
		cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
		cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		//下边框
		cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
		//左边框
		cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
		//上边框
		cellStyle.setBorderTop(CellStyle.BORDER_THIN);
		//右边框
		cellStyle.setBorderRight(CellStyle.BORDER_THIN);
		Font cellFont = wb.createFont();
		// 设置字体为斜体字
		cellFont.setItalic(false);
		// 将字体设置为“红色”
		cellFont.setColor(Font.COLOR_NORMAL);
		// 将字体大小设置为18px
		cellFont.setFontHeightInPoints((short) 12);
		// 字体应用到当前单元格上
		cellFont.setFontName(ExcelConstant.FONT_NAME_SONG_TYPEFACE);
		cellFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		cellStyle.setFont(cellFont);
		//设置自动换行
		cellStyle.setWrapText(true);

		// ------------------创建表头start---------------------
		// 创建第一行
		// 行号（行号从0开始计算）
		int rowNum = 0;
		row = sheet.createRow(rowNum);
		// 合并单元格CellRangeAddress构造参数依次表示起始行，截至行，起始列， 截至列
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 20));
		cell = row.createCell(0);
		// 设置单元格内容
		String firstCellValue = "";
		if (startDate.equals(endDate)) {
			firstCellValue = ExcelConstant.CELL_VALUE_ADVERTIMENT_DATE + "(" + startDate + ")";
		} else {
			firstCellValue = ExcelConstant.CELL_VALUE_ADVERTIMENT_DATE + "(" + startDate + "-" + endDate + ")";
		}
		cell.setCellValue(firstCellValue);
		cell.setCellStyle(titleStyle);
		// ----------------------------------------------

		// 创建第二行
		rowNum = rowNum + 1;
		row = sheet.createRow(rowNum);
		// 广告名
		sheet.addMergedRegion(new CellRangeAddress(1, 2, 0, 0));
		cell = row.createCell(0);
		cell.setCellValue(ExcelConstant.CELL_VALUE_ADVERTISEMENT_NAME);
		cell.setCellStyle(titleStyle2);
		// 住宅
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 1, 5));
		cell = row.createCell(1);
		cell.setCellValue(ExcelConstant.CELL_VALUE_RESIDENCE);
		cell.setCellStyle(titleStyle2);
		// 商住楼
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 6, 10));
		cell = row.createCell(6);
		cell.setCellValue(BusinessConstant.COMMERCIAL_RESIDENTIAL_BUILDING);
		cell.setCellStyle(titleStyle2);
		// 综合体
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 11, 15));
		cell = row.createCell(11);
		cell.setCellValue(BusinessConstant.SYNTHESIS_BUILDING);
		cell.setCellStyle(titleStyle2);
		// 写字楼
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 16, 20));
		cell = row.createCell(16);
		cell.setCellValue(BusinessConstant.OFFICE_BUILDING);
		cell.setCellStyle(titleStyle2);

		// 创建第三行
		rowNum = rowNum + 1;
		row = sheet.createRow(rowNum);

		// 单元格号
		int cellNum = 0;
		// 住宅类型列表，按照中高端住宅、商住楼、综合体、写字楼的顺序
		List<String> residenceTypeList = BusinessConstant.RESIDENCE_TYPE_LIST;

		// 从第三行的第二个单元开始
		cellNum = cellNum + 1;

		// 因为所有住宅类型的显示数据是一样的，故循环次数和住宅类型的size一样
		for (int i = 0; i < residenceTypeList.size(); i++) {
			// 设备总数
			cell = row.createCell(cellNum);
			cell.setCellValue(ExcelConstant.CELL_VALUE_DEVIVE_SUM);
			cell.setCellStyle(cellStyle);
			cellNum = cellNum + 1;

			// 播放总数
			cell = row.createCell(cellNum);
			cell.setCellValue(ExcelConstant.CELL_VALUE_PLAY_SUM);
			cell.setCellStyle(cellStyle);
			cellNum = cellNum + 1;

			// 触达总数
			cell = row.createCell(cellNum);
			cell.setCellValue(ExcelConstant.CELL_VALUE_TOUCH_SUM);
			cell.setCellStyle(cellStyle);
			cellNum = cellNum + 1;

			// 观看总数
			cell = row.createCell(cellNum);
			cell.setCellValue(ExcelConstant.CELL_VALUE_WATCH_SUM);
			cell.setCellStyle(cellStyle);
			cellNum = cellNum + 1;

			// 平均观看时长
			cell = row.createCell(cellNum);
			cell.setCellValue(ExcelConstant.CELL_VALUE_WATCH_DURATION_AVG);
			cell.setCellStyle(cellStyle);
			cellNum = cellNum + 1;
		}
		//-------------------------表头end---------------------

		// 查询数据
		Map<String, Map<String, ExportAdvertisementResidenceTypeDTO>> faceDataAdvertisementResidentTypeMap = getFaceDataAdvertisementResidentType(startDate, endDate);

		// 重置单元格号
		cellNum = 0;
		// 行号 + 1 （这里应该是从第四行开始了）
		rowNum = rowNum + 1;
		// 格式化浮点数
		DecimalFormat decimalFormat = new DecimalFormat(BusinessConstant.ZERO_POINT_ZERO_ZERO);

		for (Map.Entry<String, Map<String, ExportAdvertisementResidenceTypeDTO>> entry : faceDataAdvertisementResidentTypeMap.entrySet()) {
			row = sheet.createRow(rowNum);
			// 广告名称
			String advertisementName = entry.getKey();
			cell = row.createCell(cellNum);
			cell.setCellValue(advertisementName);
			cellNum = cellNum + 1;

			// 住宅类型对应的数据
			Map<String, ExportAdvertisementResidenceTypeDTO> residenceTypeMap = entry.getValue();

			for (String residenceType : residenceTypeList) {
				ExportAdvertisementResidenceTypeDTO middleHighDTO = residenceTypeMap.get(residenceType);

				// 设备总数
				cell = row.createCell(cellNum);
				Integer deviveSum = middleHighDTO.getDeviveSum();
				if (deviveSum != null && deviveSum != 0) {
					cell.setCellValue(deviveSum);
				}
				cellNum = cellNum + 1;

				// 播放总数
				cell = row.createCell(cellNum);
				Integer playSum = middleHighDTO.getPlaySum();
				if (playSum != null && playSum != 0) {
					cell.setCellValue(playSum);
				}
				cellNum = cellNum + 1;

				// 触达总数
				cell = row.createCell(cellNum);
				Integer touchSum = middleHighDTO.getTouchSum();
				if (touchSum != null && touchSum != 0) {
					cell.setCellValue(touchSum);
				}
				cellNum = cellNum + 1;

				// 观看总数
				cell = row.createCell(cellNum);
				Integer watchSum = middleHighDTO.getWatchSum();
				if (watchSum != null && watchSum != 0) {
					cell.setCellValue(watchSum);
				}
				cellNum = cellNum + 1;

				// 平均观看时长
				cell = row.createCell(cellNum);
				Double watchDurationAvg = middleHighDTO.getWatchDurationAvg();
				if (watchDurationAvg != null && watchDurationAvg != 0.0D) {
					String watchDurationAvgStr = decimalFormat.format(watchDurationAvg);
					double watchDuration = Double.parseDouble(watchDurationAvgStr);
					cell.setCellValue(watchDuration);
				}
				cellNum = cellNum + 1;
			}
			rowNum++;
			// 重置单元格号，便于下一行重用
			cellNum = 0;
		}
		return wb;
	}

	/**
	 * 获取设备数据
	 *
	 * @param date 日期
	 * @return List<ExportDeviceInfoDTO>
	 * @throws ServiceException
	 */
	@Override
	public List<ExportDeviceInfoDTO> listExportDeviceInfoDTO(String date) throws ServiceException, ParseException, IOException {
		if (StringUtils.isBlank(date)) {
			throw new ServiceException(MessageConstant.DATE_IS_NULLL);
		}
		// 获取终端编码对应的住宅类型信息
		Map<String, String> deviceNumberResidenceTypeMap = deviceInfoService.getDeviceNumberResidenceTypeMap();
		// 如果没有配置终端信息，不继续执行
		if (MapUtils.isEmpty(deviceNumberResidenceTypeMap)) {
			throw new ServiceException(MessageConstant.DEVICE_NUMBER_RESIDENCE_TYPE_MAP_IS_NULLL);
		}
		// 放入缓存

		// 获取终端编码对应的城市
		Map<String, String> deviceNumberCityMap = deviceInfoService.getDeviceNumberCityMap();
		if (MapUtils.isEmpty(deviceNumberCityMap)) {
			throw new ServiceException(MessageConstant.DEVICE_NUMBER_CITY_MAP_IS_NULLL);
		}

		// 索引名称
		String indexName = ElasticsearchConstant.FACE_DATA;
		// 索引类型
		// 计算一天中的最小日期
		Long startTime = DateUtils.stringTimeToLongTimeStamp(date + " " + DateConstant.MIN_HOUR_MINUTE_SECOND_MILLISECOND, DateConstant.YEAR_MONTH_DAY_HMS_SSS);
		// 计算一天中的最大日期
		Long endTime = DateUtils.stringTimeToLongTimeStamp(date + " " + DateConstant.MAX_HOUR_MINUTE_SECOND_MILLISECOND, DateConstant.YEAR_MONTH_DAY_HMS_SSS);
		// 时间查询条件
		RangeQueryBuilder dateTimeQueryBuilder = QueryBuilders.rangeQuery(ElasticsearchConstant.ENTER_TIME)
				.from(startTime)
				.to(endTime)
				.format(ElasticsearchConstant.EPOCH_MILLIS);

		// 过滤性别为“”的数据，性别为“”的数据不计入人数,只查询查性别为“M”“F”
		MatchPhraseQueryBuilder maleMatchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.GENDER_KEYWORD, ElasticsearchConstant.STRING_M);
		MatchPhraseQueryBuilder femaleMatchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.GENDER_KEYWORD, ElasticsearchConstant.STRING_F);

		BoolQueryBuilder genderBoolQueryBuilder = QueryBuilders.boolQuery();
		genderBoolQueryBuilder.should(maleMatchPhraseQueryBuilder)
				.should(femaleMatchPhraseQueryBuilder);

		// 人脸ID为-1，不计入人数
		MatchPhraseQueryBuilder visitorIdMathcPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ElasticsearchConstant.VISITOR_ID, BusinessConstant.MINUS_ONE);

		// 查询所有
		MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
		// 多条件查询
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(matchAllQueryBuilder)
				.must(dateTimeQueryBuilder)
				.must(genderBoolQueryBuilder)
				.mustNot(visitorIdMathcPhraseQueryBuilder);

		/** 查询人数 */
		// 按终端编码分组
		TermsAggregationBuilder visitorIdDeviceCodeTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_DEVICE_CODE)
				.field(ElasticsearchConstant.DEVICE_CODE_KEYWORD)
				.size(Integer.MAX_VALUE);

		// 按visitorId分组，来统计人数
		TermsAggregationBuilder visitorIdTermsAggregationBuilder = AggregationBuilders.terms(ElasticsearchConstant.GROUP_BY_VISITOR_ID)
				.field(ElasticsearchConstant.VISITOR_ID)
				.size(Integer.MAX_VALUE);

		visitorIdDeviceCodeTermsAggregationBuilder.subAggregation(visitorIdTermsAggregationBuilder);

		// 执行查询
		SearchResult visitorIdSearchResult = elasticsearchService.doSearch(indexName, "", null, boolQueryBuilder, visitorIdDeviceCodeTermsAggregationBuilder);

		// 处理ES抛出的错误信息
		String errorMessage = visitorIdSearchResult.getErrorMessage();
		if (StringUtils.isNotBlank(errorMessage)) {
			throw new ServiceException(MessageConstant.ELASTICSEARCH_ERROR_MESSAGE + errorMessage);
		} else {
			// 终端编码分组桶
			List<TermsAggregation.Entry> deviceCodeBuckets = visitorIdSearchResult.getAggregations()
					.getTermsAggregation(ElasticsearchConstant.GROUP_BY_DEVICE_CODE)
					.getBuckets();

			List<ExportDeviceInfoDTO> exportDeviceInfoList = Lists.newArrayList();

			//终端编码桶列表
			for (TermsAggregation.Entry deviceCodeEntry : deviceCodeBuckets) {
				// 终端编码
				String deviceCode = deviceCodeEntry.getKey();
				// 城市
				String city = deviceNumberCityMap.get(deviceCode);
				// 住宅类型
				String residenceType = deviceNumberResidenceTypeMap.get(deviceCode);
				if (StringUtils.isBlank(city) || StringUtils.isBlank(residenceType)) {
					continue;
				}
				//人数
				int peopleNum = deviceCodeEntry.getTermsAggregation(ElasticsearchConstant.GROUP_BY_VISITOR_ID)
						.getBuckets()
						.size();

				ExportDeviceInfoDTO exportDeviceInfoDTO = new ExportDeviceInfoDTO();
				exportDeviceInfoDTO.setDeviceNumber(deviceCode);
				exportDeviceInfoDTO.setCity(city);
				exportDeviceInfoDTO.setResidenceType(residenceType);
				exportDeviceInfoDTO.setPeopleNumber(peopleNum);

				exportDeviceInfoList.add(exportDeviceInfoDTO);
			}
			return exportDeviceInfoList;
		}
	}

	/**
	 * 获取城市住宅类型对应设备数量
	 *
	 * @param date 日期
	 * @return Map<String, Map<String, ExportCityResidenceTypeDeviceNumberDTO>>
	 * @throws ServiceException
	 */
	@Override
	public Map<String, Map<String, ExportCityResidenceTypeDeviceNumberDTO>> getFaceDataCityResidenceTypeDeviceNumber(String date) throws ServiceException {
		if (StringUtils.isBlank(date)) {
			throw new ServiceException(MessageConstant.DATE_IS_NULLL);
		}
		// 返回Map
		Map<String, Map<String, ExportCityResidenceTypeDeviceNumberDTO>> resultMap = Maps.newHashMap();
		try {
			// 从ES中查询数据
			List<ExportDeviceInfoDTO> exportDeviceInfoDTOList = listExportDeviceInfoDTO(date);

			if (CollectionUtils.isNotEmpty(exportDeviceInfoDTOList)) {
				// 将数据先城市、后住宅类型分组
				// key: 城市；value: Map<String, List<ExportDeviceInfoDTO>>
				Map<String, Map<String, List<ExportDeviceInfoDTO>>> cityResidenceTypeMap = Maps.newHashMap();

				for (ExportDeviceInfoDTO exportDeviceInfoDTO : exportDeviceInfoDTOList) {
					// 城市
					String city = exportDeviceInfoDTO.getCity();
					// key: 住宅类型；value: List<ExportDeviceInfoDTO>
					Map<String, List<ExportDeviceInfoDTO>> residenceTypeMap = cityResidenceTypeMap.get(city);
					if (MapUtils.isEmpty(residenceTypeMap)) {
						residenceTypeMap = Maps.newHashMap();
					}
					// 住宅类型
					String residenceType = exportDeviceInfoDTO.getResidenceType();
					List<ExportDeviceInfoDTO> deviceInfoList = residenceTypeMap.get(residenceType);

					if (CollectionUtils.isEmpty(deviceInfoList)) {
						deviceInfoList = Lists.newArrayList();
						residenceTypeMap.put(residenceType, deviceInfoList);
					}
					deviceInfoList.add(exportDeviceInfoDTO);
					cityResidenceTypeMap.put(city, residenceTypeMap);
				}
				
				// 先城市分组，再住宅类型分组，计算人数在各个数据段内
				for (Map.Entry<String, Map<String, List<ExportDeviceInfoDTO>>> cityEntry : cityResidenceTypeMap.entrySet()) {
					// 城市
					String city = cityEntry.getKey();
					Map<String, List<ExportDeviceInfoDTO>> valueMap = cityEntry.getValue();

					Map<String, ExportCityResidenceTypeDeviceNumberDTO> cityResidenceTypeDeviceNumberDTOMap = resultMap.get(city);
					if (MapUtils.isEmpty(cityResidenceTypeDeviceNumberDTOMap)) {
						cityResidenceTypeDeviceNumberDTOMap = Maps.newHashMap();
						resultMap.put(city, cityResidenceTypeDeviceNumberDTOMap);
					}

					// 循环住宅类型，如果住宅类型下没有数据列表，则加上默认值
					for (String residenceType : BusinessConstant.RESIDENCE_TYPE_LIST) {
						// 日均<50
						int lessThanFiftyNum = 0;
						// 50-200
						int fiftyAndTwoHundredNum = 0;
						// 200-400
						int twoAndFourHundredNum = 0;
						// 400+
						int greaterFourHoudredNum = 0;

						List<ExportDeviceInfoDTO> valueList = valueMap.get(residenceType);

						if (CollectionUtils.isNotEmpty(valueList)) {
							for (ExportDeviceInfoDTO exportDeviceInfoDTO : valueList) {
								// 人数
								int peopleNumber = exportDeviceInfoDTO.getPeopleNumber();
								// 人数小于50
								if (peopleNumber < 50) {
									lessThanFiftyNum ++;
								} else if (peopleNumber >= 50 && peopleNumber < 200){
									// 人数大于等于50小于200
									fiftyAndTwoHundredNum ++;
								} else if (peopleNumber >= 200 && peopleNumber < 400) {
									// 人数大于等于200小于400
									twoAndFourHundredNum ++;
								} else {
									// 人数等于大于400
									greaterFourHoudredNum ++;
								}
							}
						}

						ExportCityResidenceTypeDeviceNumberDTO exportCityResidenceTypeDeviceNumberDTO = new ExportCityResidenceTypeDeviceNumberDTO();
						exportCityResidenceTypeDeviceNumberDTO.setLessThanFiftyNum(lessThanFiftyNum);
						exportCityResidenceTypeDeviceNumberDTO.setFiftyAndTwoHundredNum(fiftyAndTwoHundredNum);
						exportCityResidenceTypeDeviceNumberDTO.setTwoAndFourHundredNum(twoAndFourHundredNum);
						exportCityResidenceTypeDeviceNumberDTO.setGreaterFourHoudredNum(greaterFourHoudredNum);
						cityResidenceTypeDeviceNumberDTOMap.put(residenceType, exportCityResidenceTypeDeviceNumberDTO);
					}
				}
			}
		} catch (ParseException | IOException e) {
			logger.error(LoggerConstant.GET_FACE_DATA_CITY_RESIDENCE_TYPE_DEVICE_NUMBER, e);
		}
		return resultMap;
	}

	@Override
	public Workbook getCiytResidenceTypeWorkbook(String date) throws ServiceException {
		if (StringUtils.isBlank(date)) {
			throw new ServiceException(MessageConstant.DATE_IS_NULLL);
		}

		// 创建HSSFWorkbook对象(excel的文档对象)
		Workbook wb = new XSSFWorkbook();
		// 行
		Row row = null;
		// 单元格
		Cell cell = null;
		// 建立新的sheet对象（excel的表单） 并设置sheet名字
		Sheet sheet = wb.createSheet(ExcelConstant.SHEET_NAME_DEVICE);
		// 设置默认宽度
		sheet.setDefaultRowHeightInPoints(30);
		// ----------------标题样式---------------------
		// 标题样式
		CellStyle titleStyle = wb.createCellStyle();
		titleStyle.setAlignment(CellStyle.ALIGN_CENTER);
		titleStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		//下边框
		titleStyle.setBorderBottom(CellStyle.BORDER_THIN);
		//左边框
		titleStyle.setBorderLeft(CellStyle.BORDER_THIN);
		//上边框
		titleStyle.setBorderTop(CellStyle.BORDER_THIN);
		//右边框
		titleStyle.setBorderRight(CellStyle.BORDER_THIN);
		Font ztFont = wb.createFont();
		// 设置字体为斜体字
		ztFont.setItalic(false);
		// 将字体设置为“红色”
		ztFont.setColor(Font.COLOR_NORMAL);
		// 将字体大小设置为18px
		ztFont.setFontHeightInPoints((short) 20);
		// 将“宋体”字体应用到当前单元格上
		ztFont.setFontName(ExcelConstant.FONT_NAME_SONG_TYPEFACE);
		// 加粗
		ztFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		titleStyle.setFont(ztFont);
		//-------------------------------------------

		//----------------二级标题格样式----------------------------------
		// 表格样式
		CellStyle titleStyle2 = wb.createCellStyle();
		titleStyle2.setAlignment(CellStyle.ALIGN_CENTER);
		titleStyle2.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		//下边框
		titleStyle2.setBorderBottom(CellStyle.BORDER_THIN);
		//左边框
		titleStyle2.setBorderLeft(CellStyle.BORDER_THIN);
		//上边框
		titleStyle2.setBorderTop(CellStyle.BORDER_THIN);
		//右边框
		titleStyle2.setBorderRight(CellStyle.BORDER_THIN);
		Font ztFont2 = wb.createFont();
		// 设置字体为斜体字
		ztFont2.setItalic(false);
		// 将字体设置为“红色”
		ztFont2.setColor(Font.COLOR_NORMAL);
		// 将字体大小设置为18px
		ztFont2.setFontHeightInPoints((short) 12);
		// 字体应用到当前单元格上
		ztFont2.setFontName(ExcelConstant.FONT_NAME_SONG_TYPEFACE);
		// 加粗
		ztFont2.setBoldweight(Font.BOLDWEIGHT_BOLD);
		titleStyle2.setFont(ztFont2);
		//----------------------------------------------------------
		//----------------单元格样式----------------------------------
		//表格样式
		CellStyle cellStyle = wb.createCellStyle();
		cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
		cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		//下边框
		cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
		//左边框
		cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
		//上边框
		cellStyle.setBorderTop(CellStyle.BORDER_THIN);
		//右边框
		cellStyle.setBorderRight(CellStyle.BORDER_THIN);
		Font cellFont = wb.createFont();
		// 设置字体为斜体字
		cellFont.setItalic(false);
		// 将字体设置为“红色”
		cellFont.setColor(Font.COLOR_NORMAL);
		// 将字体大小设置为18px
		cellFont.setFontHeightInPoints((short) 12);
		// 字体应用到当前单元格上
		cellFont.setFontName(ExcelConstant.FONT_NAME_SONG_TYPEFACE);
		cellFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		cellStyle.setFont(cellFont);
		//设置自动换行
		cellStyle.setWrapText(true);

		// ------------------创建表头start---------------------
		// 创建第一行
		// 行号（行号从0开始计算）
		int rowNum = 0;
		row = sheet.createRow(rowNum);
		// 合并单元格CellRangeAddress构造参数依次表示起始行，截至行，起始列， 截至列
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 16));
		cell = row.createCell(0);
		// 设置单元格内容
		cell.setCellValue(ExcelConstant.CELL_VALUE_CITY_FACE_DEVICE_NUMBER + "(" + date + ")");
		cell.setCellStyle(titleStyle);
		// ----------------------------------------------

		// 创建第二行
		rowNum = rowNum + 1;
		row = sheet.createRow(rowNum);
		// 城市
		sheet.addMergedRegion(new CellRangeAddress(1, 2, 0, 0));
		cell = row.createCell(0);
		cell.setCellValue(ExcelConstant.CELL_VALUE_CITY);
		cell.setCellStyle(titleStyle2);
		// 住宅设备数
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 1, 4));
		cell = row.createCell(1);
		cell.setCellValue(ExcelConstant.CELL_VALUE_RESIDENCE_DEVICE_NUMBER);
		cell.setCellStyle(titleStyle2);
		// 商住楼设备数
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 5, 8));
		cell = row.createCell(5);
		cell.setCellValue(ExcelConstant.CELL_VALUE_COMMERCIAL_DEVICE_NUMBER);
		cell.setCellStyle(titleStyle2);
		// 综合体设备数
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 9, 12));
		cell = row.createCell(9);
		cell.setCellValue(ExcelConstant.CELL_VALUE_SYNTHESIS_DEVICE_NUMBER);
		cell.setCellStyle(titleStyle2);
		// 写字楼设备数
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 13, 16));
		cell = row.createCell(13);
		cell.setCellValue(ExcelConstant.CELL_VALUE_OFFICE_DEVICE_NUMBER);
		cell.setCellStyle(titleStyle2);

		// 创建第三行
		rowNum = rowNum + 1;
		row = sheet.createRow(rowNum);

		// 单元格号
		int cellNum = 0;
		// 住宅类型列表，按照中高端住宅、商住楼、综合体、写字楼的顺序
		List<String> residenceTypeList = BusinessConstant.RESIDENCE_TYPE_LIST;

		// 从第三行的第二个单元开始
		cellNum = cellNum + 1;

		// 因为所有住宅类型的显示数据是一样的，故循环次数和住宅类型的size一样
		for (int i = 0; i < residenceTypeList.size(); i++) {
			// 日均<50
			cell = row.createCell(cellNum);
			cell.setCellValue(ExcelConstant.CELL_VALUE_LESS_THAN_FIFTY);
			cell.setCellStyle(cellStyle);
			cellNum = cellNum + 1;

			// 50-200
			cell = row.createCell(cellNum);
			cell.setCellValue(ExcelConstant.CELL_VALUE_BETWEEN_FIFTY_AND_TWO_HUNDRED);
			cell.setCellStyle(cellStyle);
			cellNum = cellNum + 1;

			// 200-400
			cell = row.createCell(cellNum);
			cell.setCellValue(ExcelConstant.CELL_VALUE_BETWEEN_TWO_HUNDRED_AND_FOUR_HUNDRED);
			cell.setCellStyle(cellStyle);
			cellNum = cellNum + 1;

			// 400+
			cell = row.createCell(cellNum);
			cell.setCellValue(ExcelConstant.CELL_VALUE_GETTER_THAN_FOUR_HUNDRED);
			cell.setCellStyle(cellStyle);
			cellNum = cellNum + 1;
		}
		//-------------------------表头end---------------------
		
		// 获取数据
		Map<String, Map<String, ExportCityResidenceTypeDeviceNumberDTO>> cityResidenceTypeDeviceNumberMap = getFaceDataCityResidenceTypeDeviceNumber(date);

		// 重置单元格号
		cellNum = 0;
		// 行号 + 1 （这里应该是从第四行开始了）
		rowNum = rowNum + 1;

		for (Map.Entry<String, Map<String, ExportCityResidenceTypeDeviceNumberDTO>> cityEntry : cityResidenceTypeDeviceNumberMap.entrySet()) {
			row = sheet.createRow(rowNum);
			//城市
			String city = cityEntry.getKey();
			cell = row.createCell(cellNum);
			cell.setCellValue(city);
			cellNum = cellNum + 1;

			// 住宅类型对应各个数据段的设备数量
			Map<String, ExportCityResidenceTypeDeviceNumberDTO> residenceTypeDeviceNumberMap = cityEntry.getValue();

			for (String residenceType : BusinessConstant.RESIDENCE_TYPE_LIST) {
				ExportCityResidenceTypeDeviceNumberDTO dto = residenceTypeDeviceNumberMap.get(residenceType);
				// 日均<50
				Integer lessThanFiftyNum = dto.getLessThanFiftyNum();
				cell = row.createCell(cellNum);
				if (lessThanFiftyNum != null && lessThanFiftyNum != 0) {
					cell.setCellValue(lessThanFiftyNum);
				}
				cellNum = cellNum + 1;

				// 50-200
				Integer fiftyAndTwoHundredNum = dto.getFiftyAndTwoHundredNum();
				cell = row.createCell(cellNum);
				if (fiftyAndTwoHundredNum != null && fiftyAndTwoHundredNum != 0) {
					cell.setCellValue(fiftyAndTwoHundredNum);
				}
				cellNum = cellNum + 1;
				// 200-400
				Integer twoAndFourHundredNum = dto.getTwoAndFourHundredNum();
				cell = row.createCell(cellNum);
				if (twoAndFourHundredNum != null && twoAndFourHundredNum != 0) {
					cell.setCellValue(twoAndFourHundredNum);
				}
				cellNum = cellNum + 1;

				// 400+
				Integer greaterFourHoudredNum = dto.getGreaterFourHoudredNum();
				cell = row.createCell(cellNum);
				if (greaterFourHoudredNum != null && greaterFourHoudredNum != 0) {
					cell.setCellValue(greaterFourHoudredNum);
				}
				cellNum = cellNum + 1;
			}
			rowNum++;
			// 重置单元格号，便于下一行重用
			cellNum = 0;
		}
		return wb;
	}

}
