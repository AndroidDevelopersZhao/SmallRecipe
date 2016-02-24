package com.cn.smallrecipe.datainfo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/2/19.
 */
public class Result implements Serializable {
    private String totalNum = null;
    private int pn = -1;
    private int rn = -1;
    private ArrayList<Data> data = null;

    public String getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(String totalNum) {
        this.totalNum = totalNum;
    }

    public int getPn() {
        return pn;
    }

    public void setPn(int pn) {
        this.pn = pn;
    }

    public int getRn() {
        return rn;
    }

    public void setRn(int rn) {
        this.rn = rn;
    }

    public ArrayList<Data> getData() {
        return data;
    }

    public void setData(ArrayList<Data> data) {
        this.data = data;
    }
}
