package com.luqi.repository;

import com.luqi.entity.SubwayStation;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by luqi
 * 2018-05-15 22:37.
 */
public interface SubwayStationRepository extends CrudRepository<SubwayStation, Long> {

    List<SubwayStation> findAllBySubwayId(Long subwayId);
}
