package com.houserental.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 权限实体
 */
@TableName("sys_permission")
public class Permission extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 权限编码
     */
    @TableField(value = "perm_code", exist = true)
    private String permCode;

    /**
     * 权限名称
     */
    @TableField(value = "perm_name", exist = true)
    private String permName;

    /**
     * 父权限ID
     */
    @TableField(value = "parent_id", exist = true)
    private Long parentId;

    /**
     * 权限类型（MENU-菜单，BUTTON-按钮，API-接口）
     */
    @TableField(value = "perm_type", exist = true)
    private String permType;

    /**
     * 权限路径/URL
     */
    @TableField(value = "path", exist = true)
    private String path;

    /**
     * 请求方法（GET/POST/PUT/DELETE）
     */
    @TableField(value = "method", exist = true)
    private String method;

    /**
     * 图标
     */
    @TableField(value = "icon", exist = true)
    private String icon;

    /**
     * 排序号
     */
    @TableField(value = "sort_order", exist = true)
    private Integer sortOrder = 0;

    /**
     * 状态（0-禁用，1-启用）
     */
    @TableField(value = "status", exist = true)
    private Integer status = 1;

    // Getters and Setters
    public String getPermCode() {
        return permCode;
    }

    public void setPermCode(String permCode) {
        this.permCode = permCode;
    }

    public String getPermName() {
        return permName;
    }

    public void setPermName(String permName) {
        this.permName = permName;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getPermType() {
        return permType;
    }

    public void setPermType(String permType) {
        this.permType = permType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
