package com.houserental.service.impl;

import com.houserental.entity.User;
import com.houserental.mapper.UserMapper;

import com.houserental.dto.RegisterRequest;
import com.houserental.dto.UpdatePasswordRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetByUsername() {
        String username = "testuser";
        User expectedUser = new User();
        expectedUser.setUsername(username);

        when(userMapper.selectByUsername(username)).thenReturn(expectedUser);

        User actualUser = userService.getByUsername(username);

        assertNotNull(actualUser);
        assertEquals(username, actualUser.getUsername());
        verify(userMapper, times(1)).selectByUsername(username);
    }

    @Test
    void testRegister() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setPassword("password123");
        request.setPhone("13800138000");
        request.setUserType("tenant");

        when(userMapper.existsByUsername(request.getUsername())).thenReturn(false);
        when(userMapper.existsByPhone(request.getPhone())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userMapper.insert(any(User.class))).thenReturn(1);

        User result = userService.register(request);

        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        verify(userMapper, times(1)).insert(any(User.class));
    }

    @Test
    void testUpdatePassword() {
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setOldPassword("oldPassword");
        request.setNewPassword("newPassword");

        User user = new User();
        user.setId(1L);
        user.setPassword("encodedOldPassword");

        when(userMapper.selectById(1L)).thenReturn(user);
        when(passwordEncoder.matches(request.getOldPassword(), user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(request.getNewPassword())).thenReturn("encodedNewPassword");
        when(userMapper.updateById(user)).thenReturn(1);

        userService.updatePassword(request);

        assertEquals("encodedNewPassword", user.getPassword());
        verify(userMapper, times(1)).updateById(user);
    }

    @Test
    void testDisableUser() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setStatus(1);
        user.setUsername("testuser");

        when(userMapper.selectById(userId)).thenReturn(user);
        when(userMapper.updateById(user)).thenReturn(1);

        userService.disableUser(userId);

        assertEquals(0, user.getStatus());
        verify(userMapper, times(1)).updateById(user);
    }

    @Test
    void testEnableUser() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setStatus(0);
        user.setUsername("testuser");

        when(userMapper.selectById(userId)).thenReturn(user);
        when(userMapper.updateById(user)).thenReturn(1);

        userService.enableUser(userId);

        assertEquals(1, user.getStatus());
        verify(userMapper, times(1)).updateById(user);
    }
}
