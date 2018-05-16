package com.luqi.repository;

import com.luqi.entity.HouseDetail;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by luqi
 * 2018-05-15 23:13.
 */
public interface HouseDetailRepository extends CrudRepository<HouseDetail, Long> {

    HouseDetail findByHouseId(Long houseId);

    List<HouseDetail> findAllByHouseIdIn(List<Long> houseIds);
}
