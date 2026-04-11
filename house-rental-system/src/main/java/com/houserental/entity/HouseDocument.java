package com.houserental.entity;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 房源Elasticsearch文档
 */
@Data
@Document(indexName = "house")
public class HouseDocument {

    private Long id;

    @Field(type = FieldType.Keyword)
    private String houseNo;

    private Long landlordId;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String title;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String description;

    @Field(type = FieldType.Keyword)
    private String province;

    @Field(type = FieldType.Keyword)
    private String city;

    @Field(type = FieldType.Keyword)
    private String district;

    @Field(type = FieldType.Keyword)
    private String street;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String address;

    private BigDecimal longitude;

    private BigDecimal latitude;

    @Field(type = FieldType.Keyword)
    private String houseType;

    private Integer roomCount;

    private Integer hallCount;

    private Integer bathroomCount;

    private BigDecimal area;

    private Integer floor;

    private Integer totalFloor;

    private Integer hasElevator;

    private Integer decoration;

    private Integer orientation;

    private BigDecimal rentPrice;

    private Integer depositMonth;

    private Integer paymentWay;

    private Integer rentWay;

    @Field(type = FieldType.Text)
    private String facilities;

    @Field(type = FieldType.Text)
    private String images;

    @Field(type = FieldType.Keyword)
    private String coverImage;

    private String contactName;

    private String contactPhone;

    private Integer viewTimeType;

    private LocalDateTime availableDate;

    private Integer minLeaseTerm;

    private Integer status;

    private Integer auditStatus;

    private String auditRemark;

    private Long auditorId;

    private LocalDateTime auditTime;

    private Integer viewCount;

    private Integer favoriteCount;

    private Integer appointmentCount;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
