package com.luqi.service.impl;

import com.luqi.entity.Subway;
import com.luqi.entity.SubwayStation;
import com.luqi.entity.SupportAddress;
import com.luqi.repository.SubwayRepository;
import com.luqi.repository.SubwayStationRepository;
import com.luqi.repository.SupportAddressRepository;
import com.luqi.service.AddressService;
import com.luqi.service.ServiceMultiResult;
import com.luqi.web.dto.SubwayDTO;
import com.luqi.web.dto.SubwayStationDTO;
import com.luqi.web.dto.SupportAddressDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luqi
 * 2018-05-15 21:39.
 */
@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private SupportAddressRepository supportAddressRepository;

    @Autowired
    private SubwayRepository subwayRepository;

    @Autowired
    private SubwayStationRepository subwayStationRepository;

    @Autowired
    private ModelMapper modelMapper;



    @Override
    public ServiceMultiResult<SupportAddressDTO> findAllCities() {
        List<SupportAddress> addressList = supportAddressRepository
                .findAllByLevel(SupportAddress.Level.CITY.getValue());

        List<SupportAddressDTO> addressDTOs = new ArrayList<>();
        for (SupportAddress supportAddress: addressList) {

            // modelMapper 就是转换DTO使用的
            SupportAddressDTO addressDTO = modelMapper.map(supportAddress, SupportAddressDTO.class);
            addressDTOs.add(addressDTO);
        }

        return new ServiceMultiResult<>(addressDTOs.size(), addressDTOs);
    }

    @Override
    public ServiceMultiResult<SupportAddressDTO> findAllRegionsByCityName(String cityName) {

        if (cityName == null) {
            return new ServiceMultiResult<>(0, null);
        }
        List<SupportAddressDTO> result = new ArrayList<>();
        List<SupportAddress> regions = supportAddressRepository
                .findAllByLevelAndBelongTo(SupportAddress.Level.REGION.getValue(), cityName);

        for (SupportAddress region : regions) {
            result.add(modelMapper.map(region, SupportAddressDTO.class));
        }
        return new ServiceMultiResult<>(regions.size(), result);
    }

    @Override
    public List<SubwayDTO> findAllSubwayByCity(String cityEnName) {
        List<SubwayDTO> result = new ArrayList<>();
        List<Subway> subways = subwayRepository.findAllByCityEnName(cityEnName);
        if (subways.isEmpty()) {
            return result;
        }
        subways.forEach(subway -> result.add(modelMapper.map(subway, SubwayDTO.class)));
        return result;
    }

    @Override
    public List<SubwayStationDTO> findAllStationBySubway(Long subwayId) {

        List<SubwayStationDTO> result = new ArrayList<>();
        List<SubwayStation> stations = subwayStationRepository.findAllBySubwayId(subwayId);
        if (stations.isEmpty()) {
            return result;
        }
        stations.forEach(station -> result.add(modelMapper.map(station, SubwayStationDTO.class)));
        return result;
    }
}
