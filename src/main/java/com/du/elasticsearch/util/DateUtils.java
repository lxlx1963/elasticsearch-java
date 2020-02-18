package com.du.elasticsearch.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 日期工具类
 */
public class DateUtils {

	public static final String PATTEN_YMD_HMS = "yyyy-MM-dd HH:mm:ss";
	public static final String PATTEN_YMD = "yyyy-MM-dd";
	public static final String PATTEN_YMD1 = "yyyy/MM/dd";
	public static final String PATTEN_YMD_HMS1 = "yyyy/MM/dd HH:mm:ss";

	/**
	 * 年
	 */
	public static final String DATE_YEAR = "yyyy";
	/**
	 * 月
	 */
	public static final String DATE_MONTH = "MM";
	/**
	 * 年月
	 */
	public static final String DATE_YEAR_MONTH = "yyyyMM";

	/**
	 * 年月日
	 */
	public static final String DATE_YEAR_MONTH_DAY = "yyyyMMdd";

	/**
	 * 只有时分的24小时格式
	 */
	public static final String DATE_HOUR_24 = "HH";

	/**
	 * 只有时分的24小时格式
	 */
	public static final String DATE_MINUTTE_24 = "mm";

	private static final String PATTERN_IS_NULL = "格式为空";

	/**
	 * 把long类型日期转化为格式化字符串日期
	 *
	 * @param longTimeStamp long类型时间戳
	 * @param pattern       格式
	 * @return String(格式化后的日期)
	 */
	public static String longTimeStampToStringDate(Long longTimeStamp, String pattern) {
		if (longTimeStamp == null || StringUtils.isBlank(pattern)) {
			throw new NullPointerException("时间戳或格式类型不能为空");
		}
		Date date = new Date(longTimeStamp);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		return simpleDateFormat.format(date);
	}

	/**
	 * 把字符串日期转换为LonG类型日期
	 *
	 * @param time    字符串日期
	 * @param pattern 格式
	 * @return Long日期
	 * @throws ParseException
	 */
	public static Long stringTimeToLongTimeStamp(String time, String pattern) throws ParseException {
		if (StringUtils.isBlank(time) || StringUtils.isBlank(pattern)) {
			throw new NullPointerException("时间或格式类型不能为空");
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		return simpleDateFormat.parse(time).getTime();
	}

	/**
	 * 时间范围转换为开始时间和结束时间
	 *
	 * @param dateRange 时间范围
	 * @return Map<Object, Long>
	 */
	public static Map<Object, Long> dateRangeToString(String dateRange, String pattern) throws ParseException {
		HashMap<Object, Long> resultMap = new HashMap<>(2);
		Long startTime = null;
		Long endTime = null;
		if (StringUtils.isNotBlank(dateRange)) {
			String[] dateRangeArr = dateRange.split("-");
			String startTimeStr = dateRangeArr[0];
			String endTimeStr = dateRangeArr[1];
			if (StringUtils.isNotBlank(startTimeStr)) {
				startTime = stringTimeToLongTimeStamp(startTimeStr, pattern);
				endTime = stringTimeToLongTimeStamp(endTimeStr, pattern);
			}
		}
		resultMap.put("startTime", startTime);
		resultMap.put("endTime", endTime);
		return resultMap;
	}

	/**
	 * 获取任意时间下一个月
	 *
	 * @param dateL 任意时间
	 * @return
	 */
	public static Long getPreMonth(Long dateL) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(dateL);
		cal.add(Calendar.MONTH, 1);
		return cal.getTimeInMillis();
	}

	/**
	 * 获取上一个月
	 *
	 * @param year  年
	 * @param month 月
	 * @return String（格式化的时间）
	 */
	public static String getLastDateOfMonth(int year, int month, String pattern) {
		if (StringUtils.isBlank(pattern)) {
			throw new NullPointerException("时间格式类型不能为空");
		}
		// 得到一个Calendar的实例
		Calendar calendar = Calendar.getInstance();
		// 月份是从0开始的，所以月份需要减1
		calendar.set(year, month - 1, 17);
		// 月份减1，就是上一月份
		calendar.add(Calendar.MONTH, -1);
		Date date = calendar.getTime();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		return simpleDateFormat.format(date);
	}

	/**
	 * 获取近期时间
	 *
	 * @param year      年
	 * @param month     月份
	 * @param recentNum 近期天数
	 * @return @return String[]
	 */
	public static String[] getNearDate(int year, int month, int recentNum, String pattern) {
		if (StringUtils.isBlank(pattern)) {
			throw new IllegalArgumentException(PATTERN_IS_NULL);
		}
		if (month >= 1 && month <= 12) {
			String[] sixMonthArray = new String[recentNum];
			//将本月放入第一个
			if (month >= 10) {
				sixMonthArray[0] = year + "" + month;
			} else {
				sixMonthArray[0] = year + "0" + month;
			}
			for (int i = 1; i < recentNum; i++) {
				String lastDateOfMonth = getLastDateOfMonth(year, month, pattern);
				sixMonthArray[i] = lastDateOfMonth;
				//如果月份为1月份，则需要从前一年的12月份算，所以月份就为12，年份需要减1
				if (month == 1) {
					month = 12;
					year = year - 1;
				} else {
					month--;
				}
			}
			return sixMonthArray;
		} else {
			return new String[0];
		}
	}


	/**
	 * 获取过去第几天的日期
	 *
	 * @return String（格式化的时间）
	 */
	public static String getPastDay(int past, String pattern) {
		if (StringUtils.isBlank(pattern)) {
			throw new IllegalArgumentException(PATTERN_IS_NULL);
		}
		// 得到一个Calendar的实例
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
		Date today = calendar.getTime();
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(today);
	}

	/**
	 * 获取近期日期
	 *
	 * @param recentDayNum 近期天数(包含当天)
	 * @return String[]
	 */
	public static String[] getRecentDay(int recentDayNum, String pattern) {
		//近期天数必须大于0
		if (recentDayNum < 0) {
			throw new IllegalArgumentException("recentDayNum must be greater than zero");
		}
		if (StringUtils.isBlank(pattern)) {
			throw new IllegalArgumentException(PATTERN_IS_NULL);
		}
		String[] days = null;
		if (recentDayNum > 0) {
			days = new String[recentDayNum];
			for (int i = 0; i < recentDayNum; i++) {
				String pastDay = getPastDay(i, pattern);
				days[i] = pastDay;
			}
		} else {
			//如果recentDayNum等于0，返回当天天数
			days = new String[1];
			String pastDay = getPastDay(recentDayNum, pattern);
			days[recentDayNum] = pastDay;
		}
		return days;
	}

	/**
	 * 获取未来第几天的日期
	 *
	 * @return String（格式化的时间）
	 */
	public static String getFutureDay(int past, String pattern) {
		if (StringUtils.isBlank(pattern)) {
			throw new IllegalArgumentException(PATTERN_IS_NULL);
		}
		// 得到一个Calendar的实例
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + past);
		Date today = calendar.getTime();
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(today);
	}

	/**
	 * 计算两个日期的间隔月份（开始日期、结束日期必须与pattern一致，pattern到月份即可）
	 *
	 * @param minDate 开始时间
	 * @param maxDate 结束时间
	 * @param pattern 格式化符号
	 * @return List<String>
	 */
	public static List<String> getMonthsBetween(String minDate, String maxDate, String pattern) throws ParseException {
		ArrayList<String> result = new ArrayList<>();
		//格式化为年月
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		Calendar min = Calendar.getInstance();
		Calendar max = Calendar.getInstance();

		min.setTime(sdf.parse(minDate));
		min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);

		max.setTime(sdf.parse(maxDate));
		max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);

		Calendar curr = min;
		while (curr.before(max)) {
			result.add(sdf.format(curr.getTime()));
			curr.add(Calendar.MONTH, 1);
		}
		return result;
	}

	/**
	 * 获取两个日期之间的所有日期(开始日期、结束日期必须与pattern一致)
	 *
	 * @param startTime 开始日期
	 * @param endTime   结束日期
	 * @return List<String>
	 */
	public static List<String> getDaysBetween(String startTime, String endTime, String pattern) throws ParseException {
		// 返回的日期集合
		List<String> days = new ArrayList<>();

		DateFormat dateFormat = new SimpleDateFormat(pattern);
		Date start = dateFormat.parse(startTime);
		Date end = dateFormat.parse(endTime);

		Calendar tempStart = Calendar.getInstance();
		tempStart.setTime(start);

		Calendar tempEnd = Calendar.getInstance();
		tempEnd.setTime(end);
		// 日期加1(包含结束)
		tempEnd.add(Calendar.DATE, +1);
		while (tempStart.before(tempEnd)) {
			days.add(dateFormat.format(tempStart.getTime()));
			tempStart.add(Calendar.DAY_OF_YEAR, 1);
		}
		return days;
	}

	/**
	 * 生成一天中，从某一时刻起到22:30的时段值
	 * @param startTime 开始时间
	 * @return List<String>
	 */
	public static List<String> generateTimeFrameOfDay(Long startTime) {
		//获取小时
		String hourStr = longTimeStampToStringDate(startTime, DATE_HOUR_24);
		if (hourStr.startsWith("0")) {
			hourStr = hourStr.substring(1, hourStr.length());
		}
		Integer hour = Integer.valueOf(hourStr);
		if (hour < 6) {
			//默认从6点开始
			hour = 6;
		}
		
		//获取分钟
		String minuteStr = longTimeStampToStringDate(startTime, DATE_MINUTTE_24);
		if (minuteStr.startsWith("0")) {
			minuteStr = minuteStr.substring(1, minuteStr.length());
		}
		Integer minute = Integer.valueOf(minuteStr);
		//如果分钟数大于30，则小时需要加1
		if (minute > 30) {
			hour = hour + 1;
		}
		//时段列表
		List<String> timeFrameList = new ArrayList<>();
		//因为一天中的时段到22:30就结束了，顾循环不超过23
		for (int i = hour; i < 23; i++) {
			//每隔一小时为一个时段，到每隔小时的30分
			if (i >= 10) {
				timeFrameList.add(i + ":30");
			} else {
				timeFrameList.add("0" + i + ":30");
			}
		}
		return timeFrameList;
	}

	/**
	 * 获取近期日期
	 *
	 * @param recentDayNum 近期天数(不包含当天)
	 * @return List<String>
	 */
	public static List<String> getRecentDayList(int recentDayNum, String pattern) {
		//近期天数必须大于0
		if (recentDayNum < 0) {
			throw new IllegalArgumentException("recentDayNum must be greater than zero");
		}
		if (StringUtils.isBlank(pattern)) {
			throw new IllegalArgumentException(PATTERN_IS_NULL);
		}
		List<String> dayList = new ArrayList<>();
		if (recentDayNum > 0) {
			for (int i = 1; i <= recentDayNum; i++) {
				String pastDay = getPastDay(i, pattern);
				dayList.add(pastDay);
			}
		} else {
			//如果recentDayNum等于0，返回当天天数
			String pastDay = getPastDay(recentDayNum, pattern);
			dayList.add(pastDay);
		}
		return dayList;
	}

	/**
	 * 获取不存在的元素列表
	 * @param baseElementCollection 基本元素列表
	 * @param existElementCollection 存在元素列表
	 * @return List<String>
	 */
	public static List<String> getNotExistElementList(Collection<String> baseElementCollection, Collection<String> existElementCollection) {
		List<String> notExistElementList = new ArrayList<>();
		//如果传入时间列表为空，返回整个列表
		if (CollectionUtils.isEmpty(existElementCollection)) {
			for (String time : baseElementCollection) {
				notExistElementList.add(time);
			}
		} else {
			for (String time : baseElementCollection) {
				if (existElementCollection.contains(time)) {
					continue;
				}
				notExistElementList.add(time);
			}
		}
		return notExistElementList;
	}

	/**
	 * 获取当前日期时间
	 *
	 * @param pattern 格式化格式
	 * @return String
	 */
	public static String getNowDateTime(String pattern) {
		if (StringUtils.isBlank(pattern)) {
			pattern = PATTEN_YMD_HMS;
		}
		LocalDateTime localDateTime = LocalDateTime.now();
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
		return localDateTime.format(dateTimeFormatter);
	}

}