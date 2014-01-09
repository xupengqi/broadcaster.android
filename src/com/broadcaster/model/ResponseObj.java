package com.broadcaster.model;

import java.util.ArrayList;
import java.util.List;

import com.broadcaster.util.Constants.ERROR_CODE;
import com.google.gson.JsonObject;

public class ResponseObj {
    public List<ResponseError> errors = new ArrayList<ResponseError>();;
    public JsonObject data = new JsonObject();

    public ResponseObj() {}

    public ResponseObj(ResponseError error) {
        errors.add(error);
    }

    public boolean hasError() {
        return (errors.size() > 0);
    }

    public String getError() {
        switch(errors.get(0).code) {
        case NO_CONNECTION:
            return "Connection error.";
        case USERNAME_EXISTS:
            return "Username already exist.";
        case AUTHENTICATION_FAILED:
            return "Invalid username or password.";
        case REQUIRE_LOGIN:
            return "Please login.";
        case INTERNAL_ERROR:
            return "Internal Error.";
        default:
            return "UNKNOWN ERROR";
        }
    }

    public ERROR_CODE getErrorCode() {
        if (!hasError()) {
            return null;
        }
        return errors.get(0).code;
    }

    public static class ResponseError {
        public ERROR_CODE code;

        public ResponseError() { }

        public ResponseError(ERROR_CODE code) {
            this.code = code;
        }

        public static ResponseError createNoConnectionError() {
            return new ResponseError(ERROR_CODE.NO_CONNECTION);
        }
    }
}
