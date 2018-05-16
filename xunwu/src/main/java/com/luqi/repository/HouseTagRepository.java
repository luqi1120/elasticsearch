package com.luqi.repository;

import com.luqi.entity.House;
import com.luqi.entity.HouseTag;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by luqi
 * 2018-05-15 23:12.
 */
public interface HouseTagRepository extends CrudRepository<HouseTag, Long>{

    HouseTag findByNameAndHouseId(String name, Long houseId);

    List<HouseTag> findAllByHouseId(Long id);

    List<HouseTag> findAllByHouseIdIn(List<Long> houseIds);
}
