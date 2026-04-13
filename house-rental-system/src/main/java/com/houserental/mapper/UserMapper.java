package com.houserental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.houserental.entity.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户数据访问层
 */
@Repository
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户
     */
    User selectByUsername(String username);

    /**
     * 根据手机号查询用户
     */
    User selectByPhone(String phone);

    /**
     * 根据邮箱查询用户
     */
    User selectByEmail(String email);

    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查手机号是否存在
     */
    boolean existsByPhone(String phone);

    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);

    /**
     * 根据用户类型查询用户列表
     */
    List<User> selectByUserType(String userType);

    /**
     * 根据状态查询用户列表
     */
    List<User> selectByStatus(Integer status);

    /**
     * 根据用户名或手机号查询用户
     */
    User selectByUsernameOrPhone(@Param("account") String account);

    /**
     * 查询用户角色
     */
    List<String> selectRolesByUserId(@Param("userId") Long userId);

    /**
     * 查询用户权限
     */
    List<String> selectPermissionsByUserId(@Param("userId") Long userId);

    /**
     * 根据用户类型统计数量
     */
    Long countByUserType(@Param("userType") String userType);

    /**
     * 统计今日新增用户数量
     */
    Long countTodayNew();

}
