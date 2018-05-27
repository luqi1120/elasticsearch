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
    Boolean index(Long houseId);

    /**
     * 移除房源索引
     * @param houseId
     */
    void remove(Long houseId);

}
