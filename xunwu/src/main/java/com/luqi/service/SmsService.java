package com.luqi.service;

/**
 * 验证码服务
 * Created by luqi
 * 2018-06-04 21:49.
 */
public interface SmsService {

    /**
     * 发送验证码到手机,并缓存验证码10分钟,及请求间隔时间1分钟
     * @param telephone
     * @return
     */
    ServiceResult<String> sendSms(String telephone);

    /**
     * 获取缓存中的验证码
     * @param telephone
     * @return
     */
    String getSmsCode(String telephone);

    /**
     * 移除指定手机号的验证码缓存
     */
    void remove(String telephone);
}
