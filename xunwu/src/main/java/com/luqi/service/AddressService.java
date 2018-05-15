package com.luqi.service;

import com.luqi.web.dto.SubwayDTO;
import com.luqi.web.dto.SubwayStationDTO;
import com.luqi.web.dto.SupportAddressDTO;

import java.util.List;

/**
 * 地址服务接口
 * Created by luqi
 * 2018-05-15 21:39.
 */
public interface AddressService {

    /**
     * 获取所有支持的城市列表
     * @return
     */
    ServiceMultiResult<SupportAddressDTO> findAllCities();

    /**
     * 根据城市英文简写获取该城市所有支持的区域信息
     * @param cityName
     * @return
     */
    ServiceMultiResult findAllRegionsByCityName(String cityName);

    /**
     * 获取具体城市所支持的地铁线路
     * @param cityEnName
     * @return
     */
    List<SubwayDTO> findAllSubwayByCity(String cityEnName);

    /**
     * 获取地铁线路所有的站点
     * @param subwayId
     * @return
     */
    List<SubwayStationDTO> findAllStationBySubway(Long subwayId);
}
