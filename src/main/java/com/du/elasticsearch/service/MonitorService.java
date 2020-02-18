package com.du.elasticsearch.service;

import com.xinchao.data.exception.ServiceException;
import com.xinchao.data.model.dto.MonitorAdvertisementWatchDTO;
import com.xinchao.data.model.dto.MonitorCommunityWatchDTO;
import com.xinchao.data.model.dto.MonitorWatchTouchDTO;
import com.xinchao.data.model.vo.MonitorAgeTypeVO;
import com.xinchao.data.model.vo.MonitorAgeWatchVO;
import com.xinchao.data.model.vo.MonitorDateWatchVO;
import com.xinchao.data.model.vo.SummarizeMonitorVO;

import java.util.List;
import java.util.Map;

/**
 * 监播看板Service
 *
 * @author dxy
 * @date 2019/3/7 11:02
 */
public interface MonitorService {
	/**
	 * 近7天各年龄段的观看率&观看时长
	 *
	 * @return List<MonitorAgeWatchVO>
	 * @throws ServiceException
	 */
	List<MonitorAgeWatchVO> listMonitorAgeWatch() throws ServiceException;

	/**
	 * 昨日监播数据
	 *
	 * @return SummarizeMonitorVO
	 * @throws ServiceException
	 */
	SummarizeMonitorVO getSummarizeMonitor() throws ServiceException;

	/**
	 * 近7天日均广告平均观看时长
	 *
	 * @return List<MonitorDateWatchVO>
	 * @throws ServiceException
	 */
	List<MonitorDateWatchVO> listMonitorSevenWatch() throws ServiceException;

	/**
	 * 各年龄段的人员观看率
	 *
	 * @return Map<String,List<MonitorAgeTypeVO>>
	 */
	Map<String, List<MonitorAgeTypeVO>> getMonitorAgeTypeMap();

	/**
	 * 近7天日均观看时长top10点位
	 *
	 * @return List<MonitorCommunityWatchDTO>
	 * @throws ServiceException
	 */
	List<MonitorCommunityWatchDTO> listMonitorCommunityWatch() throws ServiceException;

	/**
	 * 近7天观看次数TOP10广告
	 *
	 * @return List<MonitorAdvertisementWatchDTO>
	 */
	List<MonitorAdvertisementWatchDTO> listMonitorAdvertisementWatch() throws ServiceException;

	/**
	 * 单广告近7天每小时平均：观看人次&触达人次
	 *
	 * @return List<MonitorWatchTouchDTO>
	 * @throws ServiceException
	 */
	List<MonitorWatchTouchDTO> listMonitorWatchTouch() throws ServiceException;

}
