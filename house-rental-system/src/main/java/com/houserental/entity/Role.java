package com.houserental.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 角色实体
 */
@TableName("sys_role")
public class Role extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 角色编码
     */
    @TableField(value = "role_code", exist = true)
    private String roleCode;

    /**
     * 角色名称
     */
    @TableField(value = "role_name", exist = true)
    private String roleName;

    /**
     * 角色描述
     */
    @TableField(value = "description", exist = true)
    private String description;

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
    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
