package com.luqi.web.controller;

import com.luqi.base.ApiResponse;
import com.luqi.base.LoginUserUtil;
import com.luqi.service.ServiceResult;
import com.luqi.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by luqi
 * 2018-05-06 12:51.
 */
@Controller
public class HomeController {


    @Autowired
    private SmsService smsService;

    @GetMapping(value = {"/", "/index"}) // 兼容
    public String index(Model model) {
        model.addAttribute("name", "luqi");
        return  "index";
    }

    @GetMapping("/404")
    public String notFoundPage() {
        return "404";
    }

    @GetMapping("/403")
    public String accessError() {
        return "403";
    }

    @GetMapping("/500")
    public String internalError() {
        return "500";
    }

    @GetMapping("/logout/page")
    public String logoutPage() {
        return "logout";
    }

    /**
     * 获取验证码
     * @param telephone
     * @return
     */
    @GetMapping(value = "sms/code")
    @ResponseBody
    public ApiResponse smsCode(@RequestParam("telephone") String telephone) {

        // 检测手机号是否合法
        if (!LoginUserUtil.checkTelephone(telephone)) {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), "请输入正确的手机号");
        }
        ServiceResult<String> result = smsService.sendSms(telephone);
        if (result.isSuccess()) {
            return ApiResponse.ofSuccess("");
        } else {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), result.getMessage());
        }

    }
}
