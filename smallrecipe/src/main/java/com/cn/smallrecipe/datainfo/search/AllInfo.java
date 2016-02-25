package com.cn.smallrecipe.datainfo.search;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/2/19.
 */
public class AllInfo implements Serializable {
    private int error_code = -1;
    private String resultcode = null;
    private String reason = null;
    private Result result = null;

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public String getResultcode() {
        return resultcode;
    }

    public void setResultcode(String resultcode) {
        this.resultcode = resultcode;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}
