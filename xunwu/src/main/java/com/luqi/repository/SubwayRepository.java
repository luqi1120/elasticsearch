package com.luqi.repository;

import com.luqi.entity.Subway;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by luqi
 * 2018-05-15 22:35.
 */
public interface SubwayRepository extends CrudRepository<Subway, Long> {

    List<Subway> findAllByCityEnName(String cityEnName);
}
