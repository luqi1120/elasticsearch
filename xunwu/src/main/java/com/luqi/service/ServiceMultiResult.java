package com.luqi.service;

import java.util.List;

/**
 * 通用多结果Service返回结构
 * Created by luqi
 * 2018-05-15 21:54.
 */
public class ServiceMultiResult<T> {

    // 列表总数据
    private long total;
    private List<T> result;

    public ServiceMultiResult(long total, List<T> result) {
        this.total = total;
        this.result = result;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getResult() {
        return result;
    }

    public void setResult(List<T> result) {
        this.result = result;
    }

    // 获取当前结果集
    public int getResultSize() {
        if (this.result == null) {
            return 0;
        }
        return this.result.size();
    }
}
