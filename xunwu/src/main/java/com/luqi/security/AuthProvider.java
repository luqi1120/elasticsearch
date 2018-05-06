package com.luqi.security;

import com.luqi.entity.User;
import com.luqi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * 自定义认证
 * Created by luqi
 * 2018-05-06 16:51.
 */
public class AuthProvider implements AuthenticationProvider {

    @Autowired
    private UserService userService;

    private final Md5PasswordEncoder passwordEncoder = new Md5PasswordEncoder();


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String username = authentication.getName();
        String inputPassword = (String) authentication.getCredentials();
        User user = userService.findUserByName(username);
        if (user == null) {
            throw new AuthenticationCredentialsNotFoundException("authError");
        }
        boolean passwordValid = this.passwordEncoder.isPasswordValid(user.getPassword(), inputPassword, user.getId());

        if (passwordValid) {
            // 登录
            return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        }
        throw new BadCredentialsException("authError");
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
