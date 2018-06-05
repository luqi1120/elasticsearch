package com.luqi.service;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by luqi
 * 2018-06-04 21:55.
 */
@Service
public class SmsServiceImpl implements SmsService, InitializingBean {

    @Value("${aliyun.sms.accessKey}")
    private String accessKey;

    @Value("${aliyun.sms.accessKeySecret}")
    private String secertKey;

    @Value("${aliyun.sms.template.code}")
    private String templateCode;

    // 初始化客户端
    private IAcsClient acsClient;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private final static String SMS_CODE_CONTENT_PREFIX = "SMS::CODE::CONTENT";

    private static final String[] NUMS = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
    private static final Random random = new Random();

    @Override
    public ServiceResult<String> sendSms(String telephone) {
        String gapKey = "SMS::CODE::INTERVAL::" + telephone;

        // 从redis中获取验证码
        String result = redisTemplate.opsForValue().get(gapKey);
        if (result != null) {
            // 1分钟内 如果redis中有验证码
            return new ServiceResult<String>(false, "请求次数太频繁");
        }

        String code = generateRandomSmsCode();

        // 发送短信 套用短信模板
        String templateParam = String.format("{\"code\": \"%s\"}", code);

        // 组装请求对象
        SendSmsRequest request = new SendSmsRequest();

        // 使用post提交
        request.setMethod(MethodType.POST);
        request.setPhoneNumbers(telephone);
        request.setTemplateParam(templateParam);
        request.setTemplateCode(templateCode);
        request.setSignName("测试");

        Boolean success = false;

        try {
            SendSmsResponse response = acsClient.getAcsResponse(request);
            if ("OK".equals(response.getCode())) {
                success = true;
            } else {
                // TODO log this question
            }
        } catch (ClientException e) {
            e.printStackTrace();
        }
        if (success) {

            // 验证码发送成功,1分钟之内不能重复点击,为了让前台验证码框变成不能点击状态
            redisTemplate.opsForValue().set(gapKey, code, 60, TimeUnit.SECONDS);

            // 如果发送成功保存在redis中
            redisTemplate.opsForValue().set(SMS_CODE_CONTENT_PREFIX + telephone, code, 10, TimeUnit.MINUTES);

            return ServiceResult.of(code);
        } else {
            // 如果发送短信失败
            return new ServiceResult<String>(false, "服务忙,请稍后重试");
        }
    }

    @Override
    public String getSmsCode(String telephone) {

        // 直接从redis中获取验证码
        return this.redisTemplate.opsForValue().get(SMS_CODE_CONTENT_PREFIX + telephone);
    }

    @Override
    public void remove(String telephone) {

        // 删除redis中验证码
        this.redisTemplate.delete(SMS_CODE_CONTENT_PREFIX + telephone);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 设置超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKey, secertKey);

        // 固定的不需要改变
        String product = "Dysmsapi";
        String domain = "dysmsapi.aliyuncs.com";

        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        this.acsClient = new DefaultAcsClient(profile);
    }

    /**
     * 6位验证码生成器
     * @return
     */
    private static String generateRandomSmsCode() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(10);
            sb.append(NUMS[index]);
        }
        return sb.toString();
    }
}
