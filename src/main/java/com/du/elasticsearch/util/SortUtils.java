package com.du.elasticsearch.util;

import com.xinchao.data.constant.ElasticsearchConstant;

/**
 * @author dxy
 * @date 2019/4/8 17:05
 */
public class SortUtils {
	/**
	 * 时间排序
	 *
	 * @param timeStart 时间
	 * @param timeEnd 时间
	 * @return int
	 */
	public static int sortTime(String timeStart, String timeEnd) {
		//替换0
		if (timeStart.startsWith(ElasticsearchConstant.STRING_ZERO)) {
			if (timeStart.equals(ElasticsearchConstant.STRING_ZERO_ZERO)) {
				timeStart = ElasticsearchConstant.STRING_ZERO;
			} else {
				timeStart = timeStart.replace(ElasticsearchConstant.STRING_ZERO, "");
			}
		}
		if (timeEnd.startsWith(ElasticsearchConstant.STRING_ZERO)) {
			if (timeEnd.equals(ElasticsearchConstant.STRING_ZERO_ZERO)) {
				timeEnd = ElasticsearchConstant.STRING_ZERO;
			} else {
				timeEnd = timeEnd.replace(ElasticsearchConstant.STRING_ZERO, "");
			}
		}
		//将字符串转化为数字
		Integer timeStartNum = Integer.valueOf(timeStart);
		Integer timeEndIntNum = Integer.valueOf(timeEnd);
		return timeStartNum.compareTo(timeEndIntNum);
	}
}
