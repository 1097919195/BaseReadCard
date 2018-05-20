package com.jaydenxiao.common.basebean;

import java.util.List;

/**
 * Created by Administrator on 2018/5/8 0008.
 */

public class HttpResponseError {

    /**
     * success : false
     * msg : The given data failed to pass validation.
     * errors : [{"field":"mac_address","message":"The mac address field is required."}]
     */

    private boolean success;
    private String msg;
    private List<ErrorsBean> errors;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<ErrorsBean> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorsBean> errors) {
        this.errors = errors;
    }

    public static class ErrorsBean {
        /**
         * field : mac_address
         * message : The mac address field is required.
         */

        private String field;
        private String message;

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
