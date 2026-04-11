package com.houserental.service;

/**
 * 验证码服务
 */
public interface VerifyCodeService {

    /**
     * 生成验证码
     * @param phone 手机号
     * @return 验证码
     */
    String generateCode(String phone);

    /**
     * 验证验证码
     * @param phone 手机号
     * @param code 验证码
     * @return 是否验证通过
     */
    boolean verifyCode(String phone, String code);

    /**
     * 发送验证码
     * @param phone 手机号
     * @param code 验证码
     */
    void sendCode(String phone, String code);

}
