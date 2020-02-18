package com.du.elasticsearch.constant;

/**
 * 缓存常量类
 *
 * @author dxy
 * @date 2019/6/3 11:37
 */
public final class CacheConstant {
	/**
	 * 私有化构造器
	 */
	private CacheConstant() {

	}
	/**
	 * 监播数据（观看人次、触达人次）
	 */
	public static final String MONITOR_WATCH_TOUCH = "monitor_watch_touch";
	/**
	 * 监播数据（小区-观看人次）
	 */
	public static final String MONITOR_COMMUNITY_WATCH = "monitor_community_watch";
	/**
	 * 小区城市
	 */
	public static final String COMMUNITY_CITY = "community_city";
	/**
	 * 监播年龄段（中高端住宅）
	 */
	public static final String MONITOR_AGE_TYPE_MIDDLE_HIGH = "monitor_age_type_middle_high";
	/**
	 * 监播年龄段（商住楼）
	 */
	public static final String MONITOR_AGE_TYPE_COMMERCIAL = "monitor_age_type_commercial";
	/**
	 * 监播年龄段（商业综合体）
	 */
	public static final String MONITOR_AGE_TYPE_COMMERCIAL_SYNTHESIS = "monitor_age_type_commercial_synthesis";
	/**
	 * 人脸-小区终端编码
	 */
	public static final String FACE_COMMUNITY_DEVICE_CODE = "face_community_device_code";
	/**
	 * 人脸-人数小于给定值终端
	 */
	public static final String FACE_PEOPLE_NUM_LESS_THAN_VALUE_DEVICE_CODE = "face_people_num_less_than_value_device_code";
}
