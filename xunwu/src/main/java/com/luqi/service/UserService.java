package com.luqi.service;

import com.luqi.entity.User;

/**
 * Created by luqi
 * 2018-05-06 16:53.
 */
public interface UserService {

    User findUserByName(String username);
}
