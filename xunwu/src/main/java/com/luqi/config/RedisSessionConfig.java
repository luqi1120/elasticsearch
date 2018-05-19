//package com.luqi.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
//
///**
// * session会话
// * Created by luqi
// * 2018-05-19 19:15.
// *
// * EnableRedisHttpSession  表示实现时间为1天
// */
//@Configuration
//@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 86400)
//public class RedisSessionConfig {
//
//    @Bean
//    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
//
//        return new StringRedisTemplate(factory);
//    }
//}
