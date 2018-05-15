package com.luqi.entity;

import javax.persistence.*;

/**
 * Created by luQi
 * 2018-05-15 21:19.
 */
@Entity
@Table(name = "support_address")
public class SupportAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 上一级行政单位名 默认为0 */
    @Column(name = "belong_to")
    private String belongTo;

    /** 行政单位英文名缩写 */
    @Column(name = "en_name")
    private String enName;

    /** 行政单位中文名 */
    @Column(name = "cn_name")
    private String cnName;

    /** 行政级别 市-city 地区-region */
    private String level;

    /** 百度地图经度 */
    @Column(name = "baidu_map_lng")
    private String baiduMapLng;

    /** 百度地图纬度 */
    @Column(name = "baidu_map_lat")
    private String baiduMapLat;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBelongTo() {
        return belongTo;
    }

    public void setBelongTo(String belongTo) {
        this.belongTo = belongTo;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getCnName() {
        return cnName;
    }

    public void setCnName(String cnName) {
        this.cnName = cnName;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getBaiduMapLng() {
        return baiduMapLng;
    }

    public void setBaiduMapLng(String baiduMapLng) {
        this.baiduMapLng = baiduMapLng;
    }

    public String getBaiduMapLat() {
        return baiduMapLat;
    }

    public void setBaiduMapLat(String baiduMapLat) {
        this.baiduMapLat = baiduMapLat;
    }

    /**
     * 行政级别定义
     */
    public enum Level {
        CITY("city"),
        REGION("region");

        private String value;

        Level(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Level of(String value) {
            for (Level level : Level.values()) {
                if (level.getValue().equals(value)) {
                    return level;
                }
            }

            throw new IllegalArgumentException();
        }
    }
}
