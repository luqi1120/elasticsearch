package com.luqi.repository;

import com.luqi.entity.Subway;
import com.luqi.entity.SupportAddress;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by luqi
 * 2018-05-15 21:28.
 */
public interface SupportAddressRepository extends CrudRepository<SupportAddress,Long> {

    /**
     * 获取所有对应行政级别的信息
     * @return
     */
    List<SupportAddress> findAllByLevel(String level);

    /**
     * 根据城市英文简写获取该城市所有支持的区域信息
     * @param cityName
     * @return
     */
    List<SupportAddress> findAllByLevelAndBelongTo(String level, String cityName);

}
