package com.houserental.config;


import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
/**
 * Swagger配置类
 */
// @Configuration
// @Profile({"dev", "test"}) // 只在开发和测试环境启用
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.houserental.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("房屋租赁平台API文档")
                .description("房屋租赁平台的RESTful API接口文档")
                .version("1.0.0")
                .contact(new Contact("房屋租赁平台", "http://localhost:8080", "service@example.com"))
                .build();
    }
}
