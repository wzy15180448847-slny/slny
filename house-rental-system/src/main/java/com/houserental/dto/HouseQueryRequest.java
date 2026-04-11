package com.houserental.dto;

/**
 * 房源查询请求DTO
 */
public class HouseQueryRequest {

    /**
     * 当前页码
     */
    private Integer current = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;

    /**
     * 城市
     */
    private String city;

    /**
     * 区域
     */
    private String district;

    /**
     * 户型
     */
    private String houseType;

    /**
     * 最低价格
     */
    private String minPrice;

    /**
     * 最高价格
     */
    private String maxPrice;

    /**
     * 最小面积
     */
    private String minArea;

    /**
     * 最大面积
     */
    private String maxArea;

    /**
     * 租赁方式（1-整租，2-合租）
     */
    private Integer rentWay;

    /**
     * 排序字段
     */
    private String sortBy = "createTime";

    /**
     * 排序方向
     */
    private String sortOrder = "DESC";

    // Getters and Setters
    public Integer getCurrent() {
        return current;
    }

    public void setCurrent(Integer current) {
        this.current = current;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
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

    public String getHouseType() {
        return houseType;
    }

    public void setHouseType(String houseType) {
        this.houseType = houseType;
    }

    public String getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(String minPrice) {
        this.minPrice = minPrice;
    }

    public String getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(String maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getMinArea() {
        return minArea;
    }

    public void setMinArea(String minArea) {
        this.minArea = minArea;
    }

    public String getMaxArea() {
        return maxArea;
    }

    public void setMaxArea(String maxArea) {
        this.maxArea = maxArea;
    }

    public Integer getRentWay() {
        return rentWay;
    }

    public void setRentWay(Integer rentWay) {
        this.rentWay = rentWay;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }
}
