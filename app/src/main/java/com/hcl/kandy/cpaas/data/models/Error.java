package com.hcl.kandy.cpaas.data.models;

public class Error {
    private String message;
    private int code;


    public Error(String message, int code) {
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "Code: " + code
                + " Message: " + message;
    }
}
