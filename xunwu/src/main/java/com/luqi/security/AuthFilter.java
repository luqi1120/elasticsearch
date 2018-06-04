package com.luqi.security;

import java.util.Objects;
import com.luqi.base.LoginUserUtil;
import com.luqi.entity.User;
import com.luqi.service.SmsService;
import com.luqi.service.UserService;
import org.elasticsearch.common.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录验证
 * Created by luqi
 * 2018-06-04 21:57.
 */
public class AuthFilter extends UsernamePasswordAuthenticationFilter {

    @Autowired
    private UserService userService;

    @Autowired
    private SmsService smsService;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        // 获取用户名
        String username = obtainUsername(request);

        // 用户名不为空的时候表示用户是以用户名密码登录
        if (!Strings.isNullOrEmpty(username)) {
            request.setAttribute("username", username);
            return super.attemptAuthentication(request, response);
        }
        // 获取前端传来的telephone
        String telephone = request.getParameter("telephone");

        // 如果手机号码为空 或者 用户输入的不是一个正常的手机号
        if (Strings.isNullOrEmpty(telephone) || !LoginUserUtil.checkTelephone(telephone)) {
            throw new BadCredentialsException("Wrong telephone number");
        }

        // 通过电话获取到用户
        User user = userService.findUserByTelephone(telephone);

        // 获取用户输入的短信验证码
        String inputCode = request.getParameter("smsCode");
        String sessionCode = smsService.getSmsCode(telephone);

        // 如果用户输入的验证码和缓存中验证码一致就 进行下面的逻辑
        if (Objects.equals(inputCode, sessionCode)) {

            // 如果用户第一次用未注册的手机登陆 则自动注册该用户
            if (user == null) {

                // 用户注册
                user = userService.addUserByPhone(telephone);
            }

            // 密码设置为null
            return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        } else {
            throw new BadCredentialsException("smsCodeError");
        }
    }
}
