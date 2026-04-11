package com.houserental.common.result;

import lombok.Getter;

/**
 * 响应状态码枚举
 */
@Getter
public enum ResultCode {

    /**
     * 成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 失败
     */
    ERROR(500, "操作失败"),

    /**
     * 参数错误
     */
    PARAM_ERROR(400, "参数错误"),

    /**
     * 未授权
     */
    UNAUTHORIZED(401, "未授权，请先登录"),

    /**
     * 禁止访问
     */
    FORBIDDEN(403, "禁止访问，权限不足"),

    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),

    /**
     * 请求方法不支持
     */
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),

    /**
     * 请求超时
     */
    REQUEST_TIMEOUT(408, "请求超时"),

    /**
     * 请求过于频繁
     */
    TOO_MANY_REQUESTS(429, "请求过于频繁，请稍后再试"),

    /**
     * 用户相关错误 (1000-1099)
     */
    USER_NOT_FOUND(1000, "用户不存在"),
    USER_ALREADY_EXISTS(1001, "用户已存在"),
    USER_PASSWORD_ERROR(1002, "密码错误"),
    USER_ACCOUNT_DISABLED(1003, "账户已被禁用"),
    USER_ACCOUNT_LOCKED(1004, "账户已被锁定"),
    USER_OLD_PASSWORD_ERROR(1005, "原密码错误"),
    USER_PASSWORD_SAME(1006, "新密码不能与旧密码相同"),
    USER_NOT_LOGIN(1007, "用户未登录"),
    USER_TOKEN_EXPIRED(1008, "登录已过期，请重新登录"),
    USER_TOKEN_INVALID(1009, "无效的登录凭证"),

    /**
     * 房源相关错误 (1100-1199)
     */
    HOUSE_NOT_FOUND(1100, "房源不存在"),
    HOUSE_ALREADY_EXISTS(1101, "房源已存在"),
    HOUSE_STATUS_ERROR(1102, "房源状态异常"),
    HOUSE_NOT_AVAILABLE(1103, "房源不可租"),
    HOUSE_ALREADY_RENTED(1104, "房源已出租"),
    HOUSE_IMAGE_UPLOAD_FAILED(1105, "房源图片上传失败"),

    /**
     * 预约相关错误 (1200-1299)
     */
    APPOINTMENT_NOT_FOUND(1200, "预约记录不存在"),
    APPOINTMENT_TIME_CONFLICT(1201, "预约时间冲突"),
    APPOINTMENT_ALREADY_CONFIRMED(1202, "预约已确认"),
    APPOINTMENT_ALREADY_CANCELLED(1203, "预约已取消"),
    APPOINTMENT_STATUS_ERROR(1204, "预约状态异常"),

    /**
     * 租约相关错误 (1300-1399)
     */
    LEASE_NOT_FOUND(1300, "租约不存在"),
    LEASE_ALREADY_EXISTS(1301, "租约已存在"),
    LEASE_STATUS_ERROR(1302, "租约状态异常"),
    LEASE_EXPIRED(1303, "租约已过期"),
    LEASE_TERMINATION_FAILED(1304, "租约解除失败"),

    /**
     * 支付相关错误 (1400-1499)
     */
    PAYMENT_FAILED(1400, "支付失败"),
    PAYMENT_AMOUNT_ERROR(1401, "支付金额错误"),
    PAYMENT_ALREADY_PAID(1402, "已支付，无需重复支付"),
    PAYMENT_NOT_FOUND(1403, "支付记录不存在"),

    /**
     * 维修相关错误 (1500-1599)
     */
    MAINTENANCE_NOT_FOUND(1500, "维修申请不存在"),
    MAINTENANCE_STATUS_ERROR(1501, "维修状态异常"),

    /**
     * 评价相关错误 (1600-1699)
     */
    REVIEW_NOT_FOUND(1600, "评价不存在"),
    REVIEW_ALREADY_EXISTS(1601, "您已评价过"),
    REVIEW_NOT_ALLOWED(1602, "暂不能评价"),

    /**
     * 文件相关错误 (1700-1799)
     */
    FILE_UPLOAD_FAILED(1700, "文件上传失败"),
    FILE_DOWNLOAD_FAILED(1701, "文件下载失败"),
    FILE_NOT_FOUND(1702, "文件不存在"),
    FILE_SIZE_EXCEEDED(1703, "文件大小超出限制"),
    FILE_TYPE_NOT_ALLOWED(1704, "文件类型不允许"),

    /**
     * 系统相关错误 (9000-9099)
     */
    SYSTEM_ERROR(9000, "系统错误"),
    SYSTEM_BUSY(9001, "系统繁忙，请稍后再试"),
    DATABASE_ERROR(9002, "数据库操作失败"),
    CACHE_ERROR(9003, "缓存操作失败"),
    MESSAGE_SEND_FAILED(9004, "消息发送失败");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
