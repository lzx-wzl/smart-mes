package com.lzx.smartmes.common.exception;

import com.lzx.smartmes.common.result.Result;
import com.lzx.smartmes.common.result.ResultCode;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Result<Void> business(BusinessException e) {
        return Result.fail(
                e.getCode(),
                e.getMessage()
        );
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            ConstraintViolationException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> valid(Exception e) {
        return Result.fail(
                ResultCode.BAD_REQUEST,
                "validation failed"
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Void> denied(AccessDeniedException e) {
        return Result.fail(
                ResultCode.FORBIDDEN,
                "access denied"
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> unknown(Exception e) {
        return Result.fail(
                ResultCode.INTERNAL_ERROR,
                "internal server error"
        );
    }
}