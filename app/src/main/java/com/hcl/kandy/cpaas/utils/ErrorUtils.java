package com.hcl.kandy.cpaas.utils;

import com.google.gson.Gson;
import com.hcl.kandy.cpaas.data.models.Error;

import okhttp3.ResponseBody;

public class ErrorUtils {
    private static final int UNKNOWN_ERROR = 10001;
    private static final String UNKNOWN_ERROR_MESSAGE = "unknown_error";

    public static Error getError(ResponseBody responseBody, int code) {
        try {
            final String errorBody = responseBody.string();
            final Error error = new Gson().fromJson(errorBody, Error.class);
            error.setCode(code);
            return error;
        } catch (Exception e) {
            e.printStackTrace();
            return getError(e);
        }
    }

    public static Error getError(Throwable throwable) {
        return new Error(throwable.getMessage() != null ? throwable.getMessage() : UNKNOWN_ERROR_MESSAGE, UNKNOWN_ERROR);
    }
}
