package com.du.elasticsearch.constant;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class DateConstant {
	/**
	 * 私有化构造器
	 */
	private DateConstant() {

	}

	/**
	 * 日期短格式yyyy.MM.dd
	 */
	public static final String DATE_SHORT = "yyyy.MM.dd";

	/**
	 * 日期ISO格式;yyyy-MM-dd;
	 */
	public static final String DATE_SHORT_ISO = "yyyy-MM-dd";

	/**
	 * 日期ISO格式;yyyy-MM-dd;
	 */
	public static final String DATE_SHORT_ISO_CH = "yyyy年MM月dd日";

	/**
	 * 日期格式 yyyy-MM-dd HH:mm:ss
	 */
	public static final String LONG_DATE_FORMAT_STR = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 24小时格式
	 */
	public static final String DATE_MINUTES_24 = "yyyy.MM.dd HH:mm:ss";

	/**
	 * 12小时格式
	 */
	public static final String DATE_MINUTES_12 = "yyyy.MM.dd hh-mm-ss";

	/**
	 * 纯数字格式
	 */
	public static final String DATE_MINUTES_SECONDS = "yyyyMMddhhmmss";
	/**
	 * 纯数字格式(精确到三位毫秒)
	 */
	public static final String DATE_MINUTES_24_MILLISECOND = "yyyyMMddHHmmssSSS";
	/**
	 * 中文日期格式   yyyy年MM月dd日 HH时mm分ss秒
	 */
	public static final String LONG_DATE_FORMAT_STR_CH = "yyyy年MM月dd日 HH时mm分ss秒";
	/**
	 * 只有时分的24小时格式
	 */
	public static final String DATE_ONLYHM_24 = "HH:mm";
	/**
	 * 时分秒最小值
	 */
	public static final String H_M_S_MIN = " 00:00:00";
	/**
	 * 时分秒最大值
	 */
	public static final String H_M_S_MAX = " 23:59:59";
	/**
	 * 年月
	 */
	public static final String DATE_YEAR_MONTH = "yyyyMM";

	/**
	 * 年月日
	 */
	public static final String DATE_YEAR_MONTH_DAY = "yyyyMMdd";

	/**
	 * 年月日时分秒
	 */
	public static final String DATE_YEAR_MONTH_DAY_HMS = "yyyyMMdd HH:mm:ss";
	/**
	 * 从6:30到22:30的时间段数组（每个一小时）
	 */
	public static final String[] DAY_TIME_FRAME_ARRAY = {"6:30", "7:30", "8:30", "9:30", "10:30", "11:30", "12:30",
			"13:30", "14:30", "15:30", "16:30", "17:30", "18:30", "19:30", "20:30", "21:30", "22:30"};
	/**
	 * 时间map,获取6:30到22:30中，每一个30分的后一秒时间
	 */
	public static final Map<String, String> DAY_TIME_FRAME_MAP = Collections.unmodifiableMap(new HashMap<>());
	static {
		DAY_TIME_FRAME_MAP.put("06:30", "06:30:01");
		DAY_TIME_FRAME_MAP.put("07:30", "07:30:01");
		DAY_TIME_FRAME_MAP.put("08:30", "08:30:01");
		DAY_TIME_FRAME_MAP.put("09:30", "09:30:01");
		DAY_TIME_FRAME_MAP.put("10:30", "10:30:01");
		DAY_TIME_FRAME_MAP.put("11:30", "11:30:01");
		DAY_TIME_FRAME_MAP.put("12:30", "12:30:01");
		DAY_TIME_FRAME_MAP.put("13:30", "13:30:01");
		DAY_TIME_FRAME_MAP.put("14:30", "14:30:01");
		DAY_TIME_FRAME_MAP.put("15:30", "15:30:01");
		DAY_TIME_FRAME_MAP.put("16:30", "16:30:01");
		DAY_TIME_FRAME_MAP.put("17:30", "17:30:01");
		DAY_TIME_FRAME_MAP.put("18:30", "18:30:01");
		DAY_TIME_FRAME_MAP.put("19:30", "19:30:01");
		DAY_TIME_FRAME_MAP.put("20:30", "20:30:01");
		DAY_TIME_FRAME_MAP.put("21:30", "21:30:01");
		DAY_TIME_FRAME_MAP.put("22:30", "22:30:01");
	}
	/**
	 * 今天
	 */
	public static final int TODAY = 0;
	/**
	 * 昨天
	 */
	public static final int YESTERDAY = 1;
	/**
	 * 近七天
	 */
	public static final int RECENT_SEVEN_DAY = 2;
	/**
	 * 近30天
	 */
	public static final int RECENT_THIRTY_DAY = 3;
	/**
	 * 最小时分秒毫秒
	 */
	public static final String MIN_HOUR_MINUTE_SECOND_MILLISECOND = "00:00:00.000";
	/**
	 *
	 * 最大时分秒毫秒
	 */
	public static final String MAX_HOUR_MINUTE_SECOND_MILLISECOND = "23:59:59.999";

	/**
	 * 年月日时分秒毫秒
	 */
	public static final String YEAR_MONTH_DAY_HMS_SSS = "yyyyMMdd HH:mm:ss.SSS";

}
