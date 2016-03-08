package com.cn.smallrecipe.datainfo.recipedetail;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/2/28.
 */
public class RecipeDetailsInfo implements Serializable{
    private String image_url = null;
    private String msg_info = null;

    public RecipeDetailsInfo(String image_url, String msg_info) {
        this.image_url = image_url;
        this.msg_info = msg_info;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getMsg_info() {
        return msg_info;
    }

    public void setMsg_info(String msg_info) {
        this.msg_info = msg_info;
    }
}
