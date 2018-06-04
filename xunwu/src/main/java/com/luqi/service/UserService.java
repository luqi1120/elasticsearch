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

    /**
     * 根据电话号码寻找用户
     * @param telephone
     * @return
     */
    User findUserByTelephone(String telephone);

    /**
     * 通过手机号注册登录
     * @param telephone
     * @return
     */
    User addUserByPhone(String telephone);
}
