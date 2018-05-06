package com.luqi.service.impl;

import com.luqi.entity.Role;
import com.luqi.entity.User;
import com.luqi.repository.RoleRepository;
import com.luqi.repository.UserRepository;
import com.luqi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luqi
 * 2018-05-06 16:55.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public User findUserByName(String username) {
        User user = userRepository.findByName(username);
        if (user == null) {
            return null;
        }
        List<Role> roleList = roleRepository.findByUserId(user.getId());

        if (roleList == null || roleList.isEmpty()) {
            throw new DisabledException("权限非法");
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        roleList.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName())));

        user.setAuthorityList(authorities);  // 给user设置权限

        return user;
    }

}
