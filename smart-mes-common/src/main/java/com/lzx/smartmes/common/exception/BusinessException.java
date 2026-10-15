package com.lzx.smartmes.common.exception;

import com.lzx.smartmes.common.result.ResultCode;

public class BusinessException extends RuntimeException {

    private final ResultCode code;

    public BusinessException(String message) {
        this(
                ResultCode.BUSINESS_ERROR,
                message
        );
    }

    public BusinessException(
            ResultCode code,
            String message
    ) {
        super(message);
        this.code = code;
    }

    public ResultCode getCode() {
        return code;
    }
}