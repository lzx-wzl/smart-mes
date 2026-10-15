package com.lzx.smartmes.common.result;

public enum ResultCode {

    SUCCESS(200, "success"),

    BAD_REQUEST(400, "bad request"),

    UNAUTHORIZED(401, "unauthorized"),

    FORBIDDEN(403, "forbidden"),

    NOT_FOUND(404, "not found"),

    BUSINESS_ERROR(409, "business error"),

    INTERNAL_ERROR(500, "internal error");

    public final int code;

    public final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}