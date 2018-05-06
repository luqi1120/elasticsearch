package com.luqi.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 基于角色的登录入口控制
 * Created by luqi
 * 2018-05-06 19:42.
 */
public class LoginUrlEntryPoint extends LoginUrlAuthenticationEntryPoint {

    private final Map<String, String> authEntryPointMap;

    private PathMatcher pathMatcher = new AntPathMatcher();

    public LoginUrlEntryPoint(String loginFormUrl) {
        super(loginFormUrl);
        authEntryPointMap = new HashMap<>();

        // 普通用户登录入口映射
        authEntryPointMap.put("/user/**", "/user/login");

        // 管理员登录入口映射
        authEntryPointMap.put("/admin/**", "/admin/login");
    }

    /**
     * 根据请求跳转到指定的页面,父类是默认使用上面的入参 loginFormUrl
     * @param request
     * @param response
     * @param exception
     * @return
     */
    @Override
    protected String determineUrlToUseForThisRequest(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {

        // 1.获取到要跳转到的uri
        String uri = request.getRequestURI().replace(request.getContextPath(), "");

        // 2.根据uri的地址,判断跳转哪一个入口
        for (Map.Entry<String, String> authEntry : this.authEntryPointMap.entrySet()) {

            // 当传入的路径匹配是了就跳转这个路径
            if (this.pathMatcher.match(authEntry.getKey(), uri)) {
                return authEntry.getValue();
            }
        }
        return super.determineUrlToUseForThisRequest(request, response, exception);
    }
}
