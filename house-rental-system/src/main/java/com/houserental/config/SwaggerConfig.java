package com.houserental.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Swagger配置类
 * 暂时禁用，与Spring Boot 2.7.x存在兼容性问题
 */
//@Configuration
//@Profile({"dev", "test"})
public class SwaggerConfig {
    // Swagger/Knife4j配置暂时禁用
    // 如需启用，请在pom.xml中取消注释Knife4j依赖并配置相应的bean
}