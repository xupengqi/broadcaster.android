package com.broadcaster.model;

import java.util.ArrayList;
import java.util.List;

import com.broadcaster.util.Constants;
import com.broadcaster.util.Constants.TASK;
import com.google.gson.JsonObject;

public class ResponseObj {
    public List<ResponseError> errors = new ArrayList<ResponseError>();;
    public JsonObject data;

    public boolean hasError() {
        return (errors.size() > 0);
    }

    public String getReadableError(TASK t) {
        String errorCode = getErrorCode();
        switch(t) {
        case UPDATE_USERNAME:
            try {
                int sqlError = Integer.parseInt(errorCode);
                if (sqlError == Constants.SQL_ERROR_DUPLICATE) {
                    return "Username already exist.";
                }
            }
            catch (Exception e) {
                return getError();
            }
        case REGISTER:
            if (errorCode.equals("MISSING_PARAMETER")) {
                if (errors.get(0).custom_msg.equals("pass")) {
                    return "Please enter a password.";
                }
            }
        default:
            return getError();
        }
    }

    public String getError() {
        return errors.get(0).toString();
    }

    public String getErrorCode() {
        if (!hasError()) {
            return "";
        }
        return errors.get(0).code;
    }

    public class ResponseError {
        public Integer id;
        public String code;
        public String msg;
        public String custom_msg;

        @Override
        public String toString() {
            return msg+" "+custom_msg;
        }
    }
}
