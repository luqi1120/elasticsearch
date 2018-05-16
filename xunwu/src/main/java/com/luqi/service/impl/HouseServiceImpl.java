package com.luqi.service.impl;

import com.luqi.base.LoginUserUtil;
import com.luqi.entity.*;
import com.luqi.repository.*;
import com.luqi.service.HouseService;
import com.luqi.service.QiNiuService;
import com.luqi.service.ServiceMultiResult;
import com.luqi.service.ServiceResult;
import com.luqi.web.dto.HouseDTO;
import com.luqi.web.dto.HouseDetailDTO;
import com.luqi.web.dto.HousePictureDTO;
import com.luqi.web.from.DatatableSearch;
import com.luqi.web.from.HouseForm;
import com.luqi.web.from.PhotoForm;
import com.luqi.web.from.RentSearch;
//import org.elasticsearch.search.SearchService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by luqi
 * 2018-05-16 22:26.
 */
@Service
public class HouseServiceImpl implements HouseService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private HouseRepository houseRepository;

    @Autowired
    private HouseDetailRepository houseDetailRepository;

    @Autowired
    private HousePictureRepository housePictureRepository;

    @Autowired
    private HouseTagRepository houseTagRepository;

    @Autowired
    private SubwayRepository subwayRepository;

    @Autowired
    private SubwayStationRepository subwayStationRepository;

//    @Autowired
//    private HouseSubscribeRespository subscribeRespository;

    @Autowired
    private QiNiuService qiNiuService;

//    @Autowired
//    private SearchService searchService;

    @Value("${qiniu.cdn.prefix}")
    private String cdnPrefix;
    @Override
    public ServiceResult<HouseDTO> save(HouseForm houseForm) {
        HouseDetail detail = new HouseDetail();
        ServiceResult<HouseDTO> subwayValidtionResult = wrapperDetailInfo(detail, houseForm);
        if (subwayValidtionResult != null) {
            return subwayValidtionResult;
        }

        House house = new House();
        modelMapper.map(houseForm, house);

        Date now = new Date();
        house.setCreateTime(now);
        house.setLastUpdateTime(now);
        house.setAdminId(LoginUserUtil.getLoginUserId());
        house = houseRepository.save(house);

        detail.setHouseId(house.getId());
        detail = houseDetailRepository.save(detail);

        List<HousePicture> pictures = generatePictures(houseForm, house.getId());
        Iterable<HousePicture> housePictures = housePictureRepository.save(pictures);

        HouseDTO houseDTO = modelMapper.map(house, HouseDTO.class);
        HouseDetailDTO houseDetailDTO = modelMapper.map(detail, HouseDetailDTO.class);

        houseDTO.setHouseDetail(houseDetailDTO);

        List<HousePictureDTO> pictureDTOS = new ArrayList<>();
        housePictures.forEach(housePicture -> pictureDTOS.add(modelMapper.map(housePicture, HousePictureDTO.class)));
        houseDTO.setPictures(pictureDTOS);
        houseDTO.setCover(this.cdnPrefix + houseDTO.getCover());

        List<String> tags = houseForm.getTags();
        if (tags != null && !tags.isEmpty()) {
            List<HouseTag> houseTags = new ArrayList<>();
            for (String tag : tags) {
                houseTags.add(new HouseTag(house.getId(), tag));
            }
            houseTagRepository.save(houseTags);
            houseDTO.setTags(tags);
        }

        return new ServiceResult<HouseDTO>(true, null, houseDTO);
    }

    @Override
    public ServiceResult update(HouseForm houseForm) {
        return null;
    }

    @Override
    public ServiceMultiResult<HouseDTO> adminQuery(DatatableSearch searchBody) {
        return null;
    }

    @Override
    public ServiceResult<HouseDTO> findCompleteOne(Long id) {
        return null;
    }

    @Override
    public ServiceResult removePhoto(Long id) {
        return null;
    }

    @Override
    public ServiceResult updateCover(Long coverId, Long targetId) {
        return null;
    }

    @Override
    public ServiceResult addTag(Long houseId, String tag) {
        return null;
    }

    @Override
    public ServiceResult removeTag(Long houseId, String tag) {
        return null;
    }

    @Override
    public ServiceResult updateStatus(Long id, int status) {
        return null;
    }

    @Override
    public ServiceMultiResult<HouseDTO> query(RentSearch rentSearch) {
        return null;
    }

    /**
     * 图片对象列表信息填充
     * @param form
     * @param houseId
     * @return
     */
    private List<HousePicture> generatePictures(HouseForm form, Long houseId) {
        List<HousePicture> pictures = new ArrayList<>();
        if (form.getPhotos() == null || form.getPhotos().isEmpty()) {
            return pictures;
        }

        for (PhotoForm photoForm : form.getPhotos()) {
            HousePicture picture = new HousePicture();
            picture.setHouseId(houseId);
            picture.setCdnPrefix(cdnPrefix);
            picture.setPath(photoForm.getPath());
            picture.setWidth(photoForm.getWidth());
            picture.setHeight(photoForm.getHeight());
            pictures.add(picture);
        }
        return pictures;
    }

    /**
     * 房源详细信息对象填充
     * @param houseDetail
     * @param houseForm
     * @return
     */
    private ServiceResult<HouseDTO> wrapperDetailInfo(HouseDetail houseDetail, HouseForm houseForm) {
        Subway subway = subwayRepository.findOne(houseForm.getSubwayLineId());
        if (subway == null) {
            return new ServiceResult<>(false, "Not valid subway line!");
        }

        SubwayStation subwayStation = subwayStationRepository.findOne(houseForm.getSubwayStationId());
        if (subwayStation == null || subway.getId() != subwayStation.getSubwayId()) {
            return new ServiceResult<>(false, "Not valid subway station!");
        }

        houseDetail.setSubwayLineId(subway.getId());
        houseDetail.setSubwayLineName(subway.getName());

        houseDetail.setSubwayStationId(subwayStation.getId());
        houseDetail.setSubwayStationName(subwayStation.getName());

        houseDetail.setDescription(houseForm.getDescription());
        houseDetail.setDetailAddress(houseForm.getDetailAddress());
        houseDetail.setLayoutDesc(houseForm.getLayoutDesc());
        houseDetail.setRentWay(houseForm.getRentWay());
        houseDetail.setRoundService(houseForm.getRoundService());
        houseDetail.setTraffic(houseForm.getTraffic());
        return null;

    }
}
