package com.cn.smallrecipe.datainfo.myinfo;

/**
 * Created by Administrator on 2016/2/25.
 */
public class MyInfosTitle {
    private String image_left = null;
    private String text = null;
    private int image_right = -1;
    private String id = null;

    public MyInfosTitle(String image_left, String text, int image_right, String id) {
        this.image_left = image_left;
        this.text = text;
        this.image_right = image_right;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage_left() {
        return image_left;
    }

    public void setImage_left(String image_left) {
        this.image_left = image_left;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getImage_right() {
        return image_right;
    }

    public void setImage_right(int image_right) {
        this.image_right = image_right;
    }
}
