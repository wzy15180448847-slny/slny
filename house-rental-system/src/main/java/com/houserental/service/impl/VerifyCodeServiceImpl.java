package com.houserental.service.impl;

import com.houserental.service.VerifyCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 验证码服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VerifyCodeServiceImpl implements VerifyCodeService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 验证码过期时间（5分钟）
     */
    private static final int CODE_EXPIRATION = 5;

    /**
     * 验证码长度
     */
    private static final int CODE_LENGTH = 6;

    /**
     * 验证码前缀
     */
    private static final String CODE_PREFIX = "verify:code:";

    @Override
    public String generateCode(String phone) {
        // 生成6位数字验证码
        String code = String.format("%06d", new Random().nextInt(999999));
        // 存储到Redis，设置过期时间
        redisTemplate.opsForValue().set(
                CODE_PREFIX + phone,
                code,
                CODE_EXPIRATION,
                TimeUnit.MINUTES
        );
        log.info("生成验证码: {} for phone: {}", code, phone);
        return code;
    }

    @Override
    public boolean verifyCode(String phone, String code) {
        if (!StringUtils.hasText(phone) || !StringUtils.hasText(code)) {
            return false;
        }

        String storedCode = (String) redisTemplate.opsForValue().get(CODE_PREFIX + phone);
        if (storedCode == null) {
            log.warn("验证码已过期: {}", phone);
            return false;
        }

        boolean isValid = storedCode.equals(code);
        if (isValid) {
            // 验证通过后删除验证码
            redisTemplate.delete(CODE_PREFIX + phone);
        }
        return isValid;
    }

    @Override
    public void sendCode(String phone, String code) {
        // 这里可以集成短信发送服务，如阿里云短信、腾讯云短信等
        // 目前只是模拟发送
        log.info("向手机号 {} 发送验证码: {}", phone, code);
        // 实际项目中，这里应该调用短信发送API
        // smsService.sendSms(phone, code);
    }

}
