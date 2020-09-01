package com.learn.hadoop;

import lombok.Data;

@Data
public class LogResponse<T> {
    public static final int SUCCESS_CODE = 200;
    public static final int SERVER_EXCEPTION_CODE = 500;
    public static final int BAD_PARAM_CODE = 400;
    public static final String MSG_PARAM_ERROR= "params error";

    private int code;
    private String msg;
    private T data;

    public static <T> LogResponse<T> success(T data) {
        LogResponse<T> response = new LogResponse<>();
        response.setCode(SUCCESS_CODE);
        response.setMsg("success");
        response.setData(data);
        return response;
    }

    public static <T> LogResponse<T> exception(String msg) {
        return fail(msg, SERVER_EXCEPTION_CODE);
    }

    public static <T> LogResponse<T> badParam(String msg) {
        return fail(msg, BAD_PARAM_CODE);
    }

    private static <T> LogResponse<T> fail(String msg, int errorCode) {
        LogResponse<T> response = new LogResponse<>();
        response.setCode(errorCode);
        response.setMsg(msg);
        return response;
    }
}
