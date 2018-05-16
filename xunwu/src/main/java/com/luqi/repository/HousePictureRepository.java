package com.luqi.repository;

import com.luqi.entity.House;
import com.luqi.entity.HousePicture;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by luqi
 * 2018-05-15 23:12.
 */
public interface HousePictureRepository extends CrudRepository<HousePicture, Long>{

    List<HousePicture> findAllByHouseId(Long id);

}
