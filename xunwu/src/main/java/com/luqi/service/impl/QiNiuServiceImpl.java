package com.luqi.service.impl;

import com.luqi.service.QiNiuService;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;

/**
 * Created by luqi
 * 2018-05-07 22:11.
 */
@Service
public class QiNiuServiceImpl implements QiNiuService, InitializingBean {

    @Autowired
    private UploadManager uploadManager;  // 上传实例

    @Autowired
    private BucketManager bucketManager;  // 空间管理

//    @Autowired
//    private Auth auth;  // 认证信息

    @Value("${qiniu.Bucket}")
    private String bucket;  // 空间名

    private StringMap putPolicy;  // 返回结果

    @Override
    public Response uploadFile(File file) throws QiniuException {
        Response response = this.uploadManager.put(file, null, getUploadToken());

        int retry = 0;
        // 如果需要重新传, 就重新上传
        while (response.needRetry() && retry < 3) {
            response = this.uploadManager.put(file, null, getUploadToken());
            retry++;
        }
        return response;
    }

    @Override
    public Response uploadFile(InputStream inputStream) throws QiniuException {
        Response response = this.uploadManager.put(inputStream, null, getUploadToken(), null, null);

        int retry = 0;
        // 如果需要重新传, 就重新上传
        while (response.needRetry() && retry < 3) {
            response = this.uploadManager.put(inputStream, null, getUploadToken(), null, null);
            retry++;
        }
        return response;
    }

    @Override
    public Response deleteFile(String key) throws QiniuException {
        Response response = bucketManager.delete(this.bucket, key);
        int retry = 0;
        while (response.needRetry() && retry++ < 3) {
            response = bucketManager.delete(bucket, key);
        }
        return response;
    }

    // 官方文档 要继承InitializingBean,固定写法
    @Override
    public void afterPropertiesSet() throws Exception {
        this.putPolicy = new StringMap();
        putPolicy.put("returnBody", "{\"key\":\"$(key)\",\"hash\":\"$(etag)\"," +
                "\"bucket\":\"$(bucket)\",\"width\":$(imageInfo.width), \"height\":${imageInfo.height}}");
    }

    /**
     * 获取上传凭证
     */
    public String getUploadToken() {
//        return this.auth.uploadToken(bucket, null, 3600, putPolicy);
        return "";
    }
}
