package com.houserental.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 房源实体
 */
@TableName("biz_house")
public class House extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 房源编号
     */
    @TableField(value = "house_no", exist = true)
    private String houseNo;

    /**
     * 房东ID
     */
    @TableField(value = "landlord_id", exist = true)
    private Long landlordId;

    /**
     * 房源标题
     */
    @TableField(value = "title", exist = true)
    private String title;

    /**
     * 房源描述
     */
    @TableField(value = "description", exist = true)
    private String description;

    /**
     * 省份
     */
    @TableField(value = "province", exist = true)
    private String province;

    /**
     * 城市
     */
    @TableField(value = "city", exist = true)
    private String city;

    /**
     * 区县
     */
    @TableField(value = "district", exist = true)
    private String district;

    /**
     * 街道
     */
    @TableField(value = "street", exist = true)
    private String street;

    /**
     * 详细地址
     */
    @TableField(value = "address", exist = true)
    private String address;

    /**
     * 经度
     */
    @TableField(value = "longitude", exist = true)
    private BigDecimal longitude;

    /**
     * 纬度
     */
    @TableField(value = "latitude", exist = true)
    private BigDecimal latitude;

    /**
     * 户型（如：2室1厅1卫）
     */
    @TableField(value = "house_type", exist = true)
    private String houseType;

    /**
     * 房间数
     */
    @TableField(value = "room_count", exist = true)
    private Integer roomCount;

    /**
     * 客厅数
     */
    @TableField(value = "hall_count", exist = true)
    private Integer hallCount;

    /**
     * 卫生间数
     */
    @TableField(value = "bathroom_count", exist = true)
    private Integer bathroomCount;

    /**
     * 面积（平方米）
     */
    @TableField(value = "area", exist = true)
    private BigDecimal area;

    /**
     * 楼层
     */
    @TableField(value = "floor", exist = true)
    private Integer floor;

    /**
     * 总楼层
     */
    @TableField(value = "total_floor", exist = true)
    private Integer totalFloor;

    /**
     * 电梯（0-无，1-有）
     */
    @TableField(value = "has_elevator", exist = true)
    private Integer hasElevator;

    /**
     * 装修情况（1-毛坯，2-简装，3-精装，4-豪装）
     */
    @TableField(value = "decoration", exist = true)
    private Integer decoration;

    /**
     * 朝向（1-东，2-南，3-西，4-北，5-东南，6-西南，7-东北，8-西北）
     */
    @TableField(value = "orientation", exist = true)
    private Integer orientation;

    /**
     * 租金（元/月）
     */
    @TableField(value = "rent_price", exist = true)
    private BigDecimal rentPrice;

    /**
     * 押金（月）
     */
    @TableField(value = "deposit_month", exist = true)
    private Integer depositMonth = 1;

    /**
     * 付款方式（1-押一付一，2-押一付三，3-押一付六，4-年付）
     */
    @TableField(value = "payment_way", exist = true)
    private Integer paymentWay = 2;

    /**
     * 租赁方式（1-整租，2-合租）
     */
    @TableField(value = "rent_way", exist = true)
    private Integer rentWay = 1;

    /**
     * 配套设施（JSON格式存储）
     */
    @TableField(value = "facilities", exist = true)
    private String facilities;

    /**
     * 房源图片（JSON格式存储，多张图片URL）
     */
    @TableField(value = "images", exist = true)
    private String images;

    /**
     * 封面图片
     */
    @TableField(value = "cover_image", exist = true)
    private String coverImage;

    /**
     * 联系人姓名
     */
    @TableField(value = "contact_name", exist = true)
    private String contactName;

    /**
     * 联系人电话
     */
    @TableField(value = "contact_phone", exist = true)
    private String contactPhone;

    /**
     * 看房时间（1-随时，2-周末，3-工作日，4-提前预约）
     */
    @TableField(value = "view_time_type", exist = true)
    private Integer viewTimeType;

    /**
     * 可入住时间
     */
    @TableField(value = "available_date", exist = true)
    private LocalDateTime availableDate;

    /**
     * 最小租期（月）
     */
    @TableField(value = "min_lease_term", exist = true)
    private Integer minLeaseTerm = 12;

    /**
     * 房源状态（0-展示中，1-已租，2-已下架）
     */
    @TableField(value = "status", exist = true)
    private Integer houseStatus = 2;

    /**
     * 审核状态（0-待审核，1-审核通过，2-审核拒绝）
     */
    @TableField(value = "audit_status", exist = true)
    private Integer auditStatus = 0;

    /**
     * 审核意见
     */
    @TableField(value = "audit_remark", exist = true)
    private String auditRemark;

    /**
     * 审核人ID
     */
    @TableField(value = "auditor_id", exist = true)
    private Long auditorId;

    /**
     * 审核时间
     */
    @TableField(value = "audit_time", exist = true)
    private LocalDateTime auditTime;

    /**
     * 浏览次数
     */
    @TableField(value = "view_count", exist = true)
    private Integer viewCount = 0;

    /**
     * 收藏次数
     */
    @TableField(value = "favorite_count", exist = true)
    private Integer favoriteCount = 0;

    /**
     * 预约次数
     */
    @TableField(value = "appointment_count", exist = true)
    private Integer appointmentCount = 0;

    // Getters and Setters
    public String getHouseNo() {
        return houseNo;
    }

    public void setHouseNo(String houseNo) {
        this.houseNo = houseNo;
    }

    public Long getLandlordId() {
        return landlordId;
    }

    public void setLandlordId(Long landlordId) {
        this.landlordId = landlordId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public String getHouseType() {
        return houseType;
    }

    public void setHouseType(String houseType) {
        this.houseType = houseType;
    }

    public Integer getRoomCount() {
        return roomCount;
    }

    public void setRoomCount(Integer roomCount) {
        this.roomCount = roomCount;
    }

    public Integer getHallCount() {
        return hallCount;
    }

    public void setHallCount(Integer hallCount) {
        this.hallCount = hallCount;
    }

    public Integer getBathroomCount() {
        return bathroomCount;
    }

    public void setBathroomCount(Integer bathroomCount) {
        this.bathroomCount = bathroomCount;
    }

    public BigDecimal getArea() {
        return area;
    }

    public void setArea(BigDecimal area) {
        this.area = area;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public Integer getTotalFloor() {
        return totalFloor;
    }

    public void setTotalFloor(Integer totalFloor) {
        this.totalFloor = totalFloor;
    }

    public Integer getHasElevator() {
        return hasElevator;
    }

    public void setHasElevator(Integer hasElevator) {
        this.hasElevator = hasElevator;
    }

    public Integer getDecoration() {
        return decoration;
    }

    public void setDecoration(Integer decoration) {
        this.decoration = decoration;
    }

    public Integer getOrientation() {
        return orientation;
    }

    public void setOrientation(Integer orientation) {
        this.orientation = orientation;
    }

    public BigDecimal getRentPrice() {
        return rentPrice;
    }

    public void setRentPrice(BigDecimal rentPrice) {
        this.rentPrice = rentPrice;
    }

    public Integer getDepositMonth() {
        return depositMonth;
    }

    public void setDepositMonth(Integer depositMonth) {
        this.depositMonth = depositMonth;
    }

    public Integer getPaymentWay() {
        return paymentWay;
    }

    public void setPaymentWay(Integer paymentWay) {
        this.paymentWay = paymentWay;
    }

    public Integer getRentWay() {
        return rentWay;
    }

    public void setRentWay(Integer rentWay) {
        this.rentWay = rentWay;
    }

    public String getFacilities() {
        return facilities;
    }

    public void setFacilities(String facilities) {
        this.facilities = facilities;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public Integer getViewTimeType() {
        return viewTimeType;
    }

    public void setViewTimeType(Integer viewTimeType) {
        this.viewTimeType = viewTimeType;
    }

    public LocalDateTime getAvailableDate() {
        return availableDate;
    }

    public void setAvailableDate(LocalDateTime availableDate) {
        this.availableDate = availableDate;
    }

    public Integer getMinLeaseTerm() {
        return minLeaseTerm;
    }

    public void setMinLeaseTerm(Integer minLeaseTerm) {
        this.minLeaseTerm = minLeaseTerm;
    }

    public Integer getHouseStatus() {
        return houseStatus;
    }

    public void setHouseStatus(Integer houseStatus) {
        this.houseStatus = houseStatus;
    }

    public Integer getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(Integer auditStatus) {
        this.auditStatus = auditStatus;
    }

    public String getAuditRemark() {
        return auditRemark;
    }

    public void setAuditRemark(String auditRemark) {
        this.auditRemark = auditRemark;
    }

    public Long getAuditorId() {
        return auditorId;
    }

    public void setAuditorId(Long auditorId) {
        this.auditorId = auditorId;
    }

    public LocalDateTime getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(LocalDateTime auditTime) {
        this.auditTime = auditTime;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(Integer favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public Integer getAppointmentCount() {
        return appointmentCount;
    }

    public void setAppointmentCount(Integer appointmentCount) {
        this.appointmentCount = appointmentCount;
    }
}