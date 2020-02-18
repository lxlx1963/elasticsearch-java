package com.du.elasticsearch.constant;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import java.util.*;

/**
 * @author dxy
 * @date 2019/3/7 14:36
 */
public class BusinessConstant {
	/**
	 * 私有化构造器
	 */
	private BusinessConstant() {

	}

	/**
	 * Float-0.0F
	 */
	public static final float FLOAT_ZERO = 0.0F;
	/**
	 * 前10条
	 */
	public static final int TOP_SIZE = 10;
	/**
	 * 时间对应Map（在实际要求中，0点到5点的数据都归结到6点）
	 */
	private static final Map<String, String> TIME_TEMP_MAP = new HashMap<>();
	static {
		TIME_TEMP_MAP.put("6", "6:00");
		TIME_TEMP_MAP.put("7", "7:00");
		TIME_TEMP_MAP.put("8", "8:00");
		TIME_TEMP_MAP.put("9", "9:00");
		TIME_TEMP_MAP.put("10", "10:00");
		TIME_TEMP_MAP.put("11", "11:00");
		TIME_TEMP_MAP.put("12", "12:00");
		TIME_TEMP_MAP.put("13", "13:00");
		TIME_TEMP_MAP.put("14", "14:00");
		TIME_TEMP_MAP.put("15", "15:00");
		TIME_TEMP_MAP.put("16", "16:00");
		TIME_TEMP_MAP.put("17", "17:00");
		TIME_TEMP_MAP.put("18", "18:00");
		TIME_TEMP_MAP.put("19", "19:00");
		TIME_TEMP_MAP.put("20", "20:00");
		TIME_TEMP_MAP.put("21", "21:00");
		TIME_TEMP_MAP.put("22", "22:00");
		TIME_TEMP_MAP.put("23", "23:00");
	}
	/**
	 * 时间对应Map（在实际要求中，0点到5点的数据都归结到6点）
	 */
	public static final Map<String, String> TIME_MAP = ImmutableMap.copyOf(TIME_TEMP_MAP);

	/**
	 * 写字楼
	 */
	public static final String OFFICE_BUILDING = "写字楼";
	/**
	 * 商住楼
	 */
	public static final String COMMERCIAL_RESIDENTIAL_BUILDING = "商住楼";
	/**
	 * 综合体
	 */
	public static final String SYNTHESIS_BUILDING = "综合体";
	/**
	 * 中高端住宅
	 */
	public static final String MIDDLE_HIGH_END_RESIDENCE_BUILDING = "中高端住宅";
	/**
	 * 商业综合体
	 */
	public static final String COMMERCIAL_SYNTHESIS_BUILDING = "商业综合体";
	/**
	 * 6:00
	 */
	public static final String SIX_TIME = "6:00";
	/**
	 * 性别-不限
	 */
	public static final String SEX_UNLIMITED = "不限";
	/**
	 * 性别列表
	 */
	private static final List<String> SEX_TEMP_LIST = new ArrayList<>();
	static {
		SEX_TEMP_LIST.add("男");
		SEX_TEMP_LIST.add("女");
	}
	/**
	 * 性别列表
	 */
	public static final List<String> SEX_LIST = Collections.unmodifiableList(SEX_TEMP_LIST);

	/***--------------------------年龄范围（中文）-------------------------*/
	/**
	 * 19岁及以下
	 */
	public static final String UNDER_NINETEEN_CH = "19岁及以下";
	/**
	 * 20岁~25岁
	 */
	public static final String TWENTY_AND_TWENTYFIVE_CH = "20岁~25岁";
	/**
	 * 26岁~35岁
	 */
	public static final String TWENTYSIX_AND_THIRTYFIVE_CH = "26岁~35岁";
	/**
	 * 36岁~45岁
	 */
	public static final String THIRTYSIX_AND_FOURTYFIVE_CH = "36岁~45岁";
	/**
	 * 46岁~55岁
	 */
	public static final String FOURTYSIXE_AND_FIFTYFIVE_CH = "46岁~55岁";
	/**
	 * 56岁及以上
	 */
	public static final String ABOVE_FIFTYFIVE_CH = "56岁及以上";
	/**
	 * 年龄范围列表
	 */
	private static final List<String> AGE_RANGE_TEMP_LIST = new ArrayList<>();
	static {
		AGE_RANGE_TEMP_LIST.add(UNDER_NINETEEN_CH);
		AGE_RANGE_TEMP_LIST.add(TWENTY_AND_TWENTYFIVE_CH);
		AGE_RANGE_TEMP_LIST.add(TWENTYSIX_AND_THIRTYFIVE_CH);
		AGE_RANGE_TEMP_LIST.add(THIRTYSIX_AND_FOURTYFIVE_CH);
		AGE_RANGE_TEMP_LIST.add(FOURTYSIXE_AND_FIFTYFIVE_CH);
		AGE_RANGE_TEMP_LIST.add(ABOVE_FIFTYFIVE_CH);
	}
	public static final List<String> AGE_RANGE_LIST = Collections.unmodifiableList(AGE_RANGE_TEMP_LIST);

	/**
	 * 19岁及以下
	 */
	public static final String UNDER_NINETEEN = "19岁及以下";
	/**
	 * 20-25岁
	 */
	public static final String TWENTY_AND_TWENTYFIVE = "20-25岁";
	/**
	 * 26-35岁
	 */
	public static final String TWENTYSIX_AND_THIRTYFIVE = "26-35岁";
	/**
	 * 36-45岁
	 */
	public static final String THIRTYSIX_AND_FOURTYFIVE = "36-45岁";
	/**
	 * 446-55岁
	 */
	public static final String FOURTYSIXE_AND_FIFTYFIVE = "46-55岁";
	/**
	 * 56岁及以上
	 */
	public static final String ABOVE_FIFTYFIVE = "56岁及以上";
	/**
	 * 年龄段列表
	 */
	private static final List<String> AGE_TEMP_LIST = new ArrayList<>();
	static {
		AGE_TEMP_LIST.add(UNDER_NINETEEN);
		AGE_TEMP_LIST.add(TWENTY_AND_TWENTYFIVE);
		AGE_TEMP_LIST.add(TWENTYSIX_AND_THIRTYFIVE);
		AGE_TEMP_LIST.add(THIRTYSIX_AND_FOURTYFIVE);
		AGE_TEMP_LIST.add(FOURTYSIXE_AND_FIFTYFIVE);
		AGE_TEMP_LIST.add(ABOVE_FIFTYFIVE);
	}
	public static final List<String> AGE_LIST = Collections.unmodifiableList(AGE_TEMP_LIST);

	/**
	 * 年龄Map
	 */
	private static final Map<String, String> AGE_TEMP_MAP = new HashMap<>();
	static {
		AGE_TEMP_MAP.put(UNDER_NINETEEN_CH, UNDER_NINETEEN);
		AGE_TEMP_MAP.put(TWENTY_AND_TWENTYFIVE_CH, TWENTY_AND_TWENTYFIVE);
		AGE_TEMP_MAP.put(TWENTYSIX_AND_THIRTYFIVE_CH, TWENTYSIX_AND_THIRTYFIVE);
		AGE_TEMP_MAP.put(THIRTYSIX_AND_FOURTYFIVE_CH, THIRTYSIX_AND_FOURTYFIVE);
		AGE_TEMP_MAP.put(FOURTYSIXE_AND_FIFTYFIVE_CH, FOURTYSIXE_AND_FIFTYFIVE);
		AGE_TEMP_MAP.put(ABOVE_FIFTYFIVE_CH, ABOVE_FIFTYFIVE);
	}
	/**
	 * 年龄Map
	 */
	public static final Map<String, String> AGE_MAP = ImmutableMap.copyOf(AGE_TEMP_MAP);

	/**
	 * 年龄-未知
	 */
	public static final String AGE_STRING_UNKNOW = "未知";
	/**
	 * 时间后缀（:00）
	 */
	public static final String TIME_COLON_ZERO_ZERO = ":00";
	/**
	 * 0.00
	 */
	public static final String ZERO_POINT_ZERO_ZERO = "0.00";
	/**
	 * 字符串-23
	 */
	public static final String STRING_TWENTY_THREE = "23";

	/**
	 * 时间点列表（从早上6点到晚上23点）
	 */
	private static final List<String> TIME_POINT_TEMP_LIST = new ArrayList<>();
	static {
		TIME_POINT_TEMP_LIST.add("06");
		TIME_POINT_TEMP_LIST.add("07");
		TIME_POINT_TEMP_LIST.add("08");
		TIME_POINT_TEMP_LIST.add("09");
		TIME_POINT_TEMP_LIST.add("10");
		TIME_POINT_TEMP_LIST.add("11");
		TIME_POINT_TEMP_LIST.add("12");
		TIME_POINT_TEMP_LIST.add("13");
		TIME_POINT_TEMP_LIST.add("14");
		TIME_POINT_TEMP_LIST.add("15");
		TIME_POINT_TEMP_LIST.add("16");
		TIME_POINT_TEMP_LIST.add("17");
		TIME_POINT_TEMP_LIST.add("18");
		TIME_POINT_TEMP_LIST.add("19");
		TIME_POINT_TEMP_LIST.add("20");
		TIME_POINT_TEMP_LIST.add("21");
		TIME_POINT_TEMP_LIST.add("22");
		TIME_POINT_TEMP_LIST.add("23");
	}
	/**
	 * 时间点列表（从早上6点到晚上23点）
	 */
	public static final List<String> TIME_POINT_LIST = Collections.unmodifiableList(TIME_POINT_TEMP_LIST);

	/**
	 * 市
	 */
	public static final String CHINA_SHI = "市";
	/**
	 * 6
	 */
	public static final String STRING_SIX = "6";
	/**
	 * -1
	 */
	public static final int MINUS_ONE = -1;
	/**
	 * 住宅类型列表
	 */
	private static final List<String> RESIDENCE_TYPE_TEMP_LIST = Lists.newLinkedList();
	static {
		RESIDENCE_TYPE_TEMP_LIST.add(MIDDLE_HIGH_END_RESIDENCE_BUILDING);
		RESIDENCE_TYPE_TEMP_LIST.add(COMMERCIAL_RESIDENTIAL_BUILDING);
		RESIDENCE_TYPE_TEMP_LIST.add(SYNTHESIS_BUILDING);
		RESIDENCE_TYPE_TEMP_LIST.add(OFFICE_BUILDING);
	}
	/**
	 * 住宅类型列表
	 */
	public static final List<String> RESIDENCE_TYPE_LIST = Collections.unmodifiableList(RESIDENCE_TYPE_TEMP_LIST);

	/**
	 * 兴元华盛一二期
	 */
	public static final String FILTERING_COMMUNITY_NAME = "兴元华盛一二期";
}
