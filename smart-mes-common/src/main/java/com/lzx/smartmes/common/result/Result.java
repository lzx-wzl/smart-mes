package com.lzx.smartmes.common.result;

public class Result<T> {

    private int code;

    private String message;

    private T data;

    public Result() {
    }

    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static <T> Result<T> ok(T data) {
        return new Result<T>(
                200,
                "success",
                data
        );
    }

    public static Result<Void> ok() {
        return Result.<Void>ok(null);
    }

    public static <T> Result<T> fail(
            ResultCode code,
            String message
    ) {
        return new Result<T>(
                code.code,
                message,
                null
        );
    }
}