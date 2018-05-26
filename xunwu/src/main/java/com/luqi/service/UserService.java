package com.luqi.service;

import com.luqi.entity.User;
import com.luqi.web.dto.UserDTO;

/**
 * 用户服务
 * Created by luqi
 * 2018-05-06 16:53.
 */
public interface UserService {

    User findUserByName(String username);

    ServiceResult<UserDTO> findById(Long userId);
}
