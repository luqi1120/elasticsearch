package com.luqi.service;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;

import java.io.File;
import java.io.InputStream;

/**
 * 七牛云上传
 * Created by luqi
 * 2018-05-07 22:08.
 */
public interface QiNiuService {

    // 基于文件上传
    Response uploadFile(File file) throws QiniuException;

    // 基于文件流上传
    Response uploadFile(InputStream inputStream) throws QiniuException;

    Response deleteFile(String key) throws QiniuException;
}
