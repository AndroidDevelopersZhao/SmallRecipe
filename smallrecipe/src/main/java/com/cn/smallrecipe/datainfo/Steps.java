package com.cn.smallrecipe.datainfo;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/2/19.
 */
public class Steps implements Serializable {
    private String img = null;
    private String step =null;

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }
}
