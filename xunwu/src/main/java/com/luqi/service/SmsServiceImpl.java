package com.luqi.service;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

/**
 * Created by luqi
 * 2018-06-04 21:55.
 */
@Service
public class SmsServiceImpl implements SmsService/*, InitializingBean*/ {

    @Override
    public ServiceResult<String> sendSms(String telephone) {
        return ServiceResult.of("123456");
    }

    @Override
    public String getSmsCode(String telephone) {
        return "123456";
    }

    @Override
    public void remove(String telephone) {

    }
}
