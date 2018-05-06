package com.luqi.repository;

import com.luqi.entity.Role;
import com.luqi.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by luqi
 * 2018-05-06 17:09.
 */
public interface RoleRepository extends CrudRepository<Role, Long> {

    List<Role> findByUserId(Long userId);

}
