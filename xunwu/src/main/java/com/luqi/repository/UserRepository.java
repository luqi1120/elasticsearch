package com.luqi.repository;

import com.luqi.entity.User;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by luqi
 * 2018-05-06 11:49.
 */
public interface UserRepository extends CrudRepository<User, Long> {

    User findByName(String username);
}
