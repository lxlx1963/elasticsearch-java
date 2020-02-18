package com.du.elasticsearch.constant;

/**
 * Controller常量
 * @author dxy
 * @date 2019/7/30 11:36
 */
public final class MessageConstant {
	/**
	 * 开始日期和结束日期为空
	 */
	public static final String STARTTIME_AND_ENDTIME_IS_NULL = "开始日期和结束日期为空";
	/**
	 * 开始日期大于结束日期
	 */
	public static final String  STARTTIME_GRTTER_THAN_ENDTIME = "开始日期大于结束日期";
	/**
	 * 日期为空
	 */
	public static final String  DATE_IS_NULLL = "日期为空";
	/**
	 * 小区名称为空
	 */
	public static final String  COMMUNITY_NAME_IS_NULLL = "小区名称为空";
	/**
	 * 住宅类型为空
	 */
	public static final String  RESIDENCE_TYPE_IS_NULLL = "住宅类型为空";
	/**
	 * 终端类型为空
	 */
	public static final String  DEVICE_TYPE_IS_NULLL = "终端类型为空";
	/**
	 * 限定人数为空
	 */
	public static final String  LIMIT_PEOPLE_NUM_IS_NULLL = "限定人数为空";
	/**
	 * 城市对应省数据表没有数据，请配置frp_province_city表
	 */
	public static final String  FRP_PROVINCE_CITY_TABLE_IS_NULL = "城市对应省数据表没有数据，请配置frp_province_city表";
	/**
	 * 保存成功
	 */
	public static final String  SAVING_IS_SUCCESSFUL = "保存成功";
	/**
	 * 人次数据为空
	 */
	public static final String  PEOPLE_NUMBER_IS_NULL = "人次数据为空";
	/**
	 * 加载数据到redis缓存成功
	 */
	public static final String  PUSHING_DATA_TO_REDIS_IS_NULL = "加载数据到redis缓存成功";
	/**
	 * 日期列表为空
	 */
	public static final String DATE_LIST_IS_NULL = "日期列表为空";
	/**
	 * 索引列表为空
	 */
	public static final String INDEX_LIST_IS_NULL = "索引列表为空";
	/**
	 * 索引名称为空
	 */
	public static final String INDEX_NAME_IS_NULL = "索引名称为空";
	/**
	 * 索引前缀为空
	 */
	public static final String INDEX_PREFIX_IS_NULL = "索引前缀为空";
	/**
	 * 保留数量不能为空和小于0
	 */
	public static final String SAVING_NUMBER_IS_NULL_OR_LESS_THAN_ZERO = "保留数量不能为空和小于0";
	/**
	 * 参数为空
	 */
	public static final String PARAMETER_IS_NULL = "参数为空";
	/**
	 * 住宅类型列表为空
	 */
	public static final String RESIDENCE_TYPE_LIST_IS_NULLL = "住宅类型列表为空";
	/**
	 * 日期列表或小区名称为空
	 */
	public static final String DATE_LIST_OR_COMMUNITY_NAME_IS_NULLL = "日期列表或小区名称为空";
	/**
	 * 终端编码对应住宅类型为空
	 */
	public static final String DEVICE_NUMBER_RESIDENCE_TYPE_MAP_IS_NULLL = "终端编码对应住宅类型Map为空";
	/**
	 * 终端编码对应城市Map为空
	 */
	public static final String DEVICE_NUMBER_CITY_MAP_IS_NULLL = "终端编码对应城市Map为空";
	/**
	 * Elasticsearch ErrorMessage
	 */
	public static final String ELASTICSEARCH_ERROR_MESSAGE = "Elasticsearch ErrorMessage: ";

	private MessageConstant() {

	}
}
