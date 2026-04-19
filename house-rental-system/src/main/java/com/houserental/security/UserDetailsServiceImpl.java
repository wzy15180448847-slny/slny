package com.houserental.security;

import com.houserental.entity.User;
import com.houserental.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * 用户详情服务实现类
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.selectByUsernameOrPhone(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        if (user.getStatus() == 0) {
            throw new UsernameNotFoundException("账户已禁用: " + username);
        }

        if (user.getStatus() == 2) {
            throw new UsernameNotFoundException("账户已锁定: " + username);
        }

        List<String> roles = userMapper.selectRolesByUserId(user.getId());
        List<String> permissions = userMapper.selectPermissionsByUserId(user.getId());

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        // 添加角色权限
        roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
        // 添加功能权限
        permissions.forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission)));

        return org.springframework.security.core.userdetails.User.builder()
                .username(String.valueOf(user.getId()))
                .password(user.getPassword())
                .authorities(authorities)
                .accountLocked(user.getStatus() == 2)
                .disabled(user.getStatus() == 0)
                .build();
    }
}
