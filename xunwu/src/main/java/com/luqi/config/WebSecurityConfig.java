package com.luqi.config;

import com.luqi.security.AuthProvider;
import com.luqi.security.LoginAuthFailHandler;
import com.luqi.security.LoginUrlEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import sun.net.httpserver.AuthFilter;

import javax.servlet.Filter;

/**
 * Created by luqi
 * 2018-05-06 16:40.
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * HTTP权限控制
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http.addFilterBefore(authFilter(), UsernamePasswordAuthenticationFilter.class);

        // 资源访问权限
        http.authorizeRequests()
                .antMatchers("/admin/login").permitAll() //permitAll() 无条件允许访问 管理员登录入口
                .antMatchers("/static/**").permitAll() // 静态资源
                .antMatchers("/user/login").permitAll() // 用户登录入口
                .antMatchers("/admin/**").hasRole("ADMIN") // 同下 不过会自动添加 ROLE_ 作为前缀
                .antMatchers("/user/**").hasAnyRole("ADMIN", "USER")  // hasAnyRole() 如果用户具备给定条件就允许访问
                .antMatchers("/api/user/**").hasAnyRole("ADMIN","USER")
                .and()
                .formLogin() // 启用默认登录页面
                .loginProcessingUrl("/login") // 配置角色登录处理入口
                .failureHandler(authFailHandler()) // 登录失败验证器
                .and() // 将不同的配置指令链接在一起
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/logout/page") // 退出成功后跳转到 /logout/page 页面
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true) //session会话失效
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(urlEntryPoint())
                .accessDeniedPage("/403");

        // 防御配置,方便开发就先关闭
        http.csrf().disable(); // 这行代码表示禁用CSRF跨站请求伪造
        http.headers().frameOptions().sameOrigin();

    }

    /**
     * 自定义认证策略
     * AuthenticationManagerBuilder只能注入一个
     */
    @Autowired
    public void configGlobal(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication().withUser("admin").password("admin").roles("ADMIN").and();
        // 擦除密码
        auth.authenticationProvider(authProvider()).eraseCredentials(true);

    }

    @Bean
    public AuthProvider authProvider() {
        return new AuthProvider();
    }

    // 默认走用户的登录
    @Bean
    public LoginUrlEntryPoint urlEntryPoint() {
        return new LoginUrlEntryPoint("/user/login");
    }

    @Bean
    public LoginAuthFailHandler authFailHandler() {
        return new LoginAuthFailHandler(urlEntryPoint());
    }

//    @Bean
//    private AuthFilter authFilter() {
//        AuthFilter authFilter = new AuthFilter();
//        authFilter.setAuthenticationManager(authenticationManager());
//        authFilter.setAuthenticationFailureHandler(authFailHandler());
//        return authFilter;
//    }
}

