package com.luqi.service.search;

import com.luqi.service.ServiceMultiResult;
import com.luqi.service.ServiceResult;
import com.luqi.web.from.MapSearch;
import com.luqi.web.from.RentSearch;

import java.util.List;

/**
 * 检索接口
 * Created by luQi
 * 2018-05-27 14:18.
 */
public interface SearchService {

    /**
     * 索引目标房源
     * @param houseId
     */
    void index(Long houseId);

    /**
     * 移除房源索引
     * @param houseId
     */
    void remove(Long houseId);

    /**
     * 查询房源接口
     * @param rentSearch
     * @return
     */
    ServiceMultiResult<Long> query(RentSearch rentSearch);

    /**
     * 获取补全建议关键词
     * @param prefix
     * @return
     */
    ServiceResult<List<String>> suggest(String prefix);

    /**
     * 聚合特定小区的房间数
     */
    ServiceResult<Long> aggregateDistrictHouse(String cityEnName, String regionEnName, String district);
}
