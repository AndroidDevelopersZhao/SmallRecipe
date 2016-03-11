package com.cn.smallrecipe.datainfo.sendsayinfo;

import java.io.Serializable;
import java.util.ArrayList;

public class Resp_Say implements Serializable {
    private int resultCode = -1;//返回码
    private String resultMsg = null;//返回说明
    private ArrayList<Data_Say_Result> data = null;

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public ArrayList<Data_Say_Result> getData() {
        return data;
    }

    public void setData(ArrayList<Data_Say_Result> data) {
        this.data = data;
    }

}
