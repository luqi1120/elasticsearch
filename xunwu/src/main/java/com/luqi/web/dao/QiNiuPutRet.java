package com.luqi.web.dao;

/**
 * 七牛返回结果与QiNiuServiceImpl.afterPropertiesSet()方法中参数必须保持一致
 * Created by luqi
 * 2018-05-07 22:34.
 */
public final class QiNiuPutRet {
    public String key;
    public String hash;
    public String bucket;
    public int width;
    public int height;

    @Override
    public String toString() {
        return "QiNiuPutRet{" +
                "key='" + key + '\'' +
                ", hash='" + hash + '\'' +
                ", bucket='" + bucket + '\'' +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
