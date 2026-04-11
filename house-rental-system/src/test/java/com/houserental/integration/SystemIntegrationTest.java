package com.houserental.integration;

import com.houserental.controller.AuthController;
import com.houserental.controller.HouseController;
import com.houserental.dto.LoginRequest;
import com.houserental.dto.RegisterRequest;
import com.houserental.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * 系统集成测试
 */
@WebMvcTest({AuthController.class, HouseController.class})
public class SystemIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    public void testUserRegistrationAndLogin() throws Exception {
        // 注册新用户
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser" + System.currentTimeMillis());
        registerRequest.setPassword("password123");
        registerRequest.setPhone("13800138000");
        registerRequest.setUserType("TENANT");

        // 执行注册请求
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"" + registerRequest.getUsername() + "\", \"password\": \"" + registerRequest.getPassword() + "\", \"phone\": \"" + registerRequest.getPhone() + "\", \"userType\": \"" + registerRequest.getUserType() + "\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200));

        // 登录测试
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(registerRequest.getUsername());
        loginRequest.setPassword(registerRequest.getPassword());

        // 执行登录请求
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"" + loginRequest.getUsername() + "\", \"password\": \"" + loginRequest.getPassword() + "\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.token").exists());
    }

    @Test
    public void testHouseList() throws Exception {
        // 测试获取房屋列表
        mockMvc.perform(MockMvcRequestBuilders.get("/api/house/list")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.records").isArray());
    }
}
