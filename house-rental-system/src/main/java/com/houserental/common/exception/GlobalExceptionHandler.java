package com.houserental.common.exception;

import com.houserental.common.result.Result;
import com.houserental.common.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<Object>> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.warn("业务异常: url={}, code={}, message={}", request.getRequestURI(), e.getCode(), e.getMessage());
        return ResponseEntity.ok(Result.error(e.getCode(), e.getMessage()));
    }

    /**
     * 处理参数校验异常 (@Valid)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Object>> handleValidationException(MethodArgumentNotValidException e, HttpServletRequest request) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        String errorMessage = fieldErrors.stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn("参数校验失败: url={}, errors={}", request.getRequestURI(), errorMessage);
        return ResponseEntity.ok(Result.error(ResultCode.PARAM_ERROR.getCode(), errorMessage));
    }

    /**
     * 处理表单绑定异常 (@ModelAttribute)
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Result<Object>> handleBindException(BindException e, HttpServletRequest request) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        String errorMessage = fieldErrors.stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn("表单绑定失败: url={}, errors={}", request.getRequestURI(), errorMessage);
        return ResponseEntity.ok(Result.error(ResultCode.PARAM_ERROR.getCode(), errorMessage));
    }

    /**
     * 处理凭证错误异常（必须放在 AuthenticationException 之前）
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Result<Object>> handleBadCredentialsException(BadCredentialsException e, HttpServletRequest request) {
        log.warn("凭证错误: url={}, message={}", request.getRequestURI(), e.getMessage());
        return ResponseEntity.ok(Result.error(ResultCode.UNAUTHORIZED.getCode(), "用户名或密码错误"));
    }

    /**
     * 处理认证异常
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Result<Object>> handleAuthenticationException(AuthenticationException e, HttpServletRequest request) {
        log.warn("认证异常: url={}, message={}", request.getRequestURI(), e.getMessage());
        return ResponseEntity.ok(Result.error(ResultCode.UNAUTHORIZED.getCode(), "认证失败，请重新登录"));
    }

    /**
     * 处理数据重复异常
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Result<Object>> handleDuplicateKeyException(DuplicateKeyException e, HttpServletRequest request) {
        log.warn("数据重复: url={}, message={}", request.getRequestURI(), e.getMessage());
        return ResponseEntity.ok(Result.error(ResultCode.SYSTEM_ERROR.getCode(), "数据重复"));
    }

    /**
     * 处理数据完整性异常
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Result<Object>> handleDataIntegrityViolationException(DataIntegrityViolationException e, HttpServletRequest request) {
        log.warn("数据完整性异常: url={}, message={}", request.getRequestURI(), e.getMessage());
        return ResponseEntity.ok(Result.error(ResultCode.DATABASE_ERROR.getCode(), "数据操作失败"));
    }

    /**
     * 处理SQL异常
     */
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<Result<Object>> handleSQLException(SQLException e, HttpServletRequest request) {
        log.error("SQL异常: url={}, message={}", request.getRequestURI(), e.getMessage(), e);
        return ResponseEntity.ok(Result.error(ResultCode.DATABASE_ERROR.getCode(), "数据库操作失败"));
    }

    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Result<Object>> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        log.warn("非法参数: url={}, message={}", request.getRequestURI(), e.getMessage());
        return ResponseEntity.ok(Result.error(ResultCode.PARAM_ERROR.getCode(), e.getMessage()));
    }

    /**
     * 处理运行时异常（必须放在 Exception 之前）
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Result<Object>> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        log.error("运行时异常: url={}, message={}", request.getRequestURI(), e.getMessage(), e);
        return ResponseEntity.ok(Result.error(ResultCode.ERROR.getCode(), e.getMessage()));
    }

    /**
     * 处理其他异常（放在最后作为兜底）
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Object>> handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常: url={}, message={}", request.getRequestURI(), e.getMessage(), e);
        return ResponseEntity.ok(Result.error(ResultCode.SYSTEM_ERROR.getCode(), "系统繁忙，请稍后重试"));
    }
}