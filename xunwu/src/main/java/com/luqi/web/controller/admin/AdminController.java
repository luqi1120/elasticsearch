package com.luqi.web.controller.admin;

import com.luqi.base.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by luqi
 * 2018-05-06 15:57.
 */
@Controller
public class AdminController {

    /**
     * 后台管理中心
     * @return
     */
    @GetMapping("/admin/center")
    public String adminCenterPage() {
        return "admin/center";
    }

    /**
     * 欢迎页
     * @return
     */
    @GetMapping("/admin/welcome")
    public String welcomePage() {
        return "admin/welcome";
    }

    /**
     * 登录页
     * @return
     */
    @GetMapping("/admin/login")
    public String adminLoginPage() {
        return "admin/login";
    }

    /**
     * 添加房源页
     * @return
     */
    @GetMapping("/admin/add/house")
    public String addHousePage() {
        return "admin/house-add";
    }

    /**
     * 上传图片
     * @return
     */
    @PostMapping(value = "/admin/update/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ApiResponse updatePhoto(@RequestParam("file") MultipartFile file) {
        return ApiResponse.ofSuccess(null);
    }
}
