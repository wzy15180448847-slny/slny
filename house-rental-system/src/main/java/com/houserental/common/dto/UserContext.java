package com.houserental.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * 用户上下文信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserContext {

    @JsonProperty("userId")
    private Long userId;
    
    @JsonProperty("username")
    private String username;
    
    @JsonProperty("nickname")
    private String nickname;
    
    @JsonProperty("phone")
    private String phone;
    
    @JsonProperty("email")
    private String email;
    
    @JsonProperty("avatar")
    private String avatar;
    
    @JsonProperty("realName")
    private String realName;
    
    @JsonProperty("userType")
    private String userType;
    
    @JsonProperty("status")
    private Integer status;
    
    @JsonProperty("gender")
    private Integer gender;
    
    @JsonProperty("creditScore")
    private Integer creditScore;
    
    @JsonProperty("lastLoginTime")
    private String lastLoginTime;
    
    @JsonProperty("lastLoginIp")
    private String lastLoginIp;
    
    @JsonProperty("roles")
    private Set<String> roles;
    
    @JsonProperty("permissions")
    private Set<String> permissions;
}