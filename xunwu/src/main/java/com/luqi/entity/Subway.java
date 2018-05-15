package com.luqi.entity;

import javax.persistence.*;

/**
 * Created by luqi
 * 2018-05-15 22:21.
 */
@Entity
@Table(name = "subway")
public class Subway {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 线路名 */
    private String name;

    /** 所属城市英文名缩写 */
    @Column(name = "city_en_name")
    private String cityEnName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCityEnName() {
        return cityEnName;
    }

    public void setCityEnName(String cityEnName) {
        this.cityEnName = cityEnName;
    }
}
